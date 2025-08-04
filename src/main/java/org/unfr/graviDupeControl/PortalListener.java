package org.unfr.graviDupeControl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public final class PortalListener implements Listener {
    private final Set<Material> allowed;

    public PortalListener(Set<Material> allowed) {
        this.allowed = allowed;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock
                && !allowed.contains(fallingBlock.getBlockData().getMaterial())) {
            event.setCancelled(true);
        }
    }
}
