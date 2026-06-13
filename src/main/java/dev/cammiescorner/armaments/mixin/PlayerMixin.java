package dev.cammiescorner.armaments.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) { super(entityType, level); }

	@ModifyExpressionValue(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;autoSpinAttackDmg:F", opcode = Opcodes.GETFIELD))
	private float spinAttackDamage(float original) {
		var modifier = 0f;
		var attr = getAttribute(Attributes.ATTACK_DAMAGE);

		if(attr != null && attr.getModifier(Item.BASE_ATTACK_DAMAGE_ID) != null) {
			modifier += (float) attr.getModifier(Item.BASE_ATTACK_DAMAGE_ID).amount();
		}

		return (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) - modifier) + 8f;
	}
}
