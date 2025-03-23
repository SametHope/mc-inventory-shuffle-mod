package samethope.inventory_shuffle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samethope.inventory_shuffle.commands.InvShuffleCommand;
import samethope.inventory_shuffle.data.ModState;

public class InventoryShuffle implements ModInitializer {
	public static final String MOD_ID = "inventory-shuffle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(InvShuffleCommand::register);

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ModState.loadFromNbt(server);
			LOGGER.info("Inventory Shuffle state loaded");
		});

		ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> {
			ModState.saveToNbt(server);
			LOGGER.info("Inventory Shuffle state saved");
		});

		ServerTickEvents.END_SERVER_TICK.register(ModState::processTick);

		LOGGER.info("Inventory Shuffle initialized");
	}
}