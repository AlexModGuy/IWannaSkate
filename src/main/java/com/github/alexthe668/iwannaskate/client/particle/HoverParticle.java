package com.github.alexthe668.iwannaskate.client.particle;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.model.HoverParticleModel;
import com.github.alexthe668.iwannaskate.client.render.IWSRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoverParticle extends Particle {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/particle/hover.png");
    private static final RenderType HOVER_RENDER_TYPE = IWSRenderTypes.getHover(TEXTURE);
    private static final HoverParticleModel MODEL = new HoverParticleModel();
    private float size;
    private float prevSize;
    private float prevAlpha;

    private float alphaDecrease;

    private double yaw;
    private double pitch;
    private double prevPitch;


    private HoverParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.setSize(1F, 0.1F);
        this.alpha = 1F;
        this.gravity = 0.02F;
        this.xd = 0;
        this.yd = motionY;
        this.zd = 0;
        this.yaw = motionX;
        this.pitch = motionZ * 90;
        this.lifetime = 7;
        this.alphaDecrease = 1F / (float) Math.max(this.lifetime, 1F);
        this.size = 1.0F;

    }

    public void tick() {
        super.tick();
        this.prevSize = size;
        this.prevAlpha = alpha;
        this.prevPitch = pitch;
        this.xd *= 0.8D;
        this.yd *= 0.8D;
        this.zd *= 0.8D;
        if (this.alpha > 0.0F) {
            this.alpha = Math.max(this.alpha - alphaDecrease, 0.0F);
        }
        if (this.size > 0.5F) {
            this.size -= 0.1F;
        }
        if (this.pitch > 0) {
            this.pitch = Math.max(0, pitch - 5F);
        }

        if (this.pitch < 0) {
            this.pitch = Math.min(0, pitch + 5F);
        }
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        float alphaLerp = prevAlpha + partialTick * (alpha - prevAlpha);
        float sizeLerp = prevSize + partialTick * (size - prevSize);
        double pitchLerp = prevPitch + partialTick * (pitch - prevPitch);
        float colorMod = 1 - (sizeLerp * 2F - 1F);
        float r = colorMod;
        float g = 0.75F + colorMod * 0.25F;
        float b = 0.95F + colorMod * 0.05F;
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer1 = multibuffersource$buffersource.getBuffer(HOVER_RENDER_TYPE);
        PoseStack posestack = new PoseStack();
        posestack.translate(f, f1, f2);
        posestack.mulPose(Axis.YN.rotationDegrees((float) yaw));
        posestack.mulPose(Axis.XP.rotationDegrees((float) pitchLerp));
        posestack.scale(sizeLerp, sizeLerp, sizeLerp);
        int j = 240;
        MODEL.renderToBuffer(posestack, vertexConsumer1, j, OverlayTexture.NO_OVERLAY, r, g, b, alphaLerp);
        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {


        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HoverParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
