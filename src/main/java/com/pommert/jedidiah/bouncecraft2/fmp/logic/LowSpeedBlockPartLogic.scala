package com.pommert.jedidiah.bouncecraft2.fmp.logic

import com.pommert.jedidiah.bouncecraft2.fmp.BCMultiPart
import com.pommert.jedidiah.bouncecraft2.fmp.logic.BCPartLogic.Index
import cpw.mods.fml.relauncher.SideOnly
import com.pommert.jedidiah.bouncecraft2.ref.ModRef
import net.minecraft.util.ResourceLocation
import cpw.mods.fml.relauncher.Side
import com.pommert.jedidiah.bouncecraft2.util.EntityUtil
import net.minecraft.entity.Entity
import com.pommert.jedidiah.bouncecraft2.ref.RecRef

class LowSpeedBlockPartLogic(part: BCMultiPart, index: Index) extends BCPartLogic(part, index) {

	@Override
	@SideOnly(Side.CLIENT)
	def getModel = RecRef.Models.LOW_SPEED_PART

	@Override
	@SideOnly(Side.CLIENT)
	def getTexture = RecRef.Textures.LOW_SPEED_PART

	@Override
	override def onEntityCollision(entity: Entity) {
		EntityUtil.fall(entity)
		val dir = RotatableDirectionLogic.getDirection(part.facing, part.rotation)
		PositionableMotionLogic.move(entity, dir, 2)
	}
}