package com.github.jummes.libs.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginCommandExecutor implements CommandExecutor {

	private Map<String, Class<? extends AbstractCommand>> commandMap;

	public PluginCommandExecutor() {
		commandMap = new HashMap<String, Class<? extends AbstractCommand>>();
	}

	public void registerCommand(String name, Class<? extends AbstractCommand> commandClass) {
		commandMap.put(name, commandClass);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean isSenderPlayer = sender instanceof Player;
		String[] arguments = args.length >= 1 ? Arrays.copyOfRange(args, 0, args.length) : new String[0];
		try {
			commandMap.get(command.getName()).getConstructor(CommandSender.class, String[].class, boolean.class)
					.newInstance(sender, arguments, isSenderPlayer).checkExecution();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}