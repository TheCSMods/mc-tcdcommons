package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EntityType;

/**
 * Renders {@link OtherClientPlayerEntity}.
 */
public class TOcpeRendererElement extends TEntityRendererElement
{
	// ==================================================
	/**
	 * The {@link OtherClientPlayerEntity} that will be rendered on the screen.
	 */
	protected OtherClientPlayerEntity otherPlayer;
	// ==================================================
	public TOcpeRendererElement(int x, int y, int width, int height) { super(x, y, width, height, EntityType.PLAYER); }
	// ==================================================
	public @Override void setEntityType(EntityType<?> entityType)
	{
		if(entityType != EntityType.PLAYER) return;
		super.setEntityType(entityType);
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link GameProfile} associated with
	 * the {@link OtherClientPlayerEntity}.
	 */
	public @Nullable GameProfile getProfile() { return (this.otherPlayer != null ? this.otherPlayer.getGameProfile() : null); }
	
	/**
	 * Sets the {@link GameProfile} for the
	 * {@link OtherClientPlayerEntity} by its user-name.
	 */
	public final void setProfileN(@Nullable String playerName)
	{
		if(playerName != null) this.setProfileGP(new GameProfile(null, playerName));
		else this.setProfileGP(null);
	}
	
	/**
	 * Sets the {@link GameProfile} for the
	 * {@link OtherClientPlayerEntity} by its user-id.
	 */
	public final void setProfileU(@Nullable UUID playerUid)
	{
		if(playerUid != null) this.setProfileGP(new GameProfile(playerUid, null));
		else this.setProfileGP(null);
	}
	
	/**
	 * Sets the {@link GameProfile} for the {@link OtherClientPlayerEntity}.
	 */
	public void setProfileGP(@Nullable GameProfile profile)
	{
		//check if profile is null
		if(profile == null)
		{
			this.otherPlayer = null;
			this.livingEntity = null;
			return;
		}
		//create OCPE
		var world = CLIENT.world;
		if(world != null)
		{
			this.otherPlayer = new OtherClientPlayerEntity(world, profile);
			this.livingEntity = otherPlayer;
		}
		else this.livingEntity = null;
	}
	// ==================================================
}
