package dev.cammiescorner.armaments.client;

import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.client.models.armor.SeaCrownArmorModel;
import dev.cammiescorner.armaments.client.renderers.armor.SeaCrownArmorRenderer;
import dev.cammiescorner.armaments.client.renderers.item.SpecialItemRenderer;
import dev.cammiescorner.armaments.common.data_components.SpearChargeComponent;
import dev.cammiescorner.armaments.common.items.SpecialRenderItem;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import dev.cammiescorner.armaments.common.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;

public class ArmamentsClient implements ClientModInitializer {
	public static final Minecraft client = Minecraft.getInstance();

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(SeaCrownArmorModel.MODEL_LAYER, SeaCrownArmorModel::getTexturedModelData);

		ArmorRendererRegistryImpl.register(new SeaCrownArmorRenderer(), ModItems.SEA_CROWN.get());

		ItemProperties.register(ModItems.CRYSTAL_SPEAR.get(), Armaments.id("charge"), (itemStack, clientWorld, livingEntity, i) -> {
			SpearChargeComponent component = itemStack.getOrDefault(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, 0));

			return component.charge() / 4f;
		});

		// TODO figure out why this isn't working
		ModItems.ITEMS.stream().forEach(holder -> {
			Item item = holder.get();

			if(item instanceof SpecialRenderItem) {
				ResourceLocation id = holder.getId();
				SpecialItemRenderer specialItemRenderer = new SpecialItemRenderer(id);
				ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(specialItemRenderer);
				BuiltinItemRendererRegistry.INSTANCE.register(item, specialItemRenderer);

				ModelLoadingPlugin.register(ctx -> ctx.addModels(
					id.withPath("item/" + id.getPath() + "_gui"),
					id.withPath("item/" + id.getPath() + "_handheld")
				));
			}
		});
	}
}
