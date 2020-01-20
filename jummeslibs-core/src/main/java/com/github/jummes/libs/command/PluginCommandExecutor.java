package com.github.jummes.libs.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginCommandExecutor implements CommandExecutor {

	private Class<? extends AbstractCommand> defaultCommand;
	private Map<String, Class<? extends AbstractCommand>> commandMap;

	public PluginCommandExecutor(Class<? extends AbstractCommand> defaultCommand) {
		this.defaultCommand = defaultCommand;
		commandMap = new HashMap<String, Class<? extends AbstractCommand>>();
	}

	public void registerCommand(String name, Class<? extends AbstractCommand> commandClass) {
		commandMap.put(name, commandClass);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean isSenderPlayer = sender instanceof Player;
		String subCommand = args.length >= 1 ? args[0] : "";
		String[] arguments = args.length >= 2 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
		try {
			commandMap.getOrDefault(subCommand, defaultCommand)
					.getConstructor(CommandSender.class, String[].class, boolean.class)
					.newInstance(sender, arguments, isSenderPlayer).checkExecution();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}