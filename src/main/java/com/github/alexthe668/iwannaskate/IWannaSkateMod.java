package com.github.alexthe668.iwannaskate;

import com.github.alexthe668.iwannaskate.client.ClientProxy;
import com.github.alexthe668.iwannaskate.client.IWSClientConfig;
import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.client.particle.IWSParticleRegistry;
import com.github.alexthe668.iwannaskate.server.CommonProxy;
import com.github.alexthe668.iwannaskate.server.IWSServerConfig;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.misc.IWSSoundRegistry;
import com.github.alexthe668.iwannaskate.server.network.SkateboardKeyMessage;
import com.github.alexthe668.iwannaskate.server.network.SkateboardPartMessage;
import com.github.alexthe668.iwannaskate.server.recipe.IWSRecipeRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Mod(IWannaSkateMod.MODID)
public class IWannaSkateMod {
    public static final String MODID = "iwannaskate";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static int packetsRegistered = 0;
    public static final SimpleChannel NETWORK_WRAPPER;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final IWSServerConfig COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_CONFIG_SPEC;
    public static final IWSClientConfig CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<IWSServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(IWSServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();
        COMMON_CONFIG_SPEC = serverPair.getRight();
        final Pair<IWSClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(IWSClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }
    static {
        NetworkRegistry.ChannelBuilder channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main_channel"));
        String version = PROTOCOL_VERSION;
        version.getClass();
        channel = channel.clientAcceptedVersions(version::equals);
        version = PROTOCOL_VERSION;
        version.getClass();
        NETWORK_WRAPPER = channel.serverAcceptedVersions(version::equals).networkProtocolVersion(() -> {
            return PROTOCOL_VERSION;
        }).simpleChannel();
    }

    public IWannaSkateMod() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC, "iwannaskate-common.toml");
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC, "iwannaskate-client.toml");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityModelLayers);
        modEventBus.addListener(this::onConfigReloaded);
        IWSItemRegistry.DEF_REG.register(modEventBus);
        IWSRecipeRegistry.DEF_REG.register(modEventBus);
        IWSEntityRegistry.DEF_REG.register(modEventBus);
        IWSEnchantmentRegistry.DEF_REG.register(modEventBus);
        IWSSoundRegistry.DEF_REG.register(modEventBus);
        IWSParticleRegistry.DEF_REG.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PROXY);
        PROXY.init();
    }


    private void clientSetup(FMLClientSetupEvent event) {
        PROXY.clientInit();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, SkateboardPartMessage.class, SkateboardPartMessage::write, SkateboardPartMessage::read, SkateboardPartMessage.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, SkateboardKeyMessage.class, SkateboardKeyMessage::write, SkateboardKeyMessage::read, SkateboardKeyMessage.Handler::handle);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private void setupEntityModelLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        IWSModelLayers.register(event);
    }

    private void onConfigReloaded(final ModConfigEvent.Reloading configEvent) {
        PROXY.reloadConfig();
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }
}
