package me.wyderekk.application.discord.cmd.commands.text;

import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.util.AccountDataUtil;
import me.wyderekk.application.discord.cmd.events.TextCommandEvent;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Command(name = "player", description = "Retrieve lolpros player information.", minArgs = 1, maxArgs = 1)
public class Player implements ICommand {

    @Override
    public void executeTextCommand(TextCommandEvent event) {
        String[] args = event.getArguments();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(125, 60, 255));
        if(getMinArgs() > args.length || getMaxArgs() < args.length) {
            embedBuilder.setTitle("Usage: !player <name>");
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            ArrayList<AccountData> accountData = SQLite.getAccountData(args[0]);
            if(accountData.isEmpty()) {
                embedBuilder.setTitle("No player found with the name: " + args[0]);
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            } else {
                AccountData highestAccount = accountData.getFirst();
                embedBuilder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/13.21.1/img/profileicon/" + highestAccount.avatarId() + ".png");
                embedBuilder.setTitle(accountData.getFirst().owner());
                embedBuilder.addField("Position: ", highestAccount.position().getName(), false);
                embedBuilder.addField("Rank: ", AccountDataUtil.parse(highestAccount.rank().tier(), highestAccount.rank().division(), highestAccount.rank().lp()), false);
                embedBuilder.addField("Peak: ", AccountDataUtil.parse(highestAccount.peak().tier(), highestAccount.peak().division(), highestAccount.peak().lp()), false);
                embedBuilder.addField("Accounts: (" + accountData.size() + ")",
                        accountData.stream()
                                .map(account -> account.summonerNames().getFirst().name())
                                .collect(Collectors.joining(", ")),
                        false);
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
