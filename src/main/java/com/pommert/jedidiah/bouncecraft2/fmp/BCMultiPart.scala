package com.pommert.jedidiah.bouncecraft2.fmp

import java.util.Arrays
import org.lwjgl.opengl.GL11
import com.pommert.jedidiah.bouncecraft2.fmp.logic.BCPartLogic
import com.pommert.jedidiah.bouncecraft2.fmp.logic.BCPartLogic.Index
import com.pommert.jedidiah.bouncecraft2.log.BCLog
import codechicken.lib.data.MCDataInput
import codechicken.lib.data.MCDataOutput
import codechicken.lib.vec.Cuboid6
import codechicken.lib.vec.Rotation
import codechicken.lib.vec.Vector3
import codechicken.multipart.TCuboidPart
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.common.util.ForgeDirection
import com.pommert.jedidiah.bouncecraft2.items.BCItems
import cpw.mods.fml.relauncher.Side
import java.lang.Iterable
import java.util.ArrayList
import codechicken.lib.vec.BlockCoord
import codechicken.multipart.TileMultipart
import net.minecraft.world.World
import scala.util.control.Breaks._

class BCMultiPart(f: ForgeDirection, l: BCPartLogic, r: Byte, c: Boolean) extends TCuboidPart {

	val client = c

	var facing: ForgeDirection = f
	var rotation: Byte = r
	var logic: BCPartLogic = if (l != null) l else BCPartLogic.newLogic(Index.NULL_BCPARTLOGIC.getId, this)

	@SideOnly(Side.CLIENT)
	var texture = logic.getTexture()

	@SideOnly(Side.CLIENT)
	var model = AdvancedModelLoader.loadModel(logic.getModel())

	def setLogic(l: BCPartLogic) {
		logic = l
		setLogicClient()
	}

	@SideOnly(Side.CLIENT)
	def setLogicClient() {
		texture = logic.getTexture()
		model = AdvancedModelLoader.loadModel(logic.getModel())
	}

	def this(client: Boolean) = this(ForgeDirection.DOWN, null, 0, client)

	@Override
	def getBounds = BCMultiPart.sides(facing.ordinal)

	@Override
	override def getCollisionBoxes: Iterable[Cuboid6] = {
		logic.getCollisionBoxes()
	}

	@Override
	def getType = "bc_multipart"

	@Override
	override def load(tag: NBTTagCompound) {
		facing = ForgeDirection.VALID_DIRECTIONS(tag.getByte("facing"))
		rotation = tag.getByte("rotation")
		val id =
			if (tag.hasKey("logic_id", 3)) {
				tag.getInteger("logic_id").byteValue()
			} else if (tag.hasKey("logic_id", 1)) {
				tag.getByte("logic_id")
			} else {
				Index.NULL_BCPARTLOGIC.getId
			}
		logic = BCPartLogic.newLogic(id, this)
		logic.load(tag)
		setLogicClient()
	}

	@Override
	override def save(tag: NBTTagCompound) {
		tag.setByte("facing", if (facing != null) facing.ordinal.asInstanceOf[Byte] else 0)
		tag.setByte("rotation", rotation)
		tag.setByte("logic_id", logic.getId.getId)
		logic.save(tag)
	}

	@Override
	override def readDesc(packet: MCDataInput) {
		facing = ForgeDirection.VALID_DIRECTIONS(packet.readByte())
		rotation = packet.readByte()
		logic = BCPartLogic.newLogic(packet.readByte(), this)
		logic.readDesc(packet)
		setLogicClient()
	}

	@Override
	override def writeDesc(packet: MCDataOutput) {
		packet.writeByte(if (facing != null) facing.ordinal.asInstanceOf[Byte] else 0)
		packet.writeByte(rotation)
		packet.writeByte(logic.getId.getId)
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
	override def activate(player: EntityPlayer, hit: MovingObjectPosition, stack: ItemStack): Boolean = {
		val stackName =
			if (stack != null)
				stack.getItem().getUnlocalizedName(stack).toLowerCase()
			else
				""
		val isScrewDriver = (stackName.contains("screw") && stackName.contains("driver")) || stackName.contains("wrench") || stackName.contains("hammer")
		var worked = false
		if (isScrewDriver) {
			if (player.isSneaking()) {
				var direction = facing;

				breakable {
					for (i <- 0 to 5) {
						direction = ForgeDirection.VALID_DIRECTIONS((direction.ordinal() + 1) % 6)

						if (compareDirection(world, new BlockCoord(x, y, z), direction))
							break
					}
				}

				if (!direction.equals(facing)) {
					facing = direction;
					worked = true
				}
			} else {
				rotation = (rotation + 1).asInstanceOf[Byte]
				rotation = (rotation % 4).asInstanceOf[Byte]
				worked = true
			}
		}

		logic.activate(player, hit, stack) || worked
	}

	def compareDirection(world: World, pos: BlockCoord, direction: ForgeDirection): Boolean = {
		val tmp = TileMultipart.getTile(world, pos)
		if (tmp == null) return true
		val partList = tmp.partList
		for (ct <- partList) {
			if (ct.isInstanceOf[BCMultiPart]) {
				val bcct = ct.asInstanceOf[BCMultiPart]
				if (bcct.facing.equals(direction))
					return false
			}
		}
		true
	}

	@Override
	@SideOnly(Side.CLIENT)
	override def renderDynamic(pos: Vector3, f: Float, pass: Int) {
		if (pass == 0) {
			GL11.glPushMatrix()
			GL11.glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
			BCMultiPart.renderTransformations(facing.ordinal)(rotation)
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

	val renderTransformations: Array[(Byte) => Unit] = Array(
		(rotation: Byte) => {
			GL11.glTranslated(0, -0.5 + 1D / 32D, 0)
			GL11.glRotated(180, 0, 0, 1)
			GL11.glRotated(90 * rotation, 0, 1, 0)
			GL11.glTranslated(-0.5, -1D / 32D, -0.5)
		},
		(rotation: Byte) => {
			val r = if (rotation.&(1) == 0) {
				rotation
			} else {
				(rotation + 2) % 4
			}
			GL11.glRotated(90 * r, 0, 1, 0)
			GL11.glTranslated(-0.5, 0.5 - 1D / 16D, -0.5)
		},
		(rotation: Byte) => {
			val r = if (rotation.&(1) == 0) {
				rotation
			} else {
				(rotation + 2) % 4
			}
			GL11.glRotated(90 * r, 0, 0, 1)
			GL11.glTranslated(-0.5, -0.5, -0.5 + 1D / 16D)
			GL11.glRotated(270, 1, 0, 0)
		},
		(rotation: Byte) => {
			val r = if (rotation.&(1) == 0) {
				rotation
			} else {
				(rotation + 2) % 4
			}
			GL11.glTranslated(0, 0, 0.5 - 1D / 32D)
			GL11.glRotated(180, 0, 1, 0)
			GL11.glRotated(90 * r, 0, 0, 1)
			GL11.glTranslated(-0.5, -0.5, 1D / 32D)
			GL11.glRotated(270, 1, 0, 0)
		},
		(rotation: Byte) => {
			GL11.glTranslated(-0.5 + 1D / 16D, 0, 0)
			GL11.glRotated(90, 0, 0, 1)
			GL11.glRotated(90, 0, 1, 0)
			GL11.glRotated(90 * rotation, 0, 1, 0)
			GL11.glTranslated(-0.5, 0, -0.5)
		},
		(rotation: Byte) => {
			GL11.glRotated(90 * rotation, 1, 0, 0)
			GL11.glTranslated(0.5, 0, 0)
			GL11.glRotated(90, 0, 0, 1)
			GL11.glRotated(90, 0, 1, 0)
			GL11.glTranslated(0, 1D / 32D, -0.5)
			GL11.glRotated(180, 0, 0, 1)
			GL11.glTranslated(-0.5, -1D / 32D, 0)
		})
}