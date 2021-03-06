package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.text.DecimalFormat;

public class StatsCommand extends Command {

    public StatsCommand() {
        super(new String[]{"stats", "statistics"}, "Shows stats of the bot.", "", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setColor(Constants.VESTRIMU_PURPLE)
                .setTitle("Stats")
                .addField("Guilds", String.valueOf(event.getJDA().getGuilds().size()), true)
                .addField("Text Channels", String.valueOf(Vestrimu.getInstance().getShardManager().getTextChannels().size()), true)
                .addField("Voice Channels", String.valueOf(Vestrimu.getInstance().getShardManager().getVoiceChannels().size()), true)
                .addField("Uptime", new DecimalFormat("#.####").format((double) (System.currentTimeMillis() - Vestrimu.getInstance().getStartupTime()) / 1000 / 60 / 60 / 24) + " days", true)
                .setFooter("Vestrimu", Constants.ICON_URL);

        channel.sendMessage(eb.build()).queue();
    }

}
