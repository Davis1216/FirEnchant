package top.catnies.firenchantkt.util.resource_wrapper

import org.bukkit.configuration.ConfigurationSection
import top.catnies.firenchantkt.engine.ConfigActionTemplate
import top.catnies.firenchantkt.util.ConfigParser
import top.catnies.firenchantkt.util.YamlUtils.getConfigurationSectionList

data class MenuItemData(
    val slot: Char,
    val item: ItemStackData,
    val action: List<ConfigActionTemplate>
) {
    companion object {
        // 根据 ConfigurationSection 获取 MenuItemData, 其中Slot手动指定传入;
        fun getMenuItemDataBySection(section: ConfigurationSection, slot: Char, fileName: String = "unknow"): MenuItemData {
            // 使用节点构建物品
            val item = ItemStackData(section).apply {
                verifyItem(fileName, section.currentPath!!)
            }
            // 获取动作节点, 解析动作
            val action = section.getConfigurationSectionList("click-actions")
                .mapNotNull {
                    ConfigParser.parseActionTemplate(it, fileName, "${section.currentPath}.click-actions")
                }
            return MenuItemData(slot, item, action)
        }
    }
}



data class ETMenuItemData(
    val slot: Char,
    val onlineRender: ItemRender,
    val offlineRender: ItemRender,
    val isBookSlot: Boolean
) {
    companion object {
        fun getETMenuItemDataBySection(
            section: ConfigurationSection,
            defaultSlot: Char,
            isBookSlot: Boolean,
            fileName: String = "unknow"
        ): ETMenuItemData {
            // TODO 更优雅的处理检查空异常?
            val slot = section.getString("slot")?.first() ?: defaultSlot
            val onlineRender = ItemRender(section.getConfigurationSection("online")!!)
            val offlineRender = ItemRender(section.getConfigurationSection("offline")!!)
            return ETMenuItemData(slot, onlineRender, offlineRender, isBookSlot)
        }
    }
}