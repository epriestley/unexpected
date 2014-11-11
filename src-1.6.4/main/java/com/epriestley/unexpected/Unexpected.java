package com.epriestley.unexpected;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.item.Item;

@Mod(modid = Unexpected.MODID, version = Unexpected.VERSION)
public class Unexpected {

  public static final String MODID = "unexpected";
  public static final String VERSION = "1.0.0";

  @EventHandler
  public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new UnexpectedEventHandler());
  }
}
