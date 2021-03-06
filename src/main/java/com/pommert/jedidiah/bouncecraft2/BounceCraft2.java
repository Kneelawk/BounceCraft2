package com.pommert.jedidiah.bouncecraft2;

import com.pommert.jedidiah.bouncecraft2.blocks.BCBlocks;
import com.pommert.jedidiah.bouncecraft2.fmp.BCFMP;
import com.pommert.jedidiah.bouncecraft2.items.BCItems;
import com.pommert.jedidiah.bouncecraft2.log.BCLog;
import com.pommert.jedidiah.bouncecraft2.proxy.IProxy;
import com.pommert.jedidiah.bouncecraft2.ref.ClassPathRef;
import com.pommert.jedidiah.bouncecraft2.ref.ModRef;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModRef.MOD_ID)
public class BounceCraft2 {

	@Mod.Instance(ModRef.MOD_ID)
	private static BounceCraft2 instance;

	@SidedProxy(clientSide = ClassPathRef.PROXY_CLIENT, serverSide = ClassPathRef.PROXY_SERVER)
	private static IProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		BCLog.init(event.getModLog());
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	public static BounceCraft2 getBC() {
		return instance;
	}

	public static IProxy getProxy() {
		return proxy;
	}
}
