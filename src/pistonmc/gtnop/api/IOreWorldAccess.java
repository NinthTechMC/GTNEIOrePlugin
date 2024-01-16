package pistonmc.gtnop.api;

/**
 * Implemented by classes that have an associated ore world
 */
public interface IOreWorldAccess {
    /**
     * Get the associated ore world
     */
    IOreWorld getOreWorld();
}
