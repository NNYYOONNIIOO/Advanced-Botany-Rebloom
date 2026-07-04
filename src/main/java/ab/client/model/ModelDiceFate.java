package ab.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class ModelDiceFate extends ModelBase {
    private final ModelRenderer bb_main;

    public ModelDiceFate() {
        this.textureWidth = 48;
        this.textureHeight = 48;
        this.bb_main = new ModelRenderer(this);
        this.bb_main.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 24, -6.0f, -6.0f, -6.0f, 12, 12, 12, 0.0f));
        this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -6.0f, -6.0f, -6.0f, 12, 12, 12, 0.0f));
    }

    public void render(float rotX, float rotY, float rotZ) {
        this.setRotationAngle(this.bb_main, this.getAngel(rotX), this.getAngel(rotY), this.getAngel(rotZ));
        this.bb_main.render(0.0625f);
    }

    private float getAngel(float rot) {
        return rot / 180.0f * (float) Math.PI;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
