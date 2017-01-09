package app.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import app.ServerApplication;

import app.clan.ClanUserInfo;
import app.logger.MyLogger;
import app.shop.item.Item;
import app.utils.changeinfo.ChangeInfoParams;

public class User {
	public int id;
	public String idSocial;
	public String ip;
	public int sex;
	public String title;
	public byte role;
	public byte king;
	public int level;
	public int lastlvl;
	
	public int popular;
	public int experience;
	public int exphour;
	public int expday;
	public int nextLevelExperience;
	public int money;
	public int bullets;
	public byte bantype;
	public int setbanat;
	public int changebanat;
	public String url;
	
	public ClanUserInfo claninfo;
	
	public ArrayList<Item> itemsArr;
	public HashMap<Integer, Item> itemsObj;
	
	public MyLogger logger = new MyLogger(User.class.getName());
	
	public User(int id, String idSocial, String ip, int sex, String title, int popular, int experience, int bullets, int exphour, int expday, int lastlvl, int money, byte role, byte king, byte bantype, int setbanat, int changebanat, String url, ClanUserInfo claninfo){
		this.id = id;
		this.idSocial = idSocial;
		this.ip = ip;
		this.sex = sex;
		this.title = title;
		this.money = money;
		this.role = role;
		this.king = king;
		this.popular = popular;
		this.experience = experience;
		this.bullets = bullets;
		this.exphour = exphour;
		this.expday = expday;
		this.lastlvl = lastlvl;
		this.bantype = bantype;
		this.setbanat = setbanat;
		this.changebanat = changebanat;
		
		this.url = url;
		this.claninfo = claninfo;
		
		this.level = ServerApplication.application.userinfomanager.getLevelByExperience(experience);
		this.nextLevelExperience = ServerApplication.application.userinfomanager.levels.get(this.level + 1);	
		
		this.itemsArr = new ArrayList<Item>();
		this.itemsObj = new HashMap<Integer, Item>();
	}
	
	public void setParamsByExperience(int value){
		int level = ServerApplication.application.userinfomanager.getLevelByExperience(value);
		if(this.level != level){
			ServerApplication.application.commonroom.changeUserInfoByID(this.id, ChangeInfoParams.USER_EXPERIENCE_MAXEXPERIENCE_LEVEL, this.experience, ServerApplication.application.userinfomanager.levels.get(level + 1), level);
		}
		this.level = level;
		this.nextLevelExperience = ServerApplication.application.userinfomanager.levels.get(level + 1);		
	}	
	
	public void updateExperience(int value){
		int beexperience = Math.max(value, 0);
		int delta = beexperience - experience;
		if(delta > 0){
			expday += delta;
			exphour += delta;
		}
		experience = beexperience;
		
		checkLevelBonus();
	}
	
	public void checkLevelBonus(){		
		setParamsByExperience(experience);
		
		if(lastlvl < level){
			Connection _sqlconnection = null;				
			PreparedStatement updatelastlvl = null;
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				updatelastlvl = _sqlconnection.prepareStatement("UPDATE user SET lastlvl=? WHERE id=?");
				updatelastlvl.setInt(1, level);
				updatelastlvl.setInt(2, id);						
				
        		if (updatelastlvl.executeUpdate() > 0){
        			lastlvl = level;
        			ServerApplication.application.userinfomanager.setBonusNewLevel(id);								
        		}				
				
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();		    	
			    	if (updatelastlvl != null) updatelastlvl.close(); 
			    	
			    	_sqlconnection = null;
			    	updatelastlvl = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
	}
	
	public void updateExperience(int value, int changebanat){
		int beexperience = Math.max(value, 0);
		experience = beexperience;
		this.changebanat = changebanat;
		
		checkLevelBonus();
	}
	
	public void updateExpAndPopul(int exp, int pop, int changebanat){
		int beexperience = Math.max(exp, 0);
		int bepopular = Math.max(pop, 0);
		experience = beexperience;
		popular = bepopular;
		this.changebanat = changebanat;
		
		checkLevelBonus();		
	}
	
	public void updateExpAndPopul(int exp, int pop){
		int beexperience = Math.max(exp, 0);
		int bepopular = Math.max(pop, 0);
		experience = beexperience;
		popular = bepopular;
		
		checkLevelBonus();
	}
	
	public void updateMoney(int value){		
		int bemoney = Math.max(value, 0);
		money = bemoney;
		
//		if(money > 300000)
//			ServerApplication.application.printStackTrace("UPDATE MONEY userID: " + id + " money: " + money + " \n");
	}
	
	public void updateBullets(int value){		
		int bebullets = Math.max(value, 0);
		bullets = bebullets;
	}
	
	public void updateMoney(int value, int addtodepositm, int adddtoeposite){		
		int bemoney = Math.max(value, 0);
		money = bemoney;
		claninfo.clandepositm += addtodepositm;
		claninfo.clandeposite += adddtoeposite;
		
//		if(money > 300000)
//			ServerApplication.application.printStackTrace("UPDATE MONEY userID: " + id + " money: " + money + " \n");
	}
	
	public void update(int level, int experience, int energy){
		this.level = level;
		this.experience = experience;
	}
}