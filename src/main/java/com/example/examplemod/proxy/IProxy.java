package com.example.examplemod.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public interface IProxy {

    void onClientSetup(FMLClientSetupEvent event);
}
