package com.example.examplemod.common.init;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.tileEntity.CustomChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModTileEntityType {

    public static final List<TileEntityType<?>> TILE_ENTITIES = new ArrayList();

    public static final TileEntityType<CustomChestTileEntity> CUSTOM_CHEST = build("custom_chest_tile", CustomChestTileEntity::new, ModBlocks.CUSTOM_CHEST);

    static {
        TILE_ENTITIES.add(CUSTOM_CHEST);
    }

    static <T extends TileEntity> TileEntityType<T> build(String registryNameIn, Supplier<T> factoryIn, Block... validBlocks) {
        ResourceLocation registryName = new ResourceLocation(ExampleMod.MODID, registryNameIn);
        TileEntityType<T> tileEntityType = TileEntityType.Builder.create(factoryIn, validBlocks).build(null);
        tileEntityType.setRegistryName(registryName);
        return tileEntityType;
    }
}
