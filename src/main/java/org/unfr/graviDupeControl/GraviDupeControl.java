package org.unfr.graviDupeControl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public final class GraviDupeControl extends JavaPlugin {
    private final @NotNull Set<Material> allowed = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();
        getServer().getPluginManager().registerEvents(
                new PortalListener(this.allowed),
                this);
        if (getConfig().getBoolean("sendStartupMessage")) {
            getLogger().info("GraviDupeControl enabled. Allowed blocks: " + this.allowed);
        }
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(getReloadCommand("gdcr"), "Reload GraviDupeControl config");
        });
    }

    private boolean reload() {
        List<String> allowedList = getConfig().getStringList("allowed-dupe-blocks");
        boolean failed = false;
        allowed.clear();
        for (String materialName : allowedList) {
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material != null) {
                allowed.add(material);
            } else {
                failed = true;
                getLogger().warning("Material: \"" + materialName + "\" is not found");
            }
        }
        return !failed;
    }

    public LiteralCommandNode<CommandSourceStack> getReloadCommand(final String commandName) {
        return Commands.literal(commandName)
                .requires(source -> source.getSender().hasPermission("gravidupecontrol.reload"))
                .executes(context -> {
                    this.reloadConfig();
                    if (this.reload()) {
                        context.getSource().getSender().sendMessage("§aGraviDupeControl config reloaded successfully!");
                    } else {
                        context.getSource().getSender().sendMessage(
                                "§4Failed to reload GraviDupeControl config, see console logs for more information!");
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
