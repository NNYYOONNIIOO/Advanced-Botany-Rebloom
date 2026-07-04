package ab.client.model.armor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelArmorNebula extends ModelBiped {
    private final ModelRenderer head;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;
    private final ModelRenderer cube_r12;
    private final ModelRenderer cube_r13;
    private final ModelRenderer cube_r14;
    private final ModelRenderer cube_r15;
    private final ModelRenderer chestplate;
    private final ModelRenderer cube_r16;
    private final ModelRenderer cube_r4;
    private final ModelRenderer cube_r4_r1;
    private final ModelRenderer cube_r5;
    private final ModelRenderer cube_r5_r1;
    private final ModelRenderer cube_r6;
    private final ModelRenderer cube_r7;
    private final ModelRenderer cube_r7_r1;
    private final ModelRenderer cube_r7_r2;
    private final ModelRenderer lArm;
    private final ModelRenderer cube_r17;
    private final ModelRenderer cube_r8;
    private final ModelRenderer cube_r9;
    private final ModelRenderer rArm;
    private final ModelRenderer cube_r18;
    private final ModelRenderer cube_r10;
    private final ModelRenderer cube_r11;
    private final ModelRenderer lLeg;
    private final ModelRenderer cube_r19;
    private final ModelRenderer cube_r20;
    private final ModelRenderer cube_r21;
    private final ModelRenderer rLeg;
    private final ModelRenderer cube_r22;
    private final ModelRenderer cube_r23;
    private final ModelRenderer cube_r24;
    private final ModelRenderer lBoot;
    private final ModelRenderer cube_r25;
    private final ModelRenderer cube_r26;
    private final ModelRenderer rBoot;
    private final ModelRenderer cube_r27;
    private final ModelRenderer cube_r28;
    EntityEquipmentSlot slot;

    public ModelArmorNebula(EntityEquipmentSlot slot) {
        this.slot = slot;
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.cubeList.add(new ModelBox(this.head, 0, 66, -4.0f, -8.0f, -4.0f, 8, 2, 8, 0.21f));
        this.head.cubeList.add(new ModelBox(this.head, 36, 90, -3.0f, -8.75f, -3.0f, 6, 1, 4, 0.21f));
        this.head.cubeList.add(new ModelBox(this.head, 0, 90, -4.0f, -6.0f, -4.0f, 1, 3, 7, 0.2085f));
        this.head.cubeList.add(new ModelBox(this.head, 19, 86, 3.0f, -6.0f, -4.0f, 1, 3, 7, 0.2085f));
        this.head.cubeList.add(new ModelBox(this.head, 28, 80, -3.0f, -6.0f, -4.0f, 6, 1, 1, 0.2075f));
        this.head.cubeList.add(new ModelBox(this.head, 23, 81, -4.0f, -6.0f, 3.0f, 1, 1, 1, 0.2075f));
        this.head.cubeList.add(new ModelBox(this.head, 23, 78, 3.0f, -6.0f, 3.0f, 1, 1, 1, 0.2075f));
        this.cube_r1 = new ModelRenderer(this);
        this.cube_r1.setRotationPoint(0.0f, 0.0f, 0.3f);
        this.head.addChild(this.cube_r1);
        this.setRotationAngle(this.cube_r1, -0.1745f, -0.3491f, 0.0f);
        this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 16, 78, 0.6f, -8.4f, -6.8f, 2, 3, 1, 0.21f));
        this.cube_r2 = new ModelRenderer(this);
        this.cube_r2.setRotationPoint(0.0f, 0.0f, 0.3f);
        this.head.addChild(this.cube_r2);
        this.setRotationAngle(this.cube_r2, -0.1745f, 0.3491f, 0.0f);
        this.cube_r2.cubeList.add(new ModelBox(this.cube_r2, 9, 78, -2.6f, -8.4f, -6.8f, 2, 3, 1, 0.21f));
        this.cube_r3 = new ModelRenderer(this);
        this.cube_r3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.addChild(this.cube_r3);
        this.setRotationAngle(this.cube_r3, -0.2618f, 0.0f, 0.0f);
        this.cube_r3.cubeList.add(new ModelBox(this.cube_r3, 5, 83, -1.0f, -9.0f, -2.5f, 2, 1, 5, 0.21f));
        this.cube_r12 = new ModelRenderer(this);
        this.cube_r12.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.addChild(this.cube_r12);
        this.setRotationAngle(this.cube_r12, 0.7418f, 0.0f, -0.0873f);
        this.cube_r12.cubeList.add(new ModelBox(this.cube_r12, 47, 83, -4.6f, -7.0f, 3.0f, 2, 2, 4, 0.21f));
        this.cube_r13 = new ModelRenderer(this);
        this.cube_r13.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.addChild(this.cube_r13);
        this.setRotationAngle(this.cube_r13, 0.7418f, 0.0f, 0.0873f);
        this.cube_r13.cubeList.add(new ModelBox(this.cube_r13, 45, 76, 2.6f, -7.0f, 3.0f, 2, 2, 4, 0.21f));
        this.cube_r14 = new ModelRenderer(this);
        this.cube_r14.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.addChild(this.cube_r14);
        this.setRotationAngle(this.cube_r14, -0.0873f, 0.0f, 0.0f);
        this.cube_r14.cubeList.add(new ModelBox(this.cube_r14, 31, 84, -1.0f, -5.0f, -4.6f, 2, 1, 1, 0.21f));
        this.cube_r15 = new ModelRenderer(this);
        this.cube_r15.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.addChild(this.cube_r15);
        this.setRotationAngle(this.cube_r15, -0.48f, 0.0f, 0.0f);
        this.cube_r15.cubeList.add(new ModelBox(this.cube_r15, 0, 78, -1.0f, -7.9f, -8.0f, 2, 4, 2, 0.21f));
        this.chestplate = new ModelRenderer(this);
        this.chestplate.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.chestplate.cubeList.add(new ModelBox(this.chestplate, 0, 0, -4.5f, -0.2f, -3.0f, 9, 11, 6, 0.0f));
        this.chestplate.cubeList.add(new ModelBox(this.chestplate, 31, 10, -2.5f, 5.7f, 1.9f, 5, 4, 2, 0.0f));
        this.chestplate.cubeList.add(new ModelBox(this.chestplate, 31, 0, -3.5f, -0.6f, 2.6f, 7, 7, 2, 0.0f));
        this.cube_r16 = new ModelRenderer(this);
        this.cube_r16.setRotationPoint(0.0f, 24.0f, -1.0f);
        this.chestplate.addChild(this.cube_r16);
        this.setRotationAngle(this.cube_r16, 0.1309f, 0.0f, 0.0f);
        this.cube_r16.cubeList.add(new ModelBox(this.cube_r16, 57, 16, -1.0f, -20.7f, 7.7f, 2, 6, 1, 0.0f));
        this.cube_r4 = new ModelRenderer(this);
        this.cube_r4.setRotationPoint(-0.1f, 25.2f, 3.35f);
        this.chestplate.addChild(this.cube_r4);
        this.setRotationAngle(this.cube_r4, 0.2618f, 0.0f, 0.1745f);
        this.cube_r4_r1 = new ModelRenderer(this);
        this.cube_r4_r1.setRotationPoint(0.1f, -1.2f, -3.35f);
        this.cube_r4.addChild(this.cube_r4_r1);
        this.setRotationAngle(this.cube_r4_r1, -0.5236f, 0.0f, 0.0f);
        this.cube_r4_r1.cubeList.add(new ModelBox(this.cube_r4_r1, 50, 8, -2.0f, -25.0f, -3.0f, 2, 5, 2, 0.0f));
        this.cube_r5 = new ModelRenderer(this);
        this.cube_r5.setRotationPoint(-0.1f, 25.2f, 3.35f);
        this.chestplate.addChild(this.cube_r5);
        this.setRotationAngle(this.cube_r5, 0.2618f, 0.0f, -0.1745f);
        this.cube_r5_r1 = new ModelRenderer(this);
        this.cube_r5_r1.setRotationPoint(0.1f, -1.2f, -3.35f);
        this.cube_r5.addChild(this.cube_r5_r1);
        this.setRotationAngle(this.cube_r5_r1, -0.5236f, 0.0f, 0.0f);
        this.cube_r5_r1.cubeList.add(new ModelBox(this.cube_r5_r1, 50, 0, 0.0f, -25.0f, -3.0f, 2, 5, 2, 0.0f));
        this.cube_r6 = new ModelRenderer(this);
        this.cube_r6.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.chestplate.addChild(this.cube_r6);
        this.setRotationAngle(this.cube_r6, -0.1745f, 0.0f, 0.0f);
        this.cube_r7 = new ModelRenderer(this);
        this.cube_r7.setRotationPoint(0.0f, 23.75f, 0.0f);
        this.chestplate.addChild(this.cube_r7);
        this.setRotationAngle(this.cube_r7, -0.0873f, 0.0f, 0.0f);
        this.cube_r7_r1 = new ModelRenderer(this);
        this.cube_r7_r1.setRotationPoint(0.0f, -0.3f, 0.0f);
        this.cube_r7.addChild(this.cube_r7_r1);
        this.setRotationAngle(this.cube_r7_r1, 0.2182f, 0.0f, 0.0f);
        this.cube_r7_r1.cubeList.add(new ModelBox(this.cube_r7_r1, 34, 68, -1.5f, -23.1842f, -1.1615f, 3, 8, 2, 0.0f));
        this.cube_r7_r2 = new ModelRenderer(this);
        this.cube_r7_r2.setRotationPoint(0.0f, -0.3f, 0.0f);
        this.cube_r7.addChild(this.cube_r7_r2);
        this.setRotationAngle(this.cube_r7_r2, 0.2618f, 0.0f, 0.0f);
        this.cube_r7_r2.cubeList.add(new ModelBox(this.cube_r7_r2, 34, 17, 1.5f, -21.1842f, -0.8615f, 2, 7, 2, 0.0f));
        this.cube_r7_r2.cubeList.add(new ModelBox(this.cube_r7_r2, 54, 69, -3.5f, -21.1842f, -0.8615f, 2, 7, 2, 0.0f));
        this.lArm = new ModelRenderer(this);
        this.lArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.lArm.cubeList.add(new ModelBox(this.lArm, 0, 27, -4.0f, -2.4f, -2.5f, 5, 5, 5, 0.05f));
        this.lArm.cubeList.add(new ModelBox(this.lArm, 21, 27, -3.0f, 6.0f, -2.0f, 4, 4, 4, 0.2f));
        this.cube_r17 = new ModelRenderer(this);
        this.cube_r17.setRotationPoint(0.0f, -3.5f, 0.0f);
        this.lArm.addChild(this.cube_r17);
        this.setRotationAngle(this.cube_r17, 0.8727f, 0.0f, 0.0f);
        this.cube_r17.cubeList.add(new ModelBox(this.cube_r17, 45, 63, -1.7f, 1.3f, -2.3f, 2, 1, 4, 0.0f));
        this.cube_r8 = new ModelRenderer(this);
        this.cube_r8.setRotationPoint(5.2f, 22.3f, 0.0f);
        this.lArm.addChild(this.cube_r8);
        this.setRotationAngle(this.cube_r8, 0.0f, 0.0f, 0.2182f);
        this.cube_r8.cubeList.add(new ModelBox(this.cube_r8, 54, 25, -14.0918f, -24.4119f, -1.0f, 2, 3, 2, 0.0f));
        this.cube_r9 = new ModelRenderer(this);
        this.cube_r9.setRotationPoint(5.2f, 22.3f, 0.0f);
        this.lArm.addChild(this.cube_r9);
        this.setRotationAngle(this.cube_r9, 0.0f, 0.0f, 0.0873f);
        this.cube_r9.cubeList.add(new ModelBox(this.cube_r9, 16, 19, -11.8564f, -24.7019f, -2.0f, 4, 3, 4, 0.0f));
        this.rArm = new ModelRenderer(this);
        this.rArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.rArm.cubeList.add(new ModelBox(this.rArm, 38, 26, -1.0f, -2.4f, -2.5f, 5, 5, 5, 0.05f));
        this.rArm.cubeList.add(new ModelBox(this.rArm, 0, 38, -1.0f, 6.0f, -2.0f, 4, 4, 4, 0.2f));
        this.cube_r18 = new ModelRenderer(this);
        this.cube_r18.setRotationPoint(-10.0f, -3.5f, 0.0f);
        this.rArm.addChild(this.cube_r18);
        this.setRotationAngle(this.cube_r18, 0.8727f, 0.0f, 0.0f);
        this.cube_r18.cubeList.add(new ModelBox(this.cube_r18, 45, 63, 9.7f, 1.3f, -2.3f, 2, 1, 4, 0.0f));
        this.cube_r10 = new ModelRenderer(this);
        this.cube_r10.setRotationPoint(-4.8f, 22.3f, 0.0f);
        this.rArm.addChild(this.cube_r10);
        this.setRotationAngle(this.cube_r10, 0.0f, 0.0f, -0.2182f);
        this.cube_r10.cubeList.add(new ModelBox(this.cube_r10, 19, 36, 11.6918f, -24.4119f, -1.0f, 2, 3, 2, 0.0f));
        this.cube_r11 = new ModelRenderer(this);
        this.cube_r11.setRotationPoint(-4.8f, 22.3f, 0.0f);
        this.rArm.addChild(this.cube_r11);
        this.setRotationAngle(this.cube_r11, 0.0f, 0.0f, -0.0873f);
        this.cube_r11.cubeList.add(new ModelBox(this.cube_r11, 24, 38, 7.4564f, -24.7019f, -2.0f, 4, 3, 4, 0.0f));
        this.lLeg = new ModelRenderer(this);
        this.lLeg.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.lLeg.cubeList.add(new ModelBox(this.lLeg, 0, 50, -4.0f, 0.0f, -2.0f, 4, 8, 4, 0.2f));
        this.cube_r19 = new ModelRenderer(this);
        this.cube_r19.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.lLeg.addChild(this.cube_r19);
        this.setRotationAngle(this.cube_r19, 0.2618f, 0.0f, 0.0f);
        this.cube_r19.cubeList.add(new ModelBox(this.cube_r19, 46, 17, -3.0f, 0.6f, -3.8f, 2, 4, 1, 0.2f));
        this.cube_r20 = new ModelRenderer(this);
        this.cube_r20.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.lLeg.addChild(this.cube_r20);
        this.setRotationAngle(this.cube_r20, 0.3054f, 0.0f, 0.0f);
        this.cube_r20.cubeList.add(new ModelBox(this.cube_r20, 7, 20, -3.0f, 2.0f, 0.7f, 2, 4, 1, 0.2f));
        this.cube_r21 = new ModelRenderer(this);
        this.cube_r21.setRotationPoint(0.0f, 12.0f, 1.0f);
        this.lLeg.addChild(this.cube_r21);
        this.setRotationAngle(this.cube_r21, 0.1745f, 0.0f, -0.1745f);
        this.cube_r21.cubeList.add(new ModelBox(this.cube_r21, 17, 48, -3.0f, -10.7f, -1.9f, 2, 4, 2, 0.2f));
        this.rLeg = new ModelRenderer(this);
        this.rLeg.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.rLeg.cubeList.add(new ModelBox(this.rLeg, 42, 38, 0.0f, 0.0f, -2.0f, 4, 8, 4, 0.2f));
        this.cube_r22 = new ModelRenderer(this);
        this.cube_r22.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.rLeg.addChild(this.cube_r22);
        this.setRotationAngle(this.cube_r22, 0.2618f, 0.0f, 0.0f);
        this.cube_r22.cubeList.add(new ModelBox(this.cube_r22, 35, 46, 1.0f, 0.6f, -3.8f, 2, 4, 1, 0.2f));
        this.cube_r23 = new ModelRenderer(this);
        this.cube_r23.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.rLeg.addChild(this.cube_r23);
        this.setRotationAngle(this.cube_r23, 0.3054f, 0.0f, 0.0f);
        this.cube_r23.cubeList.add(new ModelBox(this.cube_r23, 0, 20, 1.0f, 2.0f, 0.7f, 2, 4, 1, 0.2f));
        this.cube_r24 = new ModelRenderer(this);
        this.cube_r24.setRotationPoint(0.0f, 12.0f, 1.0f);
        this.rLeg.addChild(this.cube_r24);
        this.setRotationAngle(this.cube_r24, 0.1745f, 0.0f, 0.1745f);
        this.cube_r24.cubeList.add(new ModelBox(this.cube_r24, 26, 48, 1.0f, -10.7f, -1.9f, 2, 4, 2, 0.2f));
        this.lBoot = new ModelRenderer(this);
        this.lBoot.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.lBoot.cubeList.add(new ModelBox(this.lBoot, 18, 56, 0.0f, 9.0f, -2.8f, 4, 3, 5, 0.2f));
        this.cube_r25 = new ModelRenderer(this);
        this.cube_r25.setRotationPoint(0.0f, 12.0f, -1.0f);
        this.lBoot.addChild(this.cube_r25);
        this.setRotationAngle(this.cube_r25, -0.3054f, 0.0f, 0.0f);
        this.cube_r25.cubeList.add(new ModelBox(this.cube_r25, 40, 52, 1.0f, -5.7f, 1.9f, 2, 3, 1, 0.2f));
        this.cube_r26 = new ModelRenderer(this);
        this.cube_r26.setRotationPoint(0.0f, 12.0f, -1.0f);
        this.lBoot.addChild(this.cube_r26);
        this.setRotationAngle(this.cube_r26, 0.7418f, 0.0f, 0.1745f);
        this.cube_r26.cubeList.add(new ModelBox(this.cube_r26, 33, 54, 3.2f, -1.8f, 2.2f, 1, 2, 3, 0.2f));
        this.rBoot = new ModelRenderer(this);
        this.rBoot.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.rBoot.cubeList.add(new ModelBox(this.rBoot, 44, 53, -4.0f, 9.0f, -2.8f, 4, 3, 5, 0.2f));
        this.cube_r27 = new ModelRenderer(this);
        this.cube_r27.setRotationPoint(0.0f, 12.0f, -1.0f);
        this.rBoot.addChild(this.cube_r27);
        this.setRotationAngle(this.cube_r27, -0.3054f, 0.0f, 0.0f);
        this.cube_r27.cubeList.add(new ModelBox(this.cube_r27, 27, 65, -3.0f, -5.7f, 1.9f, 2, 3, 1, 0.2f));
        this.cube_r28 = new ModelRenderer(this);
        this.cube_r28.setRotationPoint(0.0f, 12.0f, -1.0f);
        this.rBoot.addChild(this.cube_r28);
        this.setRotationAngle(this.cube_r28, 0.7418f, 0.0f, -0.1745f);
        this.cube_r28.cubeList.add(new ModelBox(this.cube_r28, 37, 61, -4.2f, -1.8f, 2.2f, 1, 2, 3, 0.2f));
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (entity instanceof EntitySkeleton || entity instanceof EntityZombie) {
            this.setRotationAnglesMonster(f, f1, f2, f3, f4, f5, entity);
        } else {
            this.prepareForRender(entity);
            this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        }
        this.head.showModel = this.slot == EntityEquipmentSlot.HEAD;
        this.chestplate.showModel = this.slot == EntityEquipmentSlot.CHEST;
        this.lArm.showModel = this.slot == EntityEquipmentSlot.CHEST;
        this.rArm.showModel = this.slot == EntityEquipmentSlot.CHEST;
        this.rLeg.showModel = this.slot == EntityEquipmentSlot.LEGS;
        this.lLeg.showModel = this.slot == EntityEquipmentSlot.LEGS;
        this.lBoot.showModel = this.slot == EntityEquipmentSlot.FEET;
        this.rBoot.showModel = this.slot == EntityEquipmentSlot.FEET;
        this.bipedBody.showModel = false;
        this.bipedHead = this.head;
        this.bipedBody = this.chestplate;
        this.bipedLeftArm = this.lArm;
        this.bipedRightArm = this.rArm;
        if (this.slot == EntityEquipmentSlot.LEGS) {
            this.bipedLeftLeg = this.lLeg;
            this.bipedRightLeg = this.rLeg;
        } else {
            this.bipedRightLeg = this.rBoot;
            this.bipedLeftLeg = this.lBoot;
        }
        if (this.isChild) {
            float f6 = 2.0f;
            GL11.glPushMatrix();
            GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6);
            GL11.glTranslatef(0.0f, 16.0f * f5, 0.0f);
            this.bipedHead.render(f5);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6);
            GL11.glTranslatef(0.0f, 24.0f * f5, 0.0f);
            this.bipedBody.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedBody.render(f5);
            GL11.glPopMatrix();
        } else {
            this.bipedHead.render(f5);
            this.bipedBody.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedBody.render(f5);
        }
    }

    public void prepareForRender(Entity entity) {
        EntityLivingBase living = (EntityLivingBase) entity;
        this.isSneak = living != null ? living.isSneaking() : false;
        if (living != null && living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;
            ItemStack itemstack = player.getHeldItemMainhand();
            this.leftArmPose = !itemstack.isEmpty() ? ModelBiped.ArmPose.ITEM : ModelBiped.ArmPose.EMPTY;
            this.rightArmPose = !itemstack.isEmpty() && player.getHeldItemOffhand().isEmpty() ? ModelBiped.ArmPose.ITEM : ModelBiped.ArmPose.EMPTY;
            if (itemstack != null && player.getItemInUseCount() > 0) {
                EnumAction enumaction = itemstack.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) {
                    this.leftArmPose = ModelBiped.ArmPose.BLOCK;
                    this.rightArmPose = ModelBiped.ArmPose.BLOCK;
                } else if (enumaction == EnumAction.BOW) {
                    this.rightArmPose = ModelBiped.ArmPose.ITEM;
                }
            }
        }
    }

    public void setRotationAnglesMonster(float limbSwing, float limbSwingAmount, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        this.setRotationAngles(limbSwing, limbSwingAmount, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
        float f6 = MathHelper.sin(limbSwing * (float) Math.PI);
        float f7 = MathHelper.sin((1.0f - (1.0f - limbSwingAmount) * (1.0f - limbSwingAmount)) * (float) Math.PI);
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleY = -(0.1f - f6 * 0.6f);
        this.bipedRightArm.rotateAngleY = 0.1f - f6 * 0.6f;
        this.bipedLeftArm.rotateAngleX = -1.5707964f;
        this.bipedRightArm.rotateAngleX = -1.5707964f;
        this.bipedLeftArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        this.bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        this.bipedLeftArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09f) * 0.05f + 0.05f;
        this.bipedRightArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09f) * 0.05f + 0.05f;
        this.bipedLeftArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067f) * 0.05f;
        this.bipedRightArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067f) * 0.05f;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
