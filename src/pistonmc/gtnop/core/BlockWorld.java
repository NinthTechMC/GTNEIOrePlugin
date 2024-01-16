package pistonmc.gtnop.core;

import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreWorldAccess;

/**
 * Blocks used to represent worlds in the GUI
 */
public class BlockWorld extends Block implements IOreWorldAccess {
    public static final List<BlockWorld> ALL = new ArrayList<BlockWorld>();
    public static void register(BlockWorld block) {
        ALL.add(block);
    }
    private int renderId;
    private IOreWorld oreWorld;

    public BlockWorld(int renderId, IOreWorld oreWorld) {
        super(Material.rock);
        this.renderId = renderId;
        this.oreWorld = oreWorld;
    }

    @Override
    public IOreWorld getOreWorld() {
        return this.oreWorld;
    }

    @Override
    public int getRenderType() {
        return this.renderId;
    }

    public Block getWorldIcon() {
        Block b = this.oreWorld.getIcon();
        if (b instanceof BlockWorld) {
            // prevent infinite recursion
            return Blocks.air;
        }
        return b;
    }

    public int getWorldIconMeta() {
        return this.oreWorld.getIconMeta();
    }

    @Override
    public String getLocalizedName() {
        return this.oreWorld.getLocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return this.oreWorld.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isBlockNormalCube() {
        return this.getWorldIcon().isBlockNormalCube();
    }

    @Override
    public boolean isNormalCube() {
        return this.getWorldIcon().isNormalCube();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.getWorldIcon().renderAsNormalBlock();
    }


    
}
