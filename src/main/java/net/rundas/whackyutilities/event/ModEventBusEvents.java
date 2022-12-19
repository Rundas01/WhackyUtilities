package net.rundas.whackyutilities.event;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.recipe.AutoHammerRecipe;

@Mod.EventBusSubscriber(modid = WhackyUtilities.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, AutoHammerRecipe.Type.ID, AutoHammerRecipe.Type.INSTANCE);
    }
}
