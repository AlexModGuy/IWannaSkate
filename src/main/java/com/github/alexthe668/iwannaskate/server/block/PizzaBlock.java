package com.github.alexthe668.iwannaskate.server.block;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;

public class PizzaBlock extends Block {
    public static final BooleanProperty PIECE_1 = BooleanProperty.create("slice_1");
    public static final BooleanProperty PIECE_2 = BooleanProperty.create("slice_2");
    public static final BooleanProperty PIECE_3 = BooleanProperty.create("slice_3");
    public static final BooleanProperty PIECE_4 = BooleanProperty.create("slice_4");
    private static final VoxelShape PIECE_1_BB = Block.box(8.0D, 0.0D, 8.0D,  16.0D, 2.0D, 16.0D);
    private static final VoxelShape PIECE_2_BB = Block.box(0, 0.0D, 8.0D,  8.0D, 2.0D, 16.0D);
    private static final VoxelShape PIECE_3_BB = Block.box(0.0D, 0.0D, 0.0D,  8.0D, 2.0D, 8.0D);
    private static final VoxelShape PIECE_4_BB = Block.box(8.0D, 0.0D, 0.0D,  16.0D, 2.0D, 8.0D);
    private static HashMap<BlockState, VoxelShape> shapes = new HashMap<>();

    public PizzaBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PIECE_1, false).setValue(PIECE_2, false).setValue(PIECE_3, false).setValue(PIECE_4, false));
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if(shapes.containsKey(blockState)){
            return shapes.get(blockState);
        }else{
            VoxelShape shape = Shapes.empty();
            if(blockState.getValue(PIECE_1)){
                shape = Shapes.or(shape, PIECE_1_BB);
            }
            if(blockState.getValue(PIECE_2)){
                shape = Shapes.or(shape, PIECE_2_BB);
            }
            if(blockState.getValue(PIECE_3)){
                shape = Shapes.or(shape, PIECE_3_BB);
            }
            if(blockState.getValue(PIECE_4)){
                shape = Shapes.or(shape, PIECE_4_BB);
            }
            shapes.put(blockState, shape);
            return shape;
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Vec3 vec = context.getClickLocation().subtract(Vec3.atLowerCornerOf(context.getClickedPos())).scale(1.01F);
        BlockState previous = context.getLevel().getBlockState(context.getClickedPos());
        BlockState pizza = this.defaultBlockState();
        if(previous.getBlock() == this){
            pizza = previous;
        }
        BooleanProperty slice = getSliceFromVec(vec);
        return pizza.setValue(slice, true);
    }


    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && this.getPiecesCount(state) < 4 ? true : super.canBeReplaced(state, context);
    }


    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        Vec3 vec = result.getLocation().subtract(Vec3.atLowerCornerOf(result.getBlockPos()));
        BooleanProperty slice = getSliceFromVec(vec);
        if(blockState.getValue(slice) && item != this.asItem()){
            if (level.isClientSide) {
                if (eat(level, pos, blockState, player, slice).consumesAction()) {
                    return InteractionResult.SUCCESS;
                }

                if (itemstack.isEmpty()) {
                    return InteractionResult.CONSUME;
                }
            }
            return eat(level, pos, blockState, player, slice);
        }else{
            return InteractionResult.PASS;
        }
    }

    public BooleanProperty getSliceFromVec(Vec3 vec3){
        if(vec3.x >= 0.5F && vec3.z >= 0.5F){
            return PIECE_1;
        }
        if(vec3.x >= 0.5F && vec3.z < 0.5F){
            return PIECE_4;
        }
        if(vec3.x < 0.5F && vec3.z >= 0.5F){
            return PIECE_2;
        }
        if(vec3.x < 0.5F && vec3.z < 0.5F){
            return PIECE_3;
        }
        return PIECE_1;
    }

    protected InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player, BooleanProperty slice) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(3, 0.2F);
            int i = getPiecesCount(state);
            level.gameEvent(player, GameEvent.EAT, pos);
            if (i > 1) {
                level.setBlock(pos, state.setValue(slice, false), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            return InteractionResult.SUCCESS;
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState pos, LevelAccessor p_51216_, BlockPos p_51217_, BlockPos p_51218_) {
        return direction == Direction.DOWN && !state.canSurvive(p_51216_, p_51217_) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, pos, p_51216_, p_51217_, p_51218_);
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP) && this.getPiecesCount(blockState) > 0;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockState) {
        blockState.add(PIECE_1, PIECE_2, PIECE_3, PIECE_4);
    }

    public int getAnalogOutputSignal(BlockState state, Level p_51199_, BlockPos p_51200_) {
        return getPiecesCount(state) * 2;
    }

    private int getPiecesCount(BlockState state){
        int i = 0;
        if(state.getValue(PIECE_1)){
            i++;
        }
        if(state.getValue(PIECE_2)){
            i++;
        }
        if(state.getValue(PIECE_3)){
            i++;
        }
        if(state.getValue(PIECE_4)){
            i++;
        }
        return i;
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public boolean isPathfindable(BlockState p_51193_, BlockGetter p_51194_, BlockPos p_51195_, PathComputationType p_51196_) {
        return false;
    }
}