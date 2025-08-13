package fr.atlasworld.fresco.source;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Represents a source entry that holds all meta-data and file inside it.
 */
public interface SourceEntry {

    /**
     * The entry file name.
     *
     * @return the entry file name.
     */
    @NotNull String filename();

    /**
     * The key of the entry, usually it's the entry name without the namespace, type and file extension.
     * <p>
     * Ex: {@code /example/model/item/foo.json} would become {@code item/foo}
     *
     * @return the key of the entry.
     */
    @NotNull String key();

    /**
     * Type of the entry.
     *
     * @return type of the entry.
     */
    @NotNull String type();

    /**
     * The namespace of the entry.
     *
     * @return namespace of the entry.
     */
    @NotNull String namespace();

    /**
     * Retrieve the entry full path relative to the root of the resource pack.
     *
     * @return full path relative the root of the resource pack.
     */
    @NotNull String fullPath();

    /**
     * Checks whether this entry is a directory.
     *
     * @return {@code true} if the entry is a directory, {@code false} otherwise.
     */
    boolean isDirectory();

    /**
     * Whether the entry is inside the namespace root.
     * Meaning that no types are associated with it.
     * <br>
     * Example: (assets/minecraft/sounds.json)
     *
     * @return {@code true} if the entry is inside the namespace root, {@code false} otherwise.
     */
    boolean isInsideNamespace();

    /**
     * Checks whether the entry is a meta file.
     * <br>
     * This is used, for example, for <a href="https://minecraft.wiki/w/Resource_pack#Texture_animation">Animated textures</a>
     *
     * @return {@code true} if the entry is a meta-file, {@code false} otherwise.
     */
    default boolean isMetaFile() {
        return !this.isDirectory() && this.filename().endsWith(".mcmeta");
    }

    /**
     * Open the entry as an input stream.
     *
     * @return input stream containing the resource.
     *
     * @throws IOException if the stream could not be opened.
     */
    @NotNull InputStream openStream() throws IOException;

    /**
     * Helper method to open the entry as JSON.
     *
     * @return {@link JsonElement} that was parsed out of the entry.
     *
     * @throws IOException if the stream could not be opened.
     * @throws JsonParseException if the input stream wasn't JSON.
     */
    default @NotNull JsonElement openAsJson() throws IOException, JsonParseException {
        try (InputStream stream = openStream();
             InputStreamReader reader = new InputStreamReader(stream)) {
            return JsonParser.parseReader(reader);
        }
    }
}
