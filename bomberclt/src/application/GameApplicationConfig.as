package application
{
	import mx.controls.Alert;
	
	import utils.servers.Server;

	public class GameApplicationConfig
	{
		public var app:GameApplication; 
		public var viewmode:int = GameViewMode.USER;
		public var exphourprizes:Array = new Array(400, 200, 100, 60, 40);
		public var expdayprizes:Array = new Array(4000, 2000, 1000, 600, 400);		
		
		public function GameApplicationConfig(app:GameApplication)
		{
			this.app = app;
		}
		
		public function get currentVersion():int{
			return 10;
		}
		
		public function get mode():int{
			if(viewer_id && secret && sid){
				return GameMode.VK;
			}else if (vid && session_key){
				return GameMode.MM;
			}else if (application_key && apiconnection){
				return GameMode.OD;
			}else if(playmode == GameMode.SITE){
				return GameMode.SITE;
			}
			return GameMode.DEBUG;
		}
		
		public function get connetionInterval():uint{
			return 10;
		}
		public function get maxMessagesInRoom():int{
			return 35;
		}
		public function get maxUsersInClan():int{
			return 20;
		}
		
		public function get swfDataAddress():String{
			return String(dataAddress + "/swf");
		}
		public function get dataAddress():String{
			if(mode == GameMode.DEBUG){
				return String("c:/data");
				//return String("");
			}else{
				return String("http://37.200.71.155/data");		
			}
		}
		
		//***********************************************SITE********************************************
		public function get oficalSiteUrl():String{
			return String("http://mouserun.ru");
		}
		public function get playmode():int{
			return app.parameters["playmode"];
		}
		public function get getparams():String{
			return app.parameters["getparams"];
		}		
		
		//*******************************************ODNOKLASSNIKI***************************************	
		public function get oficalODLink():String{
			return String("http://odnoklassniki.ru/");
		}	
		public function get authODUrl():String{
			return String("http://www.odnoklassniki.ru/oauth/authorize");
		}
		public function get hardcodeAppODId():int{
			return 1559552;
		}
		public function get hardcodeSiteODId():int{
			return 1856000;
		}
		
		//ID of the authorized user, which is permanent and will not change for the application. We may expose not the real IDs, but encoded with the key.
		public function get logged_user_id():String{
			return app.parameters["logged_user_id"];
		}	
		//API server base URL, for example http://api.odnoklassniki.ru/
		public function get api_server():String{
			return app.parameters["api_server"];
		}		
		//application public key
		public function get application_key():String{
			return app.parameters["application_key"];
		}		
		//ID of the user+application session (EXIST IN MM)
//		public function get session_key():String{
//			return app.parameters["session_key"];
//		}
		//secret key issued to the application, which must be used to sign all session related requests.
		public function get session_secret_key():String{
			return app.parameters["session_secret_key"];
		}
		//connection name, passed to ActionScript/JavaScript API
		public function get apiconnection():String{
			return app.parameters["apiconnection"];
		}
		//An MD5 hash of the user+session_key+application_secret_key. It can be used for simplified verification of the logged in user
		public function get auth_sig():String{
			return app.parameters["auth_sig"];
		}
		//An MD5 hash of the current request and your secret key, as described in the Authentication and Authorizationn section (EXIST IN MM)
//		public function get sig():String{
//			return app.parameters["sig"];
//		}
		
		//*********************************************VKONTAKTE*****************************************	
		public function get oficalVKLink():String{
			return String("http://vkontakte.ru/club30190610");
		}
		public function get authVKUrl():String{
			return String("http://api.vkontakte.ru/oauth/authorize");
		}
		public function get hardcodeAppVkId():int{
			return 2473027;
		}
		public function get hardcodeSiteVkId():int{
			return 2361599;
		}
		
		//vk (это адрес сервиса API, по которому необходимо осуществлять запросы)
		public function get api_url():String{
			return app.parameters["api_url"];
		}
		//vk (это id запущенного приложения)
		public function get api_id():String{
			return app.parameters["api_id"];
		}
		//vk (это id пользователя, со страницы которого было запущено приложение)
		public function get user_id():String{
			return app.parameters["user_id"];
		}
		//vk (id сессии для осуществления запросов к API)
		public function get sid():String{
			return app.parameters["sid"];
		}
		//vk (Секрет, необходимый для осуществления подписи запросов к API)
		public function get secret():String{
			return app.parameters["secret"];
		}
		//vk (это id пользователя, который просматривает приложение)
		public function get viewer_id():String{
			return app.parameters["viewer_id"];
		}
		//vk (если пользователь установил приложение – 1, иначе – 0)
		public function get is_app_user():Boolean{
			return Boolean(int(app.parameters["is_app_user"]));
		}
		//vk (это ключ, необходимый для авторизации пользователя на стороннем сервере)
		public function get auth_key():String{
			return app.parameters["auth_key"];
		}
		//vk (битовая маска настроек текущего пользователя в данном приложении)
		public function get api_settings():String{
			return app.parameters["api_settings"];
		}
		
		//*********************************************МОЙ МИР*****************************************
		public function get authMMUrl():String{
			return String("https://connect.mail.ru/oauth/authorize");
		}
		public function get hardcodeSiteMMId():int{
			return 625249;
		}
		
		//mm (идентификатор вашего приложения)
		public function get app_id():int{
			return app.parameters["app_id"];
		}
		//mm (идентификатор сессии)
		public function get session_key():String{
			return app.parameters["session_key"];
		}
		//mm (идентификатор пользователя, установившего приложение)
		public function get oid():int{
			return app.parameters["oid"];
		}
		//mm (идентификатор пользователя, запустившего приложение)
		public function get vid():int{
			return app.parameters["vid"];
		}		
		//mm (пользовательские настройки приложения)
		public function get ext_perm():String{
			return app.parameters["ext_perm"];
		}
		//mm (идентификатор окна, в котором запущено приложение)
		public function get window_id():String{
			return app.parameters["window_id"];
		}
		//mm (определяет место, из которого открыто приложение)
		public function get view():String{
			return app.parameters["view"];
		}
		//mm (подпись параметров)
		public function get sig():String{
			return app.parameters["sig"];
		}	
		//mm authentication_key
		public function get authentication_key():String{
			return app.parameters["authentication_key"];
		}
		
		public function get privatekeymm():String{
			return "ef9bba2e3f56b464d86a837cbd99ac19";
		}		
	}
}