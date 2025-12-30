package top.catnies.firenchantkt.language

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import top.catnies.firenchantkt.language.tags.PlaceholderTag
import java.util.*
import kotlin.collections.HashMap
object MessageTranslator {
    val miniMessage = MiniMessage.builder().tags(
        TagResolver.builder()
            .resolver(TagResolver.standard())
            .resolver(PlaceholderTag) // Papi标签解析器
            .build()
    ).build()
    val key = Key.key("firenchant:lang")
    val translations = mutableMapOf<String, MutableMap<Locale, String>>()

    fun name() = key

    // 从存储中寻找语言值
    fun getMiniMessageString(key: String, locale: Locale): String? {
        var originText = translations[key]?.get(locale) ?: return null
        originText = PlaceholderAPI.setPlaceholders(null, originText) // 预处理一下
        return legacyToMiniMessage(legacyColorToMiniMessage(originText))
    }

    /**
     * 将旧版RGB颜色字符文本转换为miniMessage格式
     *
     * @param legacy 旧版颜色字符文本
     * @return miniMessage格式文本
     */
    fun legacyColorToMiniMessage(legacy: String): String {
        var message = legacy
        message = message.replace("&#", "#")
        return message.replace("(?!:)(?<!<)#([0-9a-fA-F]{6})(?!>)(?!:)".toRegex(), "<#$1>")
    }

    /**
     * 将旧版颜色字符文本转换为miniMessage格式
     * @param legacy 旧版颜色字符文本（格式如 "§aHello §x§f§f§0§0§0§0World"）
     * @return miniMessage格式文本（格式如 "<green>Hello <#ff0000>World"）
     */
    fun legacyToMiniMessage(legacy: String): String {
        val builder = StringBuilder()
        val chars = legacy.toCharArray()
        var i = 0
        while (i < chars.size) {
            // 处理颜色代码前缀（§或&）
            if (isColorCode(chars[i])) {
                // 检查下一个字符是否有效
                if (i + 1 >= chars.size) {
                    builder.append(chars[i])
                    i++
                    continue
                }

                when (val code = chars[i + 1]) {
                    // 基础颜色和格式代码
                    '0' -> builder.append("<black>")
                    '1' -> builder.append("<dark_blue>")
                    '2' -> builder.append("<dark_green>")
                    '3' -> builder.append("<dark_aqua>")
                    '4' -> builder.append("<dark_red>")
                    '5' -> builder.append("<dark_purple>")
                    '6' -> builder.append("<gold>")
                    '7' -> builder.append("<gray>")
                    '8' -> builder.append("<dark_gray>")
                    '9' -> builder.append("<blue>")
                    'a' -> builder.append("<green>")
                    'b' -> builder.append("<aqua>")
                    'c' -> builder.append("<red>")
                    'd' -> builder.append("<light_purple>")
                    'e' -> builder.append("<yellow>")
                    'f' -> builder.append("<white>")
                    'r' -> builder.append("<reset>")
                    'l' -> builder.append("<b>")
                    'm' -> builder.append("<st>")
                    'o' -> builder.append("<i>")
                    'n' -> builder.append("<u>")
                    'k' -> builder.append("<obf>")
                    // 十六进制颜色代码（§x§R§R§G§G§B§B）
                    'x' -> {
                        if (i + 13 < chars.size &&
                            isColorCode(chars[i + 2]) &&
                            isColorCode(chars[i + 4]) &&
                            isColorCode(chars[i + 6]) &&
                            isColorCode(chars[i + 8]) &&
                            isColorCode(chars[i + 10]) &&
                            isColorCode(chars[i + 12])
                        ) {
                            builder.append("<#")
                                .append(chars[i + 3])
                                .append(chars[i + 5])
                                .append(chars[i + 7])
                                .append(chars[i + 9])
                                .append(chars[i + 11])
                                .append(chars[i + 13])
                                .append(">")
                            i += 13  // 跳过整个序列（x+12字符）
                        } else {
                            builder.append(chars[i]).append(code)
                        }
                    }
                    // 无效颜色代码
                    else -> builder.append(chars[i]).append(code)
                }
                i += 2  // 跳过颜色代码前缀和本体
                continue
            }

            // 普通字符直接添加
            builder.append(chars[i])
            i++
        }
        return builder.toString()
    }

    /** 检查字符是否为颜色代码前缀（§ 或 &） */
    private fun isColorCode(char: Char): Boolean = char == '§' || char == '&'


    fun addTranslation(key: String, locale: Locale, message: String) {
        translations.getOrPut(key) { mutableMapOf() }[locale] = message
    }

    fun removeTranslation(key: String) = translations.remove(key)
    fun clearTranslations() = translations.clear()
}