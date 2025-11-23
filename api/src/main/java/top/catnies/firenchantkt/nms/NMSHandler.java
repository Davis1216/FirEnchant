package top.catnies.firenchantkt.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NMSHandler {

    /**
     * 获取玩家的附魔种子
     * @param player 玩家
     * @return 种子
     */
    int getPlayerEnchantmentSeed(Player player);

    /**
     * 获取附魔台书架数量
     *
     * @param location 附魔台位置实例
     * @return 书架数量
     */
    int getEnchantmentTableBookShelf(Location location);

    /**
     * 获取注册表中附魔台附魔列表
     *
     * @return 附魔列表
     */
    Set<Enchantment> getEnchantmentTableEnchantmentList(World world, EnchantmentRegistryType registryType);

    /**
     * 获取玩家下次使用指定物品附魔的结果列表
     * 此方法每个选项卡只返回1个魔咒.
     * @param player 玩家实例
     * @param bookShelfCount 书架数量
     * @param itemStack 物品实例
     * @return 附魔结果列表
     */
    List<Map<Enchantment, Integer>> getPlayerNextEnchantmentTableResultByItemStack(Player player, int bookShelfCount, ItemStack itemStack, EnchantmentRegistryType registryType);

    /**
     * 获取玩家下次使用指定物品附魔的结果列表
     * 此方法每个选项卡只返回1个魔咒.
     * @param player 玩家实例
     * @param bookShelfCount 书架数量
     * @param enchantable 物品的附魔能力
     * @param enchantmentList 抽取的魔咒列表
     * @return 附魔结果列表
     */
    List<Map<Enchantment, Integer>> getPlayerNextEnchantmentTableResultByEnchantmentList(Player player, int bookShelfCount, int enchantable, Set<Enchantment> enchantmentList);

    /**
     * 获取指定玩家下次附魔指定物品的附魔结果列表
     * 附魔结果只会在传入的列表中选择, 并且只会选择适配物品的魔咒.
     * @param player 玩家实例
     * @param bookShelfCount 书架数量
     * @param itemStack 物品实例
     * @param enchantmentList 附魔列表
     * @return 附魔结果列表
     */
    List<Map<Enchantment, Integer>> getPlayerNextEnchantmentTableResult(Player player, int bookShelfCount, ItemStack itemStack, Set<Enchantment> enchantmentList);
}
