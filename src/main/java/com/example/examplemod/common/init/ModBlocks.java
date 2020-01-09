package com.example.examplemod.common.init;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.block.CustomChestBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList();

    public static Block CUSTOM_CHEST = build("custom_chest", new CustomChestBlock());

    static {
        BLOCKS.add(CUSTOM_CHEST);
    }

    static Block build(String registryNameIn, Block blockIn) {
        ResourceLocation registryName = new ResourceLocation(ExampleMod.MODID, registryNameIn);
        blockIn.setRegistryName(registryName);
        return blockIn;
    }
}
