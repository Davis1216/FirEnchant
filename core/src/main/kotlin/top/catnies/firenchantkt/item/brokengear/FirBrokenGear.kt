package top.catnies.firenchantkt.item.brokengear

import com.saicone.rtag.RtagItem
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.api.ServiceContainer
import top.catnies.firenchantkt.config.RepairTableConfig
import top.catnies.firenchantkt.util.ItemUtils.deserializeFromBytes
import top.catnies.firenchantkt.util.ItemUtils.nullOrAir
import top.catnies.firenchantkt.util.ItemUtils.serializeToBytes
import top.catnies.firenchantkt.util.resource_wrapper.ItemStackData

class FirBrokenGear: BrokenGear {

    companion object {
        @JvmStatic
        val instance: FirBrokenGear by lazy { FirBrokenGear().also {
            ServiceContainer.register(BrokenGear::class.java, it)
        } }
    }

    val config = RepairTableConfig.instance
    val fallback: () -> ItemStackData = { config.BROKEN_FALLBACK_WRAPPER_ITEM!! }
    val matches = config.BROKEN_MATCHES

    override fun isBrokenGear(item: ItemStack?): Boolean {
        if (item.nullOrAir()) return false
        return RtagItem.of(item).hasTag("FirEnchant", "FixType")
    }

    override fun toBrokenGear(item: ItemStack?): ItemStack? {
        if (item.nullOrAir() || isBrokenGear(item)) return null
        val wrapperItem = matches.find { it.matchItem(item) }?.wrapperItem ?: fallback()

        // 保存原物品
        val wrapper = wrapperItem.renderItem()
        val bytes = item.serializeToBytes()
        RtagItem.edit(wrapper) { it.set(bytes, "FirEnchant", "FixType") }

        // 迁移原物品的数据
        item.itemMeta?.displayName()?.let { wrapper.editMeta { meta -> meta.displayName(it) } }
        item.itemMeta?.lore()?.let { wrapper.editMeta { meta -> meta.lore(it) } }
        item.enchantments.forEach { (ench, level) -> wrapper.addUnsafeEnchantment(ench, level) }


        // 不可堆叠喵
        return wrapper.apply { editMeta { it.setMaxStackSize(1) } }
    }

    override fun repairBrokenGear(item: ItemStack?): ItemStack? {
        if (!isBrokenGear(item)) return null

        val bytes = RtagItem.of(item).get<ByteArray>("FirEnchant", "FixType") ?: return item
        return bytes.deserializeFromBytes()
    }
}