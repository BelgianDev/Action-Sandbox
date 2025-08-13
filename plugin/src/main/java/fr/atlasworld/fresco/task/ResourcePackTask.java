package fr.atlasworld.fresco.task;

import fr.atlasworld.fresco.FrescoProcessor;
import fr.atlasworld.fresco.config.PackMetaConfig;
import fr.atlasworld.fresco.entity.GradleSourceEntry;
import fr.atlasworld.fresco.pack.PackDefinition;
import fr.atlasworld.fresco.pack.PackMeta;
import fr.atlasworld.fresco.processor.ResourceProcessor;
import fr.atlasworld.fresco.source.EntryType;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resource pack task, handle the processing of the resource pack assets.
 */
public class ResourcePackTask extends DefaultTask {

    // Processor Settings

    private final PackMetaConfig metaConfig = new PackMetaConfig();
    private PackDefinition definition = PackDefinition.defaultDefinition();
    private final Map<EntryType, Set<ResourceProcessor>> processors = new HashMap<>();

    // Input properties

    private final Property<FileCollection> from = getProject().getObjects().property(FileCollection.class);

    // Output properties

    private final Property<String> archiveBaseName = getProject().getObjects().property(String.class);
    private final Property<String> archiveVersion = getProject().getObjects().property(String.class);
    private final Property<String> archiveClassifier = getProject().getObjects().property(String.class);
    private final Property<File> destinationDirectory = getProject().getObjects().property(File.class);

    public ResourcePackTask() {
        this.archiveBaseName.set(getProject().getName());
        this.archiveVersion.set(getProject().getVersion().toString());
        this.archiveClassifier.set("resource-pack");
        this.destinationDirectory.set(new File(getProject().getLayout().getBuildDirectory().getAsFile().get(), "resourcepacks"));
    }

    /**
     * Retrieves the metadata configuration for the resource pack.
     *
     * @return the metadata configuration as a {@link PackMetaConfig} instance.
     */
    @Nested
    public PackMetaConfig getMetaConfig() {
        return this.metaConfig;
    }

    /**
     * Configures the metadata for the resource pack using the provided action.
     *
     * @param action the action to configure the {@link PackMetaConfig} of the resource pack.
     */
    public void meta(Action<PackMetaConfig> action) {
        action.execute(this.metaConfig);
    }

    /**
     * Retrieves the file collection that represents the source files for the task.
     *
     * @return a property containing the collection of files to be used as input for the task.
     */
    @InputFiles
    public Property<FileCollection> getFrom() {
        return this.from;
    }

    /**
     * Retrieves the base name for the archive this task produces.
     *
     * @return a property representing the base name of the archive.
     */
    @Input
    public Property<String> getArchiveBaseName() {
        return this.archiveBaseName;
    }

    /**
     * Retrieves the version of the archive being created.
     *
     * @return a {@link Property} containing the archive version as a {@link String}.
     */
    @Input
    public Property<String> getArchiveVersion() {
        return this.archiveVersion;
    }

    /**
     * Returns the property representing the archive classifier that is used to
     * distinguish different variants of the archive, such as "sources" or "debug".
     *
     * @return the property for the archive classifier
     */
    @Input
    public Property<String> getArchiveClassifier() {
        return this.archiveClassifier;
    }

    /**
     * Retrieves the destination directory for the resource pack being generated.
     *
     * @return a {@link Property} representing the output directory where the resource pack files will be stored.
     */
    @OutputDirectory
    public Property<File> getDestinationDirectory() {
        return this.destinationDirectory;
    }

    /**
     * Replaces the default {@link PackDefinition} with an empty one.
     * This allows for a completely custom pack definition.
     */
    public void useCustomDefinition() {
        this.definition = new PackDefinition();
    }

    /**
     * Add a definition for specific asset paths.
     *
     * @param type  the entry type to associate with the defined paths.
     * @param paths the directory paths to associate with this entry type.
     */
    public void addDefinition(EntryType type, String... paths) {
        this.definition.addDefinition(type, paths);
    }

    /**
     * Add a custom processor to the resource pack for a specific entry type.
     *
     * @param type      the entry type the processor should process.
     * @param processor the processor implementation.
     */
    public void addProcessor(EntryType type, ResourceProcessor processor) {
        if (type == null || processor == null)
            throw new IllegalArgumentException("Type and processor must not be null!");

        this.processors.computeIfAbsent(type, k -> new HashSet<>()).add(processor);
    }

    @TaskAction
    public void process() throws IOException {
        FrescoProcessor.Builder builder = FrescoProcessor.create();

        builder.meta(new PackMeta(this.metaConfig.getDescription(), this.metaConfig.getPackFormat()));
        builder.logger(this.getLogger());
        builder.outputFile(this.computeOutputFile());
        builder.definition(this.definition);

        this.processors.forEach((type, processors) -> builder.addProcessors(type, processors.toArray(new ResourceProcessor[0])));
        this.collectEntries(builder);

        try (FrescoProcessor processor = builder.build()) {
            processor.process(); // AutoClosable should close by itself
        }
    }

    private File computeOutputFile() {
        return new File(this.destinationDirectory.get(), this.archiveBaseName.get() + "-" + this.archiveVersion.get() + "-" + this.archiveClassifier.get() + ".zip");
    }

    private void collectEntries(FrescoProcessor.Builder builder) {
        this.from.get().forEach(rootFile -> {
            Set<File> files = new HashSet<>();
            this.collectFilesRecursively(rootFile, files);

            for (File file : files) {
                String relativePath = rootFile.toPath().relativize(file.toPath()).toString();
                if (!relativePath.startsWith(FrescoProcessor.ASSETS_DIR))
                    continue;

                GradleSourceEntry entry = new GradleSourceEntry(file, rootFile);
                builder.addEntry(entry);
            }
        });
    }

    private void collectFilesRecursively(File root, Set<File> output) {
        File[] files = root.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isFile()) {
                output.add(file);
                continue;
            }

            this.collectFilesRecursively(file, output);
        }
    }
}
