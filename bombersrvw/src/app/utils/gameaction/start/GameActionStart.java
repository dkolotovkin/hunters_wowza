package app.utils.gameaction.start;

import java.util.List;

import app.user.game.UserGame;
import app.utils.gameaction.GameAction;

public class GameActionStart extends GameAction {
	public int time;
	public String locationFile;
	public String locationXML;
	public String creatorName;	
	public List<UserGame> users;
	public byte gametype;
	public int hunterId;
	
	public GameActionStart(byte type, int roomID, String creatorName, List<UserGame> users, int t, String locationFile, String xml){
		super(type, roomID);
		this.time = t;
		this.locationFile = locationFile;
		this.creatorName = creatorName;
		this.locationXML = xml;
		this.users = users;
	}
}
