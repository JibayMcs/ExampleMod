package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    /**
     * Redefine the mainWindow icon, with custom image
     * <br>Help code for @Shawiiz_z in MinecraftForgeFrance Discord
     * <br>Don't forget to add an Alpha Channel in your image otherwise you will have glitches
     *
     * @see net.minecraft.client.MainWindow#setWindowIcon(InputStream, InputStream)
     * @see Minecraft#init()
     */
    private void doClientStuff(final FMLClientSetupEvent event) {

        try {
            InputStream iconSixteenStream = event.getMinecraftSupplier().get().getResourceManager().getResource(new ResourceLocation(ExampleMod.MODID, "textures/icons/icon_16.png")).getInputStream();
            InputStream iconThirtyTwoStream = event.getMinecraftSupplier().get().getResourceManager().getResource(new ResourceLocation(ExampleMod.MODID, "textures/icons/icon_32.png")).getInputStream();
            event.getMinecraftSupplier().get().mainWindow.setWindowIcon(iconSixteenStream, iconThirtyTwoStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
