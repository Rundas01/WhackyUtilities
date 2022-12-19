package net.rundas.whackyutilities.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.block.ModBlocks;
import net.rundas.whackyutilities.recipe.AutoHammerRecipe;

import javax.annotation.Nonnull;

public class AutoHammerRecipeCategory implements IRecipeCategory<AutoHammerRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(WhackyUtilities.MOD_ID, "hammering");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(WhackyUtilities.MOD_ID, "textures/gui/auto_hammer_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public AutoHammerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.IRON_AUTO_HAMMER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AutoHammerRecipe> getRecipeClass() {
        return AutoHammerRecipe.class;
    }

    @Override
    public TextComponent getTitle() {
        return new TextComponent("Auto Hammer");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull AutoHammerRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        Item output = recipe.getResultItem().getItem();
        int count = recipe.getResultItem().getCount();
        ItemStack stack9 = new ItemStack(output,9);
        ItemStack stack17 = new ItemStack(output,17);
        ItemStack stack64 = new ItemStack(output,64);
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 38).addIngredients(recipe.getIngredients().get(0));
        if(count == 9){
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 20).addItemStack(stack9);
        }if(count == 81){
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 20).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 20).addItemStack(stack17);
        }if(count == 729){
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 20).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 20).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 20).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 20).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 38).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 38).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 38).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 38).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 56).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 56).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 56).addItemStack(stack64);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 56).addItemStack(stack17);
        }
    }
}
