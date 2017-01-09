package app.message.jp;

import app.message.Message;

public class JackPotMessage extends Message{
	public int money = 0;
	
	public JackPotMessage(byte type, int roomId, int money){
		super(type, roomId);
	
		this.money = money;
	}
}
