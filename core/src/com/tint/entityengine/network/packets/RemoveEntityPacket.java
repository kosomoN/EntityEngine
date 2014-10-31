package com.tint.entityengine.network.packets;

public class RemoveEntityPacket extends Packet {
	public long serverId;

	public RemoveEntityPacket(long serverId) {
		this.serverId = serverId;
	}
	
	//Required by de-serialization
	public RemoveEntityPacket() {}
}
