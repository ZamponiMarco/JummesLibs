package com.github.jummes.libs.command;
import com.github.jummes.libs.util.MessageUtils;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCommand implements PluginCommand {
    private static final Component NO_PERMISSION = MessageUtils.color("&cYou don't have the permission");
    private static final Component ONLY_PLAYER = MessageUtils.color("&cThis command can be used only by a player");

    public AbstractCommand() {
    }

    /**
     * Checks if the sender can execute the command
     *
     * @return true if he can
     */
    protected boolean canSenderTypeExecute(boolean isSenderPlayer) {
        return !isOnlyPlayer() || isSenderPlayer;
    }

    /**
     * Checks if sender has the permission to execute the command
     *
     * @return true if he has
     */
    protected boolean hasPermission(CommandSender sender) {
        return getPermission() == null || (getPermission() != null && sender.hasPermission(getPermission()));
    }

    /**
     * Checks if the command can be executed, if it can proceeds to execute it
     */
    public void checkExecution(CommandSender sender, String[] arguments, boolean isSenderPlayer) {
        Component errorMessage = Component.text("");
        errorMessage = !hasPermission(sender) ? NO_PERMISSION : errorMessage;
        errorMessage = !canSenderTypeExecute(isSenderPlayer) ? ONLY_PLAYER : errorMessage;
        if (canSenderTypeExecute(isSenderPlayer) && hasPermission(sender)) {
            execute(arguments, sender);
        } else {
            sender.sendMessage(errorMessage);
        }
    }

    /**
     * Executes the command
     */
    protected abstract void execute(String[] arguments, CommandSender sender);

    /**
     * Whether the command can only be executed by players or not
     *
     * @return true if it can only be executed by players
     */
    protected abstract boolean isOnlyPlayer();

    /**
     * Returns the permission needed to run this command
     *
     * @return the permission of this command
     */
    protected abstract Permission getPermission();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return Lists.newArrayList();
    }

}