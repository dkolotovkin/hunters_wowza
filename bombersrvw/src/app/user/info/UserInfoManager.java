package app.user.info;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import app.clan.ClanUserInfo;
import app.game.GameManager;
import app.logger.MyLogger;
import app.message.MessageType;
import app.room.Room;
import app.room.game.GameRoom;
import app.shop.buyresult.BuyErrorCode;
import app.shop.buyresult.BuyResult;
import app.shop.checkluckresult.CheckLuckResult;
import app.shop.item.Item;
import app.user.User;
import app.user.UserClient;
import app.user.UserForTop;
import app.user.UserFriend;
import app.user.UserMailMessage;
import app.user.UserRole;
import app.user.chage.ChangeResult;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.authorization.GameMode;
import app.utils.ban.BanResult;
import app.utils.ban.BanType;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.sex.Sex;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import app.Config;
import app.ServerApplication;

import com.wowza.wms.amf.AMFDataArray;
import com.wowza.wms.client.IClient;

public class UserInfoManager {
	private MyLogger _logger = new MyLogger(UserInfoManager.class.getName());
	public Map<Integer, Integer> levels = new HashMap<Integer, Integer>();
	public List<Integer> popularparts = Arrays.asList(0, 50, 500, 5000, 30000, 100000, 200000, 500000, 2000000, 4000000);
	//public List<Integer> popularparts = Arrays.asList(0, 100, 1000, 10000, 40000, 120000, 250000, 500000, 3000000, 10000000);
	public List<String> populartitles = Arrays.asList("Никому не известный", "Узнаваемый", "Активист", "Популярный", "Уважаемый", "Авторитет", "Звезда", "Король", "Легендарный король");
	
	public ArrayList<UserForTop> topUsers = new ArrayList<UserForTop>();
	public AMFDataArray topUsersAMF = new AMFDataArray();
	public ArrayList<UserForTop> topHourUsers = new ArrayList<UserForTop>();
	public AMFDataArray topHourUsersAMF = new AMFDataArray();
	public ArrayList<UserForTop> topDayUsers = new ArrayList<UserForTop>();
	public AMFDataArray topDayUsersAMF = new AMFDataArray();
	public ArrayList<UserForTop> topPopularUsers = new ArrayList<UserForTop>();
	public AMFDataArray topPopularUsersAMF = new AMFDataArray();
	public int usercounter = 0;
	
	public boolean topHourUsersUpdating;
	public boolean topDayUsersUpdating;
	
	public UserInfoManager(){
		Connection _sqlconnection = null;
		PreparedStatement getlevels = null;	
		ResultSet res = null;
		PreparedStatement selectusers = null;	
		ResultSet selectusersRes = null;
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			getlevels = _sqlconnection.prepareStatement("SELECT * FROM level");
			
			res = getlevels.executeQuery();					
			while(res.next()){
				levels.put(res.getInt("id"), res.getInt("experience"));
    		}
			selectusers = _sqlconnection.prepareStatement("SELECT count(id) FROM user");
			selectusersRes = selectusers.executeQuery();
			selectusersRes.next();
			usercounter = selectusersRes.getInt(1);		
		}catch (SQLException e) {
			_logger.error("UserInfoManager ERROR: " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (getlevels != null) getlevels.close();
		    	if (res != null) res.close();
		    	if (selectusers != null) selectusers.close();
		    	if (selectusersRes != null) selectusersRes.close();
		    	_sqlconnection = null;
		    	getlevels = null;
		    	res = null;
		    	selectusers = null;
		    	selectusersRes = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		
		updateTopUsers();
		updateTopHourUsers();
		updateTopDayUsers();
		updatePopularTopUsers();
	}
	
	public Boolean clientConnect(IClient client, String socialid, String passward, String ip){
		Boolean good = true;
    	if (socialid != null && socialid.length() > 0){
    		if (findAndAddUserToRoom(client, socialid, ip, false)){
    			good = true;
    		}else{
    			if (insertUserToDataBase(client, socialid) > 0){    				
    				if (findAndAddUserToRoom(client, socialid, ip, true)){
    	    			good = true;
    	    		}else{
    	    			good = false;
    	    		}
    			}else{
    				_logger.error("User don't create in DataBase");        				
    				good = false;
    			}
    		}
    	}else{
    		_logger.log("Connect user failed. This is not social game(send title and passward and check user)");
    		good = false;
    	}
    	return good;
	}
	
	public void removeUserByClientID(String connID){
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(connID);
		if(user != null){
			Set<Entry<String, Room>> set = ServerApplication.application.rooms.entrySet();
			for (Map.Entry<String, Room> room:set){
				room.getValue().removeUserByClientID(connID);			
			}
			
			Set<Entry<String, GameRoom>> gset = GameManager.gamerooms.entrySet();		
			for (Map.Entry<String, GameRoom> room:gset){    	
				room.getValue().removeUserByClientID(connID);			
			}
	    	user = null;
		}
	}
	
	public void updateParams(UserClient user, String url){
		Connection _sqlconnection = null;
		PreparedStatement updateparams = null;
		
		try {
			if (user != null && (user.user.url == null || user.user.url.toLowerCase() == "null" || user.user.url.length() == 0) && url != null){
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				updateparams = _sqlconnection.prepareStatement("UPDATE user SET url=? WHERE id=?");
				updateparams.setString(1, url);
				updateparams.setInt(2, user.user.id);				
				updateparams.executeUpdate();
				
				user.user.url = url;
			}
		}catch (SQLException e) {			
			_logger.error("UM2 " + e.toString());
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 		    	
		    	if (updateparams != null) updateparams.close();
		    	_sqlconnection = null;		    	
		    	updateparams = null;
		    }
		    catch (SQLException sqlx) {   
		    }
		}
	}
	
	private Boolean findAndAddUserToRoom(IClient client, String socialid, String ip, Boolean newUser){
    	Boolean good = true;    	
    	Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	PreparedStatement updateparams = null;
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		find = _sqlconnection.prepareStatement("SELECT * FROM user INNER JOIN clan ON user.clanid=clan.id WHERE idsocial=?");
    		find.setString(1, socialid);
    		findRes = find.executeQuery();
    		if (findRes.next()){
    			UserClient user = null;					
    			ClanUserInfo claninfo = new ClanUserInfo(findRes.getInt("user.clanid"), findRes.getString("clan.title"), findRes.getInt("clan.maxusers"), findRes.getInt("user.clandepositm"), findRes.getInt("user.clandeposite"), findRes.getByte("user.clanrole"), findRes.getInt("user.getclanmoneyat"));
    			user = new UserClient(findRes.getInt("user.id"), findRes.getString("user.idsocial"), ip, findRes.getInt("user.sex"),
    										findRes.getString("user.title"), findRes.getInt("user.popular"), findRes.getInt("user.experience"), findRes.getInt("user.bullets"),
    										findRes.getInt("user.exphour"), findRes.getInt("user.expday"),findRes.getInt("user.lastlvl"),
    										findRes.getInt("user.money"), findRes.getByte("user.role"), findRes.getByte("user.king"), (byte)findRes.getInt("user.bantype"),
    										findRes.getInt("user.setbanat"), findRes.getInt("user.changebanat"), findRes.getString("user.url"), claninfo, client);
    			
    			if(topHourUsersUpdating){
    				user.user.exphour = 0;
    			}
    			if(topDayUsersUpdating){
    				user.user.expday = 0;
    			}
    			
    			Date date = new Date();
				int currenttime = (int)(date.getTime() / 1000);
    			int timepassed = currenttime - user.user.setbanat;
    			date = null;
    			
    			int bantime = 0;
    			
    			if(!newUser){
	    			if (user.user.bantype == BanType.NO_BAN){
	    				bantime = 0;
	    			}else if (user.user.bantype == BanType.MINUT5){
	    				bantime = 5 * 60 - timepassed;    				
	    			}else if (user.user.bantype == BanType.MINUT15){
	    				bantime = 15 * 60 - timepassed;    				
	    			}else if (user.user.bantype == BanType.MINUT30){
	    				bantime = 30 * 60 - timepassed;    			
	    			}else if (user.user.bantype == BanType.HOUR1){
	    				bantime = 60 * 60 - timepassed;
	    			}else if (user.user.bantype == BanType.DAY1){
	    				bantime = 60 * 60 * 24 - timepassed;
	    			}
	    			
	    			ServerApplication.application.shopmanager.shopFillItems(user);
    			}
    			
    			if(client.isConnected()){
    				client.call("initPersParams", null, AMFWObjectBuilder.createObjInitUser(user.user, bantime, popularparts, populartitles));
    			}
    			if(user.user.role == UserRole.MODERATOR || user.user.role == UserRole.ADMINISTRATOR || user.user.role == UserRole.ADMINISTRATOR_MAIN){
    				ServerApplication.application.modsroom.addUser(user);
    			}
    			ServerApplication.application.commonroom.addUser(user);
    			if(!newUser) ServerApplication.application.commonroom.updateBan(user, currenttime);
    			if(newUser) ServerApplication.application.commonroom.showStartInfo(user);
    			good = true;    			
    			user = null;
    			client = null;
    		}else{
    			good = false;
    		}
		} catch (Throwable e) {
			_logger.error("findAndAddUserToRoom ERROR: " + e.toString());	
			good = false;
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (find != null) find.close(); 
		    	if (findRes != null) findRes.close();
		    	if (updateparams != null) updateparams.close();
		    	_sqlconnection = null;
		    	find = null;
		    	findRes = null;
		    	updateparams = null;
		    }
		    catch (SQLException sqlx) {     
		    }
		}
    	return good;
    }
    private int insertUserToDataBase(IClient client, String socialid){
    	int countInsert = 0;
    	Connection _sqlconnection = null;
    	PreparedStatement insert = null;
    	
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		usercounter++;
    		
    		insert = _sqlconnection.prepareStatement("INSERT INTO user (idsocial, sex, title, money) VALUES(?,?,?,?)");
    		insert.setString(1, socialid);
    		insert.setInt(2, Sex.MALE);
    		insert.setString(3, "Игрок " + usercounter);
    		insert.setInt(4, 50);    		
			countInsert = insert.executeUpdate();
		} catch (SQLException e) {
			_logger.error("UM4 " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (insert != null) insert.close();
		    	_sqlconnection = null;
		    	insert = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
    	return countInsert;
    }
	
	public ChangeResult changeInfo(String title, int sex, String clientConnID, ChangeResult result, boolean startMode){
		Connection _sqlconnection = null;
		PreparedStatement finduser = null;
		ResultSet finduserRes = null;
		PreparedStatement update = null;
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		finduser = _sqlconnection.prepareStatement("SELECT * FROM user WHERE title=?");
    		finduser.setString(1, title);
    		finduserRes = finduser.executeQuery();    		
    		if (finduserRes.next() && startMode == false){
    			result.errorCode = ChangeResult.USER_EXIST;
    		}else{
    			UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(clientConnID);
    			if(initiator != null && initiator.client.isConnected()){
	    			if (initiator.user.money >= Config.changeInfoPrice() || startMode == true){
	    				
	    				if(startMode == false){	    					
	    					initiator.user.updateMoney(initiator.user.money - Config.changeInfoPrice());
	    					ServerApplication.application.commonroom.changeUserInfoByID(initiator.user.id, ChangeInfoParams.USER_MONEY, initiator.user.money, 0);
	    				}
	    				update = _sqlconnection.prepareStatement("UPDATE user SET title=?,sex=? WHERE id=?");
	    				update.setString(1, title);
	    				update.setInt(2, sex);    				
	    				update.setInt(3, initiator.user.id);
	    				
	            		if (update.executeUpdate() > 0){
	            			initiator.user.title = title;
	            			initiator.user.sex = sex;
	            			
	            			result.errorCode = ChangeResult.OK;                			
	            			result.user = initiator.user;            			
	            		}else{
	            			result.errorCode = ChangeResult.UNDEFINED;             			
	            		}                		
	    			}else{
	    				result.errorCode = ChangeResult.NO_MONEY;        				
	    			}
    			}
        		initiator = null;
    		}	
		} catch (SQLException e) {		
			_logger.error("UM5 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close(); 
		    	
		    	if (finduser != null)  finduser.close(); 
		    	if (finduserRes != null)  finduserRes.close();
		    	if (update != null)  update.close();
		    	finduser = null;
		    	finduserRes = null;
		    	update = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		return result;
	}	
	
	public Boolean addMoney(int iduser, int addmoney, UserClient user){
		Connection _sqlconnection = null;
		PreparedStatement pstm = null;
		ResultSet res = null;
		PreparedStatement update = null;
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		if(user != null){
    			user.user.updateMoney(user.user.money + addmoney);
    			ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
    			
    			update = _sqlconnection.prepareStatement("UPDATE user SET money=? WHERE id=?");
				update.setInt(1, user.user.money);
				update.setInt(2, iduser);				
        		if (update.executeUpdate() > 0){
        			update.close();
        			return true;
        		}
    		}else{    		
	    		pstm = _sqlconnection.prepareStatement("SELECT * FROM user WHERE id=?");
	    		pstm.setInt(1, iduser);
	    		res = pstm.executeQuery();
	    		if (!res.next()){
	    		}else{    			
	    			update = _sqlconnection.prepareStatement("UPDATE user SET money=? WHERE id=?");
					update.setInt(1, Math.max(0, res.getInt("money") + addmoney));
					update.setInt(2, iduser);				
	        		if (update.executeUpdate() > 0){
	        			update.close();
	        			return true;
	        		}
	    		}
    		}
		} catch (SQLException e) {		
			_logger.error("UM6 " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (pstm != null) pstm.close(); 
		    	if (res != null) res.close(); 
		    	if (update != null) update.close();
		    	_sqlconnection = null;
		    	pstm = null;
		    	res = null;
		    	update = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}
		return false;
	}
	
	public byte ban(int bannedId, String initiatorConnId, byte type, boolean byip){	
		byte errorCode = 0;
		
		int price = 0;
		if (type == BanType.MINUT5){
			price = 100;
		}else if (type == BanType.MINUT15){
			price = 400;
		}else if (type == BanType.MINUT30){
			price = 800;
		}else if (type == BanType.HOUR1){
			price = 2000;
		}else if (type == BanType.DAY1){
			price = 25000;
		}
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(initiatorConnId);
		UserClient banned = ServerApplication.application.commonroom.users.get(Integer.toString(bannedId));
		
		Connection _sqlconnection = null;
		PreparedStatement updatemoney = null;
		PreparedStatement setban = null;
		
		Date date = new Date();
		int currenttime = (int)(date.getTime() / 1000);
		date = null; 		
		
		if (initiator.user.role == UserRole.MODERATOR || initiator.user.role == UserRole.ADMINISTRATOR){
			price = 0;//(int) Math.floor((float) price / 10);
		}else if (initiator.user.role == UserRole.ADMINISTRATOR_MAIN){
			price = 0;
		}else{
			return BanResult.OTHER;
		}
		
		if(initiator != null && initiator.client.isConnected()){
			if (initiator.user.money >= price){
				if(banned != null && banned.user.bantype <= type){
					initiator.user.updateMoney(initiator.user.money - price);
					ServerApplication.application.commonroom.changeUserInfoByID(initiator.user.id, ChangeInfoParams.USER_MONEY, initiator.user.money, 0);
					
					try {						
						_sqlconnection = ServerApplication.application.sqlpool.getConnection();
						
						if((initiator.user.role == UserRole.ADMINISTRATOR || initiator.user.role == UserRole.ADMINISTRATOR_MAIN) && byip){
							setban = _sqlconnection.prepareStatement("UPDATE user SET bantype=?,setbanat=?,changebanat=? WHERE id=? or ip=?");
	        				setban.setInt(1, type);
	        				setban.setInt(2, currenttime);
	        				setban.setInt(3, currenttime);
	        				setban.setInt(4, bannedId);
	        				setban.setString(5, banned.user.ip);
	        				if (setban.executeUpdate() > 0){
	        					errorCode = BanResult.OK;
	        					
	        					Set<Entry<String, UserClient>> set = ServerApplication.application.commonroom.users.entrySet();
	        					for (Map.Entry<String, UserClient> user:set){        						
	        						if(user.getValue().user.ip.toString().equals(banned.user.ip)){
	        							user.getValue().user.setbanat = currenttime;
	        							user.getValue().user.changebanat = currenttime;
	        							user.getValue().user.bantype = type;
	            						
	                					ServerApplication.application.commonroom.sendBanMessage(user.getValue().user.id, initiatorConnId, type);
	        						}
	        					}			
	        					set = null;
	        				}		       
						}else{
							setban = _sqlconnection.prepareStatement("UPDATE user SET bantype=?,setbanat=?,changebanat=? WHERE id=?");
	        				setban.setInt(1, type);
	        				setban.setInt(2, currenttime);
	        				setban.setInt(3, currenttime);
	        				setban.setInt(4, bannedId);
	        				if (setban.executeUpdate() > 0){
	        					errorCode = BanResult.OK;
	        					
	        					banned.user.setbanat = currenttime;
	        					banned.user.changebanat = currenttime;
	        					banned.user.bantype = type;
	        					
            					ServerApplication.application.commonroom.sendBanMessage(banned.user.id, initiatorConnId, type);
	        				}
						}
					}catch (SQLException e) {
						_logger.error("UM8 " + e.toString());
					}
					finally
					{
					    try{
					    	if (_sqlconnection != null) _sqlconnection.close();
					    	if (updatemoney != null) updatemoney.close();
					    	if (setban != null) setban.close();		
					    	_sqlconnection = null;
					    	updatemoney = null;
					    	setban = null;
					    }
					    catch (SQLException sqlx) {   
					    }
					}
				}else{
					errorCode = BanResult.OTHER; 
				}
			}else{
				errorCode = BanResult.NO_MONEY; 
			}
		}else{
			errorCode = BanResult.OTHER;
		}
		
		initiator = null;
		banned = null;
		return errorCode;
	}	
	
	public void banoff(int userId, String userIp){
		Connection _sqlconnection = null;
		PreparedStatement banoffst = null;		
				
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			banoffst = _sqlconnection.prepareStatement("UPDATE user SET bantype=?,setbanat=? WHERE id=?");
			banoffst.setInt(1, BanType.NO_BAN);
			banoffst.setInt(2, 0);
			banoffst.setInt(3, userId);			
			if (banoffst.executeUpdate() > 0){
			}   			
		}catch (SQLException e) {
			_logger.error("UM9 " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (banoffst != null) banoffst.close();
		    	_sqlconnection = null;
		    	banoffst = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void updateTopUsers(){		
		byte from = 1;
		byte to = 125;
		byte limit = 30;
		
		Connection _sqlconnection = null;
		PreparedStatement pstm = null;	
		ResultSet resExp = null;
		
		int fromvalue = Integer.MAX_VALUE;
		int tovalue = Integer.MAX_VALUE;
		if(levels.get(new Integer(from)) != null) fromvalue = levels.get(new Integer(from));
		if (levels.get(new Integer(to + 1)) != null) tovalue = levels.get(new Integer(to + 1)) - 1;
		
		topUsers.clear();
		topUsersAMF = null;
		topUsersAMF = new AMFDataArray();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			pstm = _sqlconnection.prepareStatement("SELECT * FROM user WHERE experience>=? AND experience<=? ORDER BY experience DESC LIMIT ?");
			pstm.setInt(1, fromvalue);
			pstm.setInt(2, tovalue);
			pstm.setInt(3, limit);
			resExp = pstm.executeQuery();
			while(resExp.next()){				
				UserForTop u = new UserForTop(resExp.getInt("id"), resExp.getInt("sex"), resExp.getString("title"), getLevelByExperience(resExp.getInt("experience")), resExp.getInt("exphour"), resExp.getInt("expday"), resExp.getInt("popular"), resExp.getString("url"));
				
				if(ServerApplication.application.commonroom.users.get(Integer.toString(u.id)) != null){
    				u.isonline = true;
    			}else{
    				u.isonline = false;
    			}
				
				topUsers.add(u);
    		}
			topUsersAMF = AMFWObjectBuilder.createObjUsersForTop(topUsers);			
		}catch (SQLException e) {
			_logger.error("UM12 " + e.toString());
		}
		finally
		{
		    try{		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (pstm != null) pstm.close();
		    	if (resExp != null) resExp.close();
		    	_sqlconnection = null;
		    	pstm = null;
		    	resExp = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}   	
    }
	
	public void updateTopHourUsers(){	
		byte limit = 10;
		
		Connection _sqlconnection = null;
		PreparedStatement pstm = null;	
		ResultSet resExp = null;		
		
		topHourUsers.clear();
		topHourUsersAMF = null;
		topHourUsersAMF = new AMFDataArray();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			pstm = _sqlconnection.prepareStatement("SELECT * FROM user ORDER BY exphour DESC LIMIT ?");			
			pstm.setInt(1, limit);
			resExp = pstm.executeQuery();
			while(resExp.next()){				
				UserForTop u = new UserForTop(resExp.getInt("id"), resExp.getInt("sex"), resExp.getString("title"), getLevelByExperience(resExp.getInt("experience")), resExp.getInt("exphour"), resExp.getInt("expday"), resExp.getInt("popular"), resExp.getString("url"));
				
				if(ServerApplication.application.commonroom.users.get(Integer.toString(u.id)) != null){
    				u.isonline = true;
    			}else{
    				u.isonline = false;
    			}
				
				topHourUsers.add(u);
    		}
			topHourUsersAMF = AMFWObjectBuilder.createObjUsersForTop(topHourUsers);
		}catch (SQLException e) {
			_logger.error("UM12 " + e.toString());
		}
		finally
		{
		    try{		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (pstm != null) pstm.close();
		    	if (resExp != null) resExp.close();
		    	_sqlconnection = null;
		    	pstm = null;
		    	resExp = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
    }
	
	public void updateTopDayUsers(){	
		byte limit = 10;
		
		Connection _sqlconnection = null;
		PreparedStatement pstm = null;	
		ResultSet resExp = null;		
		
		topDayUsers.clear();
		topDayUsersAMF = null;
		topDayUsersAMF = new AMFDataArray();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			pstm = _sqlconnection.prepareStatement("SELECT * FROM user ORDER BY expday DESC LIMIT ?");			
			pstm.setInt(1, limit);
			resExp = pstm.executeQuery();
			while(resExp.next()){				
				UserForTop u = new UserForTop(resExp.getInt("id"), resExp.getInt("sex"), resExp.getString("title"), getLevelByExperience(resExp.getInt("experience")), resExp.getInt("exphour"), resExp.getInt("expday"), resExp.getInt("popular"), resExp.getString("url"));
				
				if(ServerApplication.application.commonroom.users.get(Integer.toString(u.id)) != null){
    				u.isonline = true;
    			}else{
    				u.isonline = false;
    			}
				
				topDayUsers.add(u);
    		}
			topDayUsersAMF = AMFWObjectBuilder.createObjUsersForTop(topDayUsers);
		}catch (SQLException e) {
			_logger.error("UM12 " + e.toString());
		}
		finally
		{
		    try{		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (pstm != null) pstm.close();
		    	if (resExp != null) resExp.close();
		    	_sqlconnection = null;
		    	pstm = null;
		    	resExp = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}   	
    }
	
	public void updatePopularTopUsers(){		
		byte limit = 30;
		
		Connection _sqlconnection = null;
		PreparedStatement pstm = null;	
		ResultSet resExp = null;		
		
		topPopularUsers.clear();
		topPopularUsersAMF = null;
		topPopularUsersAMF = new AMFDataArray();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			pstm = _sqlconnection.prepareStatement("SELECT * FROM user ORDER BY popular DESC LIMIT ?");			
			pstm.setInt(1, limit);
			resExp = pstm.executeQuery();
			while(resExp.next()){				
				UserForTop u = new UserForTop(resExp.getInt("id"), resExp.getInt("sex"), resExp.getString("title"), getLevelByExperience(resExp.getInt("experience")), resExp.getInt("exphour"), resExp.getInt("expday"), resExp.getInt("popular"), resExp.getString("url"));
				
				if(ServerApplication.application.commonroom.users.get(Integer.toString(u.id)) != null){
    				u.isonline = true;
    			}else{
    				u.isonline = false;
    			}
				
				topPopularUsers.add(u);
    		}
			topPopularUsersAMF = AMFWObjectBuilder.createObjUsersForTop(topPopularUsers);
		}catch (SQLException e) {
			_logger.error("UM13 " + e.toString());
		}
		finally
		{
		    try{		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (pstm != null) pstm.close();
		    	if (resExp != null) resExp.close();
		    	_sqlconnection = null;
		    	pstm = null;
		    	resExp = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
    }
	
	public void addToFriend(int uid,int fid){
		Connection _sqlconnection = null;
		PreparedStatement findfriend = null;
		ResultSet findfriendRes = null;
		PreparedStatement insertfriend = null;		
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		findfriend = _sqlconnection.prepareStatement("SELECT * FROM friends WHERE uid=? AND fid=?");
    		findfriend.setInt(1, uid);
    		findfriend.setInt(2, fid);
    		findfriendRes = findfriend.executeQuery();    		
    		if (findfriendRes.next()){    			
    			return;		
    		}else{    			
    			insertfriend = _sqlconnection.prepareStatement("INSERT INTO friends (uid, fid) VALUES(?,?)");    			
    			insertfriend.setInt(1, uid);
    			insertfriend.setInt(2, fid); 			    		
    			insertfriend.executeUpdate();
    		}	
		} catch (SQLException e) {		
			_logger.error("UM14 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close();
		    	if (findfriend != null)  findfriend.close(); 
		    	if (findfriendRes != null)  findfriendRes.close(); 
		    	if (insertfriend != null)  insertfriend.close();
		    	findfriend = null;
		    	findfriendRes = null;
		    	insertfriend = null;		    	
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void addToEnemy(int uid,int eid){
		Connection _sqlconnection = null;
		PreparedStatement findenemy = null;
		ResultSet findenemyRes = null;
		PreparedStatement insertenemy = null;		
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		findenemy = _sqlconnection.prepareStatement("SELECT * FROM enemies WHERE uid=? AND eid=?");
    		findenemy.setInt(1, uid);
    		findenemy.setInt(2, eid);
    		findenemyRes = findenemy.executeQuery();    		
    		if (findenemyRes.next()){    			
    			return;		
    		}else{
    			insertenemy = _sqlconnection.prepareStatement("INSERT INTO enemies (uid, eid) VALUES(?,?)");    			
    			insertenemy.setInt(1, uid);
    			insertenemy.setInt(2, eid); 			    		
    			insertenemy.executeUpdate();
    		}	
		} catch (SQLException e) {		
			_logger.error("UM40 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close();
		    	if (findenemy != null)  findenemy.close(); 
		    	if (findenemyRes != null)  findenemyRes.close(); 
		    	if (insertenemy != null)  insertenemy.close();
		    	findenemy = null;
		    	findenemyRes = null;
		    	insertenemy = null;		    	
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void removeFriend(int uid,int fid){
		Connection _sqlconnection = null;
		PreparedStatement deletefriend = null;			
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		
    		deletefriend = _sqlconnection.prepareStatement("DELETE FROM friends WHERE uid=? AND fid=?");
    		deletefriend.setInt(1, uid);
    		deletefriend.setInt(2, fid);
    		deletefriend.executeUpdate();
		} catch (SQLException e) {		
			_logger.error("UM15 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close();
		    	if (deletefriend != null)  deletefriend.close();
		    	deletefriend = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public void removeEnemy(int uid,int eid){
		Connection _sqlconnection = null;
		PreparedStatement deleteenemy = null;			
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		
    		deleteenemy = _sqlconnection.prepareStatement("DELETE FROM enemies WHERE uid=? AND eid=?");
    		deleteenemy.setInt(1, uid);
    		deleteenemy.setInt(2, eid);
    		deleteenemy.executeUpdate();
		} catch (SQLException e) {		
			_logger.error("UM15 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close();
		    	if (deleteenemy != null)  deleteenemy.close();
		    	deleteenemy = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public ArrayList<UserFriend> getFiends(int uid){		
    	Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	
    	ArrayList<UserFriend> friends = new ArrayList<UserFriend>();
    	
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
    		find = _sqlconnection.prepareStatement("SELECT * FROM friends INNER JOIN user ON friends.fid=user.id WHERE friends.uid=?");
    		find.setInt(1, uid);
    		findRes = find.executeQuery();    		
    		while (findRes.next()){
    			UserFriend user = new UserFriend(findRes.getInt("user.id"), findRes.getString("user.title"), findRes.getInt("user.experience"), findRes.getInt("user.popular"), findRes.getString("user.url"));
    			
    			if(ServerApplication.application.commonroom.users.get(Integer.toString(user.id)) != null){
    				user.isonline = true;
    			}else{
    				user.isonline = false;
    			}
    			
    			friends.add(user);    			
    		}
		} catch (SQLException e) {			
			_logger.error("UM16 " + e.toString());
			
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (find != null) find.close(); 
		    	if (findRes != null) findRes.close();		    	
		    	_sqlconnection = null;
		    	find = null;
		    	findRes = null;		    	
		    }
		    catch (SQLException sqlx) {     
		    }
		}
    	return friends;
	}
	
	public ArrayList<UserFriend> getEnemies(int uid){		
    	Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	
    	ArrayList<UserFriend> friends = new ArrayList<UserFriend>();
    	
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
    		find = _sqlconnection.prepareStatement("SELECT * FROM enemies INNER JOIN user ON enemies.eid=user.id WHERE enemies.uid=?");
    		find.setInt(1, uid);
    		findRes = find.executeQuery();    		
    		while (findRes.next()){
    			UserFriend user = new UserFriend(findRes.getInt("user.id"), findRes.getString("user.title"), findRes.getInt("user.experience"), findRes.getInt("user.popular"), findRes.getString("user.url"));
    			
    			if(ServerApplication.application.commonroom.users.get(Integer.toString(user.id)) != null){
    				user.isonline = true;
    			}else{
    				user.isonline = false;
    			}
    			
    			friends.add(user);    			
    		}
		} catch (SQLException e) {			
			_logger.error("UM16 " + e.toString());
			
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (find != null) find.close(); 
		    	if (findRes != null) findRes.close();		    	
		    	_sqlconnection = null;
		    	find = null;
		    	findRes = null;		    	
		    }
		    catch (SQLException sqlx) {     
		    }
		}
    	return friends;
	}
	
	public UserClient getOfflineUser(int userID){    	
    	Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
    		find = _sqlconnection.prepareStatement("SELECT * FROM user INNER JOIN clan ON user.clanid=clan.id WHERE user.id=?");
    		find.setInt(1, userID);
    		findRes = find.executeQuery();    		
    		if (findRes.next()){
    			UserClient user = null;    					
    			ClanUserInfo claninfo = new ClanUserInfo(findRes.getInt("user.clanid"), findRes.getString("clan.title"), findRes.getInt("clan.maxusers"), findRes.getInt("user.clandepositm"), findRes.getInt("user.clandeposite"), findRes.getByte("user.clanrole"), findRes.getInt("user.getclanmoneyat"));
    			
    			user = new UserClient(findRes.getInt("user.id"), findRes.getString("user.idsocial"), findRes.getString("user.ip"), findRes.getInt("user.sex"), 
    										findRes.getString("user.title"), findRes.getInt("user.popular"), findRes.getInt("user.experience"), findRes.getInt("user.bullets"),
    										findRes.getInt("user.exphour"), findRes.getInt("user.expday"), findRes.getInt("user.lastlvl"),
    										findRes.getInt("user.money"), findRes.getByte("user.role"), findRes.getByte("user.king"), (byte)findRes.getInt("user.bantype"),
    										findRes.getInt("user.setbanat"), findRes.getInt("user.changebanat"), findRes.getString("user.url"), claninfo, null);
    			claninfo = null;
    			return user;
    		}
		} catch (SQLException e) {			
			_logger.error("UM17 " + e.toString());
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (find != null) find.close(); 
		    	if (findRes != null) findRes.close();		    	
		    	_sqlconnection = null;
		    	find = null;
		    	findRes = null;		    	
		    }
		    catch (SQLException sqlx) {     
		    }
		}
		return null;
    }
	
	public BuyResult sendMail(UserClient fromUser, int toID, String msg){
		BuyResult buyresult = new BuyResult();
		buyresult.error = BuyErrorCode.OTHER;
		
		Connection _sqlconnection = null;		
		PreparedStatement insert = null;
		
		if(fromUser != null){		
			if(fromUser.user.money > Config.sendMailPrice()){		
		    	try {		    		
		    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
		    		insert = _sqlconnection.prepareStatement("INSERT INTO messages (fromid, toid, msg) VALUES(?,?,?)");
		    		insert.setInt(1, fromUser.user.id);
		    		insert.setInt(2, toID);
		    		insert.setString(3, msg);
		    		insert.executeUpdate();
					
					buyresult.error = BuyErrorCode.OK;
					
					fromUser.user.updateMoney(fromUser.user.money - Config.sendMailPrice());
					ServerApplication.application.commonroom.changeUserInfoByID(fromUser.user.id, ChangeInfoParams.USER_MONEY, fromUser.user.money, 0);
				} catch (SQLException e) {		
					_logger.error("UM18 " + e.toString());
				}	
				finally
				{
				    try{
				    	if (_sqlconnection != null)  _sqlconnection.close();		    	
				    	if (insert != null)  insert.close();		    	
				    	insert = null;		    	
				    	_sqlconnection = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
			}else{
				buyresult.error = BuyErrorCode.NOT_ENOUGH_MONEY;
			}
		}
		return buyresult;
	}
	
	public ArrayList<UserMailMessage> getMailMessages(int uid){		
    	Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	
    	ArrayList<UserMailMessage> friends = new ArrayList<UserMailMessage>();
    	
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
    		find = _sqlconnection.prepareStatement("SELECT * FROM messages INNER JOIN user ON messages.fromid=user.id WHERE messages.toid=? ORDER BY messages.id DESC");
    		find.setInt(1, uid);
    		findRes = find.executeQuery();    		
    		while (findRes.next()){
    			UserMailMessage user = new UserMailMessage(findRes.getInt("user.id"), findRes.getString("user.title"), findRes.getInt("user.experience"), findRes.getInt("user.popular"), findRes.getString("user.url"));
    			
    			if(ServerApplication.application.commonroom.users.get(Integer.toString(user.id)) != null){
    				user.isonline = true;
    			}else{
    				user.isonline = false;
    			}
    			user.message = findRes.getString("messages.msg");
    			user.messageid = findRes.getInt("messages.id");
    			
    			friends.add(user);
    		}
		} catch (SQLException e) {			
			_logger.error("UM19 " + e.toString());
			
		} 
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close(); 
		    	if (find != null) find.close(); 
		    	if (findRes != null) findRes.close();		    	
		    	_sqlconnection = null;
		    	find = null;
		    	findRes = null;		    	
		    }
		    catch (SQLException sqlx) {     
		    }
		}
    	return friends;
	}
	
	public void removeMailMessage(int mid){
		Connection _sqlconnection = null;
		PreparedStatement delete = null;			
		
    	try {
    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();
    		
    		delete = _sqlconnection.prepareStatement("DELETE FROM messages WHERE id=?");
    		delete.setInt(1, mid);
    		delete.executeUpdate();
		} catch (SQLException e) {		
			_logger.error("UM20 " + e.toString());
		}	
		finally
		{
		    try{
		    	if (_sqlconnection != null)  _sqlconnection.close();
		    	if (delete != null)  delete.close();
		    	delete = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	public int getFriendsBonus(UserClient user, String vid, int mode, int appID, String sessionKey){
		if(user != null){
			if (mode == GameMode.DEBUG){			
		    	return 6;
	    	}else if (mode == GameMode.VK){
	    		return 0;
	    	}else if (mode == GameMode.MM){
	    		int countfriends = 0;
	    		
	    		 MessageDigest md5;
	    		 String sigstr = "app_id=" + appID + "format=xmlmethod=friends.getAppUserssecure=1session_key=" + sessionKey + Config.protectedSecretMM();
	    		 String sig = null;
				 try {
					 md5 = MessageDigest.getInstance("MD5");
					 md5.reset();
					 md5.update(sigstr.getBytes());
					 byte[] bytecode = md5.digest();	 
					 StringBuffer hexString = new StringBuffer();
					 for (int i = 0; i < bytecode.length; i++) {
						 String hex = Integer.toHexString(0xff & bytecode[i]);
			   	     	 if(hex.length() == 1) hexString.append('0');
			   	      	 hexString.append(hex);
		             }
					 
					 sig = hexString.toString();
					 
					 bytecode = null;
					 hexString = null;
				 } catch (NoSuchAlgorithmException e) {
					 ServerApplication.application.logger.log("Error getting MD5 object" + e);
				 }
				 md5 = null;			 
				
				String urlstr = "http://www.appsmail.ru/platform/api?method=friends.getAppUsers&app_id=" +
				appID + "&format=xml" +"&session_key=" + sessionKey + "&secure=1" + "&sig=" + sig;
				
				try{			 
					URL url = new URL(urlstr);
					
					HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					urlConnection.setUseCaches(false);
					urlConnection.setRequestMethod("GET");
							         
					InputStream is = urlConnection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					String line;
					StringBuffer response = new StringBuffer(); 
					while((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();
					String answer = response.toString();			
					
					Document document = null;
			        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			        factory.setValidating(false);
			        factory.setNamespaceAware(true);       
			        try
			        { 
						if(answer.indexOf("user") == -1) {
//							 ServerApplication.application.logger.log("BAD ANSWER BONUS: " + answer);
							 answer = "bad answer";	
							 
							 countfriends = 0;
						}else{
							InputStream istr = new ByteArrayInputStream(answer.getBytes());
							DocumentBuilder builder = factory.newDocumentBuilder();
							document = builder.parse(istr);
							document.getDocumentElement().normalize();
							NodeList nodelist = document.getDocumentElement().getElementsByTagName("user");
							
							countfriends = Math.min(10, nodelist.getLength());
							
				            istr.close();
				            istr = null;
				            builder = null;
				            nodelist = null;
						}
			        }
			        catch (Exception e){
			        	ServerApplication.application.logger.log("UM21 " + e.toString());        	
			        }
			         
			        factory = null;
			        document = null;
				
			        is.close();
			        is = null;
			        urlConnection = null;
				}catch(IOException e){
					ServerApplication.application.logger.log("UM22 " + e.toString());
				}
				
				int friendsdelta = 0;
				
				Connection _sqlconnection = null;
				PreparedStatement find = null;
				ResultSet findRes = null;	
				PreparedStatement update = null;
			
				try {
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					find = _sqlconnection.prepareStatement("SELECT * FROM user WHERE id=?");
					find.setInt(1, user.user.id);
					findRes = find.executeQuery();    		
					if(findRes.next()){
						int countfriendsDB = 0;
						countfriendsDB = findRes.getInt("countfriends");
						friendsdelta = Math.max(0, (countfriends - countfriendsDB));
						
						if(friendsdelta > 0){
							ServerApplication.application.logger.log("User id = " + user.user.id + " invite " + friendsdelta + " new friends. Bonus: " + (friendsdelta * Config.friendBonus()));
							
							user.user.updateMoney(user.user.money + friendsdelta * Config.friendBonus());
		    				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
		    				
		    				update = _sqlconnection.prepareStatement("UPDATE user SET countfriends=? WHERE id=?");
		    				update.setInt(1, countfriends);
		    				update.setInt(2, user.user.id);
		    				update.executeUpdate();
						}
		    		}
				} catch (SQLException e) {				
					ServerApplication.application.logger.error("UM23 " + e.toString());
				}
				finally
				{
				    try{
				    	if (find != null) find.close();
				    	if (findRes != null) findRes.close();		    	
				    	if (update != null) update.close();
				    	if (_sqlconnection != null) _sqlconnection.close();
				    	find = null;
				    	findRes = null;
				    	update = null;
				    	_sqlconnection = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
				
				return friendsdelta;
	    	}
		}
		return 0;
	}
	
	public void setBonusNewLevel(int userID){
		UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userID));
		if(user != null){
			int prize = user.user.level * user.user.level * 8;
			user.user.updateMoney(user.user.money + prize);
			
			user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageNewLevel(MessageType.NEW_LEVEL, 0, user.user.level, prize));
		}
	}	
	
	public CheckLuckResult checkLuck(UserClient user, int bet){
		CheckLuckResult result = new CheckLuckResult();
		if(user != null){
			if(bet >= 5){
				if(user.user.money >= bet){					
					int v =(int) Math.floor((float) Math.random() * 14) - 3;
					if (v < 0){
						v =(int) Math.floor((float) Math.random() * 4);
					}
					float k = (float) 1 / 5;
					int win = (int) Math.floor((float) k * v * bet);
					user.user.updateMoney(user.user.money - bet + win);
					result.result = v;
					result.addmoney = win;
					result.usermoney = user.user.money;
					result.error = BuyErrorCode.OK;
					
					if(win > 1000){
						ServerApplication.application.commonroom.sendSystemMessage("Поздравляем " + user.user.title + "! Выигрыш на колеcе фортуны: " + win + " монет.");
					}
				}else{
					result.error = BuyErrorCode.NOT_ENOUGH_MONEY;
				}
			}else{
				result.error = BuyErrorCode.MIN_BET;
			}
		}else{
			result.error = BuyErrorCode.OTHER;
		}
		return result;
	}
	
	public int getLevelByExperience(int exp){
		for(int i = 1; i <= levels.size(); i++){
			if (levels.get(new Integer(i)) >  exp){
				return (i - 1);
			}
		}
		return 1;
	}
	
	public void updateUser(User user){
		if(user != null){			
			Connection _sqlconnection = null;				
			PreparedStatement uppdate = null;
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				uppdate = _sqlconnection.prepareStatement("UPDATE user SET ip=?,experience=?,bullets=?,exphour=?,expday=?,money=?,clandepositm=?,clandeposite=?,getclanmoneyat=?,changebanat=?,popular=? WHERE id=?");
				uppdate.setString(1, user.ip);
				uppdate.setInt(2, user.experience);
				uppdate.setInt(3, user.bullets);
				uppdate.setInt(4, user.exphour);
				uppdate.setInt(5, user.expday);
				uppdate.setInt(6, user.money);
				uppdate.setInt(7, user.claninfo.clandepositm);
				uppdate.setInt(8, user.claninfo.clandeposite);
				uppdate.setInt(9, user.claninfo.getclanmoneyat);
				uppdate.setInt(10, user.changebanat);
				uppdate.setInt(11, user.popular);
				uppdate.setInt(12, user.id);
				
				uppdate.executeUpdate();
				
				if(user.itemsArr != null){
					Item item;
					for(int i = 0; i < user.itemsArr.size(); i++){	
						item = user.itemsArr.get(i);
						if(item != null && item.change){
							uppdate = _sqlconnection.prepareStatement("UPDATE usersitems SET quality=?,maxquality=?,onuser=? WHERE id=?");
							uppdate.setInt(1, item.quality);
							uppdate.setInt(2, item.maxquality);
							uppdate.setInt(3, item.onuser);
							uppdate.setInt(4, item.id);
							
							if(uppdate.executeUpdate() > 0) item.change = false;
						}
					}
					item = null;
				}
			} catch (SQLException e) {
				ServerApplication.application.logger.error(e.toString());
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
		}else{
			ServerApplication.application.logger.error("Null user Update");
		}
	}
	
	//зарплата	
	public int getOnlineTimeMoneyInfo(UserClient initiator){
		if(initiator != null){
			Date date = new Date();
			int currenttime = (int)(date.getTime() / 1000);
			int starttime = ServerApplication.application.commonroom.userstimeonline.get(initiator.user.id);
			if(starttime > 0){
				int seconds = (currenttime - starttime);
				int minute = (int) Math.floor((float)seconds / 60);
				int money = (int) Math.floor((float) minute * 8 / 15);
				if(initiator.user.role == UserRole.ADMINISTRATOR || initiator.user.role == UserRole.MODERATOR){
					money = money * 3;
				}
				return money;
			}
		}
		return 0;
	}
	
	public void getOnlineTimeMoney(UserClient initiator){		
		int money = getOnlineTimeMoneyInfo(initiator);
		if(money > 0){
			Date date = new Date();
			int currenttime = (int)(date.getTime() / 1000);
			ServerApplication.application.commonroom.userstimeonline.remove(initiator.user.id);
			ServerApplication.application.commonroom.userstimeonline.put(initiator.user.id, currenttime);
			
			initiator.user.updateMoney(initiator.user.money + money);
			initiator.user.popular = Math.max(0, initiator.user.popular - (int)Math.ceil((float) money / 5));
			ServerApplication.application.commonroom.changeUserInfoByID(initiator.user.id, ChangeInfoParams.USER_MONEY_POPULAR, initiator.user.money, initiator.user.popular, 0);
		}
	}
}
