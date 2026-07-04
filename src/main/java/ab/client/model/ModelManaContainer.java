package ab.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class ModelManaContainer extends ModelBase {
    private final ModelRenderer bb_main;

    public ModelManaContainer() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.bb_main = new ModelRenderer(this);
        this.bb_main.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 6, 14, -7.0f, -15.0f, -7.0f, 14, 5, 3, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 24, 0, -5.0f, -10.2f, -5.0f, 10, 4, 10, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 49, 37, -6.0f, -10.0f, -2.0f, 2, 6, 4, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 50, 26, 4.0f, -10.0f, -2.0f, 2, 6, 4, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 13, 23, -2.0f, -10.0f, -6.0f, 4, 6, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 23, -2.0f, -10.0f, 4.0f, 4, 6, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 27, 23, -5.0f, -18.0f, -5.0f, 1, 3, 10, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 40, 14, -3.0f, -6.2f, -3.0f, 6, 5, 6, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 55, 0, -6.0f, -17.0f, -6.0f, 2, 2, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 28, 28, 4.0f, -17.0f, -6.0f, 2, 2, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 55, 5, 4.0f, -17.0f, 4.0f, 2, 2, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 28, 23, -6.0f, -17.0f, 4.0f, 2, 2, 2, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 1, 0, 4.0f, -18.0f, -5.0f, 1, 3, 10, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 16, 5, -4.0f, -18.0f, 4.0f, 8, 3, 1, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 16, 0, -4.0f, -18.0f, -5.0f, 8, 3, 1, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 14, 38, -7.0f, -15.0f, 4.0f, 14, 5, 3, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 41, 48, 4.0f, -15.0f, -4.0f, 3, 5, 8, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 18, 48, -7.0f, -15.0f, -4.0f, 3, 5, 8, 0.0f));
    }

    public void render() {
        this.bb_main.render(0.0625f);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
