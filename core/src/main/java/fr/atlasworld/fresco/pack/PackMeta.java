package fr.atlasworld.fresco.pack;

import org.jetbrains.annotations.NotNull;

/**
 * Represents pack meta contained inside the resource packs.
 *
 * @param description description of the pack
 * @param packFormat pack format
 *
 * @see <a href="https://minecraft.wiki/w/Pack.mcmeta">Minecraft Wiki: Pack.mcmeta</a>
 */
public record PackMeta(String description, int packFormat) {

    @Override
    public @NotNull String toString() {
        return "{\"pack\":{\"description\":\"" + this.description + "\",\"pack_format\":" + this.packFormat + "}}";
    }
}
