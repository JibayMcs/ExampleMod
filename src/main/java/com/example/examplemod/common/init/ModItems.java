package com.example.examplemod.common.init;

import com.example.examplemod.common.block.CustomChestBlock;
import com.example.examplemod.common.tileEntity.CustomChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {

    public static final List<Item> ITEMS = new ArrayList();

    public static final Item CUSTOM_CHEST = buildBlockItem(ModBlocks.CUSTOM_CHEST, teisrProperties(() -> {
        CustomChestTileEntity customChestTileEntity = new CustomChestTileEntity();
        /*
         * Bind {@link net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer} to {@link CustomChestBlock} itemstack
         */
        return new ItemStackTileEntityRenderer() {
            @Override
            public void renderByItem(ItemStack itemStackIn) {
                Item item = itemStackIn.getItem();
                if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CustomChestBlock) {
                    TileEntityRendererDispatcher.instance.renderAsItem(customChestTileEntity);
                }
            }
        };
    }));

    static {
        ITEMS.add(CUSTOM_CHEST);
    }

    static Item buildBlockItem(Block blockIn, Item.Properties propertiesIn) {
        BlockItem blockItem = new BlockItem(blockIn, propertiesIn);
        return blockItem.setRegistryName(blockIn.getRegistryName());
    }

    static Item.Properties defaultProperties() {
        return new Item.Properties().group(ItemGroup.MISC);
    }

    static Item.Properties teisrProperties(Supplier<ItemStackTileEntityRenderer> itemStackRenderer) {
        return defaultProperties().setTEISR(() -> {
            return () -> itemStackRenderer.get();
        });
    }
}
