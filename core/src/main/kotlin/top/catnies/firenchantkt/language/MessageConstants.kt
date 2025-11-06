package top.catnies.firenchantkt.language

// 消息常量
object MessageConstants {
    const val DATABASE_CONNECT_ERROR = "database.connect.error"
    const val DATABASE_TABLE_CREATE_ERROR = "database.table.create.error"

    const val COMMAND_RELOAD_SUCCESS = "command.reload.success"
    const val COMMAND_VERSION_SUCCESS = "command.version.success"
    const val COMMAND_CONSOLE_CANT_EXECUTE = "command.console.cant_execute"
    const val COMMAND_DEBUG_GET_LOCATION_BOOK_SHELF_COUNT_EXECUTE = "command.debug.get_location_book_shelf_count.execute"
    const val COMMAND_DEBUG_GET_PLAYER_ENCHANTMENT_SEED_EXECUTE = "command.debug.get_player_enchantment_seed.execute"
    const val COMMAND_BROKEN_GEAR_BREAK_MAIN_HAND_SUCCESS = "command.broken_gear.break_main_hand.success"
    const val COMMAND_BROKEN_GEAR_BREAK_MAIN_HAND_FAIL = "command.broken_gear.break_main_hand.fail"
    const val COMMAND_BROKEN_GEAR_FIX_MAIN_HAND_SUCCESS = "command.broken_gear.fix_main_hand.success"
    const val COMMAND_BROKEN_GEAR_FIX_MAIN_HAND_FAIL = "command.broken_gear.fix_main_hand.fail"
    const val COMMAND_GIVE_BOOK_ENCHANTMENT_NOT_FOUND = "command.give_book.enchantment.not_found"
    const val COMMAND_GIVE_BOOK_ENCHANTMENT_SUCCESS_RECEIVE = "command.give_book.enchantment.success.receive"
    const val COMMAND_GIVE_BOOK_ENCHANTMENT_SUCCESS_EXECUTE = "command.give_book.enchantment.success.execute"

    const val RESOURCE_ENCHANTMENT_FILE_ERROR = "resource.enchantment.file.error"
    const val RESOURCE_ENCHANTMENT_FILE_PROVIDER_NOT_FOUND = "resource.enchantment.file.provider.not_found"
    const val RESOURCE_ENCHANTMENT_FILE_ITEM_NOT_FOUND = "resource.enchantment.file.item.not_found"
    const val RESOURCE_ORIGINAL_BOOK_MISSING_KEY = "resource.original_book.missing.key"
    const val RESOURCE_ORIGINAL_BOOK_INVALID_ENCHANTMENT = "resource.original_book.invalid.enchantment"
    const val RESOURCE_HOOK_ITEM_PROVIDER_NOT_FOUND = "resource.hook.item_provider.not_found"
    const val RESOURCE_HOOK_ITEM_NOT_FOUND = "resource.hook.item.not_found"
    const val RESOURCE_MENU_STRUCTURE_ERROR = "resource.menu.structure.error"
    const val RESOURCE_VALUE_INVALID_ERROR = "resource.value.invalid.error"
    const val RESOURCE_VALUE_NOT_FOUND = "resource.value.not_found"

    const val PLUGIN_COMPATIBILITY_HOOK_SUCCESS = "plugin.compatibility.hook.success"
    const val PLUGIN_FUNCTION_NOT_ENABLED = "plugin.function.not_enabled"

    const val CONFIG_ACTION_TYPE_UNKNOWN = "config.action.type.unknown"
    const val CONFIG_CONDITION_TYPE_UNKNOWN = "config.condition.type.unknown"
    const val CONFIG_ACTION_INVALID_ARGS = "config.action.invalid.args"
    const val CONFIG_CONDITION_INVALID_ARGS = "config.condition.invalid.args"
    const val CONFIG_ACTION_RUNTIME_ARGS_CAST_FAIL = "config.action.runtime.args.cast_fail"
    const val CONFIG_CONDITION_RUNTIME_ARGS_CAST_FAIL = "config.condition.runtime.args.cast_fail"

    const val ANVIL_ENCHANTED_BOOK_USE_FAIL = "anvil.enchanted.book.use.fail"
    const val ANVIL_ENCHANTED_BOOK_USE_FAIL_BREAK = "anvil.enchanted.book.use.fail.break"
    const val ANVIL_ENCHANTED_BOOK_USE_PROTECT_FAIL = "anvil.enchanted.book.use.protect.fail"

    const val REPAIR_TABLE_REPAIR_ITEM_RECEIVE_SUCCESS = "repair_table.repair_item.receive.success"
    const val REPAIR_TABLE_REPAIR_ITEM_CANCEL_SUCCESS = "repair_table.repair_item.cancel.success"
    const val REPAIR_TABLE_REPAIR_ITEM_QUEUE_FULL = "repair_table.repair_item.queue.full"
}
