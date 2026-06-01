package com.github.sachin.lootin.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.sachin.lootin.Lootin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityMetaDataPacketListener extends PacketAdapter {

    private static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public EntityMetaDataPacketListener() {
        super(Lootin.getPlugin(), TYPE);
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        if (Lootin.getPlugin().is1_16()) {
            return;
        }
//        Temporary
//        if(ServerVersion.current().isAtLeast(1,21,7)) return;
        if (Lootin.getPlugin().isBlackListWorld(player.getWorld())) {
            return;
        }
        if (Lootin.getPlugin().isRunningElytraVaults) {
            return;
        }
        Entity entity = null;
        try {
            entity = packet.getEntityModifier(event).read(0);
        } catch (Exception ignored) {
        }

        // Elytra per-player visibility handling is temporarily disabled.
        // Original logic crafted version-specific ENTITY_METADATA packets
        // (via Prilib/NMS) so an ItemFrame's elytra could be hidden from
        // specific players. This is commented out for now and will be
        // reimplemented later using ProtocolLib or a maintained NMS adapter.
        // idk lmaooo it's 4:39 in the morning and i just want to get this working again for derex smp
    }
}
