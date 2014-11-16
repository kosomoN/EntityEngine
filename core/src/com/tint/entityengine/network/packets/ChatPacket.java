package com.tint.entityengine.network.packets;

public class ChatPacket extends Packet {
	public String message;
	public int messageSenderId;
	
	public ChatPacket(String text) {
		message = text;
	}
	
	public ChatPacket() { }
}
