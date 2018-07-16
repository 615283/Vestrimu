package com.georlegacy.general.vestrimu.listeners;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BotMentionListener extends ListenerAdapter {

    @Inject private SQLManager sqlManager;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (message.getAuthor().isBot())
            return;

        if (channel.getType().equals(ChannelType.PRIVATE))
            return;

        if (message.getAuthor().getId().equals(Constants.VESTRIMU_ID))
            return;

        GuildConfiguration configuration = sqlManager.readGuild(event.getGuild().getId());

        if (message.getContentRaw().equals(event.getGuild().getMemberById(Constants.VESTRIMU_ID).getAsMention())) {

            if (configuration.isRequireaccessforhelp()) {
                boolean hasAccess = false;
                for (Role role : event.getMember().getRoles()) {
                    if (role.getId().equals(configuration.getBotaccessroleid())) {
                        hasAccess = true;
                        break;
                    }
                }
                if (!hasAccess)
                    return;
            }

            List<MessageEmbed> helps = new ArrayList<>();

            // Prefix
            helps.add(
                    new EmbedBuilder()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Prefix")
                            .setDescription("All commands are started with a prefix, this server's prefix is currently `" + configuration.getPrefix() + "`.")
                            .build()
            );

            // Access role
            helps.add(
                    new EmbedBuilder()
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Access Role")
                            .setDescription("The current role is `@" + event.getGuild().getRoleById(configuration.getBotaccessroleid()).getName() + "`.\n" +
                                    "Assign this role to anyone you wish to have access to the bot's commands. It is configurable whether the bot will respond at all to a user without the role.")
                            .build()
            );

            // Commands
            EmbedBuilder commands = new EmbedBuilder();
            for (Command command : Vestrimu.getInstance().getCommandManager().getCommands()) {
                if (command.isAdminOnly() && !(Constants.ADMIN_IDS.contains(message.getAuthor().getId())))
                    return;
                commands.addField(
                        configuration.getPrefix() +
                                String.join("|", command.getNames()) +
                                (command.isAdminOnly() ? " **[BOT ADMIN ONLY]**" : ""),
                        command.getDescription() +
                                "\n`" +
                                configuration.getPrefix() +
                                String.join("|", command.getNames()) +
                                " " +
                                command.getHelp() + "`",
                        false
                );
            }
            helps.add(
                    commands
                            .setColor(Constants.VESTRIMU_PURPLE)
                            .setTitle("Commands")
                            .setDescription("Below are all of the bot's commands.")
                            .build()
            );

            helps.forEach(msg -> message.getAuthor().openPrivateChannel().queue(dm -> dm.sendMessage(msg).queue()));

        }
    }

}
