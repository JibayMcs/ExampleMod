package com.example.examplemod.common.block;


import com.example.examplemod.common.container.CustomChestContainer;
import com.example.examplemod.common.tileEntity.CustomChestTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Example code to create a custom Chest block
 * <br>Help code for @Eno_game10 in MinecraftForgeFrance Discord
 * <br>Classes inspected to create this.
 *
 * @see ChestBlock
 * @see net.minecraft.tileentity.ChestTileEntity
 * @see net.minecraft.inventory.container.ChestContainer
 * @see CustomChestTileEntity
 * @see com.example.examplemod.client.render.tileEntity.CustomChestTileEntityRenderer
 * @see com.example.examplemod.proxy.ClientProxy
 * @see com.example.examplemod.common.init
 */
public class CustomChestBlock extends Block implements IWaterLoggable {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape SINGLE_CHEST_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    private static final CustomChestBlock.InventoryFactory<IInventory> SINGLE_CHEST_INVENTORY = new CustomChestBlock.InventoryFactory<IInventory>() {
        @Override
        public IInventory forDouble(CustomChestTileEntity leftTileEntity, CustomChestTileEntity rightTileEntity) {
            return new DoubleSidedInventory(leftTileEntity, rightTileEntity);
        }

        @Override
        public IInventory forSingle(CustomChestTileEntity customChestTileEntity) {
            return customChestTileEntity;
        }
    };
    private static final CustomChestBlock.InventoryFactory<INamedContainerProvider> DOUBLE_CHEST_INVENTORY = new CustomChestBlock.InventoryFactory<INamedContainerProvider>() {
        @Override
        public INamedContainerProvider forDouble(final CustomChestTileEntity leftTileEntity, final CustomChestTileEntity rightTileEntity) {
            final IInventory iinventory = new DoubleSidedInventory(leftTileEntity, rightTileEntity);
            return new INamedContainerProvider() {
                @Override
                @Nullable
                public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (leftTileEntity.canOpen(playerEntity) && rightTileEntity.canOpen(playerEntity)) {
                        leftTileEntity.fillWithLoot(playerInventory.player);
                        rightTileEntity.fillWithLoot(playerInventory.player);
                        return CustomChestContainer.createGeneric9X6(windowID, playerInventory, iinventory);
                    } else {
                        return null;
                    }
                }

                @Override
                public ITextComponent getDisplayName() {
                    if (leftTileEntity.hasCustomName()) {
                        return leftTileEntity.getDisplayName();
                    } else {
                        return (ITextComponent) (rightTileEntity.hasCustomName() ? rightTileEntity.getDisplayName() : new TranslationTextComponent("container.customChestDouble"));
                    }
                }
            };
        }

        @Override
        public INamedContainerProvider forSingle(CustomChestTileEntity customChestTileEntity) {
            return customChestTileEntity;
        }
    };

    public CustomChestBlock() {
        super(Block.Properties.from(Blocks.CHEST));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    /**
     * @deprecated fine.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     *
     * @deprecated
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        if (facingState.getBlock() == this && facing.getAxis().isHorizontal()) {
            ChestType chesttype = facingState.get(TYPE);
            if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
                return stateIn.with(TYPE, chesttype.opposite());
            }
        } else if (getDirectionToAttached(stateIn) == facing) {
            return stateIn.with(TYPE, ChestType.SINGLE);
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(TYPE) == ChestType.SINGLE) {
            return SINGLE_CHEST_SHAPE;
        } else {
            switch (getDirectionToAttached(state)) {
                case NORTH:
                default:
                    return SHAPE_NORTH;
                case SOUTH:
                    return SHAPE_SOUTH;
                case WEST:
                    return SHAPE_WEST;
                case EAST:
                    return SHAPE_EAST;
            }
        }
    }

    /**
     * Returns a facing pointing from the given state to its attached double chest
     */
    public static Direction getDirectionToAttached(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(TYPE) == ChestType.LEFT ? direction.rotateY() : direction.rotateYCCW();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        ChestType chesttype = ChestType.SINGLE;
        Direction direction = context.getPlacementHorizontalFacing().getOpposite();
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        boolean flag = context.isPlacerSneaking();
        Direction direction1 = context.getFace();
        if (direction1.getAxis().isHorizontal() && flag) {
            Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());
            if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
                direction = direction2;
                chesttype = direction2.rotateYCCW() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chesttype == ChestType.SINGLE && !flag) {
            if (direction == this.getDirectionToAttach(context, direction.rotateY())) {
                chesttype = ChestType.LEFT;
            } else if (direction == this.getDirectionToAttach(context, direction.rotateYCCW())) {
                chesttype = ChestType.RIGHT;
            }
        }

        return this.getDefaultState().with(FACING, direction).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    /**
     * Returns facing pointing to a chest to form a double chest with, null otherwise
     */
    @Nullable
    private Direction getDirectionToAttach(BlockItemUseContext context, Direction direction) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction));
        return blockstate.getBlock() == this && blockstate.get(TYPE) == ChestType.SINGLE ? blockstate.get(FACING) : null;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof CustomChestTileEntity) {
                ((CustomChestTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return getInventoryFactory(state, worldIn, pos, false, DOUBLE_CHEST_INVENTORY);
    }

    /**
     * Replaced original method to use {@link NetworkHooks#openGui(ServerPlayerEntity, INamedContainerProvider, BlockPos)} method.
     */
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return true;
        } else {
            NetworkHooks.openGui((ServerPlayerEntity) player, this.getContainer(state, worldIn, pos), pos);
            return true;
        }
    }

    @Nullable
    public static <T> T getInventoryFactory(BlockState state, IWorld world, BlockPos blockPos, boolean allowBlocked, CustomChestBlock.InventoryFactory<T> inventoryFactory) {
        TileEntity tileentity = world.getTileEntity(blockPos);
        if (!(tileentity instanceof CustomChestTileEntity)) {
            return (T) null;
        } else if (!allowBlocked && isBlocked(world, blockPos)) {
            return (T) null;
        } else {
            CustomChestTileEntity customChestTileEntity = (CustomChestTileEntity) tileentity;
            ChestType chesttype = state.get(TYPE);
            if (chesttype == ChestType.SINGLE) {
                return inventoryFactory.forSingle(customChestTileEntity);
            } else {
                BlockPos blockpos = blockPos.offset(getDirectionToAttached(state));
                BlockState blockstate = world.getBlockState(blockpos);
                if (blockstate.getBlock() == state.getBlock()) {
                    ChestType chesttype1 = blockstate.get(TYPE);
                    if (chesttype1 != ChestType.SINGLE && chesttype != chesttype1 && blockstate.get(FACING) == state.get(FACING)) {
                        if (!allowBlocked && isBlocked(world, blockpos)) {
                            return (T) null;
                        }

                        TileEntity tileentity1 = world.getTileEntity(blockpos);
                        if (tileentity1 instanceof CustomChestTileEntity) {
                            CustomChestTileEntity chesttileentity1 = chesttype == ChestType.RIGHT ? customChestTileEntity : (CustomChestTileEntity) tileentity1;
                            CustomChestTileEntity chesttileentity2 = chesttype == ChestType.RIGHT ? (CustomChestTileEntity) tileentity1 : customChestTileEntity;
                            return inventoryFactory.forDouble(chesttileentity1, chesttileentity2);
                        }
                    }
                }

                return inventoryFactory.forSingle(customChestTileEntity);
            }
        }
    }

    @Nullable
    public static IInventory getInventory(BlockState blockState, World world, BlockPos blockPos, boolean allowBlocked) {
        return getInventoryFactory(blockState, world, blockPos, allowBlocked, SINGLE_CHEST_INVENTORY);
    }


    private static boolean isBlocked(IWorld world, BlockPos blockPos) {
        return isBelowSolidBlock(world, blockPos) || isCatSittingOn(world, blockPos);
    }

    private static boolean isBelowSolidBlock(IBlockReader world, BlockPos blockPos) {
        BlockPos blockpos = blockPos.up();
        return world.getBlockState(blockpos).isNormalCube(world, blockpos);
    }

    private static boolean isCatSittingOn(IWorld world, BlockPos blockPos) {
        List<CatEntity> list = world.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB(blockPos.getX(), (blockPos.getY() + 1), blockPos.getZ(), (blockPos.getX() + 1), (blockPos.getY() + 2), (blockPos.getZ() + 1)));
        if (!list.isEmpty()) {
            for (CatEntity catentity : list) {
                if (catentity.isSitting()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @deprecated
     */
    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    /**
     * @deprecated
     */
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory(getInventory(blockState, worldIn, pos, false));
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     * <p>
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param state The state of the current block
     * @param world The world to create the TE in
     * @return A instance of a class extending TileEntity
     */
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CustomChestTileEntity();
    }


    interface InventoryFactory<T> {
        T forDouble(CustomChestTileEntity leftTileEntity, CustomChestTileEntity rightTileEntity);

        T forSingle(CustomChestTileEntity customChestTileEntity);
    }
}