package com.github.alexthe668.iwannaskate.client.render.item;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.ClientProxy;
import com.github.alexthe668.iwannaskate.client.model.SkateboardModel;
import com.github.alexthe668.iwannaskate.client.render.entity.SkateboardTexturer;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IWSItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final SkateboardModel SKATEBOARD_MODEL = new SkateboardModel();
    private static Map<ItemStack, Float> prevFlipProgresses = new HashMap<>();
    private static Map<ItemStack, Float> flipProgresses = new HashMap<>();
    private static final float FLIP_TIME = 5F;
    private static SkateboardData randomSkateData;
    private static int ticks = 0;

    public IWSItemstackRenderer() {
        super(null, null);
    }

    public static void tick(){
        prevFlipProgresses.putAll(flipProgresses);
        if(IWannaSkateMod.CLIENT_CONFIG.flipBoardItems.get()) {
            ItemStack currentMouseOver = ((ClientProxy) (IWannaSkateMod.PROXY)).lastHoveredItem;
            if (currentMouseOver != null) {
                float prev = flipProgresses.getOrDefault(currentMouseOver, 0F);
                if (prev < FLIP_TIME) {
                    flipProgresses.put(currentMouseOver, prev + 1);
                }
            }

            if (!flipProgresses.isEmpty()) {
                Iterator<Map.Entry<ItemStack, Float>> it = flipProgresses.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<ItemStack, Float> next = it.next();
                    float progress = next.getValue();
                    if (currentMouseOver == null || next.getKey() != currentMouseOver) {
                        if (progress == 0) {
                            it.remove();
                        } else {
                            next.setValue(progress - 1);
                        }
                    }
                }
            }
            ((ClientProxy) (IWannaSkateMod.PROXY)).lastHoveredItem = null;
        }
        if (ticks % 40 == 0) {
            randomSkateData = SkateboardMaterials.generateRandomData(Minecraft.getInstance().level.random);
        }
        ticks++;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(itemStack.getItem() instanceof BaseSkateboardItem){
            float f = 0.0F;
            boolean isCreativeTab = false;
            if(itemStack.is(IWSItemRegistry.SKATEBOARD.get()) && transformType == ItemTransforms.TransformType.GUI){
                float prev = prevFlipProgresses.getOrDefault(itemStack, 0F);
                float current = flipProgresses.getOrDefault(itemStack, 0F);
                float lerped = prev + (current - prev) * Minecraft.getInstance().getFrameTime();
                f =  lerped / FLIP_TIME;
            }
            SkateboardData data = SkateboardData.fromStack(itemStack);
            if(itemStack.getTag() != null && itemStack.getTag().getBoolean("IsCreativeTab") && randomSkateData != null){
                data = randomSkateData;
                isCreativeTab = true;
            }
            poseStack.pushPose();
            poseStack.translate(0, 0.45F, 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-180));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            if(isCreativeTab){
                poseStack.translate(0, -1F, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(360 * ticks / 40));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(65));
                poseStack.translate(0, 0, -2F);
            }
            SKATEBOARD_MODEL.animateItem(data, f);
            if(itemStack.is(IWSItemRegistry.SKATEBOARD_DECK.get())){
                SkateboardTexturer.renderDeck(SKATEBOARD_MODEL, data, poseStack, bufferIn, combinedLightIn, itemStack.hasFoil());
            }else{
                SkateboardTexturer.renderBoard(SKATEBOARD_MODEL, data, poseStack, bufferIn, combinedLightIn, itemStack.hasFoil());
            }
            poseStack.popPose();
        }
    }
}
