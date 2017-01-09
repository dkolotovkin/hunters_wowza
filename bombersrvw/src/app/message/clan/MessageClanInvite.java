package app.message.clan;

import app.clan.ClanInfo;
import app.message.Message;

public class MessageClanInvite extends Message{
	public ClanInfo claninfo;
	
	public MessageClanInvite(byte type, int roomId, ClanInfo claninfo){
		super(type, roomId);
	
		this.claninfo = claninfo;
	}
}
