package app.clan;

public class ClanUserInfo {
	public int clanid;
	public int maxusers;
	public String clantitle;
	public int clandepositm;
	public int clandeposite;
	public byte clanrole;
	public int getclanmoneyat;
	
	public ClanUserInfo(int clanid, String clantitle, int maxusers, int clandepositm, int clandeposite, byte clanrole, int getclanmoneyat){
		this.clanid = clanid;
		this.clantitle = clantitle;
		this.maxusers = maxusers;
		this.clandepositm = clandepositm;
		this.clandeposite = clandeposite;
		this.clanrole = clanrole;
		this.getclanmoneyat = getclanmoneyat;	
	}
}
