package com.github.jummes.libs.wrapper;

import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ProtocolWrapper_v1_19_R2 implements ProtocolWrapper {

    @Override
    public void sendBlockBreakAnimationPacket(Player p, Location l, int crack, int randomEid) {
        Packet<ClientGamePacketListener> packet = new ClientboundBlockDestructionPacket(randomEid,
                new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ()), crack);
        ((CraftPlayer) p).getHandle().connection.send(packet);

    }

    @Override
    public void sendDestroyTeamPacket(Player p, String team) {
        Packet<ClientGamePacketListener> packet = ClientboundSetPlayerTeamPacket.createRemovePacket(new PlayerTeam(new Scoreboard(), team));
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    @Override
    public void sendCreateTeamPacket(Player p, ChatColor color, UUID id, String team) {
        PlayerTeam scTeam = new PlayerTeam(new Scoreboard(), team);
        scTeam.getPlayers().add(id.toString());
        scTeam.setColor(ChatFormatting.values()[color.ordinal()]);
        Packet<ClientGamePacketListener> packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(scTeam, true);
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    @Override
    public void sendDestroyEntityPacket(Player p, int eid) {
        Packet<ClientGamePacketListener> packet = new ClientboundRemoveEntitiesPacket(eid);
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    @Override
    public void sendEntityMetadataPacket(Player p, int eid, byte tags) {
        Packet<ClientGamePacketListener> packet = new ClientboundSetEntityDataPacket(eid, List.of(
                new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, tags)
        ));
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    @Override
    public void sendSpawnEntityPacket(Player p, Location l, int eid, UUID id) {
        Packet<ClientGamePacketListener> packet = new ClientboundAddEntityPacket(eid, id, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(),
                EntityType.SHULKER, 0, new Vec3(0, 0, 0), 0);
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }
}
