package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        super(new String[]{"purge", "clear", "purgemessages"}, "Purges a number of messages from a channel.", "<number> [bot|user|webhook]", CommandAccessType.SERVER_MOD, true);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Message message = event.getMessage();

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        if (args.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("You need to provide a number of messages to purge")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        int toRemove;
        try {
            toRemove = Integer.parseInt(args.get(0)) + 1;
        } catch (NumberFormatException ex) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("`" + args.get(0) + "` is not a valid number of messages.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (toRemove > 100 || toRemove < 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Sorry")
                    .setDescription("I can only purge up to 99 messages at a time.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (args.size() == 1 || (!args.get(1).equalsIgnoreCase("bot") &&
                !args.get(1).equalsIgnoreCase("user") &&
                !args.get(1).equalsIgnoreCase("webhook"))) {
            channel.getHistory().retrievePast(toRemove).queue(msgs -> msgs.forEach(msg -> msg.delete().queue()));
            EmbedBuilder eb = new EmbedBuilder();
            eb
                    .setTitle("Success")
                    .setDescription(toRemove + " messages have been deleted from this channel.")
                    .setColor(Constants.VESTRIMU_PURPLE)
                    .setFooter("Vestrimu", Constants.ICON_URL);
            channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        } else {
            if (args.get(1).equalsIgnoreCase("bot")) {
                channel.getHistory().retrievePast(100).queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> m.getAuthor().isBot()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " bot messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if (args.get(1).equalsIgnoreCase("user")) {
                channel.getHistory().retrievePast(100).queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> !m.getAuthor().isBot()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " user messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if (args.get(1).equalsIgnoreCase("webhook")) {
                channel.getHistory().retrievePast(100).queue(msgs -> {
                    int i = toRemove;
                    for (Object m : msgs.stream().filter(m -> m.isWebhookMessage()).toArray()) {
                        if (i != 0) {
                            ((Message) m).delete().queue();
                            i--;
                        }
                    }
                });
                EmbedBuilder eb = new EmbedBuilder();
                eb
                        .setTitle("Success")
                        .setDescription(toRemove + " webhook messages have been deleted from this channel.")
                        .setColor(Constants.VESTRIMU_PURPLE)
                        .setFooter("Vestrimu", Constants.ICON_URL);
                channel.sendMessage(eb.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
        }
    }

}
