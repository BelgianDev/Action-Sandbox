package fr.atlasworld.fresco.pack;

import fr.atlasworld.fresco.source.EntryType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PackDefinition is used to allow customization of which directories are linked to which asset type.
 */
public class PackDefinition {
    private final Map<String, EntryType> definitions;

    /**
     * Default (vanilla) pack definition.
     *
     * @return default (vanilla) pack definition.
     */
    public static @NotNull PackDefinition defaultDefinition() {
        PackDefinition definition = new PackDefinition();

        definition.addDefinition(EntryType.ATLASES, "atlases");
        definition.addDefinition(EntryType.BLOCKSTATES, "blockstates");
        definition.addDefinition(EntryType.EQUIPMENT, "equipment");
        definition.addDefinition(EntryType.FONT, "font");
        definition.addDefinition(EntryType.ITEMS, "items");
        definition.addDefinition(EntryType.LANGUAGES, "lang");
        definition.addDefinition(EntryType.MODELS, "models");
        definition.addDefinition(EntryType.PARTICLES, "particles");
        definition.addDefinition(EntryType.POST_EFFECT, "post_effects");
        definition.addDefinition(EntryType.SHADERS, "shaders");
        definition.addDefinition(EntryType.SOUNDS, "sounds");
        definition.addDefinition(EntryType.TEXTS, "texts");
        definition.addDefinition(EntryType.TEXTURES, "textures");
        definition.addDefinition(EntryType.WAYPOINT_STYLE, "waypoint_style");

        return definition;
    }

    /**
     * Create a new empty pack definition.
     */
    public PackDefinition() {
        this.definitions = new HashMap<>();
    }

    /**
     * Add a definition to a defined path.
     *
     * @param type type these paths will be associated with.
     * @param paths paths to associate.
     *
     * @throws IllegalArgumentException if the {@code type} is {@link EntryType#ALL}, this isn't allowed.
     *                                  or that one of the {@code paths} is already defined.
     */
    public void addDefinition(@NotNull EntryType type, @NotNull String... paths) {
        Objects.requireNonNull(type, "type must not be null!");
        Objects.requireNonNull(paths, "paths must not be null!");

        if (type == EntryType.ALL)
            throw new IllegalArgumentException("Cannot add definition for ALL type");

        for (String path : paths) {
            if (this.definitions.containsKey(path))
                throw new IllegalArgumentException("Cannot add definition for path '" + path + "', it already exists!");

            this.definitions.put(path, type);
        }
    }

    /**
     * Determine the type from the provided path.
     *
     * @param path path to check for.
     *
     * @return associate entry type.
     */
    public @NotNull EntryType determineType(@NotNull String path) {
        Objects.requireNonNull(path, "Path cannot be null!");
        return this.definitions.getOrDefault(path, EntryType.CUSTOM);
    }
}
