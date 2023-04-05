package com.github.alexthe668.iwannaskate.client.model;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SkateModelMapping {
    private static final List<String> EMPTY = new ArrayList<>();
    private EntityType<?> entityType;
    private TagKey<EntityType<?>> entityTypeTag;
    private final List<String> body;
    private final List<String> head;
    private final List<String> leftArm;
    private final List<String> rightArm;
    private final List<String> leftLeg;
    private final List<String> rightLeg;
    private final float strength;
    private final float speed;
    private final boolean faceForwards;

    public SkateModelMapping(@Nullable EntityType<?> entityType, @Nullable TagKey<EntityType<?>> entityTypeTag, List<String> body, List<String> head, List<String> leftArm, List<String> rightArm, List<String> leftLeg, List<String> rightLeg, float strength, float speed, boolean faceForwards) {
        this.entityType = entityType;
        this.entityTypeTag = entityTypeTag;
        this.body = body;
        this.head = head;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.speed = speed;
        this.strength = strength;
        this.faceForwards = faceForwards;
    }

    private static List<String> deserializeParts(JsonObject jsonobject, String partIdentifier) {
        List<String> parts = Lists.newArrayList();
        if (jsonobject.has(partIdentifier)) {
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonobject, partIdentifier)) {
                parts.add(element.getAsString());
            }
        }
        return parts;
    }

    public List<String> getBody() {
        return body;
    }

    public List<String> getHead() {
        return head;
    }

    public List<String> getLeftArm() {
        return leftArm;
    }

    public List<String> getRightArm() {
        return rightArm;
    }

    public List<String> getLeftLeg() {
        return leftLeg;
    }

    public List<String> getRightLeg() {
        return rightLeg;
    }

    public boolean matchesEntityType(EntityType<?> type) {
        if(this.entityTypeTag != null){
            return type.is(this.entityTypeTag);
        }else if(this.entityType != null){
            return type == entityType;
        }
        return false;
    }


    public float getStrength() {
        return strength;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isFaceForwards() {
        return faceForwards;
    }

    public List<String> getListForType(ModelPartType type) {
        return switch (type) {
            case BODY -> getBody();
            case HEAD -> getHead();
            case LEFT_ARM -> getLeftArm();
            case LEFT_LEG -> getLeftLeg();
            case RIGHT_ARM -> getRightArm();
            case RIGHT_LEG -> getRightLeg();
        };
    }

    public static class Deserializer implements JsonDeserializer<SkateModelMapping> {

        @Override
        public SkateModelMapping deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            String entityTypeString = jsonobject.get("entity_type").getAsString();
            EntityType<?> entityType = null;
            TagKey<EntityType<?>> tagkey = null;
            if (entityTypeString.startsWith("#")) {
                ResourceLocation readStr = new ResourceLocation(entityTypeString.substring(1));
                tagkey = TagKey.create(Registries.ENTITY_TYPE, readStr);
            } else {
                ResourceLocation readsStr= new ResourceLocation(entityTypeString);
                if(!ForgeRegistries.ENTITY_TYPES.containsKey(readsStr)){
                    throw new JsonParseException("missing entity type");
                }
                entityType = ForgeRegistries.ENTITY_TYPES.getValue(readsStr);
            }
            List<String> body = deserializeParts(jsonobject, "body");
            List<String> head = deserializeParts(jsonobject, "head");
            List<String> rightArm = deserializeParts(jsonobject, "right_arm");
            List<String> leftArm = deserializeParts(jsonobject, "left_arm");
            List<String> rightLeg = deserializeParts(jsonobject, "right_leg");
            List<String> leftLeg = deserializeParts(jsonobject, "left_leg");
            float speed = jsonobject.get("animation_speed_modifier").getAsFloat();
            float strength = jsonobject.get("animation_strength_modifier").getAsFloat();
            boolean face = jsonobject.get("face_forwards").getAsBoolean();
            return new SkateModelMapping(entityType, tagkey, body, head, rightArm, leftArm, rightLeg, leftLeg, strength, speed, face);
        }
    }
}
