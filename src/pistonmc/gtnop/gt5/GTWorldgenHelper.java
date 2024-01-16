package pistonmc.gtnop.gt5;

import gregtech.api.world.GT_Worldgen;
import net.minecraft.world.World;

public class GTWorldgenHelper {
    /**
     * Checks if the generation in the given world is allowed.
     *
     * @param worldgen The worldgen object to check.
     * @param world    The world to check.
     * @param genOverworld If worldgen is allowed in the overworld.
     */
    public static boolean isGenerationAllowed(GT_Worldgen worldgen, World world, boolean genOverworld) {
        // These 2 types are used to generate the default config
        // For unknown dimensions, GT by default treats them as overworld
        // If they are equal, it will write true to the config
        int worldType = 0;
        int allowedType = genOverworld ? 0 : -1;
        return worldgen.isGenerationAllowed(world, worldType, allowedType);
    }
}
