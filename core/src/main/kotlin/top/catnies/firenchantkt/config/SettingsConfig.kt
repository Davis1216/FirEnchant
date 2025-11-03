package top.catnies.firenchantkt.config

class SettingsConfig private constructor():
    AbstractConfigFile("settings.yml")
{

    companion object {
        @JvmStatic
        val instance by lazy { SettingsConfig().apply { loadConfig() } }
    }

    /* 基础配置 */
    var LANGUAGE: String by ConfigProperty("zh_CN") // 语言

    /* 数据库 */
    var DATABASE_TYPE: String by ConfigProperty("SQLite")
    var DATABASE_SQLITE_FILE: String by ConfigProperty("database.db")
    var DATABASE_MYSQL_JDBC_URL: String by ConfigProperty("jdbc:mysql://127.0.0.1:3306/minecraft")
    var DATABASE_MYSQL_JDBC_CLASS: String by ConfigProperty("com.mysql.cj.jdbc.Driver")
    var DATABASE_MYSQL_USER: String by ConfigProperty("root")
    var DATABASE_MYSQL_PASSWORD: String by ConfigProperty("root")


    override fun loadConfig() {
        LANGUAGE = config().getString("language", "zh_CN")!!

        DATABASE_TYPE = config().getString("database.type", "h2")!!
        DATABASE_SQLITE_FILE = config().getString("database.sqlite.file", "database.db")!!
        DATABASE_MYSQL_JDBC_URL = config().getString("database.mysql.jdbc-url", "jdbc:mysql://127.0.0.1:3306/minecraft")!!
        DATABASE_MYSQL_JDBC_CLASS = config().getString("database.mysql.jdbc-class", "com.mysql.cj.jdbc.Driver")!!
        DATABASE_MYSQL_USER = config().getString("database.mysql.properties.user", "root")!!
        DATABASE_MYSQL_PASSWORD = config().getString("database.mysql.properties.password", "root")!!
    }

}
