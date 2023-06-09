package com.github.alexthe668.iwannaskate.server.network;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkateboardPartMessage {

    public int parentId;
    public int playerId;
    public int type;

    public SkateboardPartMessage(int parentId, int playerId, int type) {
        this.parentId = parentId;
        this.playerId = playerId;
        this.type = type;
    }


    public SkateboardPartMessage() {
    }

    public static SkateboardPartMessage read(FriendlyByteBuf buf) {
        return new SkateboardPartMessage(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(SkateboardPartMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.parentId);
        buf.writeInt(message.playerId);
        buf.writeInt(message.type);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(SkateboardPartMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() ->{
                Player playerSided = context.get().getSender();
                if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                    playerSided = IWannaSkateMod.PROXY.getClientSidePlayer();
                }
                Entity parent = playerSided.level().getEntity(message.parentId);
                Entity interacter = playerSided.level().getEntity(message.playerId);
                if(interacter != null && parent instanceof SkateboardEntity skateboard && interacter.distanceTo(skateboard) < 16){
                    skateboard.onInteractPacket(interacter, message.type);
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}