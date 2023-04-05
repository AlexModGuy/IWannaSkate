package com.github.alexthe668.iwannaskate.client;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.color.BoardColorSampler;
import com.github.alexthe668.iwannaskate.client.gui.SkateManualScreen;
import com.github.alexthe668.iwannaskate.client.model.ModelRootRegistry;
import com.github.alexthe668.iwannaskate.client.particle.*;
import com.github.alexthe668.iwannaskate.client.render.IWSRenderTypes;
import com.github.alexthe668.iwannaskate.client.render.blockentity.SkateboardRackRenderer;
import com.github.alexthe668.iwannaskate.client.render.entity.SkateboardRenderer;
import com.github.alexthe668.iwannaskate.client.render.entity.SkaterSkeletonRenderer;
import com.github.alexthe668.iwannaskate.client.render.entity.WanderingSkaterRenderer;
import com.github.alexthe668.iwannaskate.client.render.item.IWSItemArmorProperties;
import com.github.alexthe668.iwannaskate.client.render.item.IWSItemRenderProperties;
import com.github.alexthe668.iwannaskate.client.render.item.IWSItemstackRenderer;
import com.github.alexthe668.iwannaskate.client.sound.SkateSoundType;
import com.github.alexthe668.iwannaskate.client.sound.SkateboardSound;
import com.github.alexthe668.iwannaskate.server.CommonProxy;
import com.github.alexthe668.iwannaskate.server.blockentity.IWSBlockEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.item.DyeableHatItem;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.potion.IWSEffectRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IWannaSkateMod.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final Map<Integer, SkateboardSound> SKATEBOARD_SOUND_MAP = new HashMap<>();
    protected static final ResourceLocation OVERCAFFENIATED_OVERLAY = new ResourceLocation(IWannaSkateMod.MODID, "textures/gui/overcaffeniated_overlay.png");
    private static final ResourceLocation SKATEBOARD_INDICATOR_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/gui/skateboard_peddle_indicator.png");

    private float prevCameraRoll = 0;

    public static void onTexturesLoaded(TextureStitchEvent.Post event) {
        BoardColorSampler.sampleColorsOnLoad();
    }

    public static void setupItemColors(RegisterColorHandlersEvent.Item event) {
        IWannaSkateMod.LOGGER.info("loaded in item colorizer");
        if(IWSItemRegistry.BEANIE.isPresent()){
            event.register((stack, colorIn) -> colorIn != 0 ? -1 : ((DyeableHatItem) stack.getItem()).getColor(stack), IWSItemRegistry.BEANIE.get());
        }
        if(IWSItemRegistry.SKATER_CAP.isPresent()){
            event.register((stack, colorIn) -> colorIn != 0 ? -1 : ((DyeableHatItem) stack.getItem()).getColor(stack), IWSItemRegistry.SKATER_CAP.get());
        }
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        IWannaSkateMod.LOGGER.debug("Registered particle factories");
        registry.register(IWSParticleRegistry.HALLOWEEN.get(), HalloweenParticle.Factory::new);
        registry.register(IWSParticleRegistry.BEE.get(), BeeParticle.Factory::new);
        registry.register(IWSParticleRegistry.HOVER.get(), new HoverParticle.Factory());
        registry.register(IWSParticleRegistry.SPARKLE.get(), SparkleParticle.Factory::new);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            IWSItemstackRenderer.tick();
        }
    }

    @SubscribeEvent
    public void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event){
        if(Minecraft.getInstance().player.getVehicle() instanceof SkateboardEntity skateboard && IWannaSkateMod.CLIENT_CONFIG.rotateCameraOnBoard.get()){
            float partialTick = Minecraft.getInstance().getPartialTick();
            float targetRot = skateboard.getZRot(partialTick);
            if(Math.abs(targetRot) <= 1.0F){
                targetRot = 0;
            }
            float f = skateboard.approachRotation(prevCameraRoll, targetRot, 1F);

            float f1 = prevCameraRoll + (f - prevCameraRoll) * partialTick;
            prevCameraRoll = f;
            event.setRoll(f * 0.25F);
        }
    }

    @SubscribeEvent
    public void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) && IWannaSkateMod.CLIENT_CONFIG.hideExperienceBar.get() && getClientSidePlayer().getVehicle() instanceof SkateboardEntity skateboard) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.JUMP_BAR.id()) && getClientSidePlayer().getVehicle() instanceof SkateboardEntity skateboard) {
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            if(IWannaSkateMod.CLIENT_CONFIG.showInertiaIndicator.get()){
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
        if (event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id()) && Minecraft.getInstance().player.hasEffect(IWSEffectRegistry.OVERCAFFEINATED.get()) && IWannaSkateMod.CLIENT_CONFIG.overcaffeniatedOverlay.get()) {
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
            RenderSystem.setShaderTexture(0, OVERCAFFENIATED_OVERLAY);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onComputeFOV(ComputeFovModifierEvent event) {
        if (Minecraft.getInstance().player.hasEffect(IWSEffectRegistry.OVERCAFFEINATED.get()) && IWannaSkateMod.CLIENT_CONFIG.overcaffeniatedOverlay.get()) {
            event.setNewFovModifier(event.getFovModifier() + 1);
        }
    }

    @SubscribeEvent
    public void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ModelRootRegistry.INSTANCE);
    }

    public void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientProxy::setupParticles);
        modEventBus.addListener(ClientProxy::setupItemColors);
    }

    public void clientInit() {
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(IWSRenderTypes.SKATEBOARD_GLINT, new BufferBuilder(IWSRenderTypes.SKATEBOARD_GLINT.bufferSize()));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientProxy::onTexturesLoaded);
        EntityRenderers.register(IWSEntityRegistry.SKATEBOARD.get(), SkateboardRenderer::new);
        EntityRenderers.register(IWSEntityRegistry.SKATER_SKELETON.get(), SkaterSkeletonRenderer::new);
        EntityRenderers.register(IWSEntityRegistry.WANDERING_SKATER.get(), WanderingSkaterRenderer::new);
        BlockEntityRenderers.register(IWSBlockEntityRegistry.SKATEBOARD_RACK.get(), SkateboardRackRenderer::new);
    }

    public Object getISTERProperties() {
        return new IWSItemRenderProperties();
    }

    public Object getArmorRenderProperties() {
        return new IWSItemArmorProperties();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public boolean isKeyDown(int keyType) {
        if (keyType == 0) {
            return Minecraft.getInstance().options.keySprint.isDown();
        }
        if (keyType == 1) {
            return Screen.hasShiftDown();
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

    public void openBookGUI(ItemStack book) {
        Minecraft.getInstance().setScreen(new SkateManualScreen(book));

    }
}
