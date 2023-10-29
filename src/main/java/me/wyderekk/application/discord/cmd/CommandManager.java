package me.wyderekk.application.discord.cmd;

import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Set;

public class CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final ArrayList<ICommand> commands = new ArrayList<>();

    public static CommandManager INSTANCE = new CommandManager();


    private CommandManager() {
        LOGGER.info("Initializing commands...");
        Reflections reflections = new Reflections("me.wyderekk.application.discord.cmd.commands");
        Set<Class<? extends ICommand>> classes = reflections.getSubTypesOf(ICommand.class);
        try {
            loadCommands(classes);
        } catch (Exception e) {
            LOGGER.error("Failed to load commands", e);
        }
    }

    private void loadCommands(Set<Class<? extends ICommand>> classes) throws Exception {
        for (Class<? extends ICommand> aClass : classes) {
            Command commandAnnotation = aClass.getAnnotation(Command.class);
            if(commandAnnotation == null) {
                LOGGER.error("Command {} does not have a Command Annotation", aClass.getSimpleName());
            } else {
                LOGGER.info("Loading Command {}", aClass.getSimpleName());
                addCommand(aClass.getDeclaredConstructor().newInstance());
            }
        }
    }

    private void addCommand(ICommand command) {
        commands.add(command);
    }

    public ArrayList<ICommand> getCommands() {
        return commands;
    }
}
