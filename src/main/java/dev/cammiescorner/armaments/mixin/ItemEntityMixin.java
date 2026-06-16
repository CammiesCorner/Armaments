package dev.cammiescorner.armaments.mixin;

import dev.cammiescorner.armaments.common.data_components.LanceChargeComponent;
import dev.cammiescorner.armaments.common.items.CrystalLanceItem;
import dev.cammiescorner.armaments.common.registry.ModDataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@ModifyArg(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setItem(Lnet/minecraft/world/item/ItemStack;)V"))
	private ItemStack createItem(ItemStack stack) {
		if(stack.getItem() instanceof CrystalLanceItem)
			stack.set(ModDataComponents.LANCE_CHARGE.get(), new LanceChargeComponent(0, 0));

		return stack;
	}
}
