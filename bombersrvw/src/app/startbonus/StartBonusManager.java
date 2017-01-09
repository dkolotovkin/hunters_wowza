package app.startbonus;

import app.logger.MyLogger;
import app.user.UserClient;
import app.user.chage.ChangeResult;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;

import app.ServerApplication;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class StartBonusManager extends ModuleBase{
	public MyLogger logger = new MyLogger("StartBonusManager");
	
	public void startbonusChangeInfo(IClient client, RequestFunction function, AMFDataList params){
    	String title = getParamString(params, PARAM1);
    	int sex = getParamInt(params, PARAM2);
    	
		UserClient u = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		ChangeResult result = new ChangeResult();
		if(u != null && u.client != null && u.client.isConnected() && u.user.experience < 5){
			result = ServerApplication.application.userinfomanager.changeInfo(title, sex, Integer.toString(client.getClientId()), result, true);
			//return ChangeResult
			sendResult(client, params, AMFWObjectBuilder.createObjChangeResult(result));
		}
		result = null;
    }
}
