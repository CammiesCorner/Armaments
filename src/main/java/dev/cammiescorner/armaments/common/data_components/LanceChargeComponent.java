package dev.cammiescorner.armaments.common.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LanceChargeComponent(int charge, long startTime) {
	public static final Codec<LanceChargeComponent> CODEC = RecordCodecBuilder.create(chargeComponentInstance -> chargeComponentInstance.group(
		Codec.INT.optionalFieldOf("Charge", 0).forGetter(LanceChargeComponent::charge),
		Codec.LONG.optionalFieldOf("StartTime", 0L).forGetter(LanceChargeComponent::startTime)
	).apply(chargeComponentInstance, LanceChargeComponent::new));
	public static final StreamCodec<FriendlyByteBuf, LanceChargeComponent> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, LanceChargeComponent::charge,
		ByteBufCodecs.VAR_LONG, LanceChargeComponent::startTime,
		LanceChargeComponent::new
	);

	@Override
	public int charge() {
		return Math.min(charge, 4);
	}
}
