package com.example.outpostbane;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(OutpostBane.MOD_ID)
public class OutpostBane {
	public static final String MOD_ID = "outpostbane";

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

	public static final RegistryObject<Block> TOTEM = BLOCKS.register("outpost_totem",
		() -> new OutpostTotemBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_PURPLE)
			.strength(3.0f, 6.0f)
			.sound(SoundType.METAL)
			.lightLevel(state -> 10)));

	public static final RegistryObject<Item> TOTEM_ITEM = ITEMS.register("outpost_totem",
		() -> new BlockItem(TOTEM.get(), new Item.Properties()));

	public OutpostBane() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		modBus.addListener(this::addCreative);
		MinecraftForge.EVENT_BUS.register(new OutpostEvents());
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(TOTEM_ITEM);
		}
	}
}
