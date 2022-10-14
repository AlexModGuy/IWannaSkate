package com.github.alexthe668.iwannaskate.server.block;

import com.github.alexthe668.iwannaskate.server.blockentity.IWSBlockEntityRegistry;
import com.github.alexthe668.iwannaskate.server.blockentity.SkateboardRackBlockEntity;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SkateboardRackBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape NORTH_SHAPE = Shapes.or(Block.box(0.0D, 9.0D, 13.0D, 16.0D, 12.0D, 16.0D),
            Block.box(3.0D, 2.0D, 10.0D, 13.0D, 15.0D, 16.0D));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(Block.box(0.0D, 9.0D, 0.0D, 16.0D, 12.0D, 3.0D),
            Block.box(3.0D, 2.0D, 0.0D, 13.0D, 15.0D, 6.0D));
    private static final VoxelShape WEST_SHAPE = Shapes.or(Block.box(13.0D, 9.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(10.0D, 2.0D, 3.0D, 16.0D, 15.0D, 13.0D));
    private static final VoxelShape EAST_SHAPE = Shapes.or(Block.box(0.0D, 9.0D, 0.0D, 3.0D, 12.0D, 16.0D),
            Block.box(0.0D, 2.0D, 3.0D, 6.0D, 15.0D, 13.0D));

    public SkateboardRackBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getClickedFace();
        return dir.getAxis() == Direction.Axis.Y ? Blocks.AIR.defaultBlockState() : this.defaultBlockState().setValue(FACING, dir);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof SkateboardRackBlockEntity rack && context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() != null) {
            rack.onHoverOver(entityCollisionContext.getEntity());
        }
        switch (state.getValue(FACING)) {
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState pos, LevelAccessor p_51216_, BlockPos p_51217_, BlockPos p_51218_) {
        return !state.canSurvive(p_51216_, p_51217_) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, pos, p_51216_, p_51217_, p_51218_);
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        Direction dir = blockState.getValue(FACING);
        return canSupportCenter(level, pos.relative(dir.getOpposite()), dir);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof SkateboardRackBlockEntity rack && !player.isShiftKeyDown()) {
            int lookingAtSlot = hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)).y > 0.5F ? 0 : 1;
            if (heldItem.getItem() instanceof BaseSkateboardItem && rack.getItem(lookingAtSlot).isEmpty()) {
                rack.setItem(lookingAtSlot, heldItem.copy());
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (!rack.getItem(lookingAtSlot).isEmpty()) {
                ItemStack copy = rack.getItem(lookingAtSlot).copy();
                if (!player.addItem(copy)) {
                    popResource(worldIn, pos, rack.getItem(lookingAtSlot).copy());
                }
                rack.setItem(lookingAtSlot, ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME;
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof SkateboardRackBlockEntity rack) {
            int lookingAtSlot = target.getLocation().subtract(Vec3.atLowerCornerOf(pos)).y > 0.5F ? 0 : 1;
            if(!rack.getItem(lookingAtSlot).isEmpty()){
                return rack.getItem(lookingAtSlot).copy();
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof SkateboardRackBlockEntity rack) {
            Containers.dropContents(worldIn, pos, rack);
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, IWSBlockEntityRegistry.SKATEBOARD_RACK.get(), SkateboardRackBlockEntity::commonTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SkateboardRackBlockEntity(pos, state);
    }
}
