package app.room.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import app.Config;
import app.ServerApplication;
import app.message.MessageType;
import app.room.Room;
import app.shop.ShopManager;
import app.user.UserClient;
import app.user.UserForTop;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.ban.BanType;
import app.utils.changeinfo.ChangeInfoParams;

public class CommonRoom extends Room {
	public Timer servertimer;
	public int update5minute = 0;
	public int update2minute = 0;
	public int update30second = 0;
	public int lastprizehour = 0;
	public Date date;
	
	public Map<Integer, Integer> userstimeonline;
	
	public CommonRoom(int id, String title){
		super(id, title);
		userstimeonline = new ConcurrentHashMap<Integer, Integer>();
		servertimer = new Timer();
		servertimer.schedule(new ServerTimerTask(), 10 * 1000, 10 * 1000);	
	}
	
	@Override
	public void addUser(UserClient u){
		super.addUser(u);
		date = new Date();
		int currenttime = (int)(date.getTime() / 1000);
		if(userstimeonline.get(u.user.id) != null) userstimeonline.remove(u.user.id);
		userstimeonline.put(u.user.id, currenttime);
	}
	
	@Override
	public void removeUserByClientID(String connID){
		int userID = new Integer(userClientIDtoID.get(connID));		
		userstimeonline.remove(userID);
		super.removeUserByClientID(connID);	
	}

	public void showStartInfo(UserClient u){
		if(u.user.experience < 5){	
			if(u.client.isConnected()){
				u.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageSimple(MessageType.START_INFO, this.id, "", u.user.id, 0));
			}
		}
	}
	
	public void updateBan(UserClient u, int currenttime){		
		int timepassed = currenttime - u.user.setbanat;
		boolean passed = false;
		
		int duration = 0;
		if (u.user.bantype == BanType.MINUT5){
			duration = 5 * 60;
			if (timepassed >= duration) passed = true;
		}else if (u.user.bantype == BanType.MINUT15){
			duration = 15 * 60;
			if (timepassed >= duration) passed = true;
		}else if (u.user.bantype == BanType.MINUT30){
			duration = 30 * 60;
			if (timepassed >= duration) passed = true;
		}else if (u.user.bantype == BanType.HOUR1){
			duration = 60 * 60;
			if (timepassed >= duration) passed = true;
		}else if (u.user.bantype == BanType.DAY1){
			duration = 60 * 60 * 24;
			if (timepassed >= duration) passed = true;
		}
		
		if (u.user.bantype != BanType.NO_BAN && u.user.changebanat != 0){
			int banexperience = Math.min((int) Math.floor((float)(Math.min(currenttime, u.user.setbanat + duration) - u.user.changebanat) / 60) * 2, 1000);
			int banpopular = Math.min((int) Math.floor((float)(Math.min(currenttime, u.user.setbanat + duration) - u.user.changebanat) / 60) * Config.valuePopularUpdateInBan(), 1000);
			
			if (banexperience > 0 || banpopular > 0){
				u.user.updateExpAndPopul(u.user.experience - banexperience, u.user.popular - banpopular, currenttime);
				u.user.changebanat = currenttime;				
				changeUserInfoByID(u.user.id, ChangeInfoParams.USER_EXPERIENCE_AND_POPULAR, u.user.experience, u.user.popular);
			}
		}
		
		if(passed == true){
			u.user.bantype = BanType.NO_BAN;
			u.user.setbanat = 0;
			ServerApplication.application.userinfomanager.banoff(u.user.id, u.user.ip);
		}
	}
	
	public void updateTransactionsMM(){		
		Connection _sqlconnection = null;		
		PreparedStatement updatetransaction = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		
		try {			
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			select = _sqlconnection.prepareStatement("SELECT * FROM transactionmm WHERE status=?");
			select.setInt(1, 0);
			selectRes = select.executeQuery();
    		while(selectRes.next()){
    			int other_price = selectRes.getInt("otherprice");
    			int transactionid = selectRes.getInt("id");
    			int userid = selectRes.getInt("userid");
    			
    			int money = 0;	
    			if(other_price >= 25 && other_price < 80){
    				money += 2000;
    			}else if(other_price >= 80 && other_price < 120){
    				money += 7000;
    			}else if(other_price >= 120 && other_price < 400){
    				money += 13000;
    			}else if(other_price >= 400 && other_price < 1000){
    				money += 45000;
    			}else if(other_price >= 1000){
    				money += 120000;
    			}
    			
    			UserClient user = users.get(Integer.toString(userid));
    			
    			if(user != null){
	    			ServerApplication.application.userinfomanager.addMoney(userid, money, user);   			
					user = null;
					
					updatetransaction = _sqlconnection.prepareStatement("UPDATE transactionmm SET status=1 WHERE id=?");
					updatetransaction.setInt(1, transactionid);
					updatetransaction.executeUpdate();
    			}
    		}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();		    	
		    	if (updatetransaction != null) updatetransaction.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;		    	
		    	updatetransaction = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateTransactionsOD(){		
		Connection _sqlconnection = null;		
		PreparedStatement updatetransaction = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		
		try {			
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			select = _sqlconnection.prepareStatement("SELECT * FROM transactionod WHERE status=?");
			select.setInt(1, 0);
			selectRes = select.executeQuery();
    		while(selectRes.next()){    			
    			int price = selectRes.getInt("price");    			
    			//String uidsocial = selectRes.getString("uidsocial");
    			int transactionid = selectRes.getInt("id");
    			int userid = selectRes.getInt("userid");
    			
    			int money = 0;
    			if(price == 20){
    				money += 1500;
    			}else if(price == 50){
    				money += 4000;
    			}else if(price == 160){
    				money += 14000;
    			}else if(price == 240){
    				money += 23000;
    			}else if(price == 400){
    				money += 40000;
    			}
    			UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userid));
    			
    			if(user != null && money > 0){
	    			ServerApplication.application.userinfomanager.addMoney(userid, money, user);   			
					user = null;
					
					updatetransaction = _sqlconnection.prepareStatement("UPDATE transactionod SET status=1 WHERE id=?");
					updatetransaction.setInt(1, transactionid);
					updatetransaction.executeUpdate();
    			}
    		}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();		    	
		    	if (updatetransaction != null) updatetransaction.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;		    	
		    	updatetransaction = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateTransactionsSite(){		
		Connection _sqlconnection = null;		
		PreparedStatement updatetransaction = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		
		try {			
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			select = _sqlconnection.prepareStatement("SELECT * FROM transactionsite WHERE status=?");
			select.setInt(1, 0);
			selectRes = select.executeQuery();
    		while(selectRes.next()){    			
    			int price = selectRes.getInt("price");
    			int transactionid = selectRes.getInt("id");
    			int userid = selectRes.getInt("userid");
    			
    			int money = 0;
    			if(price == 20){
    				money += 2000;
    			}else if(price == 70){
    				money += 8000;
    			}else if(price == 100){
    				money += 13000;
    			}else if(price == 350){
    				money += 46000;
    			}
    			UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userid));
    			
    			if(user != null && money > 0){
	    			ServerApplication.application.userinfomanager.addMoney(userid, money, user);   			
					user = null;
					
					updatetransaction = _sqlconnection.prepareStatement("UPDATE transactionsite SET status=1 WHERE id=?");
					updatetransaction.setInt(1, transactionid);
					updatetransaction.executeUpdate();
    			}
    		}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();		    	
		    	if (updatetransaction != null) updatetransaction.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;		    	
		    	updatetransaction = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateTransactionsVK(){		
		Connection _sqlconnection = null;		
		PreparedStatement updatetransaction = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			select = _sqlconnection.prepareStatement("SELECT * FROM transactionvk WHERE status=0");
			selectRes = select.executeQuery();
    		while(selectRes.next()){
    			int transactionid = selectRes.getInt("id");
    			int money = selectRes.getInt("otherprice") * 400;    			
    			
    			UserClient user = getUserBySocialID(selectRes.getString("uidsocial"));
    			
    			if(user != null && money > 0){
	    			ServerApplication.application.userinfomanager.addMoney(user.user.id, money, user);
					user = null;
					
					updatetransaction = _sqlconnection.prepareStatement("UPDATE transactionvk SET status=1 WHERE id=?");
					updatetransaction.setInt(1, transactionid);
					updatetransaction.executeUpdate();
    			}
    		}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();		    	
		    	if (updatetransaction != null) updatetransaction.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;		    	
		    	updatetransaction = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateClansMoney(){
		Connection _sqlconnection = null;		
		PreparedStatement updateclan = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			select = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE money>0");	
			selectRes = select.executeQuery();
    		while(selectRes.next()){
    			UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(selectRes.getInt("ownerid")));
    			if(user != null && user.client != null && user.client.isConnected()){
	    			ServerApplication.application.userinfomanager.addMoney(selectRes.getInt("ownerid"), selectRes.getInt("money"), user);
	    			
	    			updateclan = _sqlconnection.prepareStatement("UPDATE clan SET money=0 WHERE ownerid=?");
					updateclan.setInt(1, selectRes.getInt("ownerid"));
					updateclan.executeUpdate();
    			}
    			user = null;
    		}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();		    	
		    	if (updateclan != null) updateclan.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;		    	
		    	updateclan = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}
	}
	
	public void sendHourPrize(){
		ServerApplication.application.userinfomanager.topHourUsersUpdating = true;
		
		Connection _sqlconnection = null;
		PreparedStatement insertitem = null;
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){			
			ServerApplication.application.userinfomanager.updateUser(user.getValue().user);
		}
		ServerApplication.application.userinfomanager.updateTopHourUsers();
		
		UserForTop usertop;
		int countPrizes = Math.min(5, ServerApplication.application.userinfomanager.topHourUsers.size());
		for(int i = 0; i < countPrizes; i++){
			usertop = ServerApplication.application.userinfomanager.topHourUsers.get(i);
			if(usertop != null && usertop.exphour > 0){
				try {
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					
					insertitem = _sqlconnection.prepareStatement("INSERT INTO usersextractions (userid, type, money) VALUES(?,?,?)");
					insertitem.setInt(1, usertop.id);
					insertitem.setInt(2, MessageType.BEST_HOUR);
					insertitem.setInt(3, Config.exphourprizes.get(i));
					insertitem.executeUpdate();
				} catch (SQLException e) {		
					logger.error(" error exp hour" + e.toString());
				}
				finally
				{
				    try{	    	
				    	if (insertitem != null) insertitem.close();		    	
				    	if (_sqlconnection != null) _sqlconnection.close();
				    	insertitem = null;		    	
				    	_sqlconnection = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}						
				sendSystemMessage("Лучший игрок за час. "  + (i + 1) + " место : " + usertop.title + ". Приз - " + Config.exphourprizes.get(i) + " монет.");
			}
		}
		sendSystemMessage("Призы за час будут зачислены на счет победителей в течение 2 минут.");		
		usertop = null;
		setNullExpHour();
		
		ServerApplication.application.userinfomanager.topHourUsersUpdating = false;
	}
	private void setNullExpHour(){		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){			
			user.getValue().user.exphour = 0;
		}
		
		Connection _sqlconnection = null;				
		PreparedStatement uppdate = null;
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			uppdate = _sqlconnection.prepareStatement("UPDATE user SET exphour=?");
			uppdate.setInt(1, 0);
			uppdate.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();		    	
		    	if (uppdate != null) uppdate.close();		    	
		    	_sqlconnection = null;
		    	uppdate = null;		    	
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	
	
	public void sendDayPrize(){
		ServerApplication.application.userinfomanager.topDayUsersUpdating = true;
		
		Connection _sqlconnection = null;
		PreparedStatement insertitem = null;
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){			
			ServerApplication.application.userinfomanager.updateUser(user.getValue().user);
		}
		ServerApplication.application.userinfomanager.updateTopDayUsers();		
		
		UserForTop usertop;
		int countPrizes = Math.min(5, ServerApplication.application.userinfomanager.topDayUsers.size());
		for(int i = 0; i < countPrizes; i++){
			usertop = ServerApplication.application.userinfomanager.topDayUsers.get(i);
			if(usertop != null && usertop.expday > 0){
				try {
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					
					insertitem = _sqlconnection.prepareStatement("INSERT INTO usersextractions (userid, type, money) VALUES(?,?,?)");
					insertitem.setInt(1, usertop.id);
					insertitem.setInt(2, MessageType.BEST_DAY);
					insertitem.setInt(3, Config.expdayprizes.get(i));
					insertitem.executeUpdate();
				} catch (SQLException e) {		
					logger.error(" error exp day" + e.toString());
				}
				finally
				{
				    try{	    	
				    	if (insertitem != null) insertitem.close();		    	
				    	if (_sqlconnection != null) _sqlconnection.close();
				    	insertitem = null;		    	
				    	_sqlconnection = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
				sendSystemMessage("Лучший игрок за день. "  + (i + 1) + " место : " + usertop.title + ". Приз - " + Config.expdayprizes.get(i) + " монет.");				
			}
		}
		sendSystemMessage("Призы за день будут зачислены на счет победителей в течение 2 минут.");		
		usertop = null;
		setNullExpDay();
		
		ServerApplication.application.userinfomanager.topDayUsersUpdating = false;
	}
	private void setNullExpDay(){		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){			
			user.getValue().user.expday = 0;
		}
		
		Connection _sqlconnection = null;				
		PreparedStatement uppdate = null;
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			uppdate = _sqlconnection.prepareStatement("UPDATE user SET expday=?");
			uppdate.setInt(1, 0);
			uppdate.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();		    	
		    	if (uppdate != null) uppdate.close();		    	
		    	_sqlconnection = null;
		    	uppdate = null;		    	
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateUsersExtractions(){
		Connection _sqlconnection = null;
		ResultSet selectRes = null;
		PreparedStatement select = null;
		PreparedStatement delete = null;
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();			
			select = _sqlconnection.prepareStatement("SELECT * FROM usersextractions");	
			
			selectRes = select.executeQuery();    		
			while(selectRes.next()){				
				int eid = selectRes.getInt("id");
				int userid = selectRes.getInt("userid");
				int money = selectRes.getInt("money");
				int type = selectRes.getInt("type");
				
				UserClient user = users.get(Integer.toString(userid));
				if(user != null){
					if(user.client.isConnected()){
						if(type == MessageType.BEST_DAY){
							ServerApplication.application.userinfomanager.addMoney(userid, money, user);
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.BEST_DAY, this.id, money));							
						}else if(type == MessageType.BEST_HOUR){
							ServerApplication.application.userinfomanager.addMoney(userid, money, user);
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.BEST_HOUR, this.id, money));							
						}else if(type == MessageType.KING_BET){
							ServerApplication.application.userinfomanager.addMoney(userid, money, user);
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.KING_BET, this.id, money));							
						}else if(type == MessageType.QUEEN_BET){
							ServerApplication.application.userinfomanager.addMoney(userid, money, user);
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.QUEEN_BET, this.id, money));							
						}else if(type == MessageType.KING){
							user.user.popular = user.user.popular + money;
							ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_POPULAR, user.user.money, user.user.popular);							
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.KING, this.id, money));							
						}else if(type == MessageType.QUEEN){
							user.user.popular = user.user.popular + money;
							ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_POPULAR, user.user.money, user.user.popular);							
							user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.QUEEN, this.id, money));							
						}
						
						delete = _sqlconnection.prepareStatement("DELETE FROM usersextractions WHERE id=?");
						delete.setInt(1, eid);
						delete.executeUpdate();
					}
				}
    		}	
		} catch (SQLException e) {		
			logger.error(" error exp day" + e.toString());
		}
		finally
		{
		    try{	    	
		    	if (select != null) select.close();		    	
		    	if (selectRes != null) selectRes.close();
		    	if (delete != null) delete.close();
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	select = null;	
		    	delete = null;
		    	selectRes = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}
	}
	
	@Override
	public void roomClear(){
		super.roomClear();
		if (servertimer != null){
			servertimer.cancel();
		}
		servertimer = null;
    }
	
	class ServerTimerTask extends TimerTask{
        public void run(){
        	update30second++;
        	update2minute++;
        	update5minute++;
        	try{
	        	date = new Date();
				int currenttime = (int)(date.getTime() / 1000);
				boolean needupdate2minute = false;
				boolean needupdate30second = false;
				
				int second = 1;
				int minute = second * 60;
				int hour = minute * 60;
				int hoursInDay = 24;
				
				int currenthour = (int) Math.floor((float) currenttime / hour);
				int currentminute = (int) Math.floor((float) (currenttime - currenthour * hour) / minute);
//				int currentsecond = currenttime - currentminute * minute - currenthour * hour;				
				
				if(((currenthour + 6) % hoursInDay == 0) && (currentminute == 0) && lastprizehour != currenthour){			//everyday in 22-00
					lastprizehour = currenthour;
					sendDayPrize();
					ShopManager.mapcreators = new HashMap<Integer, Integer>();												//обновляем map создателей карт (3 карты в день)
				}else if(currentminute == 0 && lastprizehour != currenthour){												//every hour
					lastprizehour = currenthour;
					sendHourPrize();					
				}
				
				if (update2minute >= 6 * 2 + 1){
					update2minute = 0;	    				
					needupdate2minute = true;					
    			}
				
				if (update30second >= 3){
					update30second = 0;	    				
					needupdate30second = true;
    			}
				
				if (update5minute >= 6 * 5 + 3){
					update5minute = 0;
    				ServerApplication.application.userinfomanager.updateTopUsers();
    				ServerApplication.application.userinfomanager.updateTopHourUsers();
    				ServerApplication.application.userinfomanager.updateTopDayUsers();
    				ServerApplication.application.userinfomanager.updatePopularTopUsers();    				
    			}
			
				updateTransactionsMM();
				updateTransactionsOD();
				updateTransactionsSite();
				updateTransactionsVK();
				
				if(needupdate2minute){
					updateClansMoney();
					updateUsersExtractions();
				}
				
	        	Set<Entry<String, UserClient>> set = users.entrySet();
	    		for (Map.Entry<String, UserClient> user:set){
	    			if (needupdate2minute == true){
	    			}
	    			
	    			if(needupdate30second){
	    				updateBan(user.getValue(), currenttime);
	    			}
	    		}	    		
        	}catch(Throwable e){
        		logger.log("Throwable: " + e.toString());
        	}
         }  
     }
}
