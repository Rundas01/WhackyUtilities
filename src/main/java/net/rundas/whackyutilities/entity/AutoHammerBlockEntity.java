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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.rundas.whackyutilities.block.PoweredCrucibleBlock;
import net.rundas.whackyutilities.networking.ModMessages;
import net.rundas.whackyutilities.networking.packet.AHEnergySyncS2CPacket;
import net.rundas.whackyutilities.recipe.AutoHammerRecipe;
import net.rundas.whackyutilities.screen.AutoHammerMenu;
import net.rundas.whackyutilities.util.ModEnergyStorage;
import net.rundas.whackyutilities.util.WrappedHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class AutoHammerBlockEntity extends BlockEntity implements MenuProvider {

    public AutoHammerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_HAMMER_BLOCK_ENTITY.get(), pos, state);
    }

    public AutoHammerBlockEntity(BlockPos pos, BlockState state, int tier) {
        this(pos,state);
        this.tier = tier;
    }

    //Items

    private final ItemStackHandler itemHandler = new ItemStackHandler(13);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    //Energy

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(64000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new AHEnergySyncS2CPacket(this.energy, worldPosition));
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AutoHammerBlockEntity pBlockEntity) {
        if(hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
                kraftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(AutoHammerBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        Optional<AutoHammerRecipe> match = level.getRecipeManager().getRecipeFor(AutoHammerRecipe.Type.INSTANCE, inventory, level);
        return match.isPresent() && canInsertIntoOutputSlots(entity, match.get().getResultItem());
    }

    private static boolean canInsertIntoOutputSlots(AutoHammerBlockEntity entity, ItemStack resultItem) {
        Item output = resultItem.getItem();
        int freeSpace = 768;
        for (int i = 1; i < entity.itemHandler.getSlots(); i++) {
            if(!entity.itemHandler.getStackInSlot(i).is(output)){
                freeSpace -= 64;
            }else{
                freeSpace -= entity.itemHandler.getStackInSlot(i).getCount();
            }
        }
        return freeSpace >= resultItem.getCount();
    }

    private static void kraftItem(AutoHammerBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        Optional<AutoHammerRecipe> match = level.getRecipeManager().getRecipeFor(AutoHammerRecipe.Type.INSTANCE, inventory, level);
        if(match.isPresent()) {
            int count = match.get().getResultItem().getCount();
            int i = 1;
            entity.itemHandler.extractItem(0,1, false);
            while (count > 0) {
                int slotCount = entity.itemHandler.getStackInSlot(i).getCount();
                int itemCount = Math.min(64 - slotCount, count);
                entity.itemHandler.setStackInSlot(i, new ItemStack(match.get().getResultItem().getItem(), itemCount));
                count -= itemCount;
                i++;
            }
            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    //Misc Fields

    public int progress;
    public final int maxProgress = 120;
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
        return new TextComponent("Auto Hammer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToClients(new AHEnergySyncS2CPacket(this.energyStorage.getEnergyStored(), worldPosition));
        return new AutoHammerMenu(id, inventory, this);
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
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("auto_hammer.inventory", itemHandler.serializeNBT());
        nbt.putInt("auto_hammer.energy", energyStorage.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("auto_hammer.inventory"));
        energyStorage.setEnergy(nbt.getInt("auto_hammer.energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
