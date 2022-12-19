package net.rundas.whackyutilities.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rundas.whackyutilities.WhackyUtilities;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WhackyUtilities.MOD_ID);

    public static final RegistryObject<RecipeSerializer<AutoHammerRecipe>> AUTO_HAMMER_SERIALIZER =
            SERIALIZERS.register("hammering", () -> AutoHammerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
