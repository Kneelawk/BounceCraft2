package com.pommert.jedidiah.bouncecraft.fmp

import codechicken.multipart.TCuboidPart
import codechicken.lib.vec.Cuboid6
import codechicken.lib.vec.Rotation
import codechicken.lib.vec.Vector3
import net.minecraftforge.client.model.AdvancedModelLoader
import com.pommert.jedidiah.bouncecraft.ref.ModRef
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.obj.WavefrontObject
import net.minecraftforge.common.util.ForgeDirection
import net.minecraft.nbt.NBTTagCompound
import codechicken.lib.data.MCDataInput
import codechicken.lib.data.MCDataOutput
import net.minecraft.item.ItemStack
import com.pommert.jedidiah.bouncecraft.items.BCItems
import java.util.Arrays
import java.lang.{ Iterable => JIterable }
import net.minecraft.util.MovingObjectPosition
import net.minecraft.entity.Entity
import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraft.client.Minecraft
import com.pommert.jedidiah.bouncecraft.fmp.logic.BCPartLogic
import org.lwjgl.opengl.GL11
import com.pommert.jedidiah.bouncecraft.log.BCLog

class BCMultiPart(f: ForgeDirection, l: BCPartLogic) extends TCuboidPart {

	var facing: ForgeDirection = f
	BCLog.info("Placed: " + facing)
	var logic: BCPartLogic = if (l != null) l else BCPartLogic.newLogic(0)

	@SideOnly(Side.CLIENT)
	val texture = logic.getTexture()

	@SideOnly(Side.CLIENT)
	val model = AdvancedModelLoader.loadModel(logic.getModel())

	def this() = this(ForgeDirection.DOWN, null)

	@Override
	def getBounds = BCMultiPart.sides(facing.ordinal)

	@Override
	def getType = "bc_multipart"

	@Override
	override def load(tag: NBTTagCompound) {
		facing = ForgeDirection.VALID_DIRECTIONS(tag.getByte("facing"))
		logic = BCPartLogic.newLogic(tag.getInteger("logic_id"))
		logic.load(tag)
	}

	@Override
	override def save(tag: NBTTagCompound) {
		tag.setByte("facing", if (facing != null) facing.ordinal().asInstanceOf[Byte] else 0)
		tag.setInteger("logic_id", logic.getId())
		logic.save(tag)
	}

	@Override
	override def readDesc(packet: MCDataInput) {
		facing = ForgeDirection.VALID_DIRECTIONS(packet.readByte())
		logic = BCPartLogic.newLogic(packet.readInt())
		logic.readDesc(packet)
	}

	@Override
	override def writeDesc(packet: MCDataOutput) {
		packet.writeByte(if (facing != null) facing.ordinal().asInstanceOf[Byte] else 0)
		packet.writeInt(logic.getId())
		logic.writeDesc(packet)
	}

	def getItem = logic.getItem()

	@Override
	override def getDrops = Arrays.asList(getItem)

	@Override
	override def pickItem(hit: MovingObjectPosition) = getItem

	@Override
	override def onEntityCollision(entity: Entity) { logic.onEntityCollision(entity) }

	@Override
	@SideOnly(Side.CLIENT)
	override def renderDynamic(pos: Vector3, f: Float, pass: Int) {
		if (pass == 0) {
			GL11.glPushMatrix()
			GL11.glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
			BCMultiPart.renderTranslations(facing.ordinal())()
			Minecraft.getMinecraft().renderEngine.bindTexture(texture)
			model.renderAll()
			GL11.glPopMatrix()
		}
	}
}

object BCMultiPart {
	val sides = new Array[Cuboid6](6)
	sides(0) = new Cuboid6(0, 0, 0, 1, 1 / 16D, 1)
	for (i <- 1 to 5) {
		val t = Rotation.sideRotations(i).at(Vector3.center)
		sides(i) = sides(0).copy().apply(t)
	}

	val renderTranslations: Array[() => Unit] = Array(
		() => {},
		() => {},
		() => {},
		() => {},
		() => {},
		() => {})
}