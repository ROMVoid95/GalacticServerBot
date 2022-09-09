package com.readonlydev.updates;

import java.sql.SQLException;
import java.util.ArrayList;

import com.readonlydev.GalacticBot;
import com.readonlydev.updates.storage.json.Root;

import de.erdbeerbaerlp.cfcore.CFCoreAPI;
import de.erdbeerbaerlp.cfcore.json.CFMod;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class CurseforgeProject implements Runnable {
    final ArrayList<CFChannel> toRemove = new ArrayList<>();
    private final ArrayList<CFChannel> channels = new ArrayList<>();
    public CFMod proj;

    public void addChannel(long idLong, long projectID) {
        for (CFChannel c : channels)
            if (c != null && c.channelID == idLong) return;

        final CFChannel cfChannel = new CFChannel(idLong, new Root());
        cfChannel.data.projects = new Long[]{projectID};
        this.channels.add(cfChannel);
        Updates.ifa.addChannelToProject(projectID, idLong);
    }

    public boolean addChannel(CFChannel channel) {
        System.out.println("Adding " + channel.channelID);
        return this.channels.add(channel);
    }

    public boolean removeChannel(long channelID) {
        for (CFChannel c : channels) {
            if (c.channelID == channelID) return removeChannel(c);
        }
        return false;
    }

    public boolean removeChannel(CFChannel channel) {
        System.out.println("Removing " + channel.channelID);
        return this.channels.remove(channel);
    }

    public CurseforgeProject(CFMod project, CFChannel channel) {
        this.channels.add(channel);
        this.proj = project;
    }

    @Override
    public void run() {
        try {
            proj = CFCoreAPI.getModFromID(proj.id);
            if (proj.latestFiles.length == 0) return;
            if (Updates.ifa.isNewFile(proj)) {
                toRemove.forEach(this::removeChannel);
                toRemove.clear();
                Updates.ifa.updateCache(proj.id, proj.latestFilesIndexes[0].fileId);
                for (final CFChannel c : channels) {
                    final TextChannel channel = GalacticBot.getJda().getTextChannelById(c.channelID);
                    if (channel == null) {
                        toRemove.add(Updates.ifa.deleteChannelFromProject(proj.id, c.channelID));
                        return;
                    }
                    try {
                        EmbedMessage.sendUpdateNotification(c, channel, proj);
                    } catch (InsufficientPermissionException e) {
                        System.out.println(channel);
                        System.out.println(channel.getName() + ":" + e.getMessage());
                        final Guild guild = channel.getGuild();
                        guild.retrieveOwner().submit().thenAccept((ow) -> {
                            System.out.println(ow);
                            if (ow != null)
                                ow.getUser().openPrivateChannel().submit().thenAccept((dm) -> {
                                    dm.sendMessage("I tried posting an update notification, but I am missing required permission for channel " + channel.getAsMention() + "\n> `" + e.getMessage() + "`").queue();
                                });
                        });

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class CFChannel {

        public final long channelID;
        public final Root data;

        public CFChannel(long channelID, Root json) {
            this.channelID = channelID;
            this.data = json;
        }

        @Override
        public String toString() {
            return "CFChannel{" +
                    "channelID=" + channelID +
                    ", data=" + data +
                    '}';
        }
    }
}