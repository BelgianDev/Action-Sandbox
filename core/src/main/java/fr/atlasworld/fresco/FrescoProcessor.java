package fr.atlasworld.fresco;

import fr.atlasworld.fresco.pack.PackDefinition;
import fr.atlasworld.fresco.pack.PackMeta;
import fr.atlasworld.fresco.processor.ProcessorOutput;
import fr.atlasworld.fresco.processor.ResourceProcessor;
import fr.atlasworld.fresco.source.EntryType;
import fr.atlasworld.fresco.source.SourceEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents the processors which will process all assets.
 */
public final class FrescoProcessor implements Closeable {
    public static final String META_FILE_LOCATION = "pack.mcmeta";
    public static final String ASSETS_DIR = "assets";

    private final @NotNull PackMeta meta;
    private final Logger logger;
    private final @NotNull Iterable<SourceEntry> entries;
    private final Map<EntryType, Set<ResourceProcessor>> processors;
    private final ProcessorOutput output;
    private final PackDefinition definition;

    private FrescoProcessor(@NotNull PackMeta meta, @NotNull Logger logger, @NotNull Iterable<SourceEntry> entries, Map<EntryType, Set<ResourceProcessor>> processors, @NotNull File outputFile, PackDefinition definition) throws IOException {
        this.meta = meta;
        this.logger = logger;
        this.entries = entries;
        this.processors = processors;
        this.definition = definition;

        this.output = new ProcessorOutput(outputFile);
    }

    /**
     * Start the processing of all the entries.
     */
    public void process() {
        Set<ResourceProcessor> allTypeProcessors = this.processors.computeIfAbsent(EntryType.ALL, k -> Set.of());

        for (SourceEntry entry : this.entries) {
            if (entry.isDirectory())
                continue;

            EntryType type;
            if (entry.isInsideNamespace())
                type = EntryType.CUSTOM;
            else
                type = this.definition.determineType(entry.type());

            this.logger.warn("Processing entry '{}' as type {}", entry.fullPath(), type);

            boolean copyEntry = true;
            Set<ResourceProcessor> processors = this.processors.computeIfAbsent(type, k -> Set.of());
            for (ResourceProcessor processor : processors) {
                logger.warn("Calling processor '{}' for entry '{}'", processor.getClass().getSimpleName(), entry.fullPath());
                try {
                    if (!processor.process(entry, this.output))
                        copyEntry = false;
                } catch (Throwable ex) {
                    this.logger.error("Failed to process entry '{}'", entry.fullPath(), ex);
                }
            }

            for (ResourceProcessor processor : allTypeProcessors) {
                try {
                    if (!processor.process(entry, this.output))
                        copyEntry = false;
                } catch (Throwable ex) {
                    this.logger.error("Failed to process entry '{}'", entry.fullPath(), ex);
                }
            }

            if (copyEntry) {
                try {
                    this.output.addEntry(entry);
                } catch (IOException ex) {
                    this.logger.error("Failed to copy entry '{}'", entry.fullPath(), ex);
                }
            }
        }

        try {
            this.output.writeEntry(this.meta.toString(), META_FILE_LOCATION);
            this.output.close();
        } catch (IOException ex) {
            this.logger.error("Failed to write pack meta", ex);
        }
    }

    @Override
    public void close() throws IOException {
        this.output.close();
    }

    /**
     * Create a new {@link FrescoProcessor.Builder}.
     *
     * @return newly created builder.
     */
    public static @NotNull Builder create() {
        return new Builder();
    }

    /**
     * {@link FrescoProcessor} builder.
     */
    public static class Builder {
        private PackMeta meta;
        private Logger logger;

        private final Map<EntryType, Set<ResourceProcessor>> processors;
        private final Set<SourceEntry> entries;

        private File outputFile;
        private PackDefinition packDefinition;

        @ApiStatus.Internal
        private Builder() {
            this.logger = LoggerFactory.getLogger(FrescoProcessor.class);

            this.processors = new HashMap<>();
            this.entries = new HashSet<>();
            this.packDefinition = PackDefinition.defaultDefinition();
        }

        /**
         * Set the pack meta.
         *
         * @param meta pack meta.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder meta(@NotNull PackMeta meta) {
            Objects.requireNonNull(meta, "pack meta must not be null!");

            this.meta = meta;
            return this;
        }

        /**
         * Sets the logger of the processor.
         *
         * @param logger processor logger.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder logger(@NotNull Logger logger) {
            Objects.requireNonNull(logger, "logger must not be null!");

            this.logger = logger;
            return this;
        }

        /**
         * Sets the pack definition that the processor will use to determine which processors to call.
         *
         * @param definition pack definition.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder definition(@NotNull PackDefinition definition) {
            Objects.requireNonNull(definition, "definition must not be null!");
            this.packDefinition = definition;
            return this;
        }

        /**
         * Set the output file.
         *
         * @param outputFile processor output file.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder outputFile(@NotNull File outputFile) {
            Objects.requireNonNull(outputFile, "output file must not be null!");

            this.outputFile = outputFile;
            return this;
        }

        /**
         * Add source entries to the processor.
         *
         * @param entry entry to be added.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder addEntry(@NotNull SourceEntry entry) {
            Objects.requireNonNull(entry, "entry must not be null!");
            this.entries.add(entry);
            return this;
        }

        /**
         * Add source entries to the processor.
         *
         * @param entries entries to be added.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder addEntries(@NotNull Collection<SourceEntry> entries) {
            Objects.requireNonNull(entries, "entries must not be null!");
            this.entries.addAll(entries);
            return this;
        }

        /**
         * Add a resource processor.
         *
         * @param type type of asset to process.
         * @param processor resource processor.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder addProcessor(@NotNull EntryType type, @NotNull ResourceProcessor processor) {
            Objects.requireNonNull(type, "type must not be null!");
            Objects.requireNonNull(processor, "processor must not be null!");

            this.processors.computeIfAbsent(type, k -> new HashSet<>()).add(processor);
            return this;
        }

        /**
         * Add resource processors.
         *
         * @param type type of resource to process.
         * @param processors resource processors.
         *
         * @return instance of this {@link Builder}.
         */
        public Builder addProcessors(@NotNull EntryType type, @NotNull ResourceProcessor... processors) {
            Objects.requireNonNull(type, "type must not be null!");
            Objects.requireNonNull(processors, "processors must not be null!");

            this.processors.computeIfAbsent(type, k -> new HashSet<>()).addAll(Arrays.asList(processors));
            return this;
        }

        /**
         * Create a new processor with the provided settings.
         *
         * @return newly created {@link FrescoProcessor}.
         *
         * @throws IllegalArgumentException if {@link #outputFile(File)} or {@link #meta(PackMeta)} are missing.
         * @throws IOException if the processor failed to initialize its output.
         */
        public @NotNull FrescoProcessor build() throws IOException {
            if (this.outputFile == null)
                throw new IllegalArgumentException("Output file must not be null!");

            if (this.meta == null)
                throw new IllegalArgumentException("Pack meta must not be null!");

            return new FrescoProcessor(this.meta, this.logger, this.entries, this.processors, this.outputFile, this.packDefinition);
        }
    }
}
