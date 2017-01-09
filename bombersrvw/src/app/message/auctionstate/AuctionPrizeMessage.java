package app.message.auctionstate;

import app.message.Message;

public class AuctionPrizeMessage extends Message {
	public int prize;	
	
	public AuctionPrizeMessage(byte type, int roomId, int prize){
		super(type, roomId);
	
		this.prize = prize;		
	}
}
