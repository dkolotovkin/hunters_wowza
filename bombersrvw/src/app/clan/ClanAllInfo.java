package app.clan;

import java.util.ArrayList;

public class ClanAllInfo {
	public ClanInfo claninfo;
	public ArrayList<UserOfClan> users;
	public int time;
	
	public ClanAllInfo(ClanInfo claninfo, ArrayList<UserOfClan> users){
		this.claninfo = claninfo;
		this.users = users;
	}
}
