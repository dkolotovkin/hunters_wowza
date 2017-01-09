package app.user;

import app.clan.ClanUserInfo;

import com.wowza.wms.client.IClient;

public class UserClient {
	public User user;
	public IClient client;
	
	public UserClient(int id, String idSocial, String ip, int sex, String title, int popular, int experience, int bullets, int exphour, 
						int expday, int lastlvl, int money, byte role, byte king, byte bantype, int setbanat, int changebanat, 
						String url,	ClanUserInfo claninfo, IClient client){
		this.client = client;		
		user = new User(id, idSocial, ip, sex, title, popular, experience, bullets, exphour, expday, lastlvl, 
						money, role, king, bantype, setbanat, changebanat, url, claninfo);
	}
	
	public void update(int level, int experience, int energy){
		user.update(level, experience, energy);
	}
}
