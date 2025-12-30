package top.catnies.firenchantkt.database.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.saicone.rtag.item.ItemTagStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import top.catnies.firenchantkt.api.FirEnchantAPI;

import java.util.UUID;

/**
 * 道具修复记录表
 * 用于记录玩家正在修复的道具信息
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "firenchant_item_repair")
public class ItemRepairTable {

    public ItemRepairTable(UUID playerId, byte[] itemData, long duration) {
        this.playerId = playerId;
        this.itemData = itemData;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    @DatabaseField(generatedId = true)
    private int id;

    @Setter
    @DatabaseField(canBeNull = false, index = true, dataType = DataType.UUID)
    private UUID playerId;

    @Setter
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] itemData; // 序列化的破损物品数据

    @Setter
    @DatabaseField(canBeNull = false, dataType = DataType.LONG)
    private long startTime;

    @Setter
    @DatabaseField(canBeNull = false, dataType = DataType.LONG)
    private long duration; // 修复所需时间（毫秒）

    @Setter
    @DatabaseField(canBeNull = false, defaultValue = "false", dataType = DataType.BOOLEAN)
    private boolean received = false;

    /**
     * 检查修复是否已经完成
     * 
     * @return 如果当前时间已超过修复完成时间则返回true
     */
    public boolean isCompleted() {
        return System.currentTimeMillis() >= (startTime + duration);
    }

    /**
     * 获取剩余时间（毫秒）
     * 
     * @return 剩余修复时间，如果已完成则返回0
     */
    public long getRemainingTime() {
        long remaining = (startTime + duration) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * 获取破损状态的物品
     */
    public ItemStack getBrokenItem() {
        return ItemTagStream.INSTANCE.fromBytes(itemData);
    }

    /**
     * 获取修复完成的物品
     */
    public ItemStack getRepairedItem() {
        return FirEnchantAPI.INSTANCE.repairBrokenGear(getBrokenItem());
    }
}
