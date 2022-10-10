package com.github.alexthe668.iwannaskate.server.blockentity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.block.SkateboardRackBlock;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import com.github.alexthe668.iwannaskate.server.network.SkateboardRackMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class SkateboardRackBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN);
    private NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private int tickCount;
    private int lastHoverTimestamp = -1;
    private boolean mouseOverTop = false;
    private int topHoverProgress;
    private int prevTopHoverProgress;
    private int bottomHoverProgress;
    private int prevBottomHoverProgress;

    public SkateboardRackBlockEntity(BlockPos pos, BlockState state) {
        super(IWSBlockEntityRegistry.SKATEBOARD_RACK.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, SkateboardRackBlockEntity entity) {
        entity.tickCount++;
        entity.prevTopHoverProgress = entity.topHoverProgress;
        entity.prevBottomHoverProgress = entity.bottomHoverProgress;
        boolean currentlyHovering = entity.lastHoverTimestamp > 0 && Math.abs(entity.tickCount - entity.lastHoverTimestamp) < 2;
        boolean top = currentlyHovering && entity.mouseOverTop;
        boolean bottom = currentlyHovering && !entity.mouseOverTop;
        if (top && entity.topHoverProgress < 3F) {
            entity.topHoverProgress++;
        }
        if (!top && entity.topHoverProgress > 0F) {
            entity.topHoverProgress--;
        }
        if (bottom && entity.bottomHoverProgress < 3F) {
            entity.bottomHoverProgress++;
        }
        if (!bottom && entity.bottomHoverProgress > 0F) {
            entity.bottomHoverProgress--;
        }

    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return new net.minecraft.world.phys.AABB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
    }

    public float getHoverOver(boolean top, float partialTick) {
        float f = top ? prevTopHoverProgress + (topHoverProgress - prevTopHoverProgress) * partialTick : prevBottomHoverProgress + (bottomHoverProgress - prevBottomHoverProgress) * partialTick;
        return f / 3F;
    }

    @Override
    public int getContainerSize() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack;

            if (this.stacks.get(index).getCount() <= count) {
                itemstack = this.stacks.get(index);
                this.stacks.set(index, ItemStack.EMPTY);
                return itemstack;
            } else {
                itemstack = this.stacks.get(index).split(count);

                if (this.stacks.get(index).isEmpty()) {
                    this.stacks.set(index, ItemStack.EMPTY);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack = this.stacks.get(index);
            this.stacks.set(index, itemstack);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        boolean flag = !stack.isEmpty() && stack.sameItem(this.stacks.get(index)) && ItemStack.tagMatches(stack, this.stacks.get(index));
        this.stacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.saveAdditional(this.getUpdateTag());
        if (!level.isClientSide) {
            IWannaSkateMod.sendMSGToAll(new SkateboardRackMessage(this.getBlockPos().asLong(), index, stacks.get(index)));
        }
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.stacks);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.stacks);
    }

    @Override
    public void startOpen(Player player) {
    }

    @Override
    public void stopOpen(Player player) {
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return stack.getItem() instanceof BaseSkateboardItem;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(packet.getTag(), this.stacks);
        }
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack lvt_2_1_ = this.stacks.get(index);
        if (lvt_2_1_.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(index, ItemStack.EMPTY);
            return lvt_2_1_;
        }
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.alexsmobs.capsid");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Direction getBlockAngle() {
        if (this.getBlockState().getBlock() instanceof SkateboardRackBlock) {
            return this.getBlockState().getValue(SkateboardRackBlock.FACING);
        }
        return Direction.NORTH;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return handlers[0].cast();
            else
                return handlers[1].cast();
        }
        return super.getCapability(capability, facing);
    }

    public void onHoverOver(Entity entity) {
        HitResult result = raytraceCarefully(entity, 15, 1.0F, true);
        if (result instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(this.getBlockPos())) {
            Vec3 vec3 = result.getLocation().subtract(Vec3.atCenterOf(this.getBlockPos()));
            mouseOverTop = vec3.y >= 0;
            lastHoverTimestamp = tickCount;
        }
    }

    public HitResult raytraceCarefully(Entity entity, double rayTraceDistance, float partialTicks, boolean rayTraceFluids) {
        Vec3 vector3d = entity.getEyePosition(partialTicks);
        Vec3 vector3d1 = entity.getViewVector(partialTicks);
        Vec3 vector3d2 = vector3d.add(vector3d1.x * rayTraceDistance, vector3d1.y * rayTraceDistance, vector3d1.z * rayTraceDistance);
        //entity must be null to avoid recursive loop
        return this.level.clip(new ClipContext(vector3d, vector3d2, ClipContext.Block.VISUAL, rayTraceFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }
}
