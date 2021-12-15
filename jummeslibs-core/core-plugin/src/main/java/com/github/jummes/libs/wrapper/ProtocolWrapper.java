package com.github.jummes.libs.wrapper;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ProtocolWrapper {

    void sendBlockBreakAnimationPacket(Player p, Location l, int crack, int randomEid);

    void sendDestroyTeamPacket(Player p, String team);

    void sendCreateTeamPacket(Player p, ChatColor color, UUID id, String team);

    void sendDestroyEntityPacket(Player p, int eid);

    void sendEntityMetadataPacket(Player p, int eid, byte tags);

    void sendSpawnEntityPacket(Player p, Location l, int eid, UUID id);
}
