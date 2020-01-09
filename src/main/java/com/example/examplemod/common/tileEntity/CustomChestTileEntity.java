package com.example.examplemod.common.tileEntity;

import com.example.examplemod.common.container.CustomChestContainer;
import com.example.examplemod.common.init.ModTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Tile Entity class for {@link com.example.examplemod.common.block.CustomChestBlock}
 *
 * @see ChestTileEntity
 * @see net.minecraft.inventory.container.ChestContainer
 */
@OnlyIn(value = Dist.CLIENT, _interface = IChestLid.class)
public class CustomChestTileEntity extends LockableLootTileEntity implements IChestLid, ITickableTileEntity {
    private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    protected float lidAngle;
    protected float prevLidAngle;
    protected int numPlayersUsing;
    private int ticksSinceSync;
    private LazyOptional<IItemHandlerModifiable> chestHandler;

    protected CustomChestTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public CustomChestTileEntity() {
        this(ModTileEntityType.CUSTOM_CHEST);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.customChest");
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.chestContents);
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }

        return compound;
    }

    @Override
    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;
        this.numPlayersUsing = getPlayerUsing(this.world, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
        this.prevLidAngle = this.lidAngle;
        float f = 0.1F;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;
            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;
            if (this.lidAngle < 0.5F && f1 >= 0.5F) {
                this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }

    }

    public static int getPlayerUsing(World world, LockableTileEntity tileEntity, int ticksSinceSync, int posX, int posY, int posZ, int numPlayersUsing) {
        if (!world.isRemote && numPlayersUsing != 0 && (ticksSinceSync + posX + posY + posZ) % 200 == 0) {
            numPlayersUsing = increasePlayerUsing(world, tileEntity, posX, posY, posZ);
        }

        return numPlayersUsing;
    }

    public static int increasePlayerUsing(World world, LockableTileEntity tileEntity, int posX, int posY, int posZ) {
        int i = 0;
        float f = 5.0F;

        for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double) ((float) posX - 5.0F), (double) ((float) posY - 5.0F), (double) ((float) posZ - 5.0F), (double) ((float) (posX + 1) + 5.0F), (double) ((float) (posY + 1) + 5.0F), (double) ((float) (posZ + 1) + 5.0F)))) {
            if (playerentity.openContainer instanceof CustomChestContainer) {
                IInventory iinventory = ((CustomChestContainer) playerentity.openContainer).getLowerChestInventory();
                if (iinventory == tileEntity || iinventory instanceof DoubleSidedInventory && ((DoubleSidedInventory) iinventory).isPartOfLargeChest(tileEntity)) {
                    ++i;
                }
            }
        }

        return i;
    }

    private void playSound(SoundEvent soundIn) {
        ChestType chesttype = this.getBlockState().get(ChestBlock.TYPE);
        if (chesttype != ChestType.LEFT) {
            double d0 = (double) this.pos.getX() + 0.5D;
            double d1 = (double) this.pos.getY() + 0.5D;
            double d2 = (double) this.pos.getZ() + 0.5D;
            if (chesttype == ChestType.RIGHT) {
                Direction direction = ChestBlock.getDirectionToAttached(this.getBlockState());
                d0 += (double) direction.getXOffset() * 0.5D;
                d2 += (double) direction.getZOffset() * 0.5D;
            }

            this.world.playSound((PlayerEntity) null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof ChestBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }

    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.chestContents = itemsIn;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getLidAngle(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return CustomChestContainer.createGeneric9X3(id, player, this);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.chestHandler != null) {
            this.chestHandler.invalidate();
            this.chestHandler = null;
        }
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
        if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.chestHandler == null) {
                this.chestHandler = net.minecraftforge.common.util.LazyOptional.of(this::createHandler);
            }
            return this.chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ChestBlock)) {
            return new net.minecraftforge.items.wrapper.InvWrapper(this);
        }
        ChestType type = state.get(ChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            BlockPos opos = this.getPos().offset(ChestBlock.getDirectionToAttached(state));
            BlockState ostate = this.getWorld().getBlockState(opos);
            if (state.getBlock() == ostate.getBlock()) {
                ChestType otype = ostate.get(ChestBlock.TYPE);
                if (otype != ChestType.SINGLE && type != otype && state.get(ChestBlock.FACING) == ostate.get(ChestBlock.FACING)) {
                    TileEntity ote = this.getWorld().getTileEntity(opos);
                    if (ote instanceof CustomChestTileEntity) {
                        IInventory top = type == ChestType.RIGHT ? this : (IInventory) ote;
                        IInventory bottom = type == ChestType.RIGHT ? (IInventory) ote : this;
                        return new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                                new net.minecraftforge.items.wrapper.InvWrapper(top),
                                new net.minecraftforge.items.wrapper.InvWrapper(bottom));
                    }
                }
            }
        }
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void remove() {
        super.remove();
        if (chestHandler != null)
            chestHandler.invalidate();
    }


    /**
     * Defined to prevent model render glitch if player look away, but if the model still in render view.<br>
     * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
    }


}