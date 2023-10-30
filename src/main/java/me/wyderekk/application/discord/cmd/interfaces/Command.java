package me.wyderekk.application.discord.cmd.interfaces;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String name();
    String description();
    Permission[] permissions() default {};
    int minArgs() default 0;
    int maxArgs() default 0;

}