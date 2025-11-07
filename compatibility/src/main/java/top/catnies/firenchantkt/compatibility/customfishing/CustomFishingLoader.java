package top.catnies.firenchantkt.compatibility.customfishing;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import top.catnies.firenchantkt.integration.HookPluginLoader;

public class CustomFishingLoader implements HookPluginLoader {

    private final JavaPlugin plugin;

    public CustomFishingLoader(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void load() {
        BukkitCustomFishingPlugin.getInstance().getIntegrationManager().registerItemProvider(new CustomFishingProvider());
        BukkitCustomFishingPlugin.getInstance().reload();
    }

}
