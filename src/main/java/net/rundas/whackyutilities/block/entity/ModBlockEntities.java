package net.rundas.whackyutilities.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.block.ModBlocks;
import net.rundas.whackyutilities.block.entity.custom.CrucibleBlockEntity;
import net.rundas.whackyutilities.block.entity.custom.PoweredCrucibleBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, WhackyUtilities.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("crucible_block_entity", () ->
                    BlockEntityType.Builder.of(CrucibleBlockEntity::new,
                            ModBlocks.CRUCIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<PoweredCrucibleBlockEntity>> POWERED_CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("powered_crucible_block_entity", () ->
                    BlockEntityType.Builder.of(PoweredCrucibleBlockEntity::new,
                            ModBlocks.POWERED_CRUCIBLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
