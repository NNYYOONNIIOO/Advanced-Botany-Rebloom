package ab.client.model;

import ab.client.render.tile.RenderTileManaCharger;
import ab.common.block.tile.TileManaCharger;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

public class ModelManaCharger extends ModelBase {
    private final ModelRenderer chargerBase;
    private final ModelRenderer chargerPlate;

    public ModelManaCharger() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.chargerBase = new ModelRenderer(this);
        this.chargerBase.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.chargerBase.cubeList.add(new ModelBox(this.chargerBase, 0, 9, -2.5f, -9.0f, -2.5f, 5, 4, 5, 0.0f));
        this.chargerBase.cubeList.add(new ModelBox(this.chargerBase, 0, 0, -3.5f, -11.0f, -3.5f, 7, 2, 7, 0.0f));
        this.chargerBase.cubeList.add(new ModelBox(this.chargerBase, 20, 9, -1.5f, -5.0f, -1.5f, 3, 3, 3, 0.0f));
        this.chargerPlate = new ModelRenderer(this);
        this.chargerPlate.setRotationPoint(0.0f, 17.0f, 0.0f);
        this.chargerPlate.cubeList.add(new ModelBox(this.chargerPlate, 0, 18, 5.0f, 0.0f, -2.0f, 4, 1, 4, 0.0f));
    }

    public void render(RenderTileManaCharger render, double time) {
        TileManaCharger tile = render.charger;
        float offset = (float) Math.sin(time / 40.0) * 0.1f + 0.05f;
        float polerot = -((float) time / 16.0f) * 25.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, offset, 0.0f);
        GL11.glRotatef(polerot, 0.0f, 1.0f, 0.0f);
        this.chargerBase.render(0.0625f);
        if (tile.getStackInSlot(0) != null) {
            float rot = this.chargerPlate.rotateAngleY * 180.0f / (float) Math.PI;
            GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(-0.125f, 0.8125f, 0.125f);
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            render.renderItemStack(tile.getStackInSlot(0));
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, offset / 1.3f + 0.185f, 0.0f);
        GL11.glScalef(0.85f, 0.85f, 0.85f);
        for (int i = 1; i < 5; ++i) {
            switch (i) {
                case 1: {
                    this.chargerPlate.rotateAngleY = 3.1416f;
                    break;
                }
                case 2: {
                    this.chargerPlate.rotateAngleY = 0.0f;
                    break;
                }
                case 3: {
                    this.chargerPlate.rotateAngleY = 1.5708f;
                    break;
                }
                case 4: {
                    this.chargerPlate.rotateAngleY = -1.5708f;
                }
            }
            if (tile.getWorld() != null) {
                time += (double) ((float) i * 36.0f);
            }
            float offset1 = (float) Math.sin(time / 15.0) * 0.1f - 0.1f;
            if (time == -1.0) {
                offset1 = 0.0f;
            }
            ItemStack stack = tile.getStackInSlot(i);
            GL11.glTranslatef(0.0f, -offset1, 0.0f);
            if (stack != null) {
                GL11.glPushMatrix();
                float manaPercent = TileManaCharger.getManaPercent(stack);
                float rot = this.chargerPlate.rotateAngleY * 180.0f / (float) Math.PI;
                if (manaPercent < 100.0f) {
                    float chargeY = (offset1 + offset1 / 2.4f) * ((100.0f - manaPercent) / 150.0f);
                    GL11.glTranslatef(0.0f, chargeY, 0.0f);
                    if (tile.clientTick[i] > 12) {
                        float posX = 0.0f;
                        float posZ = 0.0f;
                        switch (i) {
                            case 1: {
                                posX = 0.0f;
                                posZ = -0.375f;
                                break;
                            }
                            case 2: {
                                posX = 0.0f;
                                posZ = 0.375f;
                                break;
                            }
                            case 3: {
                                posX = -0.375f;
                                posZ = 0.0f;
                                break;
                            }
                            case 4: {
                                posX = 0.375f;
                                posZ = 0.0f;
                            }
                        }
                        Vector3 itemVec = Vector3.fromTileEntity(tile).add(0.5 + (double) posX + (Math.random() / 8.0 - 0.0625), 0.67 + (double) offset1, 0.5 + (double) posZ + (Math.random() / 8.0 - 0.0625));
                        Vector3 tileVec = Vector3.fromTileEntity(tile).add(0.5 + (double) posX, 0.7425 + (double) offset1 - (double) (chargeY / 2.0f), 0.5 + (double) posZ);
                        Botania.proxy.lightningFX(itemVec, tileVec, 0.5f, 1140881820, 1140901631);
                        tile.clientTick[i] = 0;
                    }
                }
                GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(0.3125f, 1.06f, 0.1245f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                render.renderItemStack(stack);
                GL11.glPopMatrix();
            }
            this.chargerPlate.render(0.0625f);
            GL11.glTranslatef(0.0f, offset1, 0.0f);
        }
        GL11.glPopMatrix();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
