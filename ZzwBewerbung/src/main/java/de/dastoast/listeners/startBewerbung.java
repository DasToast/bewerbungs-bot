package de.dastoast.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class startBewerbung extends ListenerAdapter {

    private final String adminChannelId = "1381316098024145056"; // Ersetzen
    private final String inbewerbungRoleId = "1380964425397178368"; // Ersetzen
    private final String memberRoleId = "1380964778197123073"; // Ersetzen

    public void sendBewerbungEmbed(MessageChannel channel) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Bewerbe dich hier und werde NXTL Clanmitglied!");
        embed.setColor(0x41b580);
        embed.setDescription("Klicke auf den Button um mit der Bewerbung zu beginnen!");

        Button button = Button.success("bewerbung_start", "Jetzt Bewerben!").withEmoji(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode("✅"));
        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("bewerbung_start")) {
            Modal modal = Modal.create("bewerbung_modal", "Bewerbung ausfüllen")
                    .addActionRow(TextInput.create("alter", "Alter", TextInputStyle.SHORT).setPlaceholder("z.B. 16").setRequired(true).build())
                    .addActionRow(TextInput.create("mute", "Mute-Punkte", TextInputStyle.SHORT).setPlaceholder("z.B. 0").setRequired(true).build())
                    .addActionRow(TextInput.create("bann", "Bann-Punkte", TextInputStyle.SHORT).setPlaceholder("z.B. 0").setRequired(true).build())
                    .addActionRow(TextInput.create("spielzeit", "Spielzeit (in Stunden)", TextInputStyle.SHORT).setPlaceholder("z.B. 200").setRequired(true).build())
                    .addActionRow(TextInput.create("skills", "Spezielle Fähigkeiten", TextInputStyle.PARAGRAPH).setPlaceholder("Builder, Developer...").setRequired(true).build())
                    .build();

            event.replyModal(modal).queue();
        }

        if (event.getComponentId().equals("bewerbung_accept")) {
            // Akzeptiere die Bewerbung
            Role inbewerbung = event.getGuild().getRoleById(inbewerbungRoleId);
            Role memberRole = event.getGuild().getRoleById(memberRoleId);

            if (inbewerbung != null && memberRole != null) {
                event.getGuild().removeRoleFromMember(event.getMember(), inbewerbung).queue();
                event.getGuild().addRoleToMember(event.getMember(), memberRole).queue();
                event.reply("Bewerbung akzeptiert, du hast nun die Member-Rolle!").setEphemeral(true).queue();
            } else {
                event.reply("Fehler: Rollen nicht gefunden!").setEphemeral(true).queue();
            }

        } else if (event.getComponentId().equals("bewerbung_reject")) {
            // Lehne die Bewerbung ab
            Role inbewerbung = event.getGuild().getRoleById(inbewerbungRoleId);

            if (inbewerbung != null) {
                event.getGuild().removeRoleFromMember(event.getMember(), inbewerbung).queue();
                event.reply("Bewerbung abgelehnt. Du wurdest aus der Bewerbungsrolle entfernt.").setEphemeral(true).queue();
            } else {
                event.reply("Fehler: Rolle nicht gefunden!").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("bewerbung_modal")) return;

        String user = event.getUser().getAsTag();
        String alter = event.getValue("alter").getAsString();
        String mute = event.getValue("mute").getAsString();
        String bann = event.getValue("bann").getAsString();
        String spielzeit = event.getValue("spielzeit").getAsString();
        String skills = event.getValue("skills").getAsString();

        String msg = "**Neue Bewerbung von " + user + "**\n"
                + "Alter: " + alter + "\n"
                + "Mute-Punkte: " + mute + "\n"
                + "Bann-Punkte: " + bann + "\n"
                + "Spielzeit: " + spielzeit + " Stunden\n"
                + "Fähigkeiten: " + skills;

        // Admin-Channel holen und Nachricht senden mit Annahme und Ablehnung Buttons
        event.getJDA().getTextChannelById(adminChannelId)
                .sendMessage(msg)
                .setActionRow(
                        Button.success("bewerbung_accept", "Annehmen ✅"),
                        Button.danger("bewerbung_reject", "Ablehnen ❌")
                ).queue();

        event.reply("Danke! Deine Bewerbung wurde eingereicht.").setEphemeral(true).queue();
    }
}
