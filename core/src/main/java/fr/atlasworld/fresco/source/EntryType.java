package fr.atlasworld.fresco.source;

/**
 * Entry types, used to define which pack processors should be called.
 */
public enum EntryType {

    /**
     * Represents all types in the resource pack, processors associated with this type will get called for every asset.
     */
    ALL,

    /**
     * Texture atlases, inside the {@code atlases} directory in the resource pack.
     */
    ATLASES,

    /**
     * Blockstates, inside the {@code blockstates} directory in the resource pack.
     */
    BLOCKSTATES,

    /**
     * Equipment, inside the {@code equipment} directory in the resource pack.
     */
    EQUIPMENT,

    /**
     * Fonts, inside the {@code font} directory in the resource pack.
     */
    FONT,

    /**
     * Items, inside the {@code items} directory in the resource pack.
     */
    ITEMS,

    /**
     * Languages, inside the {@code lang} directory in the resource pack.
     */
    LANGUAGES,

    /**
     * Item and block models, inside the {@code models} directory in the resource pack.
     */
    MODELS,

    /**
     * Particle definitions, inside the {@code particles} directory in the resource pack.
     */
    PARTICLES,

    /**
     * Post effects, inside the {@code post_effect} directory in the resource pack.
     */
    POST_EFFECT,

    /**
     * Shaders, inside the {@code shaders} directory in the resource pack.
     */
    SHADERS,

    /**
     * Sounds, inside the {@code sounds} directory in the resource pack.
     */
    SOUNDS,

    /**
     * Texts, inside the {@code texts} directory in the resource pack.
     */
    TEXTS,

    /**
     * Textures, inside the {@code textures} directory in the resource pack.
     */
    TEXTURES,

    /**
     * Waypoint style, inside the {@code waypoint_style} directory in the resource pack.
     */
    WAYPOINT_STYLE,

    /**
     * Undefined/unknown directory, these could be any directory not defined by any type.
     */
    CUSTOM
}
