package top.catnies.firenchantkt.config

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.engine.ConfigActionTemplate
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_MENU_STRUCTURE_ERROR
import top.catnies.firenchantkt.util.ConfigParser
import top.catnies.firenchantkt.util.ItemUtils.nullOrAir
import top.catnies.firenchantkt.util.MessageUtils.sendTranslatableComponent
import top.catnies.firenchantkt.util.YamlUtils
import top.catnies.firenchantkt.util.YamlUtils.getConfigurationSectionList
import xyz.xenondevs.invui.gui.structure.Structure

class ExtractSoulConfig private constructor():
    AbstractConfigFile("modules/extract_soul.yml")
{

    companion object {
        @JvmStatic
        val instance by lazy { ExtractSoulConfig().apply { loadConfig() } }
    }

    val fallbackMenuStructure = arrayOf(
        "X.......?",
        "IIIIIIIII",
        "IIIIIIIII",
        "IIIIIIIII",
        ".........",
        "....O...."
    )


    /*菜单设置*/
    var MENU_TITLE: String by ConfigProperty("Extract Soul Menu")
    var MENU_STRUCTURE_ARRAY: Array<String> by ConfigProperty(fallbackMenuStructure)
    var MENU_INPUT_SLOT: Char by ConfigProperty('I')
    var MENU_OUTPUT_SLOT: Char by ConfigProperty('O')

    var MENU_CUSTOM_ITEMS: Map<Char, Pair<ItemStack?, List<ConfigActionTemplate>>> by ConfigProperty(emptyMap())
    var MENU_RESULT_ITEM_PROVIDER: String? by ConfigProperty(null)
    var MENU_RESULT_ITEM_ID: String? by ConfigProperty(null)
    var MENU_RESULT_ITEM: ItemStack? by ConfigProperty(null)

    var MENU_RESULT_ITEM_CLICK_ACTIONS: List<ConfigActionTemplate>? by ConfigProperty(null)

    // 加载数据
    override fun loadConfig() {
        MENU_TITLE = config().getString("menu-setting.title", "Extract Soul Menu")!!
        try { config().getStringList("menu-setting.structure").toTypedArray()
            .also { Structure(*it); MENU_STRUCTURE_ARRAY = it } // 测试合法性然后再赋值
        } catch (exception: Exception) {
            Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_MENU_STRUCTURE_ERROR, fileName) }
        MENU_INPUT_SLOT = config().getString("menu-setting.input-slot", "I")?.first() ?: 'I'
        MENU_OUTPUT_SLOT = config().getString("menu-setting.output-slot", "O")?.first() ?: 'O'

        // 1. 读取配置文件, 尝试构建物品, 尝试构建点击逻辑链并缓存.
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

        // 2. 构建结果物品
        MENU_RESULT_ITEM_PROVIDER = config().getString("extract-item.result-hooked-plugin", null)
        MENU_RESULT_ITEM_ID = config().getString("extract-item.result-hooked-id", null)
        MENU_RESULT_ITEM = YamlUtils.tryBuildItem(MENU_RESULT_ITEM_PROVIDER, MENU_RESULT_ITEM_ID, fileName, "extract-item.result-hooked-plugin")
        if (MENU_RESULT_ITEM.nullOrAir()) return

        // 3. 构建结果物品点击事件
        config().getConfigurationSectionList("extract-item.click-actions").let { actionList ->
            MENU_RESULT_ITEM_CLICK_ACTIONS = actionList
                .mapNotNull { actionNode ->
                    ConfigParser.parseActionTemplate(actionNode, fileName, "extract-item.click-actions")
                }
        }
    }

}