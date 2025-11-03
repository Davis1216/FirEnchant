package top.catnies.firenchantkt.config

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.engine.ConfigActionTemplate
import top.catnies.firenchantkt.item.brokengear.BrokenMatchRule
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_MENU_STRUCTURE_ERROR
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_VALUE_INVALID_ERROR
import top.catnies.firenchantkt.util.ConfigParser
import top.catnies.firenchantkt.util.ItemUtils.nullOrAir
import top.catnies.firenchantkt.util.MessageUtils.sendTranslatableComponent
import top.catnies.firenchantkt.util.YamlUtils
import top.catnies.firenchantkt.util.YamlUtils.getConfigurationSectionList
import xyz.xenondevs.invui.gui.structure.Structure
import kotlin.collections.LinkedHashMap

class RepairTableConfig private constructor():
    AbstractConfigFile("modules/repair_table.yml")
{
    companion object {
        @JvmStatic
        val instance by lazy { RepairTableConfig().apply { loadConfig() } }
    }
    val fallbackMenuStructure = arrayOf(
        "X.......?",
        "....I....",
        "...CCC...",
        ".POOOOON.",
    )

    /*菜单设置*/
    var MENU_TITLE_DENY: String by ConfigProperty("Repair DENY Menu")                   // 还没有放入任何可修复物品时的菜单标题
    var MENU_TITLE_ACCEPT: String by ConfigProperty("Repair ACCEPT Menu")               // 放入了合法的可修复物品时的菜单标题
    var MENU_STRUCTURE_ARRAY: Array<String> by ConfigProperty(fallbackMenuStructure)    // 菜单结构
    var MENU_INPUT_SLOT: Char by ConfigProperty('I')

    var MENU_OUTPUT_SLOT: Char by ConfigProperty('O')                                       // 输出槽位
    var MENU_OUTPUT_ARRAY_SIZE: Int by ConfigProperty(5)                                    // 输出列表尺寸
    var MENU_OUTPUT_ARRAY_SIZE_RULE: LinkedHashMap<String, Int> by ConfigProperty(LinkedHashMap())         // 输出列表尺寸列表
    var MENU_OUTPUT_UPDATE_TIME: Int by ConfigProperty(20)                                  // 输出列表刷新间隔
    var MENU_OUTPUT_ACTIVE_ADDITION_LORE: List<String> by ConfigProperty(mutableListOf())   // 正在修复的物品的附加描述
    var MENU_OUTPUT_COMPLETED_ADDITION_LORE: List<String> by ConfigProperty(mutableListOf())// 已完成修复的物品的附加描述

    var MENU_REPAIR_SLOT: Char by ConfigProperty('C')                                                       // 按钮槽位
    var MENU_REPAIR_SLOT_ITEM: Pair<ItemStack?, List<ConfigActionTemplate>>? by ConfigProperty(null)        // 按钮槽位物品

    var MENU_PREPAGE_SLOT: Char by ConfigProperty('P')                                                      // 上一页槽位
    var MENU_PREPAGE_SLOT_ITEM: Pair<ItemStack?, List<ConfigActionTemplate>>? by ConfigProperty(null)       // 上一页槽位物品

    var MENU_NEXTPAGE_SLOT: Char by ConfigProperty('N')                                                     // 下一页槽位
    var MENU_NEXTPAGE_SLOT_ITEM: Pair<ItemStack?, List<ConfigActionTemplate>>? by ConfigProperty(null)      // 下一页槽位物品

    var MENU_CUSTOM_ITEMS: Map<Char, Pair<ItemStack?, List<ConfigActionTemplate>>> by ConfigProperty(emptyMap())    // 菜单中的自定义物品

    /*修复规则*/
    var REPAIR_TIMERULE_RULE: String by ConfigProperty("static")     // 时间计算规则
    var REPAIR_TIMERULE_STATIC_TIME: Long by ConfigProperty(600)     // 固定花费规则 -> 固定时间
    var REPAIR_TIMERULE_LEVEL_TIME: Long by ConfigProperty(600)      // 等级计算规则 -> 每级时间
    var REPAIR_TIMERULE_LEVEL_FALLBACK: Long by ConfigProperty(450)  // 等级计算规则 -> 无魔咒时间
    var REPAIR_TIMERULE_COUNT_TIME: Long by ConfigProperty(600)      // 数量计算规则 -> 每个时间
    var REPAIR_TIMERULE_COUNT_FALLBACK: Long by ConfigProperty(450)  // 数量计算规则 -> 无魔咒时间

    var REPAIR_QUICK_TRIGGER_ACTION: List<ConfigActionTemplate> by ConfigProperty(emptyList()) // 触发快速修复成功后执行的动作列表
    var REPAIR_MAGNIFICATION_RULE: LinkedHashMap<String, Double> by ConfigProperty(LinkedHashMap()) // 折扣列表

    /*破损物品*/
    var BROKEN_FALLBACK_WRAPPER_ITEM: ItemStack? by ConfigProperty(null)                // 破损物品包装
    var BROKEN_MATCHES: MutableList<BrokenMatchRule> by ConfigProperty(mutableListOf()) // 破损物品包装


    // 加载数据
    override fun loadConfig() {
        /*修复规则*/
        REPAIR_TIMERULE_RULE = config().getString("repair-rule.time-rule.rule", "static")!!.lowercase()
        when(REPAIR_TIMERULE_RULE) {
            "static" -> {
                REPAIR_TIMERULE_STATIC_TIME = config().getLong("repair-rule.time-rule.rules.static.time", 600L)
            }
            "level" -> {
                REPAIR_TIMERULE_LEVEL_TIME = config().getLong("repair-rule.time-rule.rules.level.time", 600L)
                REPAIR_TIMERULE_LEVEL_FALLBACK = config().getLong("repair-rule.time-rule.rules.level.fallback", 450L)
            }
            "count" -> {
                REPAIR_TIMERULE_COUNT_TIME = config().getLong("repair-rule.time-rule.rules.count.time", 600L)
                REPAIR_TIMERULE_COUNT_FALLBACK = config().getLong("repair-rule.time-rule.rules.count.fallback", 450L)
            }
            else -> {
                Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_VALUE_INVALID_ERROR, fileName, "repair-rule.time-rule.rule")
            }
        }

        REPAIR_MAGNIFICATION_RULE = config().getMapList("repair-rule.magnification-rule")
            .associateTo(LinkedHashMap()) { (it["permission"] as String) to (it["magnification"] as? Double ?: 1.0) }

        /*菜单设置*/
        MENU_TITLE_DENY = config().getString("menu-setting.title-deny", "Repair DENY Menu")!!
        MENU_TITLE_ACCEPT = config().getString("menu-setting.title-accept", "Repair ACCEPT Menu")!!
        try { config().getStringList("menu-setting.structure").toTypedArray()
            .also { Structure(*it); MENU_STRUCTURE_ARRAY = it } // 测试合法性然后再赋值
        } catch (exception: Exception) {
            Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_MENU_STRUCTURE_ERROR, fileName) }
        MENU_INPUT_SLOT = config().getString("menu-setting.input-slot", "I")?.first() ?: 'I'
        MENU_OUTPUT_SLOT = config().getString("menu-setting.output-array.slot", "O")?.first() ?: 'O'
        MENU_OUTPUT_ARRAY_SIZE = config().getInt("menu-setting.output-array.array-max-size", 5)
        MENU_OUTPUT_ARRAY_SIZE_RULE = config().getMapList("menu-setting.output-array.array-size-rule")
            .associateTo(LinkedHashMap()) { (it["permission"] as String) to (it["size"] as Int) }
        MENU_OUTPUT_UPDATE_TIME = config().getInt("menu-setting.output-array.update-time", 20)
        MENU_OUTPUT_ACTIVE_ADDITION_LORE = config().getStringList("menu-setting.output-array.addition-active-lore")
        MENU_OUTPUT_COMPLETED_ADDITION_LORE = config().getStringList("menu-setting.output-array.addition-completed-lore")

        MENU_REPAIR_SLOT = config().getString("menu-setting.repair-bottom.slot", "C")?.first() ?: 'C'
        MENU_REPAIR_SLOT_ITEM = config().getConfigurationSection("menu-setting.repair-bottom")?.let { section ->
            // 使用节点构建物品
            val itemStack = ConfigParser.parseItemFromConfig(section, fileName, "menu-setting.repair-bottom")
            // 获取动作节点, 解析动作
            val actionList = section.getConfigurationSectionList("click-actions")
            val actionTemplates = actionList.mapNotNull {
                actionNode -> ConfigParser.parseActionTemplate(actionNode, fileName, "menu-setting.repair-bottom.click-actions")
            }
            itemStack to actionTemplates
        }

        MENU_PREPAGE_SLOT = config().getString("menu-setting.previous-page.slot", "P")?.first() ?: 'P'
        MENU_PREPAGE_SLOT_ITEM = config().getConfigurationSection("menu-setting.previous-page")?.let { section ->
            // 使用节点构建物品
            val itemStack = ConfigParser.parseItemFromConfig(section, fileName, "menu-setting.previous-page")
            // 获取动作节点, 解析动作
            val actionList = section.getConfigurationSectionList("click-actions")
            val actionTemplates = actionList.mapNotNull {
                    actionNode -> ConfigParser.parseActionTemplate(actionNode, fileName, "menu-setting.previous-page.click-actions")
            }
            itemStack to actionTemplates
        }

        MENU_NEXTPAGE_SLOT = config().getString("menu-setting.next-page.slot", "N")?.first() ?: 'N'
        MENU_NEXTPAGE_SLOT_ITEM = config().getConfigurationSection("menu-setting.next-page")?.let { section ->
            // 使用节点构建物品
            val itemStack = ConfigParser.parseItemFromConfig(section, fileName, "menu-setting.next-page")
            // 获取动作节点, 解析动作
            val actionList = section.getConfigurationSectionList("click-actions")
            val actionTemplates = actionList.mapNotNull {
                    actionNode -> ConfigParser.parseActionTemplate(actionNode, fileName, "menu-setting.next-page.click-actions")
            }
            itemStack to actionTemplates
        }

        // 读取配置文件, 尝试构建物品, 尝试构建点击逻辑链并缓存.
        config().getConfigurationSection("menu-setting.custom-items")?.let { customItemsSection ->
            val customItems = mutableMapOf<Char, Pair<ItemStack?, List<ConfigActionTemplate>>>() // 创建结果列表
            customItemsSection.getKeys(false).forEach { itemSectionKey ->
                // 解析物品节点如 'X', '?' 等节点
                val itemSection = customItemsSection.getConfigurationSection(itemSectionKey) // 这些 key 就是 如 'X', '?' 等
                itemSection?.let { section ->
                    // 使用节点构建物品
                    val itemStack = ConfigParser.parseItemFromConfig(section, fileName, itemSectionKey)
                    // 获取动作节点, 解析动作
                    val actionList = section.getConfigurationSectionList("click-actions")
                    val actionTemplates = actionList.mapNotNull { actionNode ->
                        ConfigParser.parseActionTemplate(actionNode, fileName, itemSectionKey)
                    }
                    if (itemStack.nullOrAir()) return@forEach // 如果物品是空则跳过保存, 延迟处理是想要继续解析物品动作一类的并给予警告, 虽然物品无效整个节点都无效就是了.
                    // 保存到结果列表里
                    customItems[itemSectionKey.first()] = itemStack to actionTemplates
                }
            }
            MENU_CUSTOM_ITEMS = customItems
        }

        /*修复规则*/
        REPAIR_QUICK_TRIGGER_ACTION = config().getConfigurationSection("repair-rule.quick-repair")?.let { section ->
            section.getConfigurationSectionList("trigger-action").mapNotNull { actionNode ->
                ConfigParser.parseActionTemplate(actionNode, fileName, "repair-rule.quick-repair.trigger-action")
            }
        } ?: emptyList()

        /*破损物品*/
        val itemProviderId = config().getString("broken-item-model.fallback-item.hooked-plugin", null)
        val itemId = config().getString("broken-item-model.fallback-item.hooked-id", null)
        BROKEN_FALLBACK_WRAPPER_ITEM = YamlUtils.tryBuildItem(itemProviderId, itemId, fileName, "broken-item-model.fallback-item")
            ?: ItemStack(Material.STONE)

        // 构建捕捉规则
        config().getConfigurationSectionList("broken-item-model.matches").forEach {
            val matchIDs = it.getStringList("match-item")
            val itemProviderID = it.getString("wrapper-item.hooked-plugin")
            val itemID = it.getString("wrapper-item.hooked-id")
            val itemWrapper = YamlUtils.tryBuildItem(itemProviderID, itemID, fileName, "broken-item-model.matches") ?: return@forEach
            BROKEN_MATCHES.add(BrokenMatchRule(matchIDs, itemWrapper))
        }
    }
}