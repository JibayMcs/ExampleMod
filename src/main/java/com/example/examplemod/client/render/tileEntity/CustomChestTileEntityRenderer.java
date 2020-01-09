package com.example.examplemod.client.render.tileEntity;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.block.CustomChestBlock;
import com.example.examplemod.common.init.ModBlocks;
import com.example.examplemod.common.tileEntity.CustomChestTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.client.renderer.tileentity.model.LargeChestModel;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CustomChestTileEntityRenderer<T extends CustomChestTileEntity & IChestLid> extends TileEntityRenderer<T> {
    private static final ResourceLocation TEXTURE_NORMAL_DOUBLE = new ResourceLocation(ExampleMod.MODID, "textures/entity/custom_chest/normal_double.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation(ExampleMod.MODID, "textures/entity/custom_chest/normal.png");
    private final ChestModel simpleChest = new ChestModel();
    private final ChestModel largeChest = new LargeChestModel();

    /**
     * Renderer for {@link CustomChestBlock}
     *
     * @see net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer
     */
    public CustomChestTileEntityRenderer() {
    }

    @Override
    public void render(T tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        BlockState blockstate = tileEntityIn.hasWorld() ? tileEntityIn.getBlockState() : ModBlocks.CUSTOM_CHEST.getDefaultState().with(CustomChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.has(CustomChestBlock.TYPE) ? blockstate.get(CustomChestBlock.TYPE) : ChestType.SINGLE;
        if (chesttype != ChestType.LEFT) {
            boolean flag = chesttype != ChestType.SINGLE;
            ChestModel chestmodel = this.getChestModel(tileEntityIn, destroyStage, flag);
            if (destroyStage >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(flag ? 8.0F : 4.0F, 4.0F, 1.0F);
                GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
                GlStateManager.matrixMode(5888);
            } else {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
            GlStateManager.scalef(1.0F, -1.0F, -1.0F);
            float f = blockstate.get(CustomChestBlock.FACING).getHorizontalAngle();
            if ((double) Math.abs(f) > 1.0E-5D) {
                GlStateManager.translatef(0.5F, 0.5F, 0.5F);
                GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
                GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
            }

            this.applyLidRotation(tileEntityIn, partialTicks, chestmodel);
            chestmodel.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (destroyStage >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }

        }
    }

    private ChestModel getChestModel(T tileEntityIn, int destroyStage, boolean doubleChest) {
        ResourceLocation resourcelocation;
        if (destroyStage >= 0) {
            resourcelocation = DESTROY_STAGES[destroyStage];
        } else {
            resourcelocation = doubleChest ? TEXTURE_NORMAL_DOUBLE : TEXTURE_NORMAL;
        }

        this.bindTexture(resourcelocation);
        return doubleChest ? this.largeChest : this.simpleChest;
    }

    private void applyLidRotation(T tileEntity, float rot, ChestModel chestModel) {
        float f = tileEntity.getLidAngle(rot);
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        chestModel.getLid().rotateAngleX = -(f * ((float) Math.PI / 2F));
    }

}