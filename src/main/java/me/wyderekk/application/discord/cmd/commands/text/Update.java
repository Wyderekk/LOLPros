package me.wyderekk.application.discord.cmd.commands.text;

import me.wyderekk.application.discord.cmd.events.TextCommandEvent;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import me.wyderekk.application.task.tasks.UpdateLOLProsData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import java.awt.*;
import java.util.concurrent.TimeUnit;

@Command(name = "update", description = "Update the bot.", permissions = Permission.ADMINISTRATOR, hidden = true)
public class Update implements ICommand {

    @Override
    public void executeTextCommand(TextCommandEvent event) {
        UpdateLOLProsData updateSummonersTask = new UpdateLOLProsData();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(125, 60, 255));
        embedBuilder.setTitle("Updating database...");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            message.delete().queueAfter(5, TimeUnit.SECONDS);
        });
        updateSummonersTask.run();
    }
}
