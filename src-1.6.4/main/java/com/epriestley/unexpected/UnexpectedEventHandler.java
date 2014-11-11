package com.epriestley.unexpected;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class UnexpectedEventHandler {

  /**
   * Make filling a glass bottle from a water source block consume the
   * block when using Enviromine.
   *
   * We register with LOW `priority` so we run after Enviromine's handler and
   * can consume the water source block after it bottles it into a potion. We
   * must `receiveCanceled` because Enviromine cancels the event.
   */
	@ForgeSubscribe(priority = EventPriority.LOW, receiveCanceled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (!event.isCanceled()) {
    	// We only care about events that Enviromine canceled after it acted
    	// on them.
		  return;
    }

		if (event.getResult() == Result.DENY) {
		  return;
		}

		if ((event.action != Action.RIGHT_CLICK_BLOCK) &&
		    (event.action != Action.RIGHT_CLICK_AIR)) {
		  return;
		}

		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;

    if (world.isRemote) {
      return;
    }

		ItemStack item = player.getCurrentEquippedItem();
		if (item == null) {
		  return;
		}

		if (item.itemID != Item.glassBottle.itemID) {
		  return;
		}

		MovingObjectPosition pos = getMovingObjectPosition(world, player, true);
		if (pos == null) {
		  return;
		}

		if (pos.typeOfHit != EnumMovingObjectType.TILE) {
		  return;
		}

		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;

		if (!world.canMineBlock(player, x, y, z)) {
			return;
		}

		if (!player.canPlayerEdit(x, y, z, pos.sideHit, item)) {
			return;
		}

    if ((world.getBlockId(x, y, z) != Block.waterStill.blockID) &&
        (world.getBlockId(x, y, z) != Block.waterMoving.blockID)) {
      return;
    }

    world.setBlockToAir(x, y, z);
	}

	protected static MovingObjectPosition getMovingObjectPosition(World world, EntityPlayer player, boolean hit_liquid) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + (double)(world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight());
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
		Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;

		if (player instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
		}

		Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
		return world.rayTraceBlocks_do_do(vec3, vec31, hit_liquid, !hit_liquid);
	}

}
