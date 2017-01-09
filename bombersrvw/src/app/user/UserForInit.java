package app.user;

import java.util.List;

import app.clan.ClanUserInfo;

public class UserForInit extends User {
	public int bantime;
	public int jackpot;
	public int colortime;
	public int accessorytime;
	public List<Integer> popularparts;
	public List<String> populartitles;
	
	public UserForInit(int id, String idSocial, String ip, int sex, String title, int popular, int experience, int bullets, int exphour, int expday, int lastlvl, int money, byte role, byte king, byte bantype, int setbanat, int changebanat, String url, ClanUserInfo claninfo){
		super(id, idSocial, ip,  sex, title, popular, experience, bullets, exphour, expday, lastlvl, money, role, king, bantype, setbanat, changebanat, url, claninfo);
	}
	
	public static UserForInit createfromUser(User u){
		if(u != null){
			return new UserForInit(u.id, u.idSocial, u.ip, u.sex, u.title, u.popular, u.experience, u.bullets, u.exphour, u.expday, u.lastlvl, u.money, u.role, u.king, u.bantype, u.setbanat, u.changebanat, u.url, u.claninfo);
		}else{
			return null;
		}
	}
}
