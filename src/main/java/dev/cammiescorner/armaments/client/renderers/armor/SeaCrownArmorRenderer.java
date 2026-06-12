package dev.cammiescorner.armaments.client.renderers.armor;

import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.client.models.armor.SeaCrownArmorModel;
import dev.upcraft.sparkweave.api.client.render.CustomHumanoidModelArmorRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SeaCrownArmorRenderer extends CustomHumanoidModelArmorRenderer<LivingEntity, HumanoidModel<LivingEntity>, SeaCrownArmorModel<LivingEntity>> {
	private static final ResourceLocation TEXTURE = Armaments.id("textures/entity/armor/sea_crown.png");
	private final SeaCrownArmorModel<LivingEntity> model;

	public SeaCrownArmorRenderer(LivingEntity entity, EntityRendererProvider.Context context, RenderLayerParent<LivingEntity, ? extends EntityModel<?>> layerParent) {
		model = new SeaCrownArmorModel<>(context.bakeLayer(SeaCrownArmorModel.MODEL_LAYER));
	}

	@Override
	protected void setPartVisibility(SeaCrownArmorModel<LivingEntity> model, HumanoidModel<LivingEntity> contextModel, LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
		model.setAllVisible(true);
		model.crown.visible = slot == EquipmentSlot.HEAD;
	}

	@Override
	protected SeaCrownArmorModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
		return model;
	}

	@Override
	protected ResourceLocation getTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
		return TEXTURE;
	}
}
