package dev.cammiescorner.armaments.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.ArmamentsConfig;
import dev.cammiescorner.armaments.common.components.entity.EchoComponent;
import dev.cammiescorner.armaments.common.data_components.SpearChargeComponent;
import dev.cammiescorner.armaments.common.echos.Echo;
import dev.cammiescorner.armaments.common.items.CrystalSpearItem;
import dev.cammiescorner.armaments.common.registry.ModComponents;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import dev.cammiescorner.armaments.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Unique private final EchoComponent echo = getComponent(ModComponents.ECHO);
	@Unique private final LivingEntity self = (LivingEntity) (Object) this;

	@Shadow public float zza;
	@Shadow public abstract ItemStack getMainHandItem();

	public LivingEntityMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickRider(CallbackInfo info) {
		if(level() instanceof ServerLevel world) {
			if(getMainHandItem().getItem() instanceof CrystalSpearItem && isPassenger() && getControlledVehicle() instanceof AbstractHorse) {
				ItemStack stack = getMainHandItem();
				var component = stack.getOrDefault(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, 0));
				long timer = world.getGameTime() - component.startTime();
				int interval = ArmamentsConfig.CrystalSpear.chargeInterval;

				if(zza > 0) {
					if(component.charge() == 0 && component.startTime() + interval < world.getGameTime())
						stack.set(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, world.getGameTime()));

					if(timer % interval == 0 && component.charge() < 4) {
						stack.set(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(component.charge() + 1, component.startTime()));
						component = stack.getOrDefault(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, 0));
						world.playSound(null, getX(), getY(), getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.NEUTRAL, 1f, component.charge() / 4f);

						if(self instanceof Player player) {
							String charge = "⬤";
							String bar = "◯";

							ChatFormatting formatting = switch(component.charge()) {
								case 2 -> ChatFormatting.GOLD;
								case 3 -> ChatFormatting.YELLOW;
								case 4 -> ChatFormatting.GREEN;
								default -> ChatFormatting.RED;
							};

							player.displayClientMessage(Component.nullToEmpty(charge.repeat(component.charge()) + bar.repeat(4 - component.charge())).copy().withStyle(formatting), true);
						}
					}
				}
				else if(component.charge() > 0) {
					stack.set(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, component.startTime()));
					world.playSound(null, getX(), getY(), getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.NEUTRAL, 1f, 0f);

					if(self instanceof Player player)
						player.displayClientMessage(Component.nullToEmpty("◯◯◯◯").copy().withStyle(ChatFormatting.DARK_RED), true);
				}
			}

			if(self instanceof Player player) {
				Inventory inv = player.getInventory();

				for(int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack stack = inv.getItem(i);

					if(!(stack.getItem() instanceof CrystalSpearItem))
						continue;

					var component = stack.getOrDefault(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, 0));

					if((!player.getMainHandItem().equals(stack) || !player.isPassenger()) && component.charge() > 0)
						stack.set(ModDataComponents.SPEAR_CHARGE.get(), new SpearChargeComponent(0, component.startTime()));
				}
			}
		}
	}

	@ModifyExpressionValue(method = "checkAutoSpinAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private List<Entity> dontHitYourHorseStupid(List<Entity> original) {
		if(isPassenger())
			return original.stream().filter(entity -> !entity.getPassengers().contains(this)).collect(Collectors.toCollection(ArrayList::new));

		return original;
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private DamageSource changeEchoDaggerDamage(DamageSource source) {
		if(source.isDirect() && source.getDirectEntity() instanceof LivingEntity attacker) {
			ItemStack stack = attacker.getMainHandItem();

			if(stack.is(ModItems.ECHO_DAGGER.get()))
				return Armaments.echoDamage(level());
			if(stack.is(ModItems.ELDER_GUARDIAN_SPIKE.get()))
				return Armaments.pokeyDamage(level(), source.getDirectEntity());
		}

		return source;
	}

	@Inject(method = "hurt", at = @At("HEAD"))
	private void echoDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		amount *= ArmamentsConfig.EchoDagger.echoMultiplier;

		if(amount >= 1 && source.is(Armaments.ECHO)) {
			invulnerableTime = 0;
			echo.addEcho(new Echo(Echo.Type.DAMAGE, amount, level().getGameTime()));
			invulnerableTime = 0;
		}
	}

	@Inject(method = "heal", at = @At("HEAD"))
	public void heal(float amount, CallbackInfo info) {
		amount *= ArmamentsConfig.EchoDagger.echoMultiplier;

		if(amount >= 1)
			echo.addEcho(new Echo(Echo.Type.HEAL, amount, level().getGameTime()));
	}
}
