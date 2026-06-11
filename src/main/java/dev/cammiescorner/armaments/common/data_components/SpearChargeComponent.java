package dev.cammiescorner.armaments.common.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SpearChargeComponent(int charge, long startTime) {
	public static final Codec<SpearChargeComponent> CODEC = RecordCodecBuilder.create(chargeComponentInstance -> chargeComponentInstance.group(
		Codec.INT.optionalFieldOf("Charge", 0).forGetter(SpearChargeComponent::charge),
		Codec.LONG.optionalFieldOf("StartTime", 0L).forGetter(SpearChargeComponent::startTime)
	).apply(chargeComponentInstance, SpearChargeComponent::new));
	public static final StreamCodec<FriendlyByteBuf, SpearChargeComponent> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SpearChargeComponent::charge,
		ByteBufCodecs.VAR_LONG, SpearChargeComponent::startTime,
		SpearChargeComponent::new
	);

	@Override
	public int charge() {
		return Math.min(charge, 4);
	}
}
