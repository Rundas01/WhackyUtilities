package net.rundas.whackyutilities.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.rundas.whackyutilities.entity.CrucibleBlockEntity;
import net.rundas.whackyutilities.screen.CrucibleMenu;

import java.util.function.Supplier;

public class CValueSyncS2CPacket {
    private final int value;
    private final BlockPos pos;

    public CValueSyncS2CPacket(int val, BlockPos pos) {
        this.value = val;
        this.pos = pos;
    }

    public CValueSyncS2CPacket(FriendlyByteBuf buf) {
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
            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof CrucibleBlockEntity blockEntity) {
                blockEntity.setStone(this.value);

                if(Minecraft.getInstance().player.containerMenu instanceof CrucibleMenu menu &&
                        menu.blockEntity.getBlockPos().equals(pos)) {
                    menu.setStone(this.value);
                }
            }
        });
        return true;
    }
}
