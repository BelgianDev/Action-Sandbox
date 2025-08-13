package fr.atlasworld.fresco.processor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import fr.atlasworld.fresco.source.SourceEntry;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Processor output, represents the output of the {@link fr.atlasworld.fresco.FrescoProcessor}
 * and all the registered {@link ResourceProcessor ResourceProcessors}.
 */
public final class ProcessorOutput implements Closeable {
    public static final Gson GSON = new Gson();

    private final ZipOutputStream stream;

    /**
     * Create a new processor output.
     *
     * @param outputFile file in which the output of the processor will be written to.
     *
     * @throws IOException if the file could not be opened.
     */
    public ProcessorOutput(@NotNull File outputFile) throws IOException {
        Objects.requireNonNull(outputFile, "Output file cannot be null!");

        if (outputFile.exists())
            outputFile.delete();

        outputFile.createNewFile();
        this.stream = new ZipOutputStream(new FileOutputStream(outputFile));
    }

    /**
     * Simply copies all the contents of the entry into the zip file at the same location as the source entry.
     *
     * @param entry entry to add.
     *
     * @throws IOException if the entry could not be added.
     */
    public void addEntry(@NotNull SourceEntry entry) throws IOException {
        this.addEntry(entry, entry.fullPath());
    }

    /**
     * Copies all the contents of the entry into the zip at the specified location.
     *
     * @param entry entry to add.
     * @param location location to put the entry in the final zip.
     *
     * @throws IOException if the entry could not be added.
     */
    public void addEntry(@NotNull SourceEntry entry, @NotNull String location) throws IOException {
        Objects.requireNonNull(entry, "Entry cannot be null!");
        Objects.requireNonNull(location, "Location cannot be null!");

        try (InputStream input = entry.openStream()) {
            this.writeEntry(input, location);
        }
    }

    /**
     * Write a JSON entry to the output.
     *
     * @param json json input.
     * @param location location to put the entry in the final zip.
     *
     * @throws IOException if the entry could not be added.
     */
    public void writeJsonEntry(@NotNull JsonElement json, @NotNull String location) throws IOException {
        this.writeEntry(GSON.toJson(json), location);
    }

    /**
     * Write an entry to the output.
     *
     * @param input string input to save to the entry.
     * @param location location to put the entry in the final zip.
     *
     * @throws IOException if the entry could not be added.
     */
    public void writeEntry(@NotNull String input, @NotNull String location) throws IOException {
        this.writeEntry(input, location, StandardCharsets.UTF_8);
    }

    /**
     * Write an entry to the output.
     *
     * @param input string input to save to the entry.
     * @param location location to put the entry in the final zip.
     * @param charset charset to use to encode the string.
     *
     * @throws IOException if the entry could not be added.
     */
    public void writeEntry(@NotNull String input, @NotNull String location, @NotNull Charset charset) throws IOException {
        this.writeEntry(input.getBytes(charset), location);
    }

    /**
     * Write an entry to the output.
     *
     * @param input input to write to the output.
     * @param location location to put the entry in the final zip.
     *
     * @throws IOException if the entry could not be added.
     */
    public void writeEntry(@NotNull InputStream input, @NotNull String location) throws IOException {
        Objects.requireNonNull(input, "Input stream cannot be null!");
        Objects.requireNonNull(location, "Location cannot be null!");

        this.writeEntry(input.readAllBytes(), location);
    }

    /**
     * Write an entry to the output.
     *
     * @param bytes bytes to write to the output.
     * @param location location to put the entry in the final zip.
     *
     * @throws IOException if the entry could not be added.
     */
    public void writeEntry(byte @NotNull [] bytes, @NotNull String location) throws IOException {
        Objects.requireNonNull(bytes, "Bytes cannot be null!");
        Objects.requireNonNull(location, "Location cannot be null!");

        ZipEntry zipEntry = new ZipEntry(location);
        synchronized (this.stream) {
            this.stream.putNextEntry(zipEntry);
            this.stream.write(bytes);
            this.stream.closeEntry();
        }
    }

    /**
     * Flush the current content inside the stream to the file.
     *
     * @throws IOException if the content could not be flushed.
     */
    public void flush() throws IOException {
        synchronized (this.stream) {
            this.stream.flush();
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        synchronized (this.stream) {
            this.stream.close();
        }
    }
}
