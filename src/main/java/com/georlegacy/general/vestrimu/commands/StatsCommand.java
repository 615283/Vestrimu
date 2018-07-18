package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatsCommand extends Command {

    public StatsCommand() {
        super(new String[]{"stats", "statistics"}, "Shows stats of the bot.", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("**Stats**")
                .addField("**Guilds**", String.valueOf(event.getJDA().getGuilds().size()), true)
                .addField("**Uptime**", (System.currentTimeMillis() - Vestrimu.getInstance().getStartupTime()) / 1000 / 60 / 60 + " hours", true)
                .addField("**Commands**", String.valueOf(Vestrimu.getInstance().getCommandManager().getCommands().size()), true);

        channel.sendMessage(eb.build()).queue();
    }

}
