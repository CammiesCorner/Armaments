package dev.cammiescorner.armaments.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElderGuardianSpikeItem extends Item {
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public ElderGuardianSpikeItem(Properties settings) {
		super(settings);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE.value(), new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 2, AttributeModifier.Operation.ADD_VALUE));
		builder.put(Attributes.ATTACK_SPEED.value(), new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 2, AttributeModifier.Operation.ADD_VALUE));
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//		stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
//		if(state.getDestroySpeed(world, pos) != 0f)
//			stack.hurtAndBreak(2, miner, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

		return true;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
		return !miner.isCreative();
	}

	// TODO fix
//	@Override
//	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
//		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
//	}
}
