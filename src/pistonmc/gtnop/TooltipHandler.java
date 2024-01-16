package pistonmc.gtnop;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import pistonmc.gtnop.api.GTNEIOreAPI;

/**
 * Handle tooltips in the NEI GUI
 */
public class TooltipHandler {
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (event.toolTip.isEmpty()) {
            return;
        }
        ItemStack stack = event.itemStack;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return;
        }
        if (nbt.hasKey(GTNEIOreAPI.NBT_ORE_GEN_TYPE)) {
            this.handleOre(event, nbt);
        } else if (nbt.hasKey(GTNEIOreAPI.NBT_ORE_CHANCE)) {
            this.handleChance(event, nbt);
        }
    }

    private void handleOre(ItemTooltipEvent event, NBTTagCompound nbt) {
        int rarity = nbt.getInteger(GTNEIOreAPI.NBT_ORE_RARITY);
        switch (rarity) {
            case 1: {
                String name = event.toolTip.get(0);
                name = EnumRarity.uncommon.rarityColor + name + EnumChatFormatting.RESET;
                event.toolTip.set(0, name);
                break;
            }
            case 2: {
                String name = event.toolTip.get(0);
                name = EnumRarity.rare.rarityColor + name + EnumChatFormatting.RESET;
                event.toolTip.set(0, name);
                break;
            }
            default: {
                break;
            }
        }
        String genTypeKey = nbt.getString(GTNEIOreAPI.NBT_ORE_GEN_TYPE);
        String genType = I18n.format("gtnop.gui.nei.tooltip." + genTypeKey);
        event.toolTip.add(1, EnumChatFormatting.GRAY + genType + EnumChatFormatting.RESET);
    }

    private void handleChance(ItemTooltipEvent event, NBTTagCompound nbt) {
        float chance = nbt.getFloat(GTNEIOreAPI.NBT_ORE_CHANCE);
        String chanceStr = String.format("%.2f", chance * 100);
        String chanceText = I18n.format("gtnop.gui.nei.tooltip.chance", chanceStr);
        event.toolTip.add(1, EnumChatFormatting.GRAY + chanceText + EnumChatFormatting.RESET);
    }
}
