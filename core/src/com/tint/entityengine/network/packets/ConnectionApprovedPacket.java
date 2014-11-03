package com.tint.entityengine.network.packets;

public class ConnectionApprovedPacket extends Packet {
	public long playerEntityId;

	public ConnectionApprovedPacket(long playerEntityId) {
		this.playerEntityId = playerEntityId;
	}

	//Required by de-serialization
	public ConnectionApprovedPacket() {}
}
