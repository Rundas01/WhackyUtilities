package net.rundas.whackyutilities.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.block.ModBlocks;
import net.rundas.whackyutilities.recipe.AutoHammerRecipe;

import java.util.List;
import java.util.Objects;

public class JEIWhackyUtilitiesPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(WhackyUtilities.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AutoHammerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<AutoHammerRecipe> recipes = rm.getAllRecipesFor(AutoHammerRecipe.Type.INSTANCE);
        registration.addRecipes(new RecipeType<>(AutoHammerRecipeCategory.UID, AutoHammerRecipe.class), recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.IRON_AUTO_HAMMER.get()), AutoHammerRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_AUTO_HAMMER.get()), AutoHammerRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DIAMOND_AUTO_HAMMER.get()), AutoHammerRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NETHERITE_AUTO_HAMMER.get()), AutoHammerRecipeCategory.UID);
    }
}
