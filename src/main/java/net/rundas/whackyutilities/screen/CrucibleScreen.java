package net.rundas.whackyutilities.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.util.FluidTankRenderer;

import java.util.Optional;

import static net.rundas.whackyutilities.util.MouseUtils.isMouseAboveArea;

public class CrucibleScreen extends AbstractContainerScreen<CrucibleMenu> {

    public CrucibleScreen(CrucibleMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(WhackyUtilities.MOD_ID, "textures/gui/crucible_gui.png");

    private FluidTankRenderer renderer;

    @Override
    protected void init() {
        super.init();
        assignFluidRenderer();
    }

    private void assignFluidRenderer() {
        renderer = new FluidTankRenderer(64000, true, 16, 62);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderFluidAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderFluidAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 114, 15,16,62)) {
            renderTooltip(pPoseStack, renderer.getTooltip(menu.blockEntity.getFluidStack(), TooltipFlag.Default.NORMAL), Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderStoneArrow(pPoseStack, x, y);
        renderLavaArrowAndFlame(pPoseStack, x, y);
        drawStings(pPoseStack, x, y);
        renderer.render(pPoseStack, x + 114, y + 15, menu.blockEntity.getFluidStack());
    }

    private void renderStoneArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.blockEntity.canConvertStone(menu.blockEntity)) {
            blit(pPoseStack, x + 23, y + 24, 179, 10, 10, 13);
        }
    }

    private void renderLavaArrowAndFlame(PoseStack pPoseStack, int x, int y) {
        if(menu.blockEntity.canCreateLava(menu.blockEntity)) {
            blit(pPoseStack, x + 91, y + 41, 176, 0, 19, 10);
            blit(pPoseStack, x + 99, y + 58, 176, 10, 3, 12);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    public void drawStings(PoseStack pPoseStack, int x, int y) {
        drawString(pPoseStack, Minecraft.getInstance().font, "Material: "+menu.blockEntity.stoneValue,x+8,y+40,0xffffff);
        drawString(pPoseStack, Minecraft.getInstance().font, "Conversion: "+menu.blockEntity.getModifier(menu.blockEntity)+"X",x+8,y+54,0xffffff);
    }
}
