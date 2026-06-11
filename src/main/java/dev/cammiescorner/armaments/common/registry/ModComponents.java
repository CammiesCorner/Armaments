package dev.cammiescorner.armaments.common.registry;

import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.common.components.entity.EchoComponent;
import net.minecraft.world.entity.LivingEntity;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class ModComponents implements EntityComponentInitializer {
	public static final ComponentKey<EchoComponent> ECHO = createComponent("echo", EchoComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(LivingEntity.class, ECHO, EchoComponent::new);
	}

	private static <T extends Component> ComponentKey<T> createComponent(String name, Class<T> component) {
		return ComponentRegistry.getOrCreate(Armaments.id(name), component);
	}
}
