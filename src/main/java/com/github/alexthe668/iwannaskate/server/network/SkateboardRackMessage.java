package com.github.alexthe668.iwannaskate.server.network;

import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.blockentity.SkateboardRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkateboardRackMessage   {

    public long blockPos;
    public int slot;
    public ItemStack heldStack;

    public SkateboardRackMessage(long blockPos, int slot, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.slot = slot;
        this.heldStack = heldStack;

    }

    public SkateboardRackMessage() {
    }

    public static SkateboardRackMessage read(FriendlyByteBuf buf) {
        return new SkateboardRackMessage(buf.readLong(), buf.readInt(), PacketBufferUtils.readItemStack(buf));
    }

    public static void write(SkateboardRackMessage message, FriendlyByteBuf buf) {
        buf.writeLong(message.blockPos);
        buf.writeInt(message.slot);
        PacketBufferUtils.writeItemStack(buf, message.heldStack);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(SkateboardRackMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IWannaSkateMod.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.level() != null) {
                    BlockPos pos = BlockPos.of(message.blockPos);
                    if (player.level().getBlockEntity(pos) != null) {
                        if (player.level().getBlockEntity(pos) instanceof SkateboardRackBlockEntity blockEntity) {
                            blockEntity.setItem(message.slot, message.heldStack);
                        }
                    }
                }
            }
        }
    }

}