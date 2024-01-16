package pistonmc.gtnop.api;

import net.minecraft.world.World;

/**
 * object given to ore instances to initialize generation info
 */
public interface IWorldInit extends IOreWorldAccess {
    /**
     * Get a fake world with world.provider.getDimensionName() stubbed
     * to always return a particular name.
     */
    World getWorldForQueryingGenerator();
}
