package app.admin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import app.Config;
import app.ServerApplication;
import app.logger.MyLogger;
import app.user.UserClient;
import app.user.UserRole;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.md5.MD5;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class AdministratorManager extends ModuleBase{
	public MyLogger logger = new MyLogger(AdministratorManager.class.getName());
	
	public Timer timer;
	public String currentNotification;
	List<String> vkUids = new ArrayList<String>();
	List<String> mmUids = new ArrayList<String>();
	
	public void adminUpdateAllUsersParams(IClient client, RequestFunction function, AMFDataList params){
		int result = 0;
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null){
			if(user.user.role == UserRole.ADMINISTRATOR_MAIN){
				Set<Entry<String, UserClient>> setuser = ServerApplication.application.commonroom.users.entrySet();
				for (Map.Entry<String, UserClient> u:setuser){
					ServerApplication.application.userinfomanager.updateUser(u.getValue().user);			
				}
				setuser = null;
				result = 1;
			}
		}
		
		//return int
		sendResult(client, params, result);
	}
	
	public void adminSetParam(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		int param = getParamInt(params, PARAM2);
		int value = getParamInt(params, PARAM3);
		int result = 0;
		
		Connection _sqlconnection = null;
		PreparedStatement update = null;
		
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userID));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR_MAIN){			
			if(user != null){
				if(param == ParamType.MONEY){
					user.user.money = value;
					ServerApplication.application.commonroom.changeUserInfoByID(userID, ChangeInfoParams.USER_MONEY, value, 0);
				}
				if(param == ParamType.EXPERIENCE){
					user.user.experience = value;
					ServerApplication.application.commonroom.changeUserInfoByID(userID, ChangeInfoParams.USER_EXPERIENCE, value, 0);
				}
				if(param == ParamType.POPULAR){
					user.user.popular = value;
					ServerApplication.application.commonroom.changeUserInfoByID(userID, ChangeInfoParams.USER_POPULAR, value, 0);
				}
				result = 1;
			}else{
				try {				
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					if(param == ParamType.MONEY){
						update = _sqlconnection.prepareStatement("UPDATE user SET money=? WHERE id=?");
						update.setInt(1, value);
						update.setInt(2, userID);
					}
					if(param == ParamType.EXPERIENCE){
						update = _sqlconnection.prepareStatement("UPDATE user SET experience=? WHERE id=?");
						update.setInt(1, value);
						update.setInt(2, userID);
					}
					if(param == ParamType.POPULAR){
						update = _sqlconnection.prepareStatement("UPDATE user SET popular=? WHERE id=?");
						update.setInt(1, value);
						update.setInt(2, userID);
					}								
					if (update.executeUpdate() > 0){	
						result = 1;
					}
				} catch (SQLException e) {
					logger.error(e.toString());
				}
				finally
				{
				    try{
				    	if (_sqlconnection != null) _sqlconnection.close(); 
				    	if (update != null) update.close(); 
				    	_sqlconnection = null;
				    	update = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
			}
		}
		//return int
		sendResult(client, params, result);
	}
	
	public void adminSetNameParam(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		int param = getParamInt(params, PARAM2);
		String value = getParamString(params, PARAM3);
		int result = 0;
		
		Connection _sqlconnection = null;
		PreparedStatement update = null;
		
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userID));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR || initiator.user.role == UserRole.ADMINISTRATOR_MAIN){
			if(param == ParamType.NAME){
				if(user != null){
					logger.log("CHANGE NAME adminID: " + initiator.user.id + " userID: " + userID + " lastUserName: " + user.user.title + " newUserName: " + value);
				}else{
					logger.log("CHANGE NAME adminID: " + initiator.user.id + " userID: " + userID + " newUserName: " + value);
				}
				try {				
					_sqlconnection = ServerApplication.application.sqlpool.getConnection();
					
					update = _sqlconnection.prepareStatement("UPDATE user SET title=? WHERE id=?");
					update.setString(1, value);
					update.setInt(2, userID);
					update.execute();
					
					result = 1;
					
					if(user != null){
						user.client.setShutdownClient(true);
					}
				} catch (SQLException e) {
					logger.error(e.toString());
				}
				finally
				{
				    try{
				    	if (_sqlconnection != null) _sqlconnection.close(); 
				    	if (update != null) update.close(); 
				    	_sqlconnection = null;
				    	update = null;
				    }
				    catch (SQLException sqlx) {		     
				    }
				}
			}
		}
		//return int
		sendResult(client, params, result);
	}
	
	public void adminSetModerator(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		int result = 0;
		
		Connection _sqlconnection = null;
		PreparedStatement update = null;
		
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR_MAIN || initiator.user.role == UserRole.ADMINISTRATOR){			
			try {				
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				update = _sqlconnection.prepareStatement("UPDATE user SET role=? WHERE id=?");
				update.setInt(1, 1);
				update.setInt(2, userID);								
				
				if (update.executeUpdate() > 0){	
					result = 1;
				}
				
				UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userID));
				if(user != null){
					user.client.setShutdownClient(true);
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (update != null) update.close(); 
			    	_sqlconnection = null;
			    	update = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		//return int
		sendResult(client, params, result);
	}
	
	public void adminDeleteModerator(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		int result = 0;
		
		Connection _sqlconnection = null;
		PreparedStatement update = null;
		
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR_MAIN || initiator.user.role == UserRole.ADMINISTRATOR){			
			try {				
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				update = _sqlconnection.prepareStatement("UPDATE user SET role=? WHERE id=?");
				update.setInt(1, 0);
				update.setInt(2, userID);								
				
				if (update.executeUpdate() > 0){	
					result = 1;
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (update != null) update.close(); 
			    	_sqlconnection = null;
			    	update = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		//return int
		sendResult(client, params, result);
	}
	
	public void adminDeleteUser(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		int result = 0;
		
		Connection _sqlconnection = null;
		PreparedStatement delete = null;
		
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR_MAIN){			
			try {				
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				
				delete = _sqlconnection.prepareStatement("DELETE FROM user WHERE id=?");
				delete.setInt(1, userID);				
				
				if (delete.executeUpdate() > 0){
					result = 1;
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (delete != null) delete.close(); 
			    	_sqlconnection = null;
			    	delete = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		//return int
		sendResult(client, params, result);
	}
	
	public void adminShowInfo(IClient client, RequestFunction function, AMFDataList params){
		int userID = getParamInt(params, PARAM1);
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(initiator.user.role == UserRole.ADMINISTRATOR || initiator.user.role == UserRole.ADMINISTRATOR_MAIN){
			UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(userID));
	    	if (user != null){	    	
				sendResult(client, params, AMFWObjectBuilder.createObjInitUser(user.user, 0, null, null));    		
	    	}else{
	    		UserClient u = ServerApplication.application.userinfomanager.getOfflineUser(userID);
	    		if(u != null){
	    			sendResult(client, params, AMFWObjectBuilder.createObjInitUser(u.user, 0, null, null));
	    		}
	    	}		
		}
		//return UserForInit
	}
	
	public void adminSendNotification(IClient client, RequestFunction function, AMFDataList params){
		String msg = getParamString(params, PARAM1);
		
		Connection _sqlconnection = null;
    	PreparedStatement find = null;
    	ResultSet findRes = null;
    	
		UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		int result = 0;	
		
		currentNotification = msg;
		
		vkUids = new ArrayList<String>();
		mmUids = new ArrayList<String>();
		
		String currentVkUids = new String();
		int currentVkUidsNumbers = 0;
		String currentMmUids = new String();
		int currentMmUidsNumbers = 0;
		
		if(initiator.user.role == UserRole.ADMINISTRATOR_MAIN){
			try {
	    		_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
	    		find = _sqlconnection.prepareStatement("SELECT * FROM user");	    		
	    		findRes = find.executeQuery();    		
	    		while (findRes.next()){
	    			String idsocial = findRes.getString("idsocial");
	    			if(idsocial != null && idsocial.length() > 2){
		    			String social = idsocial.substring(0, 2);
		    			
		    			if(social.equals("vk")){
		    				if(currentVkUidsNumbers >= 100){
		    					vkUids.add(currentVkUids);		    					
		    					currentVkUids = new String();
		    					currentVkUidsNumbers = 0;		    					
		    				}
		    				if(currentVkUidsNumbers == 0 && currentVkUids.length() == 0){
		    					currentVkUids = idsocial.substring(2, idsocial.length());
		    				}else{
		    					currentVkUids = currentVkUids + "," + idsocial.substring(2, idsocial.length());
		    				}
		    				currentVkUidsNumbers++;
		    			}else if(social.equals("mm")){
		    				if(currentMmUidsNumbers >= 100){
		    					mmUids.add(currentMmUids);		    					
		    					currentMmUids = new String();
		    					currentMmUidsNumbers = 0;		    					
		    				}
		    				if(currentMmUidsNumbers == 0 && currentMmUids.length() == 0){
		    					currentMmUids = idsocial.substring(2, idsocial.length());
		    				}else{
		    					currentMmUids = currentMmUids + "," + idsocial.substring(2, idsocial.length());
		    				}
		    				currentMmUidsNumbers++;
		    			}
	    			}
	    		}
			} catch (SQLException e) {			
				logger.error("AM1 " + e.toString());
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
			if(currentVkUids.length() > 0) {vkUids.add(currentVkUids);}
			if(currentMmUids.length() > 0) {mmUids.add(currentMmUids);}			
			
			if(timer != null){
				timer.cancel();
				timer = null;
			}
			sendOdNotification(currentNotification, Config.apiUrlOD());
			
			timer = new Timer();
			timer.schedule(new TimerToStart(), 1000, 1000);
			
			result = 1;
		}
		//return int
		sendResult(client, params, result);
	}
	
	class TimerToStart extends TimerTask{
        public void run(){
        	logger.log("send notification " + vkUids.size() + " : " + mmUids.size());
        	if(vkUids.size() > 0 && currentNotification != null && currentNotification.length() > 3){
        		sendVkNotification(vkUids.get(0), currentNotification, Config.apiUrlVK(), Config.appIdVK());
        		vkUids.remove(0);
        	}else if(mmUids.size() > 0 && currentNotification != null && currentNotification.length() > 3){
        		sendMmNotification(mmUids.get(0), currentNotification, Config.apiUrlMM(), Config.appIdMM());
        		mmUids.remove(0);
        	}else{
        		logger.log("SEND NOTIFICATION COMPLETED " + vkUids.size() + " : " + mmUids.size() + " : ");
        		timer.cancel();
        		timer = null;
        		currentNotification = null;
        	}
         }  
     }
	
	public void sendVkNotification(String uids, String msg, String api_url, int appID){
		try
		{
			URL url = new URL(api_url);
			DataOutputStream wr = null;			
			try{				 
				long time = (new Date()).getTime();
				int random = Math.round((float)Math.random() * Integer.MAX_VALUE);							 

				String sig = MD5.getMD5("api_id=" + appID + "message=" + msg + "method=" + "secure.sendNotification" + "random=" + random + "timestamp=" + time + "uids=" + uids + "v=3.0" + Config.protectedSecretVK());
				
				try{msg = URLEncoder.encode(msg, "UTF-8");
				}catch(Throwable e){logger.log("AM7 " + e.toString());}		
				 
				String urlparams = "api_id=" + appID + "&method=" + "secure.sendNotification" +
				                   "&message=" + msg + "&random=" + random + "&timestamp=" + time +
					 			   "&uids=" + uids + "&v=3.0" + "&sig=" + sig;
		        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		        urlConnection.setConnectTimeout(1000);
		        urlConnection.setReadTimeout(1000);
		         
		        urlConnection.setDoInput(true);
		        urlConnection.setDoOutput(true);
		        urlConnection.setUseCaches(false);
		        urlConnection.setRequestMethod("GET");		         
		         
		        wr = new DataOutputStream (urlConnection.getOutputStream());
		        wr.writeBytes(urlparams);
		        wr.flush();
		        wr.close();
		         
		        InputStream is = urlConnection.getInputStream();			        
		        BufferedReader rd = new BufferedReader(new InputStreamReader(is));			         
		        String line;
		        StringBuffer response = new StringBuffer();
		        int counter = 0;
		        while((line = rd.readLine()) != null && counter < 250) {
		        	response.append(line);
					response.append('\r');
					counter++;				
		        }		         
		        logger.log("sendVkNotification: " + response.toString());
		         
		        rd.close();
		        rd = null;
		        is.close();
		        is = null;
		        wr = null;
		        url = null;
		        urlConnection.disconnect();
		        urlConnection = null;
			 }				
			 catch(IOException e){
				 logger.log("AM2" + e.toString());
			 }
			 finally
				{
				    try{
				    	if (wr != null) wr.close();				    	
				    	wr = null;			    	
				    }
				    catch (Throwable e) {
				    	logger.log("AM3" + e.getMessage());
				    }
				}
		}catch(MalformedURLException e){
			logger.log("AM4" + e.toString());
		}
	}
	
	public void sendMmNotification(String uids, String msg, String api_url, int appID){
		String sig = MD5.getMD5("app_id=" + appID + "format=JSON" + "method=" + "notifications.send" + "secure=" + "1" + "text=" + msg + "uids=" + uids + Config.protectedSecretMM());
		
		try{msg = URLEncoder.encode(msg, "UTF-8");
		}catch(Throwable e){logger.log("AM7 " + e.toString());}		
				
		try{			 
			URL url = new URL(api_url + "?method=notifications.send&app_id=" + appID + "&format=JSON" + "&secure=1" + "&text=" + msg + "&uids=" + uids + "&sig=" + sig);
			
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
			logger.log("sendMmNotification: " + response.toString());			
	  	
			rd.close();
			rd = null;
			is.close();
	        is = null;
	        urlConnection.disconnect();
	        urlConnection = null;
	        url = null;
		}catch(IOException e){
			ServerApplication.application.logger.log("AM6 " + e.toString());
		}
	}
	
	public void sendOdNotification(String msg, String api_url){
		String expires = "2012.02.02" + " " + "00:00";		//דד.לל.הה
		
		String sig = MD5.getMD5("application_key=" + Config.publicSecretOD() + "expires=" + expires + "status=P" + "text=" + msg + Config.protectedSecretOD());
		
		try{expires = URLEncoder.encode(expires, "UTF-8");
		}catch(Throwable e){logger.log("AM8 " + e.toString());} 
		
		try{msg = URLEncoder.encode(msg, "UTF-8");
		}catch(Throwable e){logger.log("AM9 " + e.toString());}
				
		try{
			URL url = new URL(api_url + "api/notifications/sendMass" + "?application_key=" + Config.publicSecretOD() + "&text=" + msg + "&status=P" + "&expires=" + expires + "&sig=" + sig);
			
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
			logger.log("--------sendOdNotification: " + response.toString());			
	  	
			rd.close();
			rd = null;
			is.close();
	        is = null;
			
	        urlConnection.disconnect();
	        urlConnection = null;
	        url = null;
		}catch(IOException e){
			ServerApplication.application.logger.log("AM10" + e.toString());
		}
	}
}
