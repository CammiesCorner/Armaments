package dev.cammiescorner.armaments.common.items;

import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.ArmamentsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;

public class SeaCrownItem extends ArmorItem implements Equipable {
	public SeaCrownItem(Properties settings) {
		super(ArmorMaterials.TURTLE, Type.HELMET, settings.attributes(createAttributes()));
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
		return ingredient.is(Items.PRISMARINE_SHARD);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if(entity instanceof LivingEntity wearer && wearer.getItemBySlot(EquipmentSlot.HEAD) == stack) {
			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

			if(contents != null && contents.potion().isPresent()) {
				for(MobEffectInstance effect : contents.potion().get().value().getEffects())
					wearer.addEffect(new MobEffectInstance(effect.getEffect(), 100, Math.min(effect.getAmplifier(), ArmamentsConfig.SeaCrown.potionAmplifier), true, false, true));
			}

			if(wearer instanceof ServerPlayer player)
				player.resetSentInfo();
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
		PotionContents contents = itemStack.get(DataComponents.POTION_CONTENTS);

		if(contents != null && contents.potion().isPresent()) {
			for(MobEffectInstance effect : contents.potion().get().value().getEffects()) {
				list.add(Component.translatable(
					"potion.withAmplifier",
					Component.translatable(effect.getDescriptionId()),
					Component.translatable("potion.potency." + Math.min(effect.getAmplifier(), ArmamentsConfig.SeaCrown.potionAmplifier))
				).withStyle(effect.getEffect().value().isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED));
			}
		}
	}

	public static ItemAttributeModifiers createAttributes() {
		var builder = ItemAttributeModifiers.builder();

		if(ArmamentsConfig.SeaCrown.healthModifier != 0)
			builder.add(Attributes.MAX_HEALTH, new AttributeModifier(Armaments.id("crown_health_cost"), ArmamentsConfig.SeaCrown.healthModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD);
		if(ArmamentsConfig.SeaCrown.armorPoints != 0)
			builder.add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.withDefaultNamespace("armor." + Type.HELMET.getName()), ArmamentsConfig.SeaCrown.armorPoints, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD);

		return builder.build();
	}
}
