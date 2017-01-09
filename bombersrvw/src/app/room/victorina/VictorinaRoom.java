package app.room.victorina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import app.Config;
import app.ServerApplication;
import app.message.MessageType;
import app.message.simple.MessageSimple;
import app.room.Room;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.sex.Sex;

import com.wowza.wms.amf.AMFDataObj;

public class VictorinaRoom extends Room{
public Timer timer;
	
	public int state;
	public int currenttime = 0;
	public String currentanswer = null;	
	public int countquestions;
	
	public VictorinaRoom(int id, String title){
		super(id, title);		
		setCountQuestions();
		sendQuestion();		
		setTimerToEnd();
	}
	
	public void setCountQuestions(){
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			select = _sqlconnection.prepareStatement("SELECT count(*) FROM victorina");				
			selectRes = select.executeQuery();    		
    		if(selectRes.next()){
    			countquestions = selectRes.getInt(1);
    		}
		} catch (SQLException e) {
			logger.error("VR1" + e.toString());
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
	}
	
	public void sendQuestion(){
		Connection _sqlconnection = null;
		PreparedStatement select = null;		
		ResultSet selectRes = null;
		
		String question = null;
		
		try {
			do{
				int index = Math.round((float)Math.random() * countquestions);				
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();				
				
				select = _sqlconnection.prepareStatement("SELECT * FROM victorina where id=?");
				select.setInt(1, index);
				selectRes = select.executeQuery();    		
	    		if(selectRes.next()){
	    			currentanswer = selectRes.getString("answer");    			
	    			question = selectRes.getString("question");
	    		}
			}while(question == null);
		} catch (SQLException e) {
			logger.error("VR2" + e.toString());
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
		
		sendSystemMessage("Внимание вопрос: " + question);			
	}
	
	public void sendNoWinner(){
		sendSystemMessage("Никто не дал правильного ответа. Правильный ответ - '" + currentanswer + "'. Следующий вопрос через 30 секунд.");
		currentanswer = null;
	}
	
	@Override
	public void sendMessage(String mtext, String initiatorConnID, String receiverID){
		super.sendMessage(mtext, initiatorConnID, receiverID);
		
		if(currentanswer != null && mtext.toLowerCase().equals(currentanswer.toLowerCase())){			
			UserClient user = users.get(userClientIDtoID.get(initiatorConnID));
			if(user != null){
				if(Sex.MALE == user.user.sex){
					sendSystemMessage("Правильный ответ '" + currentanswer + "' дал " + user.user.title + ". Приз " + Config.victorinaPrize() + " евро. Следующий вопрос через 30 секунд.");
				}else{
					sendSystemMessage("Правильный ответ '" + currentanswer + "' дала " + user.user.title + ". Приз " + Config.victorinaPrize() + " евро. Следующий вопрос через 30 секунд.");
				}
				user.user.updateMoney(user.user.money + Config.victorinaPrize());
				changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
				currentanswer = null;
				setTimerToStart();
			}
		}
	}
	
	@Override
	public void sendSystemMessage(String text){		
		MessageSimple message = new MessageSimple(MessageType.VICTORINA, this.id, text, 0, 0);
		AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageSimple(MessageType.VICTORINA, this.id, text, 0, 0);
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			if(user.getValue().client != null && user.getValue().client.isConnected()){
				user.getValue().client.call("processMassage", null, amfMessage);
			}		
		}	
		
		messages.add(message);
		if (messages.size() > Config.maxMessageCountInRoom()){
			messages.remove(0);
		}		
		
		set = null;
		message = null;
		amfMessage = null;
	}
	
	public void setTimerToEnd(){
		if (timer != null){
			timer.cancel();
			timer = null;	
		}
		timer = new Timer();
		timer.schedule(new TimerToEnd(), 20 * 1000, 15 * 1000);
	}
	
	public void setTimerToStart(){
		if (timer != null){
			timer.cancel();
			timer = null;	
		}
		timer = new Timer();
		timer.schedule(new TimerToStart(), 30 * 1000, 45 * 1000);
	}
	
	class TimerToEnd extends TimerTask{
        public void run(){ 
        	sendNoWinner();
        	setTimerToStart();
         }  
     }
	
	class TimerToStart extends TimerTask{
        public void run(){
        	sendQuestion();
        	setTimerToEnd();
         }  
     }
}
