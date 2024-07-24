package io.github.thecsdev.tcdcommons.client.world;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.EntityHandler;

@Beta
public @Virtual class ClientSandboxEntityManager extends ClientEntityManager<Entity>
{
	public ClientSandboxEntityManager() { super(Entity.class, new ClientSandboxEntityHandler()); }
	
	@Beta
	public @Virtual static class ClientSandboxEntityHandler implements EntityHandler<Entity>
	{
		public @Virtual @Override void create(Entity entity) {}
		public @Virtual @Override void destroy(Entity entity) {}
		public @Virtual @Override void startTicking(Entity entity) {}
		public @Virtual @Override void startTracking(Entity entity) {}
		public @Virtual @Override void stopTicking(Entity entity) {}
		public @Virtual @Override void stopTracking(Entity entity) {}
		public @Virtual @Override void updateLoadStatus(Entity entity) {}
	}
}