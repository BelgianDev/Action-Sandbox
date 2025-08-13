package fr.atlasworld.fresco.processor;

import fr.atlasworld.fresco.source.SourceEntry;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Used to process a resource pack entry.
 */
@FunctionalInterface
public interface ResourceProcessor {

    /**
     * Process a resource pack entry.
     *
     * @param entry entry to process.
     * @param output output.
     *
     * @return whether to copy the entry to the final zip or not,
     *         if {@code true} the entry will be copied, {@code false} the entry won't be copied.
     *
     * @throws IOException if the processor could not properly process the entry.
     */
    boolean process(@NotNull SourceEntry entry, @NotNull ProcessorOutput output) throws IOException;
}
