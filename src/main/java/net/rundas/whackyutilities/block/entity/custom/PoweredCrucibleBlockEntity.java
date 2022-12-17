package net.rundas.whackyutilities.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import net.rundas.whackyutilities.block.custom.PoweredCrucibleBlock;
import net.rundas.whackyutilities.block.entity.ModBlockEntities;
import net.rundas.whackyutilities.screen.PoweredCrucibleMenu;
import net.rundas.whackyutilities.util.ModEnergyStorage;
import net.rundas.whackyutilities.util.WrappedHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PoweredCrucibleBlockEntity extends BlockEntity implements MenuProvider {

    public PoweredCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWERED_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PoweredCrucibleBlockEntity.this.stoneValue;
                    case 1 -> PoweredCrucibleBlockEntity.this.maxStoneValue;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> PoweredCrucibleBlockEntity.this.stoneValue = value;
                    case 1 -> PoweredCrucibleBlockEntity.this.maxStoneValue = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public PoweredCrucibleBlockEntity(BlockPos pos, BlockState state, int tier) {
        this(pos,state);
        this.tier = tier;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(Tags.Items.STONE);
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final FluidTank FLUID_TANK = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
        }
    };
    protected final ContainerData data;
    public int stoneValue = 0;
    private int maxStoneValue = 64000;
    private int tier;


    @Override
    public Component getDisplayName() {
        return new TextComponent("Powered Crucible");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        this.level.sendBlockUpdated(this.getBlockPos(),this.getBlockState(),this.getBlockState(),2);
        return new PoweredCrucibleMenu(id, inventory, this, this.data);
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

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
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
        nbt = FLUID_TANK.writeToNBT(nbt);
        nbt.putInt("powered_crucible.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("powered_crucible.inventory"));
        stoneValue = nbt.getInt("powered_crucible.stone");
        ENERGY_STORAGE.setEnergy(nbt.getInt("powered_crucible.energy"));
        FLUID_TANK.readFromNBT(nbt);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
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
        return pBlockEntity.stoneValue > 0 && pBlockEntity.FLUID_TANK.getSpace() > 0;
    }

    private void createLava(PoweredCrucibleBlockEntity pBlockEntity){
        int fluid;
        fluid = Math.min(1000,pBlockEntity.stoneValue);
        pBlockEntity.stoneValue -= fluid;
        pBlockEntity.FLUID_TANK.fill(new FluidStack(Fluids.LAVA,fluid), IFluidHandler.FluidAction.EXECUTE);
    }

    //Syncing
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("powered_crucible.inventory", itemHandler.serializeNBT());
        nbt.putInt("powered_crucible.stone", this.stoneValue);
        ENERGY_STORAGE.setEnergy(nbt.getInt("powered_crucible.energy"));
        nbt = FLUID_TANK.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public FluidStack getFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    public IEnergyStorage getEnergyStorage() {
        return this.ENERGY_STORAGE;
    }
}
