package net.rundas.whackyutilities.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.rundas.whackyutilities.entity.AutoHammerBlockEntity;
import net.rundas.whackyutilities.entity.PoweredCrucibleBlockEntity;
import net.rundas.whackyutilities.screen.AutoHammerMenu;
import net.rundas.whackyutilities.screen.PoweredCrucibleMenu;

import java.util.function.Supplier;

public class AHEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public AHEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public AHEnergySyncS2CPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof AutoHammerBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof AutoHammerMenu menu &&
                        menu.blockEntity.getBlockPos().equals(pos)) {
                    menu.setEnergyLevel(energy);
                }
            }
        });
        return true;
    }
}