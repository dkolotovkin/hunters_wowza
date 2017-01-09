package app.utils.authorization;

import app.Config;
import app.ServerApplication;
import app.utils.md5.MD5;

public class Authorization {
	public static boolean check(String authkey, String vid, int mode, String appID){
		if (mode == GameMode.DEBUG){
			if(Config.mode() == Config.TEST){return true;}else{return false;}
    	}else if (mode == GameMode.VK){
    		String hashMD5 = MD5.getMD5((new Integer(Config.appIdVK())).toString() + '_' +  vid + '_' +  Config.protectedSecretVK());    		
    		if (hashMD5.toString().equals(authkey)){					
    			return true;
			}else{ServerApplication.application.logger.log("AuthorizationVK bad, user: " + vid + ":" + Config.appIdVK() + ":" + appID);}
    	}else if (mode == GameMode.MM){
    		String hashMD5 = MD5.getMD5((new Integer(Config.appIdMM())).toString() + '_' +  vid + '_' +  Config.protectedSecretMM());    		
    		if (hashMD5.toString().equals(authkey)){					
    			return true;
			}else{ServerApplication.application.logger.log("AuthorizationMM bad, user: " + vid + ":" + Config.appIdMM() + ":" + appID);}    		   		
    	}else if (mode == GameMode.OD){
    		//в appID для одноклассников передается параметр session_key, т.к. он необходим для авторизации и appID не передается в flashvars
			String hashMD5 = MD5.getMD5(vid + appID +  Config.protectedSecretOD());
			if (hashMD5.toString().equals(authkey)){					
				return true;
			}else{ServerApplication.application.logger.log("AuthorizationOD bad, user: " + vid + ":" + appID);}			  		
		}
		return false;
	}
}