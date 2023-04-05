package com.github.alexthe668.iwannaskate.server.network;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkateboardJumpMessage {

    public int skateboardId;
    public int playerId;
    public int jumpAmount;

    public SkateboardJumpMessage(int skateboardId, int playerId, int jumpAmount) {
        this.skateboardId = skateboardId;
        this.playerId = playerId;
        this.jumpAmount = jumpAmount;
    }


    public SkateboardJumpMessage() {
    }

    public static SkateboardJumpMessage read(FriendlyByteBuf buf) {
        return new SkateboardJumpMessage(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(SkateboardJumpMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.skateboardId);
        buf.writeInt(message.playerId);
        buf.writeInt(message.jumpAmount);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(SkateboardJumpMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() ->{
                Player playerSided = context.get().getSender();
                if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                    playerSided = IWannaSkateMod.PROXY.getClientSidePlayer();
                }
                Entity parent = playerSided.level.getEntity(message.skateboardId);
                Entity jumpPlayer = playerSided.level.getEntity(message.playerId);
                if(jumpPlayer != null && parent instanceof SkateboardEntity skateboard && jumpPlayer instanceof Player && jumpPlayer.isPassengerOfSameVehicle(skateboard)){
                    skateboard.handleStartJump(Mth.clamp(message.jumpAmount, 0, 1000));
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}