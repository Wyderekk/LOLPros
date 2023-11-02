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

    /**
     * Permissions which commands require to use
     */
    Permission[] permissions() default {};

    /**
    Min arguments which text commands can have
     */
    int minArgs() default 0;

    /**
    Max arguments which text commands can have
    */
    int maxArgs() default 0;

    /**
    Should command be hidden from the help command
    */
    boolean hidden() default false;

}