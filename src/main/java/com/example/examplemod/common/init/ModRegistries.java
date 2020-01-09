package com.example.examplemod.common.init;

import com.example.examplemod.ExampleMod;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistries {

    @SubscribeEvent
    public static void onRegisterBlock(RegistryEvent.Register<Block> event) {
        ModBlocks.BLOCKS.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRegisterItem(RegistryEvent.Register<Item> event) {
        ModItems.ITEMS.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRegisterTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTileEntityType.TILE_ENTITIES.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRegisterContainersTypes(RegistryEvent.Register<ContainerType<?>> event) {
        ModContainerType.CONTAINERS.forEach(event.getRegistry()::register);
    }
}
