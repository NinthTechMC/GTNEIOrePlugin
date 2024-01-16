package pistonmc.gtnop.api;

/**
 * Interface used to add custom ore world to the config
 */
public interface IOreWorldConfig {
    /**
     * Return if the world should be added
     */
    public boolean isEnabled();

    /**
     * @return The name of the dimension, should be the same as
     * what you get from {@link net.minecraft.world.WorldProvider#getDimensionName()}
     * of the actual world
     */
    public String getDimensionName();

    /**
     * Create an {@link IOreWorldNamed} to be added to the config
     */
    public IOreWorldNamed createOreWorld();
}
