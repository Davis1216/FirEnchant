package top.catnies.firenchantkt.compatibility.customcrops;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import top.catnies.firenchantkt.integration.HookPluginLoader;

public class CustomCropsLoader implements HookPluginLoader {

    private final JavaPlugin plugin;

    public CustomCropsLoader(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void load() {
        BukkitCustomCropsPlugin.getInstance().getIntegrationManager().registerItemProvider(new CustomCropsProvider());
        BukkitCustomCropsPlugin.getInstance().reload();
    }

}
