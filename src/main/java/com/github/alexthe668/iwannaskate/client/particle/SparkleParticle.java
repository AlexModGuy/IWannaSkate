package com.github.alexthe668.iwannaskate.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SparkleParticle extends TextureSheetParticle {

    protected SparkleParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 0.7F + world.random.nextFloat() * 0.2F;
        this.lifetime = 2;
        this.gravity = 0.01F;
        this.hasPhysics = true;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setAlpha(0.5F);
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double)0.75F;
            this.yd *= (double)0.75F;
            this.zd *= (double)0.75F;

        }
    }

    public int getLightColor(float partialTicks) {
        float f = ((float)this.age + partialTicks) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTicks);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)((1 - f) * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SparkleParticle heartparticle = new SparkleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            heartparticle.pickSprite(this.spriteSet);
            return heartparticle;
        }
    }
}
