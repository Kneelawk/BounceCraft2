package com.pommert.jedidiah.bouncecraft.items;

import net.minecraftforge.client.MinecraftForgeClient;

import com.pommert.jedidiah.bouncecraft.items.render.ItemRenderTestMultipart;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BCItemsClient {

	@SideOnly(Side.CLIENT)
	public static void init() {
		MinecraftForgeClient.registerItemRenderer(
				BCItems.items.get("itemTestMultiPart"),
				new ItemRenderTestMultipart());
	}
}