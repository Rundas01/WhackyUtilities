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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.rundas.whackyutilities.block.PoweredCrucibleBlock;
import net.rundas.whackyutilities.networking.ModMessages;
import net.rundas.whackyutilities.networking.packet.*;
import net.rundas.whackyutilities.screen.PoweredCrucibleMenu;
import net.rundas.whackyutilities.util.ModEnergyStorage;
import net.rundas.whackyutilities.util.WrappedHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PoweredCrucibleBlockEntity extends BlockEntity implements MenuProvider {

    public PoweredCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWERED_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    public PoweredCrucibleBlockEntity(BlockPos pos, BlockState state, int tier) {
        this(pos,state);
        this.tier = tier;
    }

    //Items

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new PCValueSyncS2CPacket(PoweredCrucibleBlockEntity.this.stoneValue, worldPosition));
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
                ModMessages.sendToClients(new PCFluidSyncS2CPacket(this.fluid, worldPosition));
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

    //Energy

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(64000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new PCEnergySyncS2CPacket(this.energy, worldPosition));
            }
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    };

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public void setEnergyLevel(int energy) {
        this.energyStorage.setEnergy(energy);
    }

    //Krafting

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PoweredCrucibleBlockEntity pBlockEntity) {
        if (pBlockEntity.canConvertStone(pBlockEntity)){
            pBlockEntity.convertStone(pBlockEntity);
        }
        if (pBlockEntity.canCreateLava(pBlockEntity)){
            pBlockEntity.createLava(pBlockEntity);
        }
    }

    public boolean canConvertStone(PoweredCrucibleBlockEntity pBlockEntity){
        return pBlockEntity.itemHandler.getStackInSlot(0).is(Tags.Items.STONE) && pBlockEntity.stoneValue <= pBlockEntity.maxStoneValue - 1000;
    }

    private void convertStone(PoweredCrucibleBlockEntity pBlockEntity){
        pBlockEntity.itemHandler.extractItem(0,1,false);
        pBlockEntity.stoneValue += 1000;
    }

    public boolean canCreateLava(PoweredCrucibleBlockEntity pBlockEntity){
        return pBlockEntity.stoneValue > 0 && pBlockEntity.fluidTank.getSpace() > 0
                && getModifier(pBlockEntity) > 0;
    }

    private void createLava(PoweredCrucibleBlockEntity pBlockEntity){
        int mod = getModifier(pBlockEntity);
        int amount = Math.min(mod,pBlockEntity.fluidTank.getSpace());
        amount = Math.min(amount, pBlockEntity.stoneValue);
        pBlockEntity.stoneValue -= amount;
        pBlockEntity.fluidTank.fill(new FluidStack(Fluids.LAVA,amount), IFluidHandler.FluidAction.EXECUTE);
    }
    public int getModifier(PoweredCrucibleBlockEntity pBlockEntity){
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
        if (pBlockEntity.energyStorage.getEnergyStored() > 0) {
            mod *= 2;
        }
        return mod*pBlockEntity.tier;
    }

    //Misc Fields

    public int stoneValue = 0;
    private final int maxStoneValue = 64000;
    private int tier;

    //Misc Methods
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
    public Component getDisplayName() {
        return new TextComponent("Powered Crucible");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToClients(new PCValueSyncS2CPacket(this.stoneValue, worldPosition));
        ModMessages.sendToClients(new PCFluidSyncS2CPacket(this.fluidTank.getFluid(), worldPosition));
        ModMessages.sendToClients(new PCEnergySyncS2CPacket(this.energyStorage.getEnergyStored(), worldPosition));
        return new PoweredCrucibleMenu(id, inventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) {
                return lazyItemHandler.cast();
            }
            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(PoweredCrucibleBlock.FACING);

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
        if(cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    //Syncing

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("powered_crucible.inventory", itemHandler.serializeNBT());
        nbt.putInt("powered_crucible.stone", this.stoneValue);
        nbt.putInt("powered_crucible.fluid", this.fluidTank.getFluidAmount());
        nbt.putInt("powered_crucible.energy", energyStorage.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("powered_crucible.inventory"));
        stoneValue = nbt.getInt("powered_crucible.stone");
        energyStorage.setEnergy(nbt.getInt("powered_crucible.energy"));
        fluidTank.setFluid(new FluidStack(Fluids.LAVA, nbt.getInt("powered_crucible.fluid")));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
