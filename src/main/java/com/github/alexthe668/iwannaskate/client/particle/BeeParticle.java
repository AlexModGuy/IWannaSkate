package com.github.alexthe668.iwannaskate.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeeParticle extends TextureSheetParticle {

    private SpriteSet spriteSet;
    private final double xTarget;
    private final double yTarget;
    private final double zTarget;

    protected BeeParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 0.5F + world.random.nextFloat() * 0.3F;
        this.hasPhysics = true;
        this.xTarget = xSpeed;
        this.yTarget = ySpeed;
        this.zTarget = zSpeed;
        this.spriteSet = spriteSet;
        this.lifetime = (int)(Math.random() * 10.0D) + 40;
        this.friction = 0.8F;
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int sprite = this.age % 4 >= 2 ? 1 : 0;
        this.setSprite(spriteSet.get(sprite,1 ));
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = 1F / this.lifetime;
            double xT = (xTarget - x) * 0.001F;
            double yT = (yTarget - y) * 0.001F;
            double zT = (zTarget - z) * 0.001F;

            this.xd += xT + random.nextGaussian() * 0.015F;
            this.yd += yT + random.nextGaussian() * 0.015F;
            if(this.onGround){
                yd += 0.3F;
            }
            this.zd += zT + random.nextGaussian() * 0.015F;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double)this.friction;
            this.yd *= (double)this.friction;
            this.zd *= (double)this.friction;

        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BeeParticle heartparticle = new BeeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            heartparticle.setSprite(spriteSet.get(0,1));
            return heartparticle;
        }
    }
}
