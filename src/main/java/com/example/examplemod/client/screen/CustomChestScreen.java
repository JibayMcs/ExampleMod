package com.example.examplemod.client.screen;

import com.example.examplemod.common.container.CustomChestContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomChestScreen extends ContainerScreen<CustomChestContainer> implements IHasContainer<CustomChestContainer> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int inventoryRows;

    /**
     * Custom screen for {@link com.example.examplemod.common.block.CustomChestBlock} inventory
     */
    public CustomChestScreen(CustomChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.passEvents = false;
        this.inventoryRows = container.getNumRows();
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}