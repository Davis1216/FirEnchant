package top.catnies.firenchantkt.compatibility.enchantmentslots

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import top.catnies.firenchantkt.api.event.anvil.AnvilApplicableItemRegisterEvent
import top.catnies.firenchantkt.api.event.anvil.EnchantedBookPreUseEvent

class EnchantmentSlotsListener(
    val loader: EnchantmentSlotsLoader
): Listener {

    // 注册附魔槽位相关物品
    @EventHandler
    fun onAnvilItemRegister(event: AnvilApplicableItemRegisterEvent) {
        event.register.registerItem(FirSlotRune(loader))
    }

    // 监听使用附魔书事件, 如果槽位不够则不处理
    @EventHandler
    fun onPreEnchantedBookUseEvent(event: EnchantedBookPreUseEvent) {
        val remainingSlots = EnchantmentSlotsUtil.getRemainingSlots(event.player, event.firstItem)
        if (remainingSlots <= 0) {
            event.isCancelled = true
        }
    }

}