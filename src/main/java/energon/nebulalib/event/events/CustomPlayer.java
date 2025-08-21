package energon.nebulalib.event.events;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomPlayer extends ModelBase {
	public final ModelRenderer head;
	public final ModelRenderer headwear;
	public final ModelRenderer body;
	public final ModelRenderer jacket;
	public final ModelRenderer left_arm;
	public final ModelRenderer left_sleeve;
	public final ModelRenderer right_arm;
	public final ModelRenderer right_sleeve;
	public final ModelRenderer left_leg;
	public final ModelRenderer left_pants;
	public final ModelRenderer right_leg;
	public final ModelRenderer right_pants;

	public CustomPlayer() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		headwear = new ModelRenderer(this);
		headwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		headwear.cubeList.add(new ModelBox(headwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		jacket = new ModelRenderer(this);
		jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
		jacket.cubeList.add(new ModelBox(jacket, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(5.0F, 2.0F, 0.0F);
		left_arm.cubeList.add(new ModelBox(left_arm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		left_sleeve = new ModelRenderer(this);
		left_sleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
		left_sleeve.cubeList.add(new ModelBox(left_sleeve, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		right_arm.cubeList.add(new ModelBox(right_arm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		right_sleeve = new ModelRenderer(this);
		right_sleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
		right_sleeve.cubeList.add(new ModelBox(right_sleeve, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
		left_leg.cubeList.add(new ModelBox(left_leg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		left_pants = new ModelRenderer(this);
		left_pants.setRotationPoint(2.0F, 12.0F, 0.0F);
		left_pants.cubeList.add(new ModelBox(left_pants, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		right_leg.cubeList.add(new ModelBox(right_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		right_pants = new ModelRenderer(this);
		right_pants.setRotationPoint(-2.0F, 12.0F, 0.0F);
		right_pants.cubeList.add(new ModelBox(right_pants, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
	}

	public void renderAll(float f5) {
		head.render(f5);
		headwear.render(f5);
		body.render(f5);
		jacket.render(f5);
		left_arm.render(f5);
		left_sleeve.render(f5);
		right_arm.render(f5);
		right_sleeve.render(f5);
		left_leg.render(f5);
		left_pants.render(f5);
		right_leg.render(f5);
		right_pants.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}