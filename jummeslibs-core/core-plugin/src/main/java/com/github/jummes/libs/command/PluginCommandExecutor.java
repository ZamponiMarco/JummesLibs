package com.github.jummes.libs.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PluginCommandExecutor implements CommandExecutor, TabCompleter, PluginCommand {

    private final Map<String, PluginCommand> commandMap = new HashMap<>();
    private final PluginCommand defaultCommand;

    public PluginCommandExecutor(String defaultCommandName, PluginCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
        commandMap.put(defaultCommandName, defaultCommand);
    }

    public void registerCommand(String name, PluginCommand commandClass) {
        commandMap.put(name, commandClass);
    }

    public PluginCommand getCommand(String name) {
        return commandMap.getOrDefault(name, defaultCommand);
    }

    public Set<String> getRegisteredNames() {
        return commandMap.keySet();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        boolean isSenderPlayer = sender instanceof Player;
        String subCommand = args.length >= 1 ? args[0] : "";
        String[] arguments = args.length >= 2 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        try {
            PluginCommand cmd = getCommand(subCommand);
            if (cmd instanceof CommandExecutor) {
                return ((CommandExecutor) cmd).onCommand(sender, command, label, arguments);
            } else if (cmd instanceof AbstractCommand) {
                ((AbstractCommand) cmd).checkExecution(sender, arguments, isSenderPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], getRegisteredNames(), completions);
        } else {
            String subCommand = args.length >= 1 ? args[0] : "";
            String[] arguments = args.length >= 2 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
            try {
                PluginCommand cmd = getCommand(subCommand);
                if (cmd != null) {
                    return cmd.onTabComplete(sender, command, alias, arguments);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(completions);
        return completions;
    }

}