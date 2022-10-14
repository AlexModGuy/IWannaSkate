package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.util.*;

public class ModelRootRegistry implements ResourceManagerReloadListener {

    //for loading in baked models
    private static Map<ResourceLocation, ModelPart> BAKED_ID_TO_ROOT_PART = new HashMap<>();
    private static Map<EntityModel, ResourceLocation> MODEL_TO_BAKED_ID = new HashMap<>();
    //for animating and mapping baked models
    private static List<SkateModelMapping> ALL_MAPPINGS = new ArrayList<>();
    private static Map<EntityModel, SkateModelParts> MODEL_TO_ANIMATING_PARTS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(SkateModelMapping.class, new SkateModelMapping.Deserializer()).create();
    private ResourceLocation lastBaked = null;
    public static ModelRootRegistry INSTANCE = new ModelRootRegistry();

    private ModelRootRegistry(){
        reload(Minecraft.getInstance().getResourceManager());
    }

    public static void onCallBakeLayer(ModelLayerLocation modelLocation, ModelPart root){
        if(modelLocation.getLayer().equals("main")){
            BAKED_ID_TO_ROOT_PART.put(modelLocation.getModel(), root);
            INSTANCE.lastBaked = modelLocation.getModel();
        }
    }

    public static void onConstructRenderer(EntityModel model){
        if(INSTANCE.lastBaked != null){
            MODEL_TO_BAKED_ID.put(model, INSTANCE.lastBaked);
            INSTANCE.lastBaked = null;
        }
    }

    private void reload(ResourceManager manager){
        ALL_MAPPINGS.clear();
        MODEL_TO_ANIMATING_PARTS.clear();
        int emptyMappingsCount = 0;
        Collection<ResourceLocation> collection = manager.listResources("skate_model_mappings", (resourceLocation) -> {
            return resourceLocation.getPath().endsWith(".json");
        }).keySet();
        for(ResourceLocation res : collection){
            try {
                Optional<Resource> resource = manager.getResource(res);
                if (resource.isPresent()) {
                    BufferedReader inputstream = ((Resource)resource.get()).openAsReader();
                    try {
                        SkateModelMapping mapping = GsonHelper.fromJson(GSON, inputstream, SkateModelMapping.class);
                        ALL_MAPPINGS.add(mapping);
                    }catch (JsonParseException parseException){
                        emptyMappingsCount++;
                    }
                }
            } catch (Exception e) {
                IWannaSkateMod.LOGGER.error("Could not load skate model mappings for " + res);
                e.printStackTrace();
            }
        }
        IWannaSkateMod.LOGGER.info("Loaded {} model mappings for skating animations", ALL_MAPPINGS.size());
        if(emptyMappingsCount > 0 ){
            IWannaSkateMod.LOGGER.info("Skipping {} model mappings because their entity type does not exist, likely because a compatible mod is not installed", emptyMappingsCount);

        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        reload(manager);
    }

    @Nullable
    private ModelPart getRootForModel(EntityModel model){
        if(MODEL_TO_BAKED_ID.containsKey(model)){
            return BAKED_ID_TO_ROOT_PART.get(MODEL_TO_BAKED_ID.get(model));
        }
        return null;
    }

    public List<ModelPartWrapper> getVanillaModelPartFromRoot(EntityModel model, ModelPart root, SkateModelMapping mapping, ModelPartType type){
        List<ModelPartWrapper> parts = Lists.newArrayList();
        for(String partName : mapping.getListForType(type)){
            try{
                if(type == ModelPartType.HEAD && model instanceof HasHeadModelParts listModel){
                    listModel.getHeadModelParts().forEach(part -> parts.add(new ModelPartWrapper((ModelPart) part)));
                }else{
                    boolean isRoot = partName.equals("root") && type == ModelPartType.BODY;
                    ModelPart part = isRoot ? root : getNestedChildPart(root, partName);
                    if(part != null){
                        parts.add(new ModelPartWrapper(part, isRoot));
                    }
                }
            }catch (NoSuchElementException exception){
                IWannaSkateMod.LOGGER.warn("Error animating model {} : {}", model.getClass(), exception.getMessage());
            }
        }

        return parts;
    }

    public List<ModelPartWrapper> getAdvancedModelPartFromRoot(AdvancedEntityModel model, SkateModelMapping mapping, ModelPartType type){
        List<ModelPartWrapper> parts = Lists.newArrayList();
        for(String partName : mapping.getListForType(type)){
            try{
                Iterator<AdvancedModelBox> itr = model.getAllParts().iterator();
                while(itr.hasNext()){
                    AdvancedModelBox box = itr.next();
                    if(box.boxName != null && box.boxName.equals(partName)){
                        parts.add(new ModelPartWrapper(box));
                    }
                }
            }catch (NoSuchElementException exception){
                IWannaSkateMod.LOGGER.warn("Error animating model:" + exception.getMessage());
            }
        }
        return parts;
    }


    private ModelPart getNestedChildPart(ModelPart root, String partName) {
        if(root.hasChild(partName)){
            return root.getChild(partName);
        }else{
            List<ModelPart> allParts = root.getAllParts().toList();
            for(ModelPart inAll : allParts){
                if(inAll.hasChild(partName)){
                    return inAll.getChild(partName);
                }
            }
        }
        return null;
    }

    @Nullable
    public SkateModelParts getAnimationData(EntityModel model, EntityType<?> entityType){
        MODEL_TO_ANIMATING_PARTS.clear();
        if(MODEL_TO_ANIMATING_PARTS.containsKey(model)){
            return MODEL_TO_ANIMATING_PARTS.get(model);
        }else{
            SkateModelMapping mapping = getMappingForEntity(entityType);
            int i = 0;
            float strength = 1.0F;
            float speed = 1.0F;
            boolean faceForwards = false;
            ModelPartWrapper[][] allPartsForAnim = new ModelPartWrapper[6][];
            if(mapping != null){
                strength = mapping.getStrength();
                speed = mapping.getSpeed();
                faceForwards = mapping.isFaceForwards();
                if(model instanceof AdvancedEntityModel advancedEntityModel){
                    for(ModelPartType partType : ModelPartType.values()) {
                        List<ModelPartWrapper> modelParts = getAdvancedModelPartFromRoot(advancedEntityModel, mapping, partType);
                        allPartsForAnim[i++] = modelParts.toArray(new ModelPartWrapper[0]);
                    }
                }else{
                    ModelPart root = getRootForModel(model);
                    if(root != null){
                        for(ModelPartType partType : ModelPartType.values()){
                            List<ModelPartWrapper> modelParts = getVanillaModelPartFromRoot(model, root, mapping, partType);
                            allPartsForAnim[i++] = modelParts.toArray(new ModelPartWrapper[0]);
                        }
                    }
                }
                SkateModelParts animatedParts = new SkateModelParts(allPartsForAnim[0], allPartsForAnim[1], allPartsForAnim[2], allPartsForAnim[3], allPartsForAnim[4], allPartsForAnim[5], strength, speed, faceForwards);
                MODEL_TO_ANIMATING_PARTS.put(model, animatedParts);
                return animatedParts;
            }else{
                MODEL_TO_ANIMATING_PARTS.put(model, null);
                return null;
            }
        }
    }

    public SkateModelMapping getMappingForEntity(EntityType<?> entityType){
        SkateModelMapping mapping = null;
        for(SkateModelMapping compareTo : ALL_MAPPINGS){
            if(compareTo.matchesEntityType(entityType)){
                mapping = compareTo;
                break;
            }
        }
        return mapping;
    }


    public record SkateModelParts(ModelPartWrapper[] body, ModelPartWrapper[] head, ModelPartWrapper[] rightArm, ModelPartWrapper[] leftArm, ModelPartWrapper[] rightLeg, ModelPartWrapper[] leftLeg, float strength, float speed, boolean faceForwards){};
}
