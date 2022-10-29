package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.SkaterPose;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.google.common.collect.ImmutableList;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

public class SkateboardModel extends AdvancedEntityModel<SkateboardEntity> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox board;
    public final AdvancedModelBox frontAxel;
    public final AdvancedModelBox leftFrontWheel;
    public final AdvancedModelBox rightFrontWheel;
    public final AdvancedModelBox backAxel;
    public final AdvancedModelBox leftBackWheel;
    public final AdvancedModelBox rightBackWheel;

    public SkateboardModel() {
        texWidth = 128;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 20.0F, 0.0F);

        board = new AdvancedModelBox(this, "board");
        board.setRotationPoint(0.0F, -5.0F, 0.0F);
        root.addChild(board);
        board.setTextureOffset(0, 0).addBox(-4.0F, 4.0F, -15.0F, 8.0F, 1.0F, 30.0F, 0.0F, false);

        frontAxel = new AdvancedModelBox(this, "frontAxel");
        frontAxel.setRotationPoint(0.0F, 5.0F, 9.5F);
        board.addChild(frontAxel);
        frontAxel.setTextureOffset(59, 4).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        frontAxel.setTextureOffset(59, 1).addBox(-3.0F, 2.0F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        leftFrontWheel = new AdvancedModelBox(this, "leftFrontWheel");
        leftFrontWheel.setRotationPoint(-3.0F, 2.5F, 0.0F);
        frontAxel.addChild(leftFrontWheel);
        leftFrontWheel.setTextureOffset(47, 0).addBox(-2.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        rightFrontWheel = new AdvancedModelBox(this, "rightFrontWeel");
        rightFrontWheel.setRotationPoint(3.0F, 2.5F, 0.0F);
        frontAxel.addChild(rightFrontWheel);
        rightFrontWheel.setTextureOffset(47, 0).addBox(0.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        backAxel = new AdvancedModelBox(this, "backAxel");
        backAxel.setRotationPoint(0.0F, 5.0F, -9.5F);
        board.addChild(backAxel);
        backAxel.setTextureOffset(59, 4).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        backAxel.setTextureOffset(59, 1).addBox(-3.0F, 2.0F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        leftBackWheel = new AdvancedModelBox(this, "leftBackWheel");
        leftBackWheel.setRotationPoint(-3.0F, 2.5F, 0.0F);
        backAxel.addChild(leftBackWheel);
        leftBackWheel.setTextureOffset(47, 0).addBox(-2.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        rightBackWheel = new AdvancedModelBox(this, "rightBackWheel");
        rightBackWheel.setRotationPoint(3.0F, 2.5F, 0.0F);
        backAxel.addChild(rightBackWheel);
        rightBackWheel.setTextureOffset(47, 0).addBox(0.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, board, frontAxel, backAxel, rightFrontWheel, leftFrontWheel, rightBackWheel, leftBackWheel);
    }

    @Override
    public void setupAnim(SkateboardEntity entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch) {
        this.resetToDefaultPose();
        float partialTick = age - entity.tickCount;
        float wheelRot = (float) Math.toRadians(entity.getWheelRot(partialTick));
        float axelUp = (float) (Math.abs(Math.sin(wheelRot)) * 0.5F);
        float maxRoll = (float) Math.toRadians(35);
        float ground = entity.getOnGroundProgress(partialTick);
        float boardRot = (float) Math.toRadians(entity.getViewXRot(partialTick));
        float multi = boardRot < 0 ? 10F : 9F;
        float boardUp = multi * (float) Math.sin(boardRot) * ground;
        wheelRot -= ground * boardRot;
        this.rightBackWheel.rotateAngleX -= wheelRot;
        this.leftBackWheel.rotateAngleX -= wheelRot;
        this.rightFrontWheel.rotateAngleX -= wheelRot;
        this.leftFrontWheel.rotateAngleX -= wheelRot;
        this.frontAxel.rotationPointY -= axelUp;
        this.backAxel.rotationPointY -= axelUp;
        this.root.rotateAngleX -= boardRot;
        this.root.rotationPointY += boardUp;
        this.backAxel.rotateAngleX += boardRot * 0.85F * ground;
        this.rightBackWheel.rotateAngleX -= boardRot * 0.85F * ground;
        this.leftBackWheel.rotateAngleX -= boardRot * 0.85F * ground;
        this.backAxel.rotationPointZ += boardUp * 0.1F;
        this.root.rotateAngleZ = (float) Math.toRadians(entity.getZRot(partialTick));
        this.frontAxel.rotateAngleZ -= Mth.clamp(this.root.rotateAngleZ, -maxRoll, maxRoll);
        this.backAxel.rotateAngleZ -= Mth.clamp(this.root.rotateAngleZ, -maxRoll, maxRoll);
        if(entity.getSkaterPose() == SkaterPose.KICKFLIP){
            this.root.rotateAngleZ += (float) Math.toRadians(age * 40);
        }
        if(entity.isGrinding()){
            this.board.rotationPointZ += 8;
        }
    }

    public void animateItem(SkateboardData data, float f) {
        this.resetToDefaultPose();
        this.root.rotateAngleZ = (float) (f * Math.PI);
    }

    public void animateCreativeTab(float f) {
        float f1 = f * 0.1f;
        this.root.rotateAngleX = (float) (-Math.sin(f1 * 1F) * Math.PI);
        this.root.rotateAngleY = (float) (Math.cos(f1 * 0.6F + Math.PI * 0.5F) * Math.PI);
        this.root.rotateAngleZ = (float) (Math.sin(f1 * 0.3F - Math.PI * 1.5F) * Math.PI);

    }

    public void copyFrom(SkateboardModel other){
        this.root.copyModelAngles(other.root);
        this.board.copyModelAngles(other.board);
        this.frontAxel.copyModelAngles(other.frontAxel);
        this.backAxel.copyModelAngles(other.backAxel);
        this.leftFrontWheel.copyModelAngles(other.leftFrontWheel);
        this.rightFrontWheel.copyModelAngles(other.rightFrontWheel);
        this.leftBackWheel.copyModelAngles(other.leftBackWheel);
        this.rightBackWheel.copyModelAngles(other.rightBackWheel);
    }

    public void hideWheels() {
        this.frontAxel.showModel = false;
        this.backAxel.showModel = false;
        this.leftFrontWheel.showModel = false;
        this.rightFrontWheel.showModel = false;
        this.leftBackWheel.showModel = false;
        this.rightBackWheel.showModel = false;
    }

    public void showWheels() {
        this.frontAxel.showModel = true;
        this.backAxel.showModel = true;
        this.leftFrontWheel.showModel = true;
        this.rightFrontWheel.showModel = true;
        this.leftBackWheel.showModel = true;
        this.rightBackWheel.showModel = true;
    }
}