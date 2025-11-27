package top.catnies.firenchantkt.database.dao;

import top.catnies.firenchantkt.database.entity.ItemRepairTable;

import java.util.List;
import java.util.UUID;

/**
 * 道具修复数据管理器接口
 */
public interface ItemRepairData {
    
    /**
     * 创建新的修复记录
     */
    void insert(ItemRepairTable repairData);

    /**
     * 删除某个记录
     */
    void remove(ItemRepairTable repairData);

    /**
     * 更新一个修复记录的值
     */
    void update(ItemRepairTable repairData, boolean async);

    /**
     * 标记记录为已领取
     */
    void markAsReceived(int id);

    /**
     * 获取所有玩家的修复记录
     */
    List<ItemRepairTable> getAllList();

    /**
     * 获取所有玩家正在修复中的记录
     */
    List<ItemRepairTable> getAllActiveList();

    /**
     * 获取所有玩家已修复完成但未领取的记录
     */
    List<ItemRepairTable> getAllCompletedList();

    /**
     * 获取所有玩家已修复并且已领取的记录
     */
    List<ItemRepairTable> getAllReceivedList();

    /**
     * 获取所有玩家正在修复中+已修复完成的记录
     */
    List<ItemRepairTable> getAllActiveAndCompletedList();

    /**
     * 获取玩家所有的修复记录
     */
    List<ItemRepairTable> getByPlayer(UUID playerId);
    
    /**
     * 获取玩家正在修复中的记录
     */
    List<ItemRepairTable> getByPlayerActive(UUID playerId);

    /**
     * 获取玩家已修复完成但还未领取的记录
     */
    List<ItemRepairTable> getByPlayerCompleted(UUID playerId);

    /**
     * 获取玩家已修复并且已经领取的记录
     */
    List<ItemRepairTable> getByPlayerReceived(UUID playerId);

    /**
     * 获取玩家正在修复中+已修复完成的记录
     */
    List<ItemRepairTable> getByPlayerActiveAndCompletedList(UUID playerId);

}