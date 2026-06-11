package dev.cammiescorner.armaments.common.compat;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.cammiescorner.armaments.Armaments;
import dev.cammiescorner.armaments.ArmamentsConfig;

public class ModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ResourcefulConfig config = Armaments.configurator.getConfig(ArmamentsConfig.class);

			if(config == null)
				return null;

			return new ConfigScreen(null, config);
		};
	}
}
