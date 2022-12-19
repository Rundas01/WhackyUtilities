package net.rundas.whackyutilities.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.rundas.whackyutilities.entity.PoweredCrucibleBlockEntity;
import net.rundas.whackyutilities.screen.PoweredCrucibleMenu;

import java.util.function.Supplier;

public class PCValueSyncS2CPacket {
    private final int value;
    private final BlockPos pos;

    public PCValueSyncS2CPacket(int val, BlockPos pos) {
        this.value = val;
        this.pos = pos;
    }

    public PCValueSyncS2CPacket(FriendlyByteBuf buf) {
        this.value = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(value);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof PoweredCrucibleBlockEntity blockEntity) {
                blockEntity.setStone(this.value);

                if(Minecraft.getInstance().player.containerMenu instanceof PoweredCrucibleMenu menu &&
                        menu.blockEntity.getBlockPos().equals(pos)) {
                    menu.setStone(this.value);
                }
            }
        });
        return true;
    }
}
