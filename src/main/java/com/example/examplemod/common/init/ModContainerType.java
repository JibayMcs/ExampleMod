package com.example.examplemod.common.init;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.container.CustomChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModContainerType {

    public static final List<ContainerType<?>> CONTAINERS = new ArrayList();

    /**
     * Define single and double inventory container with 9x3 and 9x6 slots matrices
     */
    public static final ContainerType<CustomChestContainer> CUSTOM_CHEST_CONTAINER_9X3 = build("custom_chest_container_9x3", CustomChestContainer::createGeneric9X3);
    public static final ContainerType<CustomChestContainer> CUSTOM_CHEST_CONTAINER_9X6 = build("custom_chest_container_9x6", CustomChestContainer::createGeneric9X6);

    static {
        CONTAINERS.add(CUSTOM_CHEST_CONTAINER_9X3);
        CONTAINERS.add(CUSTOM_CHEST_CONTAINER_9X6);
    }

    static <T extends Container> ContainerType<T> build(String registryNameIn, ContainerType.IFactory<T> containerIn) {
        ResourceLocation registryName = new ResourceLocation(ExampleMod.MODID, registryNameIn);
        ContainerType<T> containerType = new ContainerType<>(containerIn);
        containerType.setRegistryName(registryName);
        return containerType;
    }
}
