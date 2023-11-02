package me.wyderekk.application.discord.cmd;

import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final List<ICommand> textCommands = new CopyOnWriteArrayList<>();
    private final List<ICommand> slashCommands = new CopyOnWriteArrayList<>();

    private CommandManager() {
        LOGGER.info("Initializing commands...");
        initializeCommands("me.wyderekk.application.discord.cmd.commands.text", textCommands);
        initializeCommands("me.wyderekk.application.discord.cmd.commands.slash", slashCommands);
    }

    private static class Loader {
        static final CommandManager INSTANCE = new CommandManager();
    }

    public static CommandManager getInstance() {
        return Loader.INSTANCE;
    }

    private void initializeCommands(String packagePath, List<ICommand> commandList) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<? extends ICommand>> commandClasses = reflections.getSubTypesOf(ICommand.class);
        loadCommands(commandClasses, commandList);
    }

    private void loadCommands(Set<Class<? extends ICommand>> classes, List<ICommand> commandList) {
        for (Class<? extends ICommand> clazz : classes) {
            Command commandAnnotation = clazz.getAnnotation(Command.class);
            if (commandAnnotation == null) {
                LOGGER.error("Command {} does not have a Command Annotation", clazz.getSimpleName());
            } else {
                try {
                    ICommand commandInstance = clazz.getDeclaredConstructor().newInstance();
                    commandList.add(commandInstance);
                    LOGGER.info("Successfully loaded Command {}", clazz.getSimpleName());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    LOGGER.error("Failed to instantiate command {}", clazz.getSimpleName(), e);
                }
            }
        }
    }

    public List<ICommand> getTextCommands() {
        return textCommands;
    }

    public List<ICommand> getSlashCommands() {
        return slashCommands;
    }

}