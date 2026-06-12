package dev.cammiescorner.armaments.common.items;

import dev.cammiescorner.armaments.common.data_components.SpearChargeComponent;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalSpearItem extends TieredItem implements SpecialRenderItem, FabricItem {
	public CrystalSpearItem(Tier material, Properties settings) {
		super(material, settings.attributes(createAttributes()));
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
		var component = newStack.getOrDefault(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, 0));

		return component.charge() != 0 && super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack);
	}

	public static ItemAttributeModifiers createAttributes() {
		var builder = ItemAttributeModifiers.builder();

		// TODO figure out how to get the attack bonus based on charge
		builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 9, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);

		return builder.build();
	}
}
