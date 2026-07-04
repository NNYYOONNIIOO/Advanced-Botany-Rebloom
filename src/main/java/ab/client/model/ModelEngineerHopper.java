package ab.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelEngineerHopper extends ModelBase {
    private final ModelRenderer hopperBase;
    private final ModelRenderer hopperTop;
    private final ModelRenderer hopperBottom;

    public ModelEngineerHopper() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.hopperBase = new ModelRenderer(this);
        this.hopperBase.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 0, 45, -7.0f, -16.0f, -7.0f, 2, 5, 14, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 12, 27, -5.0f, -11.2f, -5.0f, 10, 3, 10, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 0, 38, -6.0f, -11.0f, -2.0f, 2, 5, 4, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 52, 38, 4.0f, -11.0f, -2.0f, 2, 5, 4, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 0, 49, -2.0f, -11.0f, -6.0f, 4, 5, 2, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 52, 49, -2.0f, -11.0f, 4.0f, 4, 5, 2, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 32, 45, 5.0f, -16.0f, -7.0f, 2, 5, 14, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 20, 50, -5.0f, -16.0f, 5.0f, 10, 5, 2, 0.0f));
        this.hopperBase.cubeList.add(new ModelBox(this.hopperBase, 20, 42, -5.0f, -16.0f, -7.0f, 10, 5, 2, 0.0f));
        this.hopperTop = new ModelRenderer(this);
        this.hopperTop.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.hopperTop.cubeList.add(new ModelBox(this.hopperTop, 20, 13, -3.0f, -20.0f, -3.0f, 6, 6, 6, 0.0f));
        this.hopperBottom = new ModelRenderer(this);
        this.hopperBottom.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.hopperBottom.cubeList.add(new ModelBox(this.hopperBottom, 24, 3, -2.0f, -5.0f, -2.0f, 4, 4, 4, 0.0f));
    }

    public void renderHoper(double time) {
        float offset = (float) Math.sin(time / 10.0) * 0.05f + 0.01f;
        this.hopperBase.render(0.0625f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, offset, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.6f);
        this.hopperTop.render(0.0625f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -offset, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.6f);
        this.hopperBottom.render(0.0625f);
        GL11.glPopMatrix();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
