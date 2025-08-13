package fr.atlasworld.fresco.entity;

import fr.atlasworld.fresco.source.SourceEntry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class GradleSourceEntry implements SourceEntry {
    public static final int ELEMENT_COUNT = 4; // 3 elements + 1 for the assets/ dir

    private static final int NAMESPACE_INDEX = 1;
    private static final int TYPE_INDEX = 2;
    private static final int KEY_INDEX = 3;

    private final String[] pathElements;
    private final String relativePath;
    private final File source;

    public GradleSourceEntry(File source, File rootDir) {
        this.relativePath = rootDir.toPath().relativize(source.toPath()).toString();
        this.pathElements = this.relativePath.split("/");
        this.source = source;

        if (pathElements.length < ELEMENT_COUNT - 1) // Need to take into account files in the root of the namespace (ex: assets/minecraft/sounds.json)
            throw new IllegalArgumentException("Source file '" + source.getAbsolutePath() + "' is not a valid resource pack entry!");
    }

    @Override
    public @NotNull String filename() {
        return this.relativePath.substring(this.relativePath.lastIndexOf('/') + 1);
    }

    @Override
    public @NotNull String key() {
        if (this.isInsideNamespace())
            return this.filename().substring(0, this.filename().lastIndexOf('.'));

        String key = this.pathElements[KEY_INDEX];
        return key.substring(0, key.lastIndexOf('.'));
    }

    @Override
    public @NotNull String type() {
        return this.pathElements[TYPE_INDEX];
    }

    @Override
    public @NotNull String namespace() {
        return this.pathElements[NAMESPACE_INDEX];
    }

    @Override
    public @NotNull String fullPath() {
        return this.relativePath;
    }

    @Override
    public boolean isDirectory() {
        return this.source.isDirectory();
    }

    @Override
    public boolean isInsideNamespace() {
        return this.pathElements.length == ELEMENT_COUNT - 1;
    }

    @Override
    public @NotNull InputStream openStream() throws IOException {
        return new FileInputStream(this.source);
    }
}
