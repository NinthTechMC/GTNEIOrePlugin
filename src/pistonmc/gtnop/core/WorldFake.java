package pistonmc.gtnop.core;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Fake world for querying GT worldgen for non-vanilla and non-GC dimensions
 */
public class WorldFake extends World {
    public static class FakeWorldProvider extends WorldProvider {
        /** 
         * name used to stub provider.getDimensionName() 
         *
         * For example, "Twilight Forest"
         */
        private String dimName;

        public FakeWorldProvider(String dimName) {
            this.dimName = dimName;
        }

        @Override
        public String getDimensionName() {
            return this.dimName;
        }
    }

    public WorldFake(String dimName) {
        super(
            null /*saveHandler*/, 
            dimName, 
            new FakeWorldProvider(dimName) , 
            new WorldSettings(0L, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT), 
            null /*profiler*/);

    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    protected int func_152379_p() {
        // render distance, doesn't matter for us
        return 8;
    }

    @Override
    public Entity getEntityByID(int id) {
        // retur null for entity that doesn't exist
        return null;
    }
}
