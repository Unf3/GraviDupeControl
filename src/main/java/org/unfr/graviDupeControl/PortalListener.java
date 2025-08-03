package org.unfr.graviDupeControl;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

import java.util.Set;

public final class PortalListener implements Listener {
    private final Set<Material> allowed;

    public PortalListener(Set<Material> allowed) {
        this.allowed = allowed;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityPortal(EntityPortalEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof FallingBlock fb)) return;

        Material mat = fb.getBlockData().getMaterial();
        if (!allowed.contains(mat)) {
            e.setCancelled(true);
        }
    }
}
