package net.rundas.whackyutilities.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.item.ModCreativeModeTab;
import net.rundas.whackyutilities.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WhackyUtilities.MOD_ID);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static final RegistryObject<Block> CRUCIBLE = registerBlock("crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS),1), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> IRON_CRUCIBLE = registerBlock("iron_crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),2), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> GOLD_CRUCIBLE = registerBlock("gold_crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK),3), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> DIAMOND_CRUCIBLE = registerBlock("diamond_crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK),4), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> NETHERITE_CRUCIBLE = registerBlock("netherite_crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK),5), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> POWERED_CRUCIBLE = registerBlock("powered_crucible",
            () -> new PoweredCrucibleBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS),1), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> POWERED_IRON_CRUCIBLE = registerBlock("powered_iron_crucible",
            () -> new PoweredCrucibleBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),2), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> POWERED_GOLD_CRUCIBLE = registerBlock("powered_gold_crucible",
            () -> new PoweredCrucibleBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK),3), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> POWERED_DIAMOND_CRUCIBLE = registerBlock("powered_diamond_crucible",
            () -> new PoweredCrucibleBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK),4), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> POWERED_NETHERITE_CRUCIBLE = registerBlock("powered_netherite_crucible",
            () -> new PoweredCrucibleBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK),5), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> IRON_AUTO_HAMMER = registerBlock("iron_auto_hammer",
            () -> new AutoHammerBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),1), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> GOLD_AUTO_HAMMER = registerBlock("gold_auto_hammer",
            () -> new AutoHammerBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK),2), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> DIAMOND_AUTO_HAMMER = registerBlock("diamond_auto_hammer",
            () -> new AutoHammerBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK),3), ModCreativeModeTab.WHACKY_UTILITIES);
    public static final RegistryObject<Block> NETHERITE_AUTO_HAMMER = registerBlock("netherite_auto_hammer",
            () -> new AutoHammerBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK),4), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE = registerBlock("1x_compressed_cobblestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> DOUBLE_COMPRESSED_COBBLESTONE = registerBlock("2x_compressed_cobblestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> TRIPLE_COMPRESSED_COBBLESTONE = registerBlock("3x_compressed_cobblestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> COMPRESSED_GRAVEL = registerBlock("1x_compressed_gravel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> DOUBLE_COMPRESSED_GRAVEL = registerBlock("2x_compressed_gravel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> TRIPLE_COMPRESSED_GRAVEL = registerBlock("3x_compressed_gravel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> COMPRESSED_DIRT = registerBlock("1x_compressed_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> DOUBLE_COMPRESSED_DIRT = registerBlock("2x_compressed_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> TRIPLE_COMPRESSED_DIRT = registerBlock("3x_compressed_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> COMPRESSED_SAND = registerBlock("1x_compressed_sand",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SAND)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> DOUBLE_COMPRESSED_SAND = registerBlock("2x_compressed_sand",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SAND)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> TRIPLE_COMPRESSED_SAND = registerBlock("3x_compressed_sand",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SAND)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> DUST = registerBlock("dust",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POWDER_SNOW)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> CRUSHED_DEEPSLATE = registerBlock("crushed_deepslate",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> CRUSHED_BASALT = registerBlock("crushed_basalt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BASALT)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> CRUSHED_NETHERRACK = registerBlock("crushed_netherrack",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERRACK)), ModCreativeModeTab.WHACKY_UTILITIES);

    public static final RegistryObject<Block> CRUSHED_END_STONE = registerBlock("crushed_end_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE)), ModCreativeModeTab.WHACKY_UTILITIES);
}
