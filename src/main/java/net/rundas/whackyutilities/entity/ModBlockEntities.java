package net.rundas.whackyutilities.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, WhackyUtilities.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("crucible_block_entity", () ->
                    BlockEntityType.Builder.of(CrucibleBlockEntity::new,
                            ModBlocks.CRUCIBLE.get(),ModBlocks.IRON_CRUCIBLE.get(),ModBlocks.GOLD_CRUCIBLE.get(),
                            ModBlocks.DIAMOND_CRUCIBLE.get(),ModBlocks.NETHERITE_CRUCIBLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PoweredCrucibleBlockEntity>> POWERED_CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("powered_crucible_block_entity", () ->
                    BlockEntityType.Builder.of(PoweredCrucibleBlockEntity::new,
                            ModBlocks.POWERED_CRUCIBLE.get(),ModBlocks.POWERED_IRON_CRUCIBLE.get(),
                            ModBlocks.POWERED_GOLD_CRUCIBLE.get(),ModBlocks.POWERED_DIAMOND_CRUCIBLE.get(),
                            ModBlocks.POWERED_NETHERITE_CRUCIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AutoHammerBlockEntity>> AUTO_HAMMER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("auto_hammer_block_entity", () ->
                    BlockEntityType.Builder.of(AutoHammerBlockEntity::new,
                            ModBlocks.IRON_AUTO_HAMMER.get(),ModBlocks.GOLD_AUTO_HAMMER.get(),
                            ModBlocks.DIAMOND_AUTO_HAMMER.get(), ModBlocks.NETHERITE_AUTO_HAMMER.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
