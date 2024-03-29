package com.github.alexthe668.iwannaskate.client.render.item;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.gui.SkateManualScreen;
import com.github.alexthe668.iwannaskate.client.model.SkateboardModel;
import com.github.alexthe668.iwannaskate.client.render.entity.SkateboardTexturer;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IWSItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final SkateboardModel SKATEBOARD_MODEL = new SkateboardModel();
    private static Map<ItemStack, Float> prevFlipProgresses = new HashMap<>();
    private static Map<ItemStack, Float> flipProgresses = new HashMap<>();
    private static final float FLIP_TIME = 5F;
    private static SkateboardData randomSkateData;
    private static int ticks = 0;

    private static RandomSource randomSource = RandomSource.createThreadSafe();

    public IWSItemstackRenderer() {
        super(null, null);
    }

    public static void tick() {
        if (ticks % 40 == 0) {
            Set<Item> set;
            if(SkateboardMaterials.isLoaded()){
                set = SkateboardMaterials.getSkateboardMaterials();
            }else{
                set = Set.of(Items.OAK_SLAB);
            }
            randomSkateData = SkateboardMaterials.generateRandomData(set, randomSource, false);
        }
        ticks++;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (itemStack.getItem() instanceof BaseSkateboardItem skateboardItem) {
            boolean isCreativeTab = false;
            float f = 0.0F;
            if (itemStack.is(IWSItemRegistry.SKATEBOARD.get()) && transformType == ItemDisplayContext.GUI && IWannaSkateMod.CLIENT_CONFIG.flipBoardItems.get()) {
                f = Citadel.PROXY.getMouseOverProgress(itemStack);
                if (Minecraft.getInstance().screen instanceof SkateManualScreen && skateboardItem.canFlipInInventory(itemStack)) {
                    f = 1.0F;
                }
            }
            SkateboardData data = SkateboardData.fromStack(itemStack);
            if (itemStack.getTag() != null && itemStack.getTag().getBoolean("IsCreativeTab") && randomSkateData != null) {
                data = randomSkateData;
                isCreativeTab = true;
            }
            poseStack.pushPose();
            poseStack.translate(0, 0.45F, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            SKATEBOARD_MODEL.animateItem(data, f);
            if (isCreativeTab) {
                float lerpTicks = ticks;
                if(!Minecraft.getInstance().isPaused()){
                    lerpTicks += Minecraft.getInstance().getFrameTime();
                }
                SKATEBOARD_MODEL.animateCreativeTab(lerpTicks);
            }
            if (itemStack.is(IWSItemRegistry.SKATEBOARD_DECK.get())) {
                SkateboardTexturer.renderDeck(SKATEBOARD_MODEL, data, poseStack, bufferIn, combinedLightIn, itemStack.hasFoil());
            } else {
                SkateboardTexturer.renderBoard(SKATEBOARD_MODEL, data, poseStack, bufferIn, combinedLightIn, itemStack.hasFoil());
            }
            poseStack.popPose();
        }
    }
}
