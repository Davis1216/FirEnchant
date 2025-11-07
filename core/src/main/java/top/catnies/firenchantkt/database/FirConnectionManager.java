package top.catnies.firenchantkt.database;

import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import top.catnies.firenchantkt.api.ServiceContainer;
import top.catnies.firenchantkt.config.SettingsConfig;
import top.catnies.firenchantkt.database.connection.MysqlConnection;
import top.catnies.firenchantkt.database.connection.SQLiteConnection;
import top.catnies.firenchantkt.database.dao.EnchantLogData;
import top.catnies.firenchantkt.database.dao.EnchantingHistoryData;
import top.catnies.firenchantkt.database.dao.ItemRepairData;
import top.catnies.firenchantkt.database.dao.EnchantLogDao;
import top.catnies.firenchantkt.database.dao.EnchantingHistoryDao;
import top.catnies.firenchantkt.database.dao.ItemRepairDao;

@Getter
@SuppressWarnings("unused")
public class FirConnectionManager implements ConnectionManager {

    // 单例
    private static FirConnectionManager instance;
    private FirConnectionManager() {}
    
    public static FirConnectionManager getInstance() {
        if (instance == null) {
            instance = new FirConnectionManager();
            instance.initDatabase(); // 创建链接
            ServiceContainer.INSTANCE.register(ConnectionManager.class, instance);
        }
        return instance;
    }

    private Connection connection;
    private EnchantingHistoryData enchantingHistoryData;
    private EnchantLogData enchantLogData;
    private ItemRepairData itemRepairData;

    @Override
    public void initDatabase() {
        SettingsConfig config = SettingsConfig.getInstance();
        String type = config.getDATABASE_TYPE().toLowerCase();
        
        switch (type) {
            // 初始化MySQL数据库的连接
            case "mysql" -> {
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(config.getDATABASE_MYSQL_JDBC_URL());
                hikariConfig.setDriverClassName(config.getDATABASE_MYSQL_JDBC_CLASS());
                hikariConfig.setUsername(config.getDATABASE_MYSQL_USER());
                hikariConfig.setPassword(config.getDATABASE_MYSQL_PASSWORD());
                connection = new MysqlConnection(hikariConfig);
                connection.connect();
                enchantingHistoryData = EnchantingHistoryDao.getInstance();
                enchantLogData = EnchantLogDao.getInstance();
                itemRepairData = ItemRepairDao.getInstance();
            }
            // 初始化Sqlite数据库的连接
            case "sqlite" -> {
                String fileName = config.getDATABASE_SQLITE_FILE();
                String url = "jdbc:sqlite:plugins/FirEnchantKt/" + fileName;
                connection = new SQLiteConnection(url);
                connection.connect();
                enchantingHistoryData = EnchantingHistoryDao.getInstance();
                enchantLogData = EnchantLogDao.getInstance();
                itemRepairData = ItemRepairDao.getInstance();
            }
            default -> throw new RuntimeException("不支持的数据库类型: " + type);
        }
    }



    @Override
    @SneakyThrows
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return connection != null ? connection.getConnectionSource() : null;
    }
}
