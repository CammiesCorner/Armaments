package dev.cammiescorner.armaments.client.renderers.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.cammiescorner.armaments.client.ArmamentsClient;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class SpecialItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, IdentifiableResourceReloadListener, SimpleSynchronousResourceReloadListener {
	private final ResourceLocation id;
	private final ResourceLocation itemId;
	private ItemRenderer itemRenderer;
	private BakedModel inventoryItemModel;
	private BakedModel worldItemModel;

	public SpecialItemRenderer(ResourceLocation itemId) {
		this.id = ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), itemId.getPath() + "_renderer");
		this.itemId = itemId;
	}

	@NotNull
	@Override
	public ResourceLocation getFabricId() {
		return this.id;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return Set.of(ResourceReloadListenerKeys.MODELS);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		itemRenderer = ArmamentsClient.client.getItemRenderer();
		inventoryItemModel = ArmamentsClient.client.getModelManager().getModel(itemId.withPrefix("item/").withSuffix("_gui"));
		worldItemModel = ArmamentsClient.client.getModelManager().getModel(itemId.withPrefix("item/").withSuffix("_handheld"));
	}

	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		matrices.popPose();
		matrices.pushPose();
		Lighting.setupForFlatItems();

		if(mode != ItemDisplayContext.FIRST_PERSON_LEFT_HAND && mode != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && mode != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && mode != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND && mode != ItemDisplayContext.HEAD) {
			itemRenderer.render(stack, mode, false, matrices, vertexConsumers, light, overlay, getModel(inventoryItemModel, stack));
		}
		else {
			boolean leftHanded;

			switch(mode) {
				case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
				default -> leftHanded = false;
			}

			matrices.pushPose();

			if(mode == ItemDisplayContext.HEAD) {
				matrices.translate(0, -0.25, 0);
				matrices.scale(2, 2, 2);
			}

			itemRenderer.render(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, getModel(worldItemModel, stack));
			matrices.popPose();
		}
	}

	private BakedModel getModel(BakedModel model, ItemStack stack) {
		BakedModel overrides = model.getOverrides().resolve(model, stack, ArmamentsClient.client.level, null, 0);

		return overrides != null ? overrides : model;
	}
}
