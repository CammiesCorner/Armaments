package dev.cammiescorner.armaments.common.registry;

import com.mojang.serialization.Codec;
import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.common.data_components.LanceChargeComponent;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModDataComponents {
	public static final RegistryHandler<DataComponentType<?>> DATA_COMPONENTS = RegistryHandler.create(Registries.DATA_COMPONENT_TYPE, Armaments.MOD_ID);

	public static final RegistrySupplier<DataComponentType<LanceChargeComponent>> LANCE_CHARGE = DATA_COMPONENTS.register("lance_charge", () -> DataComponentType.<LanceChargeComponent>builder()
		.persistent(LanceChargeComponent.CODEC)
		.networkSynchronized(LanceChargeComponent.STREAM_CODEC)
		.build()
	);
	public static final RegistrySupplier<DataComponentType<Boolean>> LANCE_JOUST = DATA_COMPONENTS.register("lance_joust", () -> DataComponentType.<Boolean>builder()
		.persistent(Codec.BOOL)
		.networkSynchronized(ByteBufCodecs.BOOL)
		.build()
	);
	public static final RegistrySupplier<DataComponentType<Integer>> ECHO_CHARGE = DATA_COMPONENTS.register("echo_charge", () -> DataComponentType.<Integer>builder()
		.persistent(Codec.INT)
		.networkSynchronized(ByteBufCodecs.VAR_INT)
		.build()
	);
}
