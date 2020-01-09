package com.example.examplemod.proxy;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.render.tileEntity.CustomChestTileEntityRenderer;
import com.example.examplemod.client.screen.CustomChestScreen;
import com.example.examplemod.common.init.ModContainerType;
import com.example.examplemod.common.tileEntity.CustomChestTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;
import java.io.InputStream;

public class ClientProxy extends CommonProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        this.changeWindowIcon(event);
        this.registerScreenFactory();
        this.registerTileEntityRenderer();
    }

    /**
     * Register screen factory for custom containers
     */
    void registerScreenFactory() {
        ScreenManager.registerFactory(ModContainerType.CUSTOM_CHEST_CONTAINER_9X3, CustomChestScreen::new);
        ScreenManager.registerFactory(ModContainerType.CUSTOM_CHEST_CONTAINER_9X6, CustomChestScreen::new);
    }

    /**
     * Bind special renderer on block
     */
    void registerTileEntityRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(CustomChestTileEntity.class, new CustomChestTileEntityRenderer());
    }


    /**
     * Redefine the mainWindow icon, with custom image
     * <br>Help code for @Shawiiz_z in MinecraftForgeFrance Discord
     * <br>Don't forget to add an Alpha Channel in your image otherwise you will have glitches
     *
     * @see net.minecraft.client.MainWindow#setWindowIcon(InputStream, InputStream)
     * @see Minecraft#init()
     */
    void changeWindowIcon(FMLClientSetupEvent event) {
        try {
            InputStream iconSixteenStream = event.getMinecraftSupplier().get().getResourceManager().getResource(new ResourceLocation(ExampleMod.MODID, "textures/icons/icon_16.png")).getInputStream();
            InputStream iconThirtyTwoStream = event.getMinecraftSupplier().get().getResourceManager().getResource(new ResourceLocation(ExampleMod.MODID, "textures/icons/icon_32.png")).getInputStream();
            //Define Window Icon
            event.getMinecraftSupplier().get().mainWindow.setWindowIcon(iconSixteenStream, iconThirtyTwoStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
