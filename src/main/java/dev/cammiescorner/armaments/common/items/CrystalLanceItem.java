package dev.cammiescorner.armaments.common.items;

import dev.cammiescorner.armaments.ArmamentsConfig;
import dev.cammiescorner.armaments.common.data_components.LanceChargeComponent;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalLanceItem extends TieredItem implements SpecialRenderItem, FabricItem {
	public CrystalLanceItem(Tier material, Properties settings) {
		super(material, settings.attributes(createAttributes()));
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
		return 720000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		var stack = player.getItemInHand(interactionHand);

		player.startUsingItem(interactionHand);
		stack.set(ModDataComponents.LANCE_JOUST.get(), true);

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
		itemStack.set(ModDataComponents.LANCE_JOUST.get(), false);

		super.releaseUsing(itemStack, level, livingEntity, i);
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
		if(livingEntity.getControlledVehicle() instanceof AbstractHorse horse) {
			var box = horse.getBoundingBox().expandTowards(0, livingEntity.getBbHeight(), 0).inflate(1);
			var entities = level.getEntitiesOfClass(LivingEntity.class, box, entity -> entity != livingEntity && entity != horse);
			var damageSource = livingEntity instanceof Player player ? level.damageSources().playerAttack(player) : level.damageSources().mobAttack(livingEntity);

			for(LivingEntity entity : entities) {
				if(livingEntity instanceof Player player && !player.getCooldowns().isOnCooldown(itemStack.getItem())) {
					if(entity.hurt(damageSource, (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
						var component = itemStack.get(ModDataComponents.LANCE_CHARGE.get());
						var charge = Math.max(component.charge() - 1, 0);

						player.getCooldowns().addCooldown(itemStack.getItem(), ArmamentsConfig.CrystalLance.joustingCoolDown);
						itemStack.set(ModDataComponents.LANCE_CHARGE.get(), new LanceChargeComponent(charge, level.getGameTime()));

						String s = "⬤";
						String bar = "◯";

						ChatFormatting formatting = switch(charge) {
							case 2 -> ChatFormatting.GOLD;
							case 3 -> ChatFormatting.YELLOW;
							case 4 -> ChatFormatting.GREEN;
							default -> ChatFormatting.RED;
						};

						player.displayClientMessage(Component.nullToEmpty(s.repeat(charge) + bar.repeat(4 - charge)).copy().withStyle(formatting), true);
					}
				}
			}
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public void postHurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.hurtAndBreak(1, livingEntity2, EquipmentSlot.MAINHAND);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
		return !miner.isCreative();
	}

	@Override
	public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		var component = newStack.getOrDefault(ModDataComponents.LANCE_CHARGE.get(), new LanceChargeComponent(0, 0));

		return component.charge() != 0 && super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack);
	}

	@Override
	public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
		var modifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes());
		var builder = ItemAttributeModifiers.builder();

		modifiers.forEach(EquipmentSlot.MAINHAND, (attributeHolder, attributeModifier) -> {
			if(attributeHolder.is(Attributes.ATTACK_DAMAGE)) {
				var component = itemStack.getOrDefault(ModDataComponents.LANCE_CHARGE.get(), new LanceChargeComponent(0, 0));
				var damage = 9 + (5 * component.charge());

				builder.add(attributeHolder, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
			}
			else {
				builder.add(attributeHolder, attributeModifier, EquipmentSlotGroup.MAINHAND);
			}
		});

		itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
	}

	public static ItemAttributeModifiers createAttributes() {
		var builder = ItemAttributeModifiers.builder();

		builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 9, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);

		return builder.build();
	}
}
