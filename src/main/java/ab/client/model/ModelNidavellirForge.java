package ab.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class ModelNidavellirForge extends ModelBase {
    private final ModelRenderer bottomAnvil;
    private final ModelRenderer topAnvil;

    public ModelNidavellirForge() {
        this.textureWidth = 48;
        this.textureHeight = 48;
        this.bottomAnvil = new ModelRenderer(this);
        this.bottomAnvil.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.bottomAnvil.cubeList.add(new ModelBox(this.bottomAnvil, 32, 26, -3.0f, -1.0f, -4.0f, 6, 1, 1, 0.0f));
        this.bottomAnvil.cubeList.add(new ModelBox(this.bottomAnvil, 0, 31, -5.0f, -1.0f, -3.0f, 12, 1, 6, 0.0f));
        this.bottomAnvil.cubeList.add(new ModelBox(this.bottomAnvil, 32, 17, -2.0f, -3.0f, -2.0f, 4, 1, 4, 0.0f));
        this.bottomAnvil.cubeList.add(new ModelBox(this.bottomAnvil, 0, 8, -4.0f, -2.0f, -3.0f, 8, 1, 6, 0.0f));
        this.bottomAnvil.cubeList.add(new ModelBox(this.bottomAnvil, 32, 23, -3.0f, -1.0f, 3.0f, 6, 1, 1, 0.0f));
        this.topAnvil = new ModelRenderer(this);
        this.topAnvil.setRotationPoint(0.0f, 26.0f, 0.0f);
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 0, 23, -6.5f, -11.0f, -3.0f, 12, 2, 6, 0.0f));
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 0, 38, -5.5f, -12.0f, -4.0f, 13, 2, 8, 0.0f));
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 0, 15, -5.5f, -9.0f, -3.0f, 9, 2, 6, 0.0f));
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 17, 0, -4.5f, -11.0f, 2.5f, 7, 3, 1, 0.0f));
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 0, 0, -4.5f, -11.0f, -3.5f, 7, 3, 1, 0.0f));
        this.topAnvil.cubeList.add(new ModelBox(this.topAnvil, 30, 12, -2.5f, -7.0f, -2.0f, 5, 1, 4, 0.0f));
    }

    public void renderBottom() {
        this.bottomAnvil.render(0.0625f);
    }

    public void renderTop() {
        this.topAnvil.render(0.0625f);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
