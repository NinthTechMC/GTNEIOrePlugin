package pistonmc.gtnop;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import pistonmc.gtnop.core.BlockWorld;

/**
 * Renderer for the mining dimension block.
 *
 * It delegates the rendering to whatever icon block is set
 */
public class RenderHandler implements ISimpleBlockRenderingHandler {
    private BlockWorld block;
    public RenderHandler(BlockWorld block) {
        this.block = block;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
            RenderBlocks renderer) {
        Block icon = this.block.getWorldIcon();
        if (icon == Blocks.air) {
            return;
        }
        renderer.renderBlockAsItem(icon, this.block.getWorldIconMeta(), 1.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
            int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        Block icon = this.block.getWorldIcon();
        if (icon == Blocks.air) {
            return false;
        }
        return RenderBlocks.renderItemIn3d(icon.getRenderType());
    }

    @Override
    public int getRenderId() {
        return this.block.getRenderType();
    }
}
