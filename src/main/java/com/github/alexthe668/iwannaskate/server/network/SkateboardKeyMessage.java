package com.github.alexthe668.iwannaskate.server.network;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkateboardKeyMessage {

    public int skateboardId;
    public int playerId;
    public int type;

    public SkateboardKeyMessage(int skateboardId, int playerId, int type) {
        this.skateboardId = skateboardId;
        this.playerId = playerId;
        this.type = type;
    }


    public SkateboardKeyMessage() {
    }

    public static SkateboardKeyMessage read(FriendlyByteBuf buf) {
        return new SkateboardKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(SkateboardKeyMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.skateboardId);
        buf.writeInt(message.playerId);
        buf.writeInt(message.type);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(SkateboardKeyMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() ->{
                Player playerSided = context.get().getSender();
                if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                    playerSided = IWannaSkateMod.PROXY.getClientSidePlayer();
                }
                Entity parent = playerSided.level.getEntity(message.skateboardId);
                Entity keyPresser = playerSided.level.getEntity(message.playerId);
                if(keyPresser != null && parent instanceof SkateboardEntity skateboard && keyPresser instanceof Player && keyPresser.isPassengerOfSameVehicle(skateboard)){
                    skateboard.onKeyPacket(keyPresser, message.type);
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}