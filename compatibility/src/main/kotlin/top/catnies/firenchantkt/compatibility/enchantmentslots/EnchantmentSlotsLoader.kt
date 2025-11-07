package top.catnies.firenchantkt.compatibility.enchantmentslots

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.catnies.firenchantkt.integration.HookPluginLoader
import top.catnies.firenchantkt.integration.ItemProvider

class EnchantmentSlotsLoader(
    val plugin: JavaPlugin,

    // 扩展符文需要的函数
    // TODO 无法获取配置文件对象, 直接把值转成Lambda这段太神经了, 有没有更好的办法?
    val enabled: () -> Boolean,
    val costExp: () -> Int,
    val itemProvider: () -> ItemProvider?,
    val id: () -> String?
): HookPluginLoader {

    override fun load() {
        Bukkit.getPluginManager().registerEvents(EnchantmentSlotsListener(this), plugin)
    }

}