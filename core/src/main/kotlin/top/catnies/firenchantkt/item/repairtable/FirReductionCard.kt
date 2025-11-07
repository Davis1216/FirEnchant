package top.catnies.firenchantkt.item.repairtable

import com.saicone.rtag.RtagItem
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.api.event.repairtable.ReductionCardUseEvent
import top.catnies.firenchantkt.config.RepairTableConfig
import top.catnies.firenchantkt.context.RepairTableContext

class FirReductionCard: ReductionCard {

    override fun matches(itemStack: ItemStack): Boolean {
        val tag = RtagItem.of(itemStack)
        val typeStr = tag.get<String>("FirEnchant", "RepairType") ?: return false
        val type = ReductionType.entries.find { it.name.equals(typeStr, true) } ?: return false

        // 根据不同type采取不同方式读取value，避免类型转换错误
        return when (type) {
            ReductionType.PERCENT -> (tag.get<Number>("FirEnchant", "RepairValue")?.toFloat() != null)
            ReductionType.STATIC -> (tag.get<Number>("FirEnchant", "RepairValue")?.toInt() != null)
        }
    }

    override fun onUse(event: InventoryClickEvent, context: RepairTableContext) {
        val tag = RtagItem.of(context.cursor)
        val typeStr = tag.get<String>("FirEnchant", "RepairType") ?: return
        val type = ReductionType.entries.find { it.name.equals(typeStr, true) } ?: return

        // 根据具体type安全地读取value
        val value: Double = when (type) {
            ReductionType.PERCENT -> tag.get<Number>("FirEnchant", "RepairValue")?.toDouble() ?: return
            ReductionType.STATIC -> tag.get<Number>("FirEnchant", "RepairValue")?.toInt()?.toDouble() ?: return
        }

        // 广播事件
        val useEvent = ReductionCardUseEvent(
            context.player,
            event,
            context.cursor,
            context.itemRepairTable,
            type, value
        )
        Bukkit.getPluginManager().callEvent(useEvent)
        if (useEvent.isCancelled) return

        // 根据类型减少修复时间
        when (type) {
            ReductionType.PERCENT -> {
                val lng = (context.itemRepairTable.remainingTime * useEvent.value).toLong()
                context.itemRepairTable.duration -= lng
            }
            ReductionType.STATIC -> {
                val remaining = context.itemRepairTable.remainingTime - (useEvent.value * 1000L)
                if (remaining <= 0) context.itemRepairTable.duration = 0L
                else context.itemRepairTable.duration = remaining.toLong()
            }
        }

        // 触发额外动作
        RepairTableConfig.instance.REPAIR_QUICK_TRIGGER_ACTION.forEach { action ->
            action.executeIfAllowed(
                mapOf(
                    "player" to context.player,
                    "clickType" to event.click,
                    "event" to event
                )
            )
        }

        // 减少玩家的光标物品
        context.cursor.apply { amount -= 1 }
    }
}