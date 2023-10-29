package me.wyderekk.application.discord.cmd.interfaces;

import me.wyderekk.application.discord.cmd.CommandEvent;

public interface ICommand {

    void executeCommand(CommandEvent event);

    default String getCommandName() {
        return getClass().getAnnotation(Command.class).name();
    }

    default String getCommandDescription() {
        return getClass().getAnnotation(Command.class).description();
    }

    default int getMinArgs() {
        return getClass().getAnnotation(Command.class).minArgs();
    }

    default int getMaxArgs() {
        return getClass().getAnnotation(Command.class).maxArgs();
    }
}
