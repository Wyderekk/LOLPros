package me.wyderekk.application.discord.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
    }
}
