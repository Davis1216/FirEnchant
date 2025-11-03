package top.catnies.firenchantkt.config

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import top.catnies.firenchantkt.api.FirEnchantAPI
import top.catnies.firenchantkt.engine.ConfigActionTemplate
import top.catnies.firenchantkt.language.MessageConstants.RESOURCE_MENU_STRUCTURE_ERROR
import top.catnies.firenchantkt.util.ConfigParser
import top.catnies.firenchantkt.util.ItemUtils.nullOrAir
import top.catnies.firenchantkt.util.MessageUtils.sendTranslatableComponent
import top.catnies.firenchantkt.util.YamlUtils.getConfigurationSectionList
import xyz.xenondevs.invui.gui.structure.Structure

class ShowEnchantedBooksConfig private constructor():
    AbstractConfigFile("modules/show_enchantedbooks.yml")
{

    companion object {
        @JvmStatic
        val instance by lazy { ShowEnchantedBooksConfig().apply { loadConfig() } }
    }

    val fallbackMenuStructure = arrayOf(
        "X.......?",
        "IIIIIIIII",
        "IIIIIIIII",
        "IIIIIIIII",
        "IIIIIIIII",
        ".......<>"
    )

    // 菜单
    var MENU_TITLE: String by ConfigProperty("All EnchantedBook Menu")
    var MENU_STRUCTURE_ARRAY: Array<String> by ConfigProperty(fallbackMenuStructure)    // 菜单结构
    var MENU_CONTENT_SLOT: Char by ConfigProperty('I')                                  // 内容槽位

    var MENU_PREPAGE_SLOT: Char by ConfigProperty('<')
    var MENU_PREPAGE_SLOT_ITEM: Pair<ItemStack?, List<ConfigActionTemplate>>? by ConfigProperty(null)

    var MENU_NEXTPAGE_SLOT: Char by ConfigProperty('>')
    var MENU_NEXTPAGE_SLOT_ITEM: Pair<ItemStack?, List<ConfigActionTemplate>>? by ConfigProperty(null)

    var MENU_CUSTOM_ITEMS: Map<Char, Pair<ItemStack?, List<ConfigActionTemplate>>> by ConfigProperty(emptyMap())

    // 配置
    var SHOW_ENCHANTEDBOOKS: List<ItemStack> by ConfigProperty(mutableListOf())     // 隐藏不显示的魔咒



    // 加载数据
    override fun loadConfig() {
        MENU_TITLE = config().getString("menu-setting.title", "All EnchantedBook Menu")!!
        try { config().getStringList("menu-setting.structure").toTypedArray()
            .also { Structure(*it); MENU_STRUCTURE_ARRAY = it } // 测试合法性然后再赋值
        } catch (exception: Exception) {
            Bukkit.getConsoleSender().sendTranslatableComponent(RESOURCE_MENU_STRUCTURE_ERROR, fileName) }
        MENU_CONTENT_SLOT = config().getString("menu-setting.content-slot", "I")?.first() ?: 'I'

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

        val hideEnchantments = config().getStringList("hide-enchantments")
        SHOW_ENCHANTEDBOOKS = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
            .fold(mutableListOf<ItemStack>()) { acc, enchantment ->
                if (hideEnchantments.contains(enchantment.key.asString())) return@fold acc
                val setting = FirEnchantAPI.getSettingsByData(enchantment.key, enchantment.maxLevel, 0, 0)
                setting?.toItemStack()?.let { acc.add(it) }
                return@fold acc
            }
    }

}