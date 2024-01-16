package pistonmc.gtnop.api;

/**
 * Specialized ore for small ores
 */
public interface IOreSmall extends IOre {
    /**
     * Get the amount of ore per chunk.
     *
     * Return negative to skip rendering this info.
     */
    public int getAmountPerChunk();
}
