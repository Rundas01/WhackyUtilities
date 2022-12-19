package net.rundas.whackyutilities.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.rundas.whackyutilities.WhackyUtilities;
import net.rundas.whackyutilities.networking.packet.*;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(WhackyUtilities.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ExampleC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ExampleC2SPacket::new)
                .encoder(ExampleC2SPacket::toBytes)
                .consumer(ExampleC2SPacket::handle)
                .add();

        net.messageBuilder(PCEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PCEnergySyncS2CPacket::new)
                .encoder(PCEnergySyncS2CPacket::toBytes)
                .consumer(PCEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(PCValueSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PCValueSyncS2CPacket::new)
                .encoder(PCValueSyncS2CPacket::toBytes)
                .consumer(PCValueSyncS2CPacket::handle)
                .add();

        net.messageBuilder(PCFluidSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PCFluidSyncS2CPacket::new)
                .encoder(PCFluidSyncS2CPacket::toBytes)
                .consumer(PCFluidSyncS2CPacket::handle)
                .add();

        net.messageBuilder(CValueSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CValueSyncS2CPacket::new)
                .encoder(CValueSyncS2CPacket::toBytes)
                .consumer(CValueSyncS2CPacket::handle)
                .add();

        net.messageBuilder(CFluidSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CFluidSyncS2CPacket::new)
                .encoder(CFluidSyncS2CPacket::toBytes)
                .consumer(CFluidSyncS2CPacket::handle)
                .add();

        net.messageBuilder(AHEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AHEnergySyncS2CPacket::new)
                .encoder(AHEnergySyncS2CPacket::toBytes)
                .consumer(AHEnergySyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
