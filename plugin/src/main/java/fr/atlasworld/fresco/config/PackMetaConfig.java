package fr.atlasworld.fresco.config;

import fr.atlasworld.fresco.pack.PackMeta;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

public class PackMetaConfig {
    private String description;
    private int packFormat;

    @Input
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Input
    public int getPackFormat() {
        return packFormat;
    }

    public void setPackFormat(int packFormat) {
        this.packFormat = packFormat;
    }
}
