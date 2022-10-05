package com.github.alexthe668.iwannaskate.client;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.color.BoardColorSampler;
import com.github.alexthe668.iwannaskate.client.model.ModelRootRegistry;
import com.github.alexthe668.iwannaskate.client.particle.BeeParticle;
import com.github.alexthe668.iwannaskate.client.particle.HalloweenParticle;
import com.github.alexthe668.iwannaskate.client.particle.IWSParticleRegistry;
import com.github.alexthe668.iwannaskate.client.render.IWSRenderTypes;
import com.github.alexthe668.iwannaskate.client.render.entity.SkateboardRenderer;
import com.github.alexthe668.iwannaskate.client.render.entity.SkaterSkeletonRenderer;
import com.github.alexthe668.iwannaskate.client.render.item.IWSItemRenderProperties;
import com.github.alexthe668.iwannaskate.client.render.item.IWSItemstackRenderer;
import com.github.alexthe668.iwannaskate.client.sound.SkateSoundType;
import com.github.alexthe668.iwannaskate.client.sound.SkateboardSound;
import com.github.alexthe668.iwannaskate.server.CommonProxy;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.SlowableEntity;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IWannaSkateMod.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public ItemStack lastHoveredItem = null;
    private static final ResourceLocation SKATEBOARD_INDICATOR_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/gui/skateboard_peddle_indicator.png");
    public static final Map<Integer, SkateboardSound> SKATEBOARD_SOUND_MAP = new HashMap<>();

    public static void onTexturesLoaded(TextureStitchEvent.Post event) {
        BoardColorSampler.sampleColorsOnLoad();
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if(IWannaSkateMod.COMMON_CONFIG.enableSlowMotion.get()){
                int ticksPerSecond = Minecraft.getInstance().player == null ? 20 : ((SlowableEntity)Minecraft.getInstance().player).getTickRate();
                Minecraft.getInstance().timer.msPerTick = 1000.0F / ticksPerSecond;
            }
            IWSItemstackRenderer.tick();
        }
    }

    @SubscribeEvent
    public void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        if (event.getItemStack().getItem() instanceof BaseSkateboardItem skateboardItem && skateboardItem.canFlipInInventory(event.getItemStack()) && IWannaSkateMod.CLIENT_CONFIG.flipBoardItems.get()) {
            lastHoveredItem = event.getItemStack();
        }else{
            lastHoveredItem = null;
        }
    }

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if(event.getOverlay().id().equals(VanillaGuiOverlay.JUMP_BAR.id()) && IWannaSkateMod.CLIENT_CONFIG.showInertiaIndicator.get() && getClientSidePlayer().getVehicle() instanceof SkateboardEntity skateboard){
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int j = screenWidth / 2 - IWannaSkateMod.CLIENT_CONFIG.inertiaIndicatorX.get();
            int k = screenHeight - IWannaSkateMod.CLIENT_CONFIG.inertiaIndicatorY.get();
            float f = skateboard.getForwards() / skateboard.getMaxForwardsTicks();
            event.getPoseStack().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SKATEBOARD_INDICATOR_TEXTURE);
            GuiComponent.blit(event.getPoseStack(), j, k, 50, 0, 0, 29, 9, 64, 64);
            GuiComponent.blit(event.getPoseStack(), j, k, 50, 0, 9, Math.round(29 * f), 9, 64, 64);
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ModelRootRegistry.INSTANCE);
    }

    public void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientProxy::setupParticles);
    }

    public void clientInit() {
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(IWSRenderTypes.SKATEBOARD_GLINT, new BufferBuilder(IWSRenderTypes.SKATEBOARD_GLINT.bufferSize()));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientProxy::onTexturesLoaded);
        EntityRenderers.register(IWSEntityRegistry.SKATEBOARD.get(), SkateboardRenderer::new);
        EntityRenderers.register(IWSEntityRegistry.SKATER_SKELETON.get(), SkaterSkeletonRenderer::new);
    }

    public Object getISTERProperties() {
        return new IWSItemRenderProperties();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public boolean isKeyDown(int keyType) {
        if(keyType == 0){
            return Minecraft.getInstance().options.keySprint.isDown();
        }
        return false;
    }

    @Override
    public void onEntityStatus(Entity entity, byte updateKind) {
        if (entity instanceof SkateboardEntity skateboard && entity.isAlive() && updateKind == 67 && IWannaSkateMod.CLIENT_CONFIG.skateboardLoopSounds.get()) {
            SkateboardSound sound;
            if (SKATEBOARD_SOUND_MAP.get(entity.getId()) == null || SKATEBOARD_SOUND_MAP.get(entity.getId()).isDifferentBoard(entity)) {
                sound = new SkateboardSound(SkateSoundType.getForSkateboard(skateboard), 0.0F, skateboard);
                SKATEBOARD_SOUND_MAP.put(entity.getId(), sound);
            } else {
                sound = SKATEBOARD_SOUND_MAP.get(entity.getId());
            }
            if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound()) {
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
    }

    public void reloadConfig() {
        Minecraft.getInstance().timer.msPerTick = 50.0F;
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        IWannaSkateMod.LOGGER.debug("Registered particle factories");
        registry.register(IWSParticleRegistry.HALLOWEEN.get(), HalloweenParticle.Factory::new);
        registry.register(IWSParticleRegistry.BEE.get(), BeeParticle.Factory::new);
    }
}
