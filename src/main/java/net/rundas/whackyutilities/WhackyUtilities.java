package net.rundas.whackyutilities;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.rundas.whackyutilities.block.ModBlocks;
import net.rundas.whackyutilities.block.entity.ModBlockEntities;
import net.rundas.whackyutilities.item.ModItems;
import net.rundas.whackyutilities.networking.ModMessages;
import net.rundas.whackyutilities.screen.CrucibleScreen;
import net.rundas.whackyutilities.screen.ModMenuTypes;

@Mod(WhackyUtilities.MOD_ID)
public class WhackyUtilities {
    public static final String MOD_ID = "whackyutilities";

    public WhackyUtilities() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        //ModRecipes.register(eventBus);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLCommonSetupEvent event) {
        //ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRUCIBLE.get(), RenderType.translucent());
        //ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_CRUCIBLE.get(), RenderType.translucent());
        MenuScreens.register(ModMenuTypes.CRUCIBLE_MENU.get(), CrucibleScreen::new);
        //MenuScreens.register(ModMenuTypes.POWERED_CRUCIBLE_MENU.get(), PoweredCrucibleScreen::new);
        //MenuScreens.register(ModMenuTypes.AUTO_HAMMER_MENU.get(), AutoHammerScreen::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }
}
