package top.catnies.firenchantkt.compatibility.auraskill;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.catnies.firenchantkt.api.event.enchantingtable.EnchantItemEvent;

public class AuraSkillListener implements Listener {

    @EventHandler
    public void onEnchantingTableEnchant(EnchantItemEvent event) {
        AuraSkillsApi auraSkills = AuraSkillsApi.get();
        SkillsUser user = auraSkills.getUser(event.getPlayer().getUniqueId());
    }
}
