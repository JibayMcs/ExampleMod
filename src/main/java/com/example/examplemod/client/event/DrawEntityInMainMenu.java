package com.example.examplemod.client.event;

import com.example.examplemod.ExampleMod;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Example code to draw Player Model with the associated logged in user skin
 * <br>Help code for @Kysio in MinecraftForgeFrance Discord
 * <br>Classes inspected to create this.
 *
 * @see net.minecraft.client.gui.screen.inventory.InventoryScreen
 * @see net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer
 * @see net.minecraft.client.renderer.entity.layers.HeadLayer
 * @see net.minecraft.client.entity.player.AbstractClientPlayerEntity
 * @see net.minecraft.client.network.play.NetworkPlayerInfo
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class DrawEntityInMainMenu {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static GameProfile gameProfile = MINECRAFT.getSession().getProfile();
    private static PlayerModel playerModel = null;

    //Define a default player skin to avoid nulls
    private static ResourceLocation playerSkin = new ResourceLocation("textures/entity/steve.png");

    @SubscribeEvent
    public static void onPreInitScreen(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.getGui() instanceof MainMenuScreen) {

            //Fill GameProfile properties with texture key
            if (!gameProfile.getProperties().containsKey("textures")) {
                Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), (Property) null);
                if (property == null) {
                    gameProfile = MINECRAFT.getSessionService().fillProfileProperties(gameProfile, true);
                }
            }

            //Grab and load player skin with GameProfile informations
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = MINECRAFT.getSkinManager().loadSkinFromCache(gameProfile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                playerSkin = MINECRAFT.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

            /*
             * Initialize Player Model and Override render method to suppress LivingEntity interactions
             * Like getTicksElytraFlying() because Entity and World is null
             * */
            playerModel = new PlayerModel(0.0F, false) {

                /**
                 * Override this methods to remove interaction with the entityIn parameters
                 * <br>Because World and LivingEntity is null
                 * */
                @Override
                public void render(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                    this.bipedHead.render(scale);
                    this.bipedBody.render(scale);
                    this.bipedRightArm.render(scale);
                    this.bipedLeftArm.render(scale);
                    this.bipedRightLeg.render(scale);
                    this.bipedLeftLeg.render(scale);
                    this.bipedHeadwear.render(scale);

                    this.bipedLeftLegwear.render(scale);
                    this.bipedRightLegwear.render(scale);
                    this.bipedLeftArmwear.render(scale);
                    this.bipedRightArmwear.render(scale);
                    this.bipedBodyWear.render(scale);
                }
            };
        }
    }

    @SubscribeEvent
    public static void onDrawInScreen(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof MainMenuScreen) {
            int width = event.getGui().width;
            int height = event.getGui().height;
            if (playerModel != null)
                drawEntityOnScreen(width / 2 + 130, height / 2, 30, gameProfile, playerModel);
        }
    }

    /**
     * Draws an entity on the screen
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, GameProfile gameProfileIn, BipedModel entityModelIn) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();

        GlStateManager.translatef((float) posX, (float) posY, 50.0F);
        GlStateManager.scalef((float) (-scale), (float) scale, (float) scale);

        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(45F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(45F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, 0.0F, 0.0F);

        //Bind Player Skin
        MINECRAFT.getTextureManager().bindTexture(playerSkin);
        //Render Model with overrided render method
        entityModelIn.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

}
