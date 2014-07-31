package com.pommert.jedidiah.bouncecraft.fmp.logic

import com.pommert.jedidiah.bouncecraft.ref.ModRef
import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraft.util.ResourceLocation
import net.minecraft.nbt.NBTTagCompound
import codechicken.lib.data.MCDataInput
import codechicken.lib.data.MCDataOutput

class NullBCPartLogic extends BCPartLogic {

	def getItem = null

	@SideOnly(Side.CLIENT)
	def getTexture = new ResourceLocation(ModRef.MOD_ID, "textures/blocks/blockMissingTexture.png")

	@SideOnly(Side.CLIENT)
	def getModel = new ResourceLocation(ModRef.MOD_ID, "models/blockMissingTexture.obj")

	def load(tag: NBTTagCompound) {}

	def save(tag: NBTTagCompound) {}

	def readDesc(packet: MCDataInput) {}

	def writeDesc(packet: MCDataOutput) {}
}