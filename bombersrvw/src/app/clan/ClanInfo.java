package app.clan;

public class ClanInfo {
	public int id;
	public int maxusers;
	public String title;
	public String ownertitle;
	public int ownerid;
	public int money;
	public int experience;
	
	public ClanInfo(int id, String title, int maxusers, String ownertitle, int ownerid, int money, int experience){
		this.id = id;
		this.title = title;
		this.maxusers = maxusers;
		this.ownertitle = ownertitle;
		this.ownerid = ownerid;
		this.money = money;
		this.experience = experience;
	}
}
