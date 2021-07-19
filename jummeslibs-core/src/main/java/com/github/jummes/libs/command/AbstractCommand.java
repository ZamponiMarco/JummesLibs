package com.github.jummes.libs.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.github.jummes.libs.util.MessageUtils;

public abstract class AbstractCommand {

    private static final Component NO_PERMISSION = MessageUtils.color("&cYou don't have the permission");
    private static final Component ONLY_PLAYER = MessageUtils.color("&cThis command can be used only by a player");

    protected CommandSender sender;
    protected String subCommand;
    protected String[] arguments;
    protected boolean isSenderPlayer;

    /**
     * Constructs a command
     *
     * @param sender         sender of the command
     * @param subCommand     subCommand
     * @param arguments      arguments given to subcommand
     * @param isSenderPlayer whether the sender is a player
     */
    public AbstractCommand(CommandSender sender, String subCommand, String[] arguments, boolean isSenderPlayer) {
        this.sender = sender;
        this.subCommand = subCommand;
        this.arguments = arguments;
        this.isSenderPlayer = isSenderPlayer;
    }

    /**
     * Checks if the sender can execute the command
     *
     * @return true if he can
     */
    protected boolean canSenderTypeExecute() {
        return !isOnlyPlayer() || isSenderPlayer;
    }

    /**
     * Checks if sender has the permission to execute the command
     *
     * @return true if he has
     */
    protected boolean hasPermission() {
        return getPermission() == null || (getPermission() != null && sender.hasPermission(getPermission()));
    }

    /**
     * Checks if the command can be executed, if it can proceeds to execute it
     */
    public void checkExecution() {
        Component errorMessage = Component.text("");
        errorMessage = !hasPermission() ? NO_PERMISSION : errorMessage;
        errorMessage = !canSenderTypeExecute() ? ONLY_PLAYER : errorMessage;
        if (canSenderTypeExecute() && hasPermission()) {
            execute();
        } else {
            sender.sendMessage(errorMessage);
        }
    }

    /**
     * Executes the command
     */
    protected abstract void execute();

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

}