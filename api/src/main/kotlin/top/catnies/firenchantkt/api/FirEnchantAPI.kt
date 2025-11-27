package top.catnies.firenchantkt.api

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import top.catnies.firenchantkt.database.ConnectionManager
import top.catnies.firenchantkt.engine.ConditionRegistry
import top.catnies.firenchantkt.enchantment.EnchantmentData
import top.catnies.firenchantkt.enchantment.EnchantmentManager
import top.catnies.firenchantkt.enchantment.EnchantmentSetting
import top.catnies.firenchantkt.enchantment.EnchantmentSettingFactory
import top.catnies.firenchantkt.engine.ActionRegistry
import top.catnies.firenchantkt.integration.ItemProviderRegistry
import top.catnies.firenchantkt.item.AnvilItemRegistry
import top.catnies.firenchantkt.item.EnchantingTableItemRegistry
import top.catnies.firenchantkt.item.RepairTableItemRegistry
import top.catnies.firenchantkt.item.anvil.EnchantSoul
import top.catnies.firenchantkt.item.anvil.PowerRune
import top.catnies.firenchantkt.item.anvil.ProtectionRune
import top.catnies.firenchantkt.item.brokengear.BrokenGear


object FirEnchantAPI {

    // 物品提供者注册表
    val itemProviderRegistry: () -> ItemProviderRegistry = { ServiceContainer.get(ItemProviderRegistry::class.java) }
    // 铁砧物品注册表
    val anvilItemRegistry: () -> AnvilItemRegistry = { ServiceContainer.get(AnvilItemRegistry::class.java) }
    // 修复桌物品注册表
    val repairTableItemRegistry: () -> RepairTableItemRegistry = { ServiceContainer.get(RepairTableItemRegistry::class.java) }
    // 附魔台物品注册表
    val enchantingTableItemRegistry: () -> EnchantingTableItemRegistry = { ServiceContainer.get(EnchantingTableItemRegistry::class.java) }
    // 条件注册表
    val conditionRegistry: () -> ConditionRegistry = { ServiceContainer.get(ConditionRegistry::class.java) }
    // 动作注册表
    val actionRegistry: () -> ActionRegistry = { ServiceContainer.get(ActionRegistry::class.java) }
    // 数据库链接管理器
    val connectionManager: () -> ConnectionManager = { ServiceContainer.get(ConnectionManager::class.java) }


    /**
     * 构建一本附魔书.
     * @param key 魔咒的Key, 或者自行构建, 例如 `Key.key("minecraft:lure")`
     * @param level 附魔书魔咒的等级.
     * @param failure 附魔书的失败率.
     * @param consumedSouls 附魔书已经使用的灵魂数量.
     */
    @Nullable
    fun getSettingsByData(@NotNull key: Key, @NotNull level: Int, @NotNull failure: Int, @NotNull consumedSouls: Int = 0): EnchantmentSetting? {
        val data = getEnchantmentData(key) ?: return null
        return ServiceContainer.get(EnchantmentSettingFactory::class.java).fromData(data, level, failure, consumedSouls)
    }
    @Nullable
    fun getSettingsByItemStack(@NotNull item: ItemStack): EnchantmentSetting? {
        return ServiceContainer.get(EnchantmentSettingFactory::class.java).fromItemStack(item)
    }


    /**
     * 获取服务器内已经存在的附魔数据类, 注意, 如果插件reload了, 获取的副本可能会过期.
     * @param key 魔咒的ID, 如 "minecraft:unbreaking"
     */
    @Nullable
    fun getEnchantmentData(@NotNull key: String): EnchantmentData? {
        return ServiceContainer.get(EnchantmentManager::class.java).getEnchantmentData(key)
    }
    @Nullable
    fun getEnchantmentData(@NotNull key: Key): EnchantmentData? {
        return ServiceContainer.get(EnchantmentManager::class.java).getEnchantmentData(key)
    }
    @NotNull
    fun getAllEnchantmentData(): List<EnchantmentData> {
        return ServiceContainer.get(EnchantmentManager::class.java).getAllEnchantmentData()
    }


    /**
     * 魔咒之魂相关的接口
     */
    @NotNull
    fun isEnchantmentSoul(item: ItemStack): Boolean {
        val enchantmentSoul = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(EnchantSoul::class.java) ?: throw IllegalStateException("EnchantSoul is not registered.")
        return enchantmentSoul.matches(item)
    }
    @NotNull
    fun getPreEnchantmentSoulSubChance(): Int {
        val enchantmentSoul = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(EnchantSoul::class.java) ?: throw IllegalStateException("EnchantSoul is not registered.")
        return enchantmentSoul.getPreSoulSubChance()
    }


    /**
     * 升级符文的相关检查, 检查物品, 使用.
     */
    @NotNull
    fun getPowerRuneChance(powerRuneItem: ItemStack): Int {
        val powerRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(PowerRune::class.java) ?: throw IllegalStateException("PowerRune is not registered.")
        return powerRune.getChance(powerRuneItem)
    }
    @NotNull
    fun canUsePowerRune(item: ItemStack): Boolean {
        val powerRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(PowerRune::class.java) ?: throw IllegalStateException("PowerRune is not registered.")
        return powerRune.canUpgrade(item)
    }
    @NotNull
    fun applyPowerRune(item: ItemStack): Boolean {
        val powerRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(PowerRune::class.java) ?: throw IllegalStateException("PowerRune is not registered.")
        return powerRune.upgrade(item)
    }


    /**
     * 物品上保护符文的检查, 添加和删除.
     * @param item 物品
     */
    @NotNull
    fun hasProtectionRune(item: ItemStack): Boolean {
        val protectionRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(ProtectionRune::class.java) ?: throw IllegalStateException("ProtectionRune is not registered.")
        return protectionRune.hasProtectionRune(item)
    }
    @NotNull
    fun addProtectionRune(item: ItemStack): Boolean {
        val protectionRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(ProtectionRune::class.java) ?: throw IllegalStateException("ProtectionRune is not registered.")
        return protectionRune.addProtectionRune(item)
    }
    @NotNull
    fun removeProtectionRune(item: ItemStack): Boolean {
        val protectionRune = ServiceContainer.get(AnvilItemRegistry::class.java).getItem(ProtectionRune::class.java) ?: throw IllegalStateException("ProtectionRune is not registered.")
        return protectionRune.removeProtectionRune(item)
    }


    /**
     * 破损物品的检查, 转换和修复.
     */
    @NotNull
    fun isBrokenGear(item: ItemStack): Boolean {
        val brokenGear = ServiceContainer.get(BrokenGear::class.java)
        return brokenGear.isBrokenGear(item)
    }
    @Nullable
    fun toBrokenGear(item: ItemStack): ItemStack? {
        val brokenGear = ServiceContainer.get(BrokenGear::class.java)
        return brokenGear.toBrokenGear(item)
    }
    @Nullable
    fun repairBrokenGear(item: ItemStack): ItemStack? {
        val brokenGear = ServiceContainer.get(BrokenGear::class.java)
        return brokenGear.repairBrokenGear(item)
    }


    /**
     * 原始附魔书
     */


    /**
     * 重生之书
     */


    /**
     * 反转之书
     */


    /**
     * 快速修复卡
     */
}