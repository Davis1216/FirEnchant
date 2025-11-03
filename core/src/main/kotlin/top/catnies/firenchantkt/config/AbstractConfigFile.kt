package top.catnies.firenchantkt.config

import org.bukkit.configuration.file.YamlConfiguration
import top.catnies.firenchantkt.FirEnchantPlugin
import java.io.File
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class AbstractConfigFile(
    val fileName: String
) {
    protected val plugin get() = FirEnchantPlugin.instance
    protected val logger get() = plugin.logger

    private var config: YamlConfiguration by Delegates.notNull()

    init {
        initFile()
    }

    // 初始化文件
    private fun initFile() {
        val file = File(plugin.dataFolder, fileName)
        if (!file.exists()) {
            plugin.saveResource(fileName, false)
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    // 安全获取属性
    protected fun config() = config

    // 子类实现
    protected abstract fun loadConfig()

    // 重新加载配置
    fun reload() {
        initFile()
        loadConfig()
    }

    // 自定义属性委托
    protected class ConfigProperty<T>(private val defaultValue: T) : ReadWriteProperty<AbstractConfigFile, T> {
        private var value: T = defaultValue

        override fun getValue(thisRef: AbstractConfigFile, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: AbstractConfigFile, property: KProperty<*>, value: T) {
            this.value = value
        }
    }
}