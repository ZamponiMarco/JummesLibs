package com.github.jummes.libs.wrapper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProtocolWrapper_v1_17_R1 implements ProtocolWrapper {

    @Override
    public void sendBlockBreakAnimationPacket(Player p, Location l, int crack, int randomEid) {
        Packet packet = new ClientboundBlockDestructionPacket(randomEid,
                new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ()), crack);
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }

    @Override
    public void sendDestroyTeamPacket(Player p, String team) {
        Packet packet = ClientboundSetPlayerTeamPacket.createRemovePacket(new PlayerTeam(new Scoreboard(), team));
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }

    @Override
    public void sendCreateTeamPacket(Player p, ChatColor color, UUID id, String team) {
        PlayerTeam scTeam = new PlayerTeam(new Scoreboard(), team);
        scTeam.getPlayers().add(id.toString());
        scTeam.setColor(ChatFormatting.values()[color.ordinal()]);
        Packet packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(scTeam, true);
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }

    @Override
    public void sendDestroyEntityPacket(Player p, int eid) {
        Packet packet = new ClientboundRemoveEntitiesPacket(eid);
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }

    @Override
    public void sendEntityMetadataPacket(Player p, int eid, byte tags) {
        SynchedEntityData watcher = new SynchedEntityData(null);
        watcher.set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), tags);
        Packet packet = new ClientboundSetEntityDataPacket(eid, watcher, true);
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }

    @Override
    public void sendSpawnEntityPacket(Player p, Location l, int eid, UUID id) {
        Packet packet = new ClientboundAddEntityPacket(eid, id, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(),
                EntityType.SHULKER, 0, new Vec3(0, 0, 0));
        ((CraftPlayer) p).getHandle().networkManager.send(packet);
    }
}
