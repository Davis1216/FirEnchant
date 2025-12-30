package top.catnies.firenchantkt.util.resource_wrapper

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
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
    val id: String,
    val render: ItemRender
) {
    lateinit var itemProvider: ItemProvider
    lateinit var baseItem: ItemStack

    constructor(section: ConfigurationSection, render: ItemRender) : this (
        section.getString("hooked-plugin") ?: "null",
        section.getString("hooked-id") ?: "null",
        render
    )

    constructor(section: ConfigurationSection) : this (
        section.getString("hooked-plugin") ?: "null",
        section.getString("hooked-id") ?: "null",
        ItemRender(section)
    )

    // 验证物品是否存在, 并生成基础物品缓存;
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

    // 获取渲染后的物品
    fun renderItem(ptr: Player? = null, args: Map<String, String> = mutableMapOf()): ItemStack {
        if (!::baseItem.isInitialized) return ItemStack(org.bukkit.Material.AIR)
        return render.renderItem(baseItem.clone(), args = args)
    }


    // 不走缓存获取基础物品
    fun renderItemNoCache(ptr: Player? = null, args: Map<String, String> = mutableMapOf()): ItemStack {
        return baseItemNoCache().apply { render.renderItem(this, ptr, args) }
    }

    // 不走缓存重新获取基础物品
    fun baseItemNoCache(): ItemStack {
        return itemProvider.getItemById(id)!!
    }

}


class ItemRender(
    var itemName: String? = null,
    var lore: List<String>? = null,
    var material: String? = null,
    var customModelData: Int? = null,
    var amount: Int = 1,
    var damage: Int = 0
) {

    constructor(section: ConfigurationSection) : this (
        itemName = section.getString("item-name"),
        lore = section.getStringList("lore"),
        material = section.getString("material"),
        customModelData = section.getInt("custom-model-data"),
        amount = section.getInt("amount", 1),
        damage = section.getInt("damage", 0)
    )

    // 渲染物品
    fun renderItem(item: ItemStack, ptr: Player? = null, args: Map<String, String> = mutableMapOf()): ItemStack {
        item.editMeta { meta ->
            // 物品名
            itemName?.let {
                val renderedComponent = it.renderToComponent(ptr, args)
                    .replaceText { builder ->
                        builder
                            .matchLiteral("{original_name}")
                            .replacement(meta.displayName())
                    }
                meta.displayName(renderedComponent)
            }
            // 物品描述
            lore?.let {
                if (it.isEmpty()) return@let // 空则返回
                val originalLore = meta.lore()
                val resultLore = it.fold(mutableListOf<Component>()) { acc, line ->
                    if (line.contains("{original_lore}") && originalLore != null) acc.addAll(originalLore)
                    else acc.add(line.renderToComponent(ptr, args))
                    acc
                }
                meta.lore(resultLore)
            }
            // 物品材质
            material?.let { matName ->
                org.bukkit.Material.matchMaterial(matName)?.let { mat ->
                    item.type = mat
                }
            }
            // 物品模型数据
            customModelData?.let {
                meta.setCustomModelData(it)
            }
            // 物品损伤
            damage.takeIf { it > 0 }?.let { 
                 if (meta is org.bukkit.inventory.meta.Damageable) {
                     meta.damage = it
                 }
            }
        }
        
        // 物品数量
        amount.takeIf { it > 0 }?.let { item.amount = it }

        return item
    }

}