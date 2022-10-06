package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.particle.IWSParticleRegistry;
import com.github.alexthe668.iwannaskate.server.misc.IWSCreativeTab;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public enum SkateboardWheels {
    DEFAULT,
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK,
    ENDERPEARL(true),
    FLAME(true),
    SOUL_FLAME(true),
    RAINBOW(true),
    SPOOKY(false),
    SNOWY(false),
    SHOCKING(true),
    HONEY(true),
    AESTHETIC(true);

    private final ResourceLocation texture;
    private final boolean isTrade;
    private RegistryObject<Item> itemRegistryObject;

    SkateboardWheels() {
        this(false);
    }

    SkateboardWheels(boolean isTrade){
        this.texture = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/wheels/wheels_" + this.name().toLowerCase() + ".png");
        this.isTrade = isTrade;
    }

    public static void init(){
        for(SkateboardWheels wheelType : SkateboardWheels.values()){
            String id = wheelType == DEFAULT ? "skateboard_wheels" : "skateboard_wheels_" + wheelType.name().toLowerCase();
            wheelType.itemRegistryObject = IWSItemRegistry.DEF_REG.register(id, () -> new SkateboardWheelsItem(new Item.Properties().tab(IWSCreativeTab.INSTANCE), wheelType));
        }
    }

    public static SkateboardWheels fromItem(Item item){
        if(item instanceof SkateboardWheelsItem wheelsItem){
            return wheelsItem.getWheelType();
        }
        return DEFAULT;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public RegistryObject<Item> getItemRegistryObject() {
        return itemRegistryObject;
    }

    public boolean isTrade(){
        return isTrade;
    }

    @Nullable
    public ParticleOptions getWheelParticles(){
        if(this == FLAME){
            return ParticleTypes.FLAME;
        }
        if(this == SOUL_FLAME){
            return ParticleTypes.SOUL_FIRE_FLAME;
        }
        if(this == ENDERPEARL){
            return ParticleTypes.PORTAL;
        }
        if(this == SPOOKY){
            return IWSParticleRegistry.HALLOWEEN.get();
        }
        if(this == SNOWY){
            return ParticleTypes.SNOWFLAKE;
        }
        if(this == HONEY){
            return IWSParticleRegistry.BEE.get();
        }
        return null;
    }

    public boolean hasTrail() {
        return this == RAINBOW || this == AESTHETIC;
    }

    public boolean isEmissive() {
        return this == FLAME || this == SOUL_FLAME || this == RAINBOW || this == AESTHETIC;
    }

    public float getParticleChancePerTick() {
        if(this == FLAME || this == SOUL_FLAME || this == ENDERPEARL){
            return 0.3F;
        }
        if(this == SPOOKY || this == SNOWY){
            return 0.1F;
        }
        if(this == HONEY){
            return 0.03F;
        }
        return 0;
    }
}
