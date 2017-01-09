package app.clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import app.Config;
import app.ServerApplication;
import app.logger.MyLogger;
import app.message.MessageType;
import app.shop.buyresult.BuyErrorCode;
import app.shop.buyresult.BuyResult;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class ClanManager extends ModuleBase{
	public MyLogger logger = new MyLogger(ClanManager.class.getName());
	
	public void clanGetClansInfo(IClient client, RequestFunction function, AMFDataList params){
		ArrayList<ClanInfo> clans = new ArrayList<ClanInfo>();
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			select = _sqlconnection.prepareStatement("SELECT * FROM clan INNER JOIN user ON clan.ownerid=user.id ORDER BY clan.experience DESC");
			selectRes = select.executeQuery();    		
    		while(selectRes.next()){
    			if(selectRes.getInt("id") > 0){
	    			ClanInfo clan = new ClanInfo(selectRes.getInt("id"), selectRes.getString("title"), selectRes.getInt("maxusers"), selectRes.getString("user.title"), selectRes.getInt("ownerid"), selectRes.getInt("user.money"), selectRes.getInt("clan.experience"));
	    			clans.add(clan);
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
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		
		//return ArrayList<ClanInfo>
		sendResult(client, params, AMFWObjectBuilder.createObjClansInfo(clans));  	
    	return;	
	}
	
	public void clanGetClanAllInfo(IClient client, RequestFunction function, AMFDataList params){
    	int clanID = getParamInt(params, PARAM1);
    	
		ArrayList<UserOfClan> users = new ArrayList<UserOfClan>();
		ClanInfo claninfo = null;
		
		Connection _sqlconnection = null;
		PreparedStatement selectclan = null;
		ResultSet selectclanRes = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			selectclan = _sqlconnection.prepareStatement("SELECT * FROM clan INNER JOIN user ON clan.ownerid=user.id AND clan.id=?");
			selectclan.setInt(1, clanID);
			selectclanRes = selectclan.executeQuery();
			if (selectclanRes.next()){
				claninfo = new ClanInfo(selectclanRes.getInt("id"), selectclanRes.getString("title"), selectclanRes.getInt("maxusers"), selectclanRes.getString("user.title"), selectclanRes.getInt("ownerid"), selectclanRes.getInt("user.money"), selectclanRes.getInt("clan.experience"));
				select = _sqlconnection.prepareStatement("SELECT * FROM user WHERE clanid=? AND clanrole>? ORDER BY clandeposite DESC");
				select.setInt(1, clanID);
				select.setInt(2, (int)ClanRole.INVITED);
				selectRes = select.executeQuery();
				while(selectRes.next()){					
					if(selectRes.getByte("clanrole") > ClanRole.INVITED && selectRes.getByte("clanrole") != ClanRole.OWNER){						
						UserOfClan user = new UserOfClan(selectRes.getInt("id"), selectRes.getString("title"), ServerApplication.application.userinfomanager.getLevelByExperience(selectRes.getInt("experience")), selectRes.getInt("popular"), selectRes.getInt("clandepositm"), selectRes.getInt("clandeposite"), selectRes.getByte("clanrole"), selectRes.getInt("getclanmoneyat"));
						
						if(ServerApplication.application.commonroom.users.get(Integer.toString(user.id)) != null){
		    				user.isonline = true;
		    			}else{
		    				user.isonline = false;
		    			}
						users.add(user);
					}
	    		}				
			}    		
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (selectclan != null) selectclan.close(); 
		    	if (selectclanRes != null) selectclanRes.close();
		    	if (select != null) select.close(); 
		    	if (selectRes != null) selectRes.close();
		    	_sqlconnection = null;
		    	selectclan = null;
		    	selectclanRes = null;
		    	select = null;
		    	selectRes = null;
		    }
		    catch (SQLException sqlx) {	     
		    }
		}
		
		ClanAllInfo allinfo = new ClanAllInfo(claninfo, users);
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		Date date = new Date();
		int currenttime = (int)(date.getTime() / 1000);
		allinfo.time = Math.max(0, 60 * 60 - (currenttime - user.user.claninfo.getclanmoneyat));
		
		date = null;
		user = null;
		claninfo = null;	
		users = null;
		
		//return ClanAllInfo
		sendResult(client, params, AMFWObjectBuilder.createObjClanAllInfo(allinfo));
    	return;	
	}
	
	public void clanCreateClan(IClient client, RequestFunction function, AMFDataList params){
    	String title = getParamString(params, PARAM1);

		CreateClanResult result = new CreateClanResult();
		result.error = ClanError.OTHER;
		Connection _sqlconnection = null;
		PreparedStatement selectclan = null;
		ResultSet selectclanRes = null;
		PreparedStatement insertclan = null;
		PreparedStatement updateClanInfo = null;		
		PreparedStatement findclan = null;
		ResultSet findclanRes = null;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null && user.client.isConnected()){		
			if (user.user.level >= Config.createClanNeedLevel()){	
				try {
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					findclan = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE title=?");
					findclan.setString(1, title);
					findclanRes = findclan.executeQuery();    	
		    		if(findclanRes.next()){    			
		    			result.error = ClanError.CLAN_EXIST;
		    		}else{
		    			if (user.user.money >= Config.createClanPrice()){
		    				
		    				if(user.user.claninfo.clanid > 0 && user.user.claninfo.clanrole > ClanRole.INVITED){
		    					result.error = ClanError.INOTHERCLAN;
		    					user = null;
		    					//return CreateClanResult
		    					sendResult(client, params, AMFWObjectBuilder.createObjCreateClanResult(result));
		    			    	return;	
		    				}
		    				
		    				insertclan = _sqlconnection.prepareStatement("INSERT INTO clan (title, ownerid, maxusers, money, experience) VALUES(?,?,?,?,?)");
		    				insertclan.setString(1, title);
		    				insertclan.setInt(2, user.user.id);
		    				insertclan.setInt(3, Config.maxUsersInClan());
		    				insertclan.setInt(4, 0);
		    				insertclan.setInt(5, 0);				
		    				
		    				logger.log("Create clan " + title + " from user: " + user.user.id);
		    				
			    			if (insertclan.executeUpdate() > 0){			    				
			    				selectclan = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE ownerid=?");
			    				selectclan.setInt(1, user.user.id);
			    				selectclanRes = selectclan.executeQuery();  
			    				selectclanRes.next();		    				
			    				result.clanid = selectclanRes.getInt("id");
			    				
			    				user.user.updateMoney(user.user.money - Config.createClanPrice());
			    				user.user.claninfo.clanid = result.clanid;
			    				user.user.claninfo.clantitle = selectclanRes.getString("title");
			    				user.user.claninfo.maxusers = Config.maxUsersInClan();
			    				user.user.claninfo.clandeposite = 0;
			    				user.user.claninfo.clandepositm = 0;	    				
			    				user.user.claninfo.clanrole = ClanRole.OWNER;
			    				
			    				result.clantitle = user.user.claninfo.clantitle;
			    				result.maxusers = user.user.claninfo.maxusers;
			    				result.clandeposite = user.user.claninfo.clandeposite;
			    				result.clandepositm = user.user.claninfo.clandepositm;
			    				result.clanrole = user.user.claninfo.clanrole;
			    				result.money = user.user.money;
			    				
			    				updateClanInfo = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clanrole=? WHERE id=?");			    				
			    				updateClanInfo.setInt(1, result.clanid);			    				
			    				updateClanInfo.setInt(2, (int)ClanRole.OWNER);
			    				updateClanInfo.setInt(3, user.user.id);
			    				
			            		if (updateClanInfo.executeUpdate() > 0){
			            		}else{
			            			result.error = ClanError.OTHER;
			            			user = null;
			            			//return CreateClanResult
			    					sendResult(client, params, AMFWObjectBuilder.createObjCreateClanResult(result));
			    			    	return;	
			            		}		    				
			    				result.error = ClanError.OK;
			    			}else{    	    				
			    				result.error = ClanError.OTHER;
			    				user = null;
			    				//return CreateClanResult
		    					sendResult(client, params, AMFWObjectBuilder.createObjCreateClanResult(result));
		    			    	return;	
			    			}
		    			}else{
		    				result.error = ClanError.NOT_ENOUGHT_MONEY;
		    			}
		    		}
				} catch (SQLException e) {
					logger.error(e.toString());
				}
				finally
				{
				    try{
				    	if (_sqlconnection != null) _sqlconnection.close();
				    	if (insertclan != null) insertclan.close(); 
				    	if (updateClanInfo != null) updateClanInfo.close();			    	
				    	if (findclan != null) findclan.close();
				    	if (findclanRes != null) findclanRes.close();			    	
				    	if (selectclanRes != null) selectclanRes.close();
				    	if (selectclan != null) selectclan.close();
				    	_sqlconnection = null;
				    	insertclan = null;
				    	updateClanInfo = null;
				    	findclan = null;
				    	findclanRes = null;
				    	selectclanRes = null;
				    	selectclan = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
			}else{
				result.error = ClanError.LOWLEVEL;
			}
		}
		user = null;
		//return CreateClanResult
		sendResult(client, params, AMFWObjectBuilder.createObjCreateClanResult(result));
    	return;
	}
	
	public void clanInviteUser(IClient client, RequestFunction function, AMFDataList params){
    	String userID = getParamString(params, PARAM1);

		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		PreparedStatement selectUsers = null;
		ResultSet selectUsersRes = null;
		PreparedStatement updateuser = null;
		
		int error = ClanError.OTHER;
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		UserClient inviteduser = ServerApplication.application.commonroom.users.get(userID);
		if(inviteduser != null){
			if(inviteduser.user.claninfo.clanrole <= ClanRole.INVITED){				
				if(user != null && user.user.claninfo.clanrole == ClanRole.OWNER){					
					try {
						_sqlconnection = ServerApplication.application.sqlpool.getConnection();
						select = _sqlconnection.prepareStatement("SELECT * FROM clan INNER JOIN user ON clan.ownerid=? AND user.id=?");
						select.setInt(1, user.user.id);
						select.setInt(2, user.user.id);
						selectRes = select.executeQuery();						
			    		if(selectRes.next()){
			    			selectUsers = _sqlconnection.prepareStatement("SELECT count(*) FROM user WHERE clanid=? AND clanrole>? AND clanrole<?");
			    			selectUsers.setInt(1, selectRes.getInt("clan.id"));
			    			selectUsers.setInt(2, ClanRole.INVITED);
			    			selectUsers.setInt(3, ClanRole.OWNER);
			    			selectUsersRes = selectUsers.executeQuery();	
			    			if(selectUsersRes.next()){
			        			int countusers = selectUsersRes.getInt(1);
			        			int maxusers = selectRes.getInt("clan.maxusers");
			        			
				    			if(countusers < maxusers){
					    			updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clanrole=? WHERE id=?");
					    			updateuser.setInt(1, selectRes.getInt("clan.id"));
					    			updateuser.setInt(2, (int)ClanRole.INVITED);
					    			updateuser.setInt(3, inviteduser.user.id);
				    				
				            		if (updateuser.executeUpdate() > 0){		            			
				            			inviteduser.user.claninfo.clanid = selectRes.getInt("clan.id");
				            			inviteduser.user.claninfo.clanrole = ClanRole.INVITED;
				            			
				            			ClanInfo claninfo = new ClanInfo(selectRes.getInt("clan.id"), selectRes.getString("clan.title"), selectRes.getInt("clan.maxusers"), selectRes.getString("user.title"), selectRes.getInt("clan.ownerid"), selectRes.getInt("user.money"), selectRes.getInt("clan.experience"));
				            			if(inviteduser.client.isConnected()){
				            				inviteduser.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageClanInvite(MessageType.CLAN_INVITE, ServerApplication.application.commonroom.id, claninfo));
				            			}		        				
				        				
				        				claninfo = null;
				            			error = ClanError.OK;
				            		}
				    			}else{
				    				error = ClanError.NOT_ENOUGHT_SEATS;
				    			}
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
					    	if (selectUsers != null) selectUsers.close(); 
					    	if (selectUsersRes != null) selectUsersRes.close();
					    	if (updateuser != null) updateuser.close();
					    	_sqlconnection = null;
					    	select = null;
					    	selectRes = null;
					    	selectUsers = null;
					    	selectUsersRes = null;
					    	updateuser = null;
					    }
					    catch (SQLException sqlx) {		     
					    }
					}
				}else{
					error = ClanError.YOUNOTOWNER;
				}
			}else{
				error = ClanError.INOTHERCLAN;
			}
		}else{
			error = ClanError.USEROFFLINE;
		}
		user = null;
		inviteduser = null;
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanInviteReject(IClient client, RequestFunction function, AMFDataList params){
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;	
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clanrole=? WHERE id=?");
			updateuser.setInt(1, 0);
			updateuser.setInt(2, (int)ClanRole.NO_ROLE);
			updateuser.setInt(3, user.user.id);
			if (updateuser.executeUpdate() > 0){
				user.user.claninfo.clanid = 0;
				user.user.claninfo.clanrole = ClanRole.NO_ROLE;
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (updateuser != null) updateuser.close();
		    	_sqlconnection = null;
		    	updateuser = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		user = null;
		//return void
		sendResult(client, params, true);
    	return;
	}
	
	public void clanInviteAccept(IClient client, RequestFunction function, AMFDataList params){
		int clanid = 0;
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		PreparedStatement selectUsers = null;
		ResultSet selectUsersRes = null;
		
		Date date = new Date();
		int currenttime = (int)(date.getTime() / 1000);
		date = null;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.user.claninfo.clanrole == ClanRole.INVITED && user.user.claninfo.clanid > 0){
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				select = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE id=?");
				select.setInt(1, user.user.claninfo.clanid);
				selectRes = select.executeQuery();
	    		if(selectRes.next()){
	    			selectUsers = _sqlconnection.prepareStatement("SELECT count(*) FROM user WHERE clanid=? AND clanrole>? AND clanrole<?");
	    			selectUsers.setInt(1, selectRes.getInt("clan.id"));
	    			selectUsers.setInt(2, ClanRole.INVITED);
	    			selectUsers.setInt(3, ClanRole.OWNER);
	    			selectUsersRes = selectUsers.executeQuery();	
	    			if(selectUsersRes.next()){
	        			int countusers = selectUsersRes.getInt(1);
	        			int maxusers = selectRes.getInt("clan.maxusers");
	        			
	        			if(countusers < maxusers){
		        			updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanrole=?,clandeposite=?,clandepositm=?,getclanmoneyat=? WHERE id=?");
							updateuser.setInt(1, (int)ClanRole.ROLE1);
							updateuser.setInt(2, 0);
							updateuser.setInt(3, 0);
							updateuser.setInt(4, currenttime);
							updateuser.setInt(5, user.user.id);
							if (updateuser.executeUpdate() > 0){
								user.user.claninfo.clanrole = ClanRole.ROLE1;
								user.user.claninfo.clandeposite = 0;
								user.user.claninfo.clandepositm = 0;
								user.user.claninfo.getclanmoneyat = currenttime;
								user.user.claninfo.clantitle = selectRes.getString("title");
								clanid = user.user.claninfo.clanid;
							}
	        			}
	    			}
	    		}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (updateuser != null) updateuser.close();
			    	if (select != null) select.close();
			    	if (selectRes != null) selectRes.close();
			    	if (selectUsers != null) selectUsers.close();
			    	if (selectUsersRes != null) selectUsersRes.close();
			    	_sqlconnection = null;
			    	updateuser = null;
			    	select = null;
			    	selectRes = null;
			    	selectUsers = null;
			    	selectUsersRes = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		user = null;
		//return int
		sendResult(client, params, clanid);
    	return;
	}
	
	public void clanKick(IClient client, RequestFunction function, AMFDataList params){
    	String userID = getParamString(params, PARAM1);

		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		
		int error = ClanError.OTHER;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		UserClient kickeduser = ServerApplication.application.commonroom.users.get(userID);
		
		if(user != null && user.user.claninfo.clanrole == ClanRole.OWNER){
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				select = _sqlconnection.prepareStatement("SELECT * FROM user WHERE id=?");
				select.setInt(1, new Integer(userID));
				selectRes = select.executeQuery();
	    		if(selectRes.next()){
	    			if(selectRes.getInt("clanid") == user.user.claninfo.clanid){				
						updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clandepositm=?,clandeposite=?,clanrole=? WHERE id=?");
						updateuser.setInt(1, 0);
						updateuser.setInt(2, 0);
						updateuser.setInt(3, 0);
						updateuser.setInt(4, (int)ClanRole.NO_ROLE);
						updateuser.setInt(5, new Integer(userID));
						if (updateuser.executeUpdate() > 0){							
							if(kickeduser != null){								
								kickeduser.user.claninfo.clanid = 0;
								kickeduser.user.claninfo.clandeposite = 0;
								kickeduser.user.claninfo.clandepositm = 0;
								kickeduser.user.claninfo.clanrole = ClanRole.NO_ROLE;
								kickeduser.user.claninfo.clantitle = null;
								
								if(kickeduser.client.isConnected()){
									kickeduser.client.call("processMassage", null, AMFWObjectBuilder.createObjMessage(MessageType.CLAN_KICK, ServerApplication.application.commonroom.id));
								}
							}
							error = ClanError.OK;
						}
	    			}else{
	    				error = ClanError.INOTHERCLAN;
	    			}
	    		}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (updateuser != null) updateuser.close();
			    	if (select != null) select.close();
			    	if (selectRes != null) selectRes.close();
			    	_sqlconnection = null;
			    	updateuser = null;
			    	select = null;
			    	selectRes = null;
			    }
			    catch (SQLException sqlx) {	     
			    }
			}
		}else{
			error = ClanError.YOUNOTOWNER;
		}
		kickeduser = null;
		user = null;
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanSetRole(IClient client, RequestFunction function, AMFDataList params){
    	String userID = getParamString(params, PARAM1);
    	int role = getParamInt(params, PARAM2);
    	
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		
		int error = ClanError.OTHER;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		UserClient roler = ServerApplication.application.commonroom.users.get(userID);
		if(role == ClanRole.ROLE1 || role == ClanRole.ROLE2 || role == ClanRole.ROLE3 || role == ClanRole.ROLE4 || role == ClanRole.ROLE5){
			if(user != null && user.user.claninfo.clanrole == ClanRole.OWNER){
				try {
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					select = _sqlconnection.prepareStatement("SELECT * FROM user WHERE id=?");
					select.setInt(1, new Integer(userID));
					selectRes = select.executeQuery();
		    		if(selectRes.next()){		    			
		    			if(selectRes.getInt("clanid") == user.user.claninfo.clanid && selectRes.getInt("clanrole") > ClanRole.INVITED){
							updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanrole=? WHERE id=?");
							updateuser.setInt(1, role);
							updateuser.setInt(2, new Integer(userID));
							if (updateuser.executeUpdate() > 0){
								error = ClanError.OK;
								if(roler != null){
									roler.user.claninfo.clanrole = (byte)role;
								}
							}
		    			}else{
		    				error = ClanError.INOTHERCLAN;
		    			}
		    		}
				} catch (SQLException e) {
					logger.error(e.toString());
				}
				finally
				{
				    try{
				    	if (_sqlconnection != null) _sqlconnection.close();
				    	if (updateuser != null) updateuser.close();
				    	if (select != null) select.close();
				    	if (selectRes != null) selectRes.close();
				    	_sqlconnection = null;
				    	updateuser = null;
				    	select = null;
				    	selectRes = null;
				    }
				    catch (SQLException sqlx) {	     
				    }
				}
			}else{
				error = ClanError.YOUNOTOWNER;
			}
		}else{
			error = ClanError.NOROLE;
		}
		roler = null;
		user = null;
		
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanLeave(IClient client, RequestFunction function, AMFDataList params){
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		
		int error = ClanError.OTHER;		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));		
		
		if(user != null && user.user.claninfo.clanid > 0 && (user.user.claninfo.clanrole == ClanRole.ROLE1 || 
				user.user.claninfo.clanrole == ClanRole.ROLE2 || user.user.claninfo.clanrole == ClanRole.ROLE3 || 
				user.user.claninfo.clanrole == ClanRole.ROLE4 || user.user.claninfo.clanrole == ClanRole.ROLE5)){
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clanrole=?,clandeposite=?,clandepositm=? WHERE id=?");
				updateuser.setInt(1, 0);
				updateuser.setInt(2, (int)ClanRole.NO_ROLE);
				updateuser.setInt(3, 0);
				updateuser.setInt(4, 0);
				updateuser.setInt(5, user.user.id);
				if (updateuser.executeUpdate() > 0){
					error = ClanError.OK;
					user.user.claninfo.clanid = 0;
					user.user.claninfo.clanrole = ClanRole.NO_ROLE;;
					user.user.claninfo.clandeposite = 0;
					user.user.claninfo.clandepositm = 0;
					user.user.claninfo.clantitle = null;
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (updateuser != null) updateuser.close();
			    	_sqlconnection = null;
			    	updateuser = null;
			    }
			    catch (SQLException sqlx) {    
			    }
			}
		}		
		user = null;
		
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanReset(IClient client, RequestFunction function, AMFDataList params){
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		
		int error = ClanError.OTHER;		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));		
		
		if(user != null && user.user.claninfo.clanrole == ClanRole.OWNER){
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				updateuser = _sqlconnection.prepareStatement("UPDATE user SET clandeposite=?,clandepositm=? WHERE clanid=?");
				updateuser.setInt(1, 0);
				updateuser.setInt(2, 0);
				updateuser.setInt(3, user.user.claninfo.clanid);
				if (updateuser.executeUpdate() > 0){
					error = ClanError.OK;
					
					Set<Entry<String, UserClient>> set = ServerApplication.application.commonroom.users.entrySet();
					for (Map.Entry<String, UserClient> cruser:set){
						if(cruser.getValue().user.claninfo.clanid == user.user.claninfo.clanid){
							cruser.getValue().user.claninfo.clandeposite = 0;
							cruser.getValue().user.claninfo.clandepositm = 0;
						}					
					}
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (updateuser != null) updateuser.close();
			    	_sqlconnection = null;
			    	updateuser = null;
			    }
			    catch (SQLException sqlx) {    
			    }
			}
		}else{
			error = ClanError.YOUNOTOWNER;
		}
		user = null;
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanDestroy(IClient client, RequestFunction function, AMFDataList params){
		Connection _sqlconnection = null;
		PreparedStatement updateuser = null;
		PreparedStatement deleteclan = null;
		
		int error = ClanError.OTHER;		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));		
		int clanID = user.user.claninfo.clanid;
		
		
		if(user != null && user.user.claninfo.clanrole == ClanRole.OWNER){
			ServerApplication.application.logger.log("DESTROY CLAN " + user.user.claninfo.clanid + " USER: " + user.user.id);
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				updateuser = _sqlconnection.prepareStatement("UPDATE user SET clanid=?,clanrole=?,clandeposite=?,clandepositm=? WHERE clanid=?");
				updateuser.setInt(1, 0);
				updateuser.setInt(2, (int)ClanRole.NO_ROLE);
				updateuser.setInt(3, 0);
				updateuser.setInt(4, 0);
				updateuser.setInt(5, clanID);
				if (updateuser.executeUpdate() > 0){
					error = ClanError.OK;
					
					Set<Entry<String, UserClient>> set = ServerApplication.application.commonroom.users.entrySet();
					for (Map.Entry<String, UserClient> cruser:set){
						if(cruser.getValue().user.claninfo.clanid == clanID){
							cruser.getValue().user.claninfo.clanid = 0;
							cruser.getValue().user.claninfo.clanrole = ClanRole.NO_ROLE;
							cruser.getValue().user.claninfo.clandeposite = 0;
							cruser.getValue().user.claninfo.clandepositm = 0;
							cruser.getValue().user.claninfo.clantitle = null;
						}					
					}
					
					deleteclan = _sqlconnection.prepareStatement("DELETE FROM clan WHERE id=?");
					deleteclan.setInt(1, clanID);					
            		if (deleteclan.executeUpdate() > 0){            			
            		}
				}				
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (deleteclan != null) deleteclan.close();
			    	if (updateuser != null) updateuser.close();
			    	_sqlconnection = null;
			    	deleteclan = null;
			    	updateuser = null;
			    }
			    catch (SQLException sqlx) {    
			    }
			}
		}else{
			error = ClanError.YOUNOTOWNER;
		}
		user = null;
		
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void updateClanDeposits(Map<Integer, ClanDeposit> clandeposits){		
		Connection _sqlconnection = null;
		PreparedStatement updateclan = null;		
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			Set<Entry<Integer, ClanDeposit>> set = clandeposits.entrySet();
			for (Map.Entry<Integer, ClanDeposit> deposit:set){
				updateclan = _sqlconnection.prepareStatement("UPDATE clan SET money=money+?,experience=experience+? WHERE id=?");
				updateclan.setInt(1, Math.max(0, deposit.getValue().depositm));
				updateclan.setInt(2, Math.max(0, deposit.getValue().deposite));
				updateclan.setInt(3, deposit.getKey());
				updateclan.executeUpdate();				
			}			
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (updateclan != null) updateclan.close();
		    	_sqlconnection = null;
		    	updateclan = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}	
	}
	
	public void clanGetMoney(IClient client, RequestFunction function, AMFDataList params){
		Connection _sqlconnection = null;
		PreparedStatement selectclan = null;
		ResultSet selectclanRes = null;
		PreparedStatement selectowner = null;
		ResultSet selectownerRes = null;				
		int error = ClanError.OTHER;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){
			Date date = new Date();
			int currenttime = (int)(date.getTime() / 1000);
			int time = Math.max(0, 60 * 60 - (currenttime - user.user.claninfo.getclanmoneyat));
			date = null;
			
			if(user.user.claninfo.clanid > 0 && user.user.claninfo.clanrole > ClanRole.INVITED){
				if(time <= 0){
					int needmoney = 0;
					if(user.user.claninfo.clanrole == ClanRole.ROLE1){
						needmoney = 10;
					}else if(user.user.claninfo.clanrole == ClanRole.ROLE2){
						needmoney = 20;
					}else if(user.user.claninfo.clanrole == ClanRole.ROLE3){
						needmoney = 50;
					}else if(user.user.claninfo.clanrole == ClanRole.ROLE4){
						needmoney = 100;
					}else if(user.user.claninfo.clanrole == ClanRole.ROLE5){
						needmoney = 300;
					}
					
					try {
						_sqlconnection = ServerApplication.application.sqlpool.getConnection();
						selectclan = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE id=?");
						selectclan.setInt(1, user.user.claninfo.clanid);
						selectclanRes = selectclan.executeQuery();
			    		if(selectclanRes.next()){
			    			int ownerid = selectclanRes.getInt("ownerid");
			    			UserClient owner = ServerApplication.application.commonroom.users.get(Integer.toString(ownerid));
			    			
			    			selectowner = _sqlconnection.prepareStatement("SELECT * FROM user WHERE id=?");
			    			selectowner.setInt(1, ownerid);
			    			selectownerRes = selectowner.executeQuery();
				    		if(selectownerRes.next()){
				    			int money = selectownerRes.getInt("money");
				    			if(owner != null && owner.client.isConnected()) money = owner.user.money;				    			
				    			
				    			needmoney = Math.min(needmoney, money);
				    			
				    			user.user.updateMoney(user.user.money + needmoney);
				    			ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
				    			
								user.user.claninfo.clandepositm -= needmoney;
								user.user.claninfo.getclanmoneyat = currenttime;
				    			
								ServerApplication.application.userinfomanager.addMoney(ownerid, -needmoney, owner);
				    			error = ClanError.OK;
				    		}			    		
				    		owner = null;
			    		}
					} catch (SQLException e) {
						logger.error(e.toString());
					}
					finally
					{
					    try{
					    	if (_sqlconnection != null) _sqlconnection.close();
					    	if (selectclan != null) selectclan.close();
					    	if (selectclanRes != null) selectclanRes.close();
					    	if (selectowner != null) selectowner.close();
					    	if (selectownerRes != null) selectownerRes.close();					    					    
					    	_sqlconnection = null;
					    	selectclan = null;
					    	selectclanRes = null;
					    	selectowner = null;
					    	selectownerRes = null;					    						    	
					    }
					    catch (SQLException sqlx) {		     
					    }
					}				
				}else{
					error = ClanError.TIMEERROR;
				}
				user = null;
			}else{
				error = ClanError.INOTHERCLAN;
			}
		}
		//return int
		sendResult(client, params, error);
    	return;
	}
	
	public void clanBuySeats(IClient client, RequestFunction function, AMFDataList params){
    	int count = getParamInt(params, PARAM1);
    	
		BuyResult result = new BuyResult();
		result.error = result.error = BuyErrorCode.OTHER;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null && user.client != null && user.client.isConnected()){
			Connection _sqlconnection = null;	
			PreparedStatement selectclan = null;
			ResultSet selectclanRes = null;
			PreparedStatement updateclan = null;
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				selectclan = _sqlconnection.prepareStatement("SELECT * FROM clan WHERE ownerid=?");
				selectclan.setInt(1, user.user.id);
				selectclanRes = selectclan.executeQuery();
	    		if(selectclanRes.next()){
	    			int maxusers = selectclanRes.getInt("maxusers");
	    			
	    			int  from = maxusers + 1;
					int to = from + count;
					int price = 0;
					for(int i = from; i < to; i++){
						int seatIndex = i - Config.maxUsersInClan();;
						int seatprice = (int) Math.floor(Math.pow(1.5, seatIndex - 1) * 20);
						price += seatprice;
					}
					if(price > 0 && count <= 5 && price < Integer.MAX_VALUE){
						if (user.user.money >= price){
							updateclan = _sqlconnection.prepareStatement("UPDATE clan SET maxusers=? WHERE id=?");
							updateclan.setInt(1, maxusers + count);
							updateclan.setInt(2, selectclanRes.getInt("id"));
							
							if (updateclan.executeUpdate() > 0){	    			
				    			user.user.updateMoney(user.user.money - price);
				    			ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_MAX_CLAN_SEATS, user.user.money, maxusers + count);
				    			
				    			result.error = BuyErrorCode.OK;
				    		}else{
				    			result.error = BuyErrorCode.OTHER;
				    			//return BuyResult
				    			sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
				    	    	return;
				    		}
						}else{
							result.error = BuyErrorCode.NOT_ENOUGH_MONEY;
						}
					}
	    		}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close();
			    	if (selectclan != null) selectclan.close();
			    	if (selectclanRes != null) selectclanRes.close();
			    	if (updateclan != null) updateclan.close();			    					    
			    	_sqlconnection = null;
			    	selectclan = null;
			    	selectclanRes = null;
			    	updateclan = null;				    						    	
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		user = null;
		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
    	return;
	}
}
