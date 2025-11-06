package top.catnies.firenchantkt.util.resource_wrapper

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.key.Key
import net.kyori.adventure.pointer.Pointered
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.api.FirEnchantAPI
import top.catnies.firenchantkt.integration.ItemProvider
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_HOOK_ITEM_NOT_FOUND
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_HOOK_ITEM_PROVIDER_NOT_FOUND
import top.catnies.firenchantkt.util.ItemUtils.nullOrAir
import top.catnies.firenchantkt.util.MessageUtils.renderToComponent
import top.catnies.firenchantkt.util.MessageUtils.sendTranslatableComponent

class ItemStackData(
    val plugin: String,
    val id: String
) {
    lateinit var itemProvider: ItemProvider
    lateinit var baseItem: ItemStack

    var itemName: String? = null
    var lore: List<String>? = null
    var itemModel: String? = null
    var customModelData: Int? = null
    var amount: Int = 1
    var damage: Int = 0

    constructor(section: ConfigurationSection) : this(
        section.getString("hooked-plugin") ?: "null",
        section.getString("hooked-id") ?: "null"
    ) {
        itemName = section.getString("item-name")
        lore = section.getStringList("lore")
        itemModel = section.getString("item-model")
        customModelData = section.getInt("custom-model-data")
        amount = section.getInt("amount", 1)
        damage = section.getInt("damage", 0)
    }

    // 验证物品是否存在
    fun verifyItem(fileName: String, path: String): Boolean {
        itemProvider = FirEnchantAPI.itemProviderRegistry().getItemProvider(plugin) ?: run {
            Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_HOOK_ITEM_PROVIDER_NOT_FOUND, fileName, path, plugin)
            return false
        }
        baseItem = itemProvider.getItemById(id)
            ?.takeUnless { it.nullOrAir() }
            ?: run {
                Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_HOOK_ITEM_NOT_FOUND, fileName, path, id)
                return false
            }
        return true
    }

    // 渲染物品
    fun renderItem(ptr: Pointered? = null, args: Map<String, String> = mutableMapOf()): ItemStack {
        val resultItem = baseItem.clone()
        // 物品名
        itemName?.let {
            val renderedComponent = it.renderToComponent(ptr, args)
                .replaceText { builder ->
                    builder
                        .matchLiteral("{original_name}")
                        .replacement(resultItem.getData(DataComponentTypes.ITEM_NAME))
                }
            resultItem.setData(DataComponentTypes.ITEM_NAME, renderedComponent)
        }
        // 物品描述
        lore?.let {
            if (it.isEmpty()) return@let // 空则返回
            val originalLore = baseItem.getData(DataComponentTypes.LORE)?.lines()
            val resultLore = it.fold(mutableListOf<Component>()) { acc, line ->
                if (line.contains("{original_lore}") && originalLore != null) acc.addAll(originalLore)
                else acc.add(line.renderToComponent(ptr, args))
                acc
            }
            resultItem.setData(DataComponentTypes.LORE, ItemLore.lore(resultLore))
        }
        // 物品模型
        itemModel?.let {
            resultItem.setData(DataComponentTypes.ITEM_MODEL, Key.key(it))
        }
        // 物品模型数据
        customModelData?.let {
            resultItem.editMeta { meta -> meta.setCustomModelData(it) }
        }
        // 物品数量
        amount.takeIf { it > 0 }?.let { resultItem.amount = it }
        // 物品损伤
        damage.takeIf { it > 0 }?.let { resultItem.setData(DataComponentTypes.DAMAGE, it) }
        return resultItem
    }

}