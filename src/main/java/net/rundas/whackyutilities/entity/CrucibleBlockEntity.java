package net.rundas.whackyutilities.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.rundas.whackyutilities.block.CrucibleBlock;
import net.rundas.whackyutilities.networking.ModMessages;
import net.rundas.whackyutilities.networking.packet.*;
import net.rundas.whackyutilities.screen.CrucibleMenu;
import net.rundas.whackyutilities.util.WrappedHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CrucibleBlockEntity extends BlockEntity implements MenuProvider {

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    public CrucibleBlockEntity(BlockPos pos, BlockState state, int tier) {
        this(pos,state);
        this.tier = tier;
    }

    //Items

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new CValueSyncS2CPacket(CrucibleBlockEntity.this.stoneValue, worldPosition));
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(Tags.Items.STONE);
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public void setStone(int value) {
        this.stoneValue = value;
    }

    //Fluids

    private final FluidTank fluidTank = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new CFluidSyncS2CPacket(this.fluid, worldPosition));
            }
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }
    };

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public FluidStack getFluidStack() {
        return this.fluidTank.getFluid();
    }

    public void setFluid(FluidStack fluidStack) {
        this.fluidTank.setFluid(fluidStack);
    }

    //Syncing

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("crucible.inventory", itemHandler.serializeNBT());
        nbt.putInt("crucible.stone", this.stoneValue);
        nbt.putInt("crucible.fluid", this.fluidTank.getFluidAmount());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("crucible.inventory"));
        stoneValue = nbt.getInt("crucible.stone");
        fluidTank.setFluid(new FluidStack(Fluids.LAVA, nbt.getInt("crucible.fluid")));
    }

    //Krafting

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CrucibleBlockEntity pBlockEntity) {
        if (pBlockEntity.canConvertStone(pBlockEntity)){
            pBlockEntity.convertStone(pBlockEntity);
        }
        if (pBlockEntity.canCreateLava(pBlockEntity)){
            pBlockEntity.createLava(pBlockEntity);
        }
    }

    public boolean canConvertStone(CrucibleBlockEntity pBlockEntity){
        return pBlockEntity.itemHandler.getStackInSlot(0).is(Tags.Items.STONE) && pBlockEntity.stoneValue <= pBlockEntity.maxStoneValue - 1000;
    }

    private void convertStone(CrucibleBlockEntity pBlockEntity){
        pBlockEntity.itemHandler.extractItem(0,1,false);
        pBlockEntity.stoneValue += 1000;
    }

    public boolean canCreateLava(CrucibleBlockEntity pBlockEntity){
        return pBlockEntity.stoneValue > 0 && pBlockEntity.fluidTank.getSpace() > 0
                && getModifier(pBlockEntity) > 0;
    }

    private void createLava(CrucibleBlockEntity pBlockEntity){
        int mod = getModifier(pBlockEntity);
        int amount = Math.min(mod,pBlockEntity.fluidTank.getSpace());
        amount = Math.min(amount, pBlockEntity.stoneValue);
        pBlockEntity.stoneValue -= amount;
        pBlockEntity.fluidTank.fill(new FluidStack(Fluids.LAVA,amount), IFluidHandler.FluidAction.EXECUTE);
    }
    public int getModifier(CrucibleBlockEntity pBlockEntity){
        int mod = 0;
        Block block = level.getBlockState(pBlockEntity.getBlockPos().below()).getBlock();
        if (block == Blocks.TORCH) {
            mod = 1;
        }
        if (block == Blocks.FIRE || block == Blocks.CAMPFIRE) {
            mod = 2;
        }
        if (block == Blocks.LAVA || block == Blocks.SOUL_FIRE || block == Blocks.SOUL_CAMPFIRE) {
            mod = 3;
        }
        return mod*pBlockEntity.tier;
    }

    //Misc Fields

    public int stoneValue = 0;
    private final int maxStoneValue = 64000;
    private int tier;

    //Misc Methods

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) {
                return lazyItemHandler.cast();
            }
            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(CrucibleBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Crucible");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToClients(new CValueSyncS2CPacket(this.stoneValue, worldPosition));
        ModMessages.sendToClients(new CFluidSyncS2CPacket(this.fluidTank.getFluid(), worldPosition));
        return new CrucibleMenu(id, inventory, this);
    }
}
