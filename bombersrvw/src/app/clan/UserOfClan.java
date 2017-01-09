package app.clan;

public class UserOfClan {
	public int id;
	public String title;
	public int popular;
	public int level;
	public boolean isonline;
	
	public int clandepositm;
	public int clandeposite;
	public byte clanrole;
	public int getclanmoneyat;
	
	public UserOfClan(int id, String title, int level, int popular, int clandepositm, int clandeposite, byte clanrole, int getclanmoneyat){
		this.id = id;
		this.title = title;
		this.level = level;
		this.popular = popular;
		this.clandepositm = clandepositm;
		this.clandeposite = clandeposite;
		this.clanrole = clanrole;
		this.getclanmoneyat = getclanmoneyat;		
	}
}
