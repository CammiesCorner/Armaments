package dev.cammiescorner.armaments.common.recipes;

import dev.cammiescorner.armaments.common.registry.ModItems;
import dev.cammiescorner.armaments.common.registry.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PotionCrownRecipe extends CustomRecipe {
	public PotionCrownRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput recipeInput, Level level) {
		boolean hasCrown = false;
		boolean hasPotion = false;

		for(ItemStack stack : recipeInput.items()) {
			if(stack.is(ModItems.SEA_CROWN.get())) {
				if(hasCrown)
					return false;

				hasCrown = true;
			}
			if(stack.is(Items.POTION)) {
				var contents = stack.get(DataComponents.POTION_CONTENTS);

				if(contents != null && contents.potion().isPresent()) {
					Holder<Potion> potion = contents.potion().get();

					if(hasPotion || potion == Potions.WATER || potion.value().hasInstantEffects())
						return false;

					hasPotion = true;
				}
			}
		}

		return hasCrown && hasPotion;
	}

	@Override
	public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
		Holder<Potion> potion = Potions.WATER;
		ItemStack seaCrown = ItemStack.EMPTY;

		for(ItemStack stack : recipeInput.items()) {
			if(stack.is(ModItems.SEA_CROWN.get()))
				seaCrown = stack.copy();
			if(stack.is(Items.POTION)) {
				var contents = stack.get(DataComponents.POTION_CONTENTS);

				if(contents != null && contents.potion().isPresent())
					potion = contents.potion().get();
			}
		}

		if(potion != Potions.WATER && !seaCrown.isEmpty()) {
			seaCrown.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

			return seaCrown;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.POTION_CROWN.get();
	}
}
