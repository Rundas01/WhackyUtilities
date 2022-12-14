package net.rundas.whackyutilities.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.rundas.whackyutilities.block.ModBlocks;

public class ModCreativeModeTab {
    public static final CreativeModeTab WHACKY_UTILITIES = new CreativeModeTab("whackyutilities") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.CRUCIBLE.get());
        }
    };
}