package dev.cammiescorner.armaments.common.items;

import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.ArmamentsConfig;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import dev.cammiescorner.armaments.common.registry.ModStatusEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EchoDaggerItem extends Item {
	private static final int MAX_CHARGE = 100;

	public EchoDaggerItem(Properties settings) {
		super(settings.attributes(createAttributes()));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13f - (100 - getCharge(stack)) * 13f / MAX_CHARGE);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x009295;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(!attacker.level().isClientSide() && (isUsable(stack) || (attacker instanceof Player player && player.isCreative())))
			target.addEffect(new MobEffectInstance(ModStatusEffects.ECHO.holder(), ArmamentsConfig.EchoDagger.potionDuration, 0, true, false, true), attacker);

		return true;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
		return !miner.isCreative();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand).copy();

		if(user.isShiftKeyDown()) {
			if(isUsable(stack) || user.isCreative()) {
				user.hurt(Armaments.echoDamage(world), 2);
				user.addEffect(new MobEffectInstance(ModStatusEffects.ECHO.holder(), ArmamentsConfig.EchoDagger.potionDuration, 0, true, false, true), user);
				return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
			}
			else {
				Inventory inv = user.getInventory();

				for(int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack itemStack = inv.getItem(i);

					if(itemStack.is(Items.ECHO_SHARD)) {
						itemStack.shrink(1);
						resetCharge(stack);
						return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
					}
				}
			}
		}

		return super.use(world, user, hand);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if(isUsable(stack))
			decrementCharge(stack);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || isUsable(stack);
	}

	@Override
	public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return !isUsable(newStack);
	}

	public static ItemAttributeModifiers createAttributes() {
		var builder = ItemAttributeModifiers.builder();

		builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);

		return builder.build();
	}

	public static boolean isUsable(ItemStack stack) {
		return getCharge(stack) > 0;
	}

	public static int getCharge(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ECHO_CHARGE.get(), 0);
	}

	public static void setCharge(ItemStack stack, int value) {
		stack.set(ModDataComponents.ECHO_CHARGE.get(), Mth.clamp(value, 0, MAX_CHARGE));
	}

	public static void decrementCharge(ItemStack stack) {
		setCharge(stack, getCharge(stack) - 1);
	}

	public static void resetCharge(ItemStack stack) {
		setCharge(stack, MAX_CHARGE);
	}
}
