package de.dastoast;

import de.dastoast.listeners.startBewerbung;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class Main {

    public static String token = "MTM2NDM1ODk4OTc2ODI5NDQ5Mg.GcVAg5.OqQtxOVS0LHp7kDAqR9oHSNbGxDh05L_20YKMU";
    public static String bewerbungsChannelId = "1381312209199562803"; // <- Channel-ID vom Bewerbungschannel

    public static void main(String[] args) {
        String status = "Neue Bewerbungen";

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching(status));

        startBewerbung listener = new startBewerbung();
        builder.addEventListeners(listener);

        try {
            JDA jda = builder.build().awaitReady();
            System.out.println("Online!");

            TextChannel bewerbungsChannel = jda.getTextChannelById(bewerbungsChannelId);
            if (bewerbungsChannel != null) {
                bewerbungsChannel.getHistory().retrievePast(100).queue(messages -> {
                    if (messages.size() >= 2) {
                        bewerbungsChannel.deleteMessages(messages).queue(success -> {
                            listener.sendBewerbungEmbed(bewerbungsChannel);
                        });
                    } else {
                        for (Message message : messages) {
                            message.delete().queue();
                        }
                        listener.sendBewerbungEmbed(bewerbungsChannel);
                    }
                });
            }

        } catch (InterruptedException e) {
            System.out.println("Bot-Start unterbrochen: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Fehler beim Starten des Bots: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
