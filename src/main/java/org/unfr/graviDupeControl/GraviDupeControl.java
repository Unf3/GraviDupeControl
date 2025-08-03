package org.unfr.graviDupeControl;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;


import java.util.*;

public final class GraviDupeControl extends JavaPlugin {
    private @Nullable Set<Material> allowed;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadAllowed();
        getServer().getPluginManager().registerEvents(
                new PortalListener(this.allowed),
                this
        );
        if (getConfig().getBoolean("sendStartupMessage")) {
            getLogger().info("GraviDupeControl enabled. Allowed blocks: " + this.allowed);
        }
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(reloadCommand("gdcr"), "Reload GraviDupeControl config");
        });
    }

    private void reloadAllowed() {
        List<String> allowedList = getConfig().getStringList("allowed-dupe-blocks");
        if (allowedList.isEmpty()) {
            allowed = Set.of();
        } else {
            Set<Material> set = new HashSet<>();
            for (String name : allowedList) {
                try {
                    set.add(Material.valueOf(name.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Material: \"" + name + "\" is not found");
                }
            }
            allowed = Collections.unmodifiableSet(set);
        }
    }

    public static LiteralCommandNode<CommandSourceStack> reloadCommand(final String commandName) {
        return Commands.literal(commandName)
                .requires(source -> source.getSender().hasPermission("gravidupecontrol.reload"))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    GraviDupeControl plugin = JavaPlugin.getPlugin(GraviDupeControl.class);

                    plugin.reloadConfig();
                    plugin.reloadAllowed();

                    sender.sendMessage("§aGraviDupeControl config reloaded successfully!");
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
