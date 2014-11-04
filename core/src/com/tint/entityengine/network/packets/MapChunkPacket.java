package com.tint.entityengine.network.packets;

public class MapChunkPacket extends Packet {
	public short[][] tiles;
	public int startX, startY;
}
