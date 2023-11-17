package me.wyderekk.application.discord;

import me.wyderekk.application.discord.cmd.CommandManager;
import me.wyderekk.application.discord.listeners.MessageListener;
import me.wyderekk.application.discord.listeners.ReadyListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Core {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    public static void runBot() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(System.getenv("BOT_TOKEN"));
        builder.setActivity(Activity.streaming("LOLPros", "https://www.twitch.tv/wyderekk"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.MESSAGE_CONTENT
        );
        try {
            ShardManager shardManager = builder.build();
            shardManager.addEventListener(new ReadyListener(), new MessageListener());
        } catch (Exception e) {
            LOGGER.error("Failed to start the bot", e);
        }
    }
}
