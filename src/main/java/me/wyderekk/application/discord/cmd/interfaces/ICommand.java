package me.wyderekk.application.discord.cmd.interfaces;

import me.wyderekk.application.discord.cmd.events.SlashCommandEvent;
import me.wyderekk.application.discord.cmd.events.TextCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public interface ICommand {

    default void executeTextCommand(TextCommandEvent event) {

    }

    default void executeSlashCommand(SlashCommandEvent event) {

    }

    default List<OptionData> getCommandOptions() {
        return List.of();
    }

    default String getCommandName() {
        return getClass().getAnnotation(Command.class).name();
    }

    default String getCommandDescription() {
        return getClass().getAnnotation(Command.class).description();
    }

    default Permission[] getCommandPermissions() {
        return getClass().getAnnotation(Command.class).permissions();

    }

    default int getMinArgs() {
        return getClass().getAnnotation(Command.class).minArgs();
    }

    default int getMaxArgs() {
        return getClass().getAnnotation(Command.class).maxArgs();
    }

    default boolean isHidden() {
        return getClass().getAnnotation(Command.class).hidden();
    }
}
