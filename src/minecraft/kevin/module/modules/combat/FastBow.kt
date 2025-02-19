package kevin.module.modules.combat

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.IntegerValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.RotationUtils
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class FastBow : Module("FastBow", "Allows you to use bow faster.", category = ModuleCategory.COMBAT) {
    private val packetsValue = IntegerValue("Packets", 20, 3, 20)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isUsingItem) {
            return
        }

        val currentItem = mc.thePlayer.inventory.getCurrentItem()

        if (currentItem != null && (currentItem.item)is ItemBow) {

            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, currentItem, 0F, 0F, 0F))

            val yaw = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.yaw
            else
                mc.thePlayer.rotationYaw

            val pitch = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.pitch
            else
                mc.thePlayer.rotationPitch

            for (i in 0 until packetsValue.get())
                mc.netHandler.addToSendQueue(C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, true))

            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            mc.thePlayer.itemInUseCount = currentItem.maxItemUseDuration - 1
        }
    }
}