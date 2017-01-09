package utils.managers.apimanager
{
	import application.GameApplication;
	import application.GameMode;
	import application.components.popup.buymoney.site.PopUpBuyMoneySite;
	import application.gamecontainer.StartSitePage;
	
	import flash.net.SharedObject;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	import flash.net.navigateToURL;
	
	import mx.controls.Alert;
	
	import utils.getparams.GetParams;

	public class ApiManagerSite extends ApiManager
	{		
		public function ApiManagerSite()
		{			
		}
		
		override public function init():void{			
			var code:String = GetParams.getParamByName(GameApplication.app.config.getparams, "code");
			var lastconnectedmode:int;
			if(GameApplication.app.so) lastconnectedmode = GameApplication.app.so.data["lastconnectedmode"];
			if(GameApplication.app.so && GameApplication.app.so.data["redirecing"] == true && code){				
				GameApplication.app.serversmanager.currentServerIndex = GameApplication.app.so.data["currentServerIndex"];
				GameApplication.app.connect();
			}else{			
				if((lastconnectedmode == GameMode.VK || lastconnectedmode == GameMode.MM || lastconnectedmode == GameMode.OD) && code){				
//					GameApplication.app.serversmanager.checkServers();
					GameApplication.app.serversmanager.currentServerIndex = 2;
					GameApplication.app.connect();
				}else{
					if(GameApplication.app.so){
						GameApplication.app.so.data["redirecing"] = false;
						GameApplication.app.so.flush();
					}
					GameApplication.app.addElement(new StartSitePage());				
				}
			}
			GameApplication.app.gameContainer.promotionMode = false;
		}
		
		override public function buyMoney(money:uint = 0, gamemoney:uint = 0):void{
			var fields:URLVariables = new URLVariables();			
			
			fields.spShopId = 7877;													//id магазина			
			fields.spShopPaymentId = money;											//id покупки
			fields.spAmount = money;												//сумма покупки		
			fields.spCurrency = "rur";												//валюта
			
			fields.spPurpose = "Покупка игровой валюты: " + gamemoney + " евро.";	//описание
			fields.lang = "ru";														//язык
			fields.tabNum = 1;														//таб, который будет выделен при переходе на сайт оплаты (1 - электронные платежи)
			fields.spUserDataUserID = addMoneyUserID;								//id пользователя
			
			var request:URLRequest = new URLRequest("http://sprypay.ru/sppi/");
			request.method = URLRequestMethod.POST;
			request.data = fields;
			
			try {				
				navigateToURL(request, '_blank');
			} catch (e:Error) {
				trace("Error occurred!");
			}
		}
		
		override public function showBuyMoneyPopUp():void{
			GameApplication.app.popuper.showInfoPopUp("Платежи на сайте в настоящее время не принимаются");
//			GameApplication.app.popuper.show(new PopUpBuyMoneySite());
		}
		
		public function redirect():void{
			if(GameApplication.app.so){
				GameApplication.app.so.data["redirecing"] = true;
				GameApplication.app.so.data["currentServerIndex"] = GameApplication.app.serversmanager.currentServerIndex;
				GameApplication.app.so.flush();
				
				if(GameApplication.app.so.data["lastconnectedmode"] == GameMode.VK){
					authVK();
				}else if(GameApplication.app.so.data["lastconnectedmode"] == GameMode.MM){
					authMM();
				}else if(GameApplication.app.so.data["lastconnectedmode"] == GameMode.OD){
					authOD();
				}
			}
		}
		
		public function authVK():void{
			if(GameApplication.app.so){
				GameApplication.app.so.data["lastconnectedmode"] = GameMode.VK;
				GameApplication.app.so.flush();
			}
			
			var request:URLRequest = new URLRequest(GameApplication.app.config.authVKUrl);
			request.method = URLRequestMethod.GET;
			var variables:URLVariables = new URLVariables();
			variables.client_id = GameApplication.app.config.hardcodeSiteVkId;
			variables.scope = "notify";
			variables.redirect_uri = GameApplication.app.config.oficalSiteUrl;
			variables.response_type = "code";
			request.data = variables;
			navigateToURL(request, "_self");
		}
		
		public function authMM():void{
			if(GameApplication.app.so){
				GameApplication.app.so.data["lastconnectedmode"] = GameMode.MM;
				GameApplication.app.so.flush();
			}
			
			var request:URLRequest = new URLRequest(GameApplication.app.config.authMMUrl);
			request.method = URLRequestMethod.GET;
			var variables:URLVariables = new URLVariables();
			variables.client_id = GameApplication.app.config.hardcodeSiteMMId;
			variables.scope = "widget";
			variables.redirect_uri = GameApplication.app.config.oficalSiteUrl;
			variables.response_type = "code";
			request.data = variables;
			navigateToURL(request, "_self");
		}
		
		public function authOD():void{
			if(GameApplication.app.so){
				GameApplication.app.so.data["lastconnectedmode"] = GameMode.OD;
				GameApplication.app.so.flush();
			}
			
			var request:URLRequest = new URLRequest(GameApplication.app.config.authODUrl);
			request.method = URLRequestMethod.GET;
			var variables:URLVariables = new URLVariables();
			variables.client_id = GameApplication.app.config.hardcodeSiteODId;
			variables.scope = "";
			variables.redirect_uri = GameApplication.app.config.oficalSiteUrl;
			variables.response_type = "code";
			request.data = variables;
			navigateToURL(request, "_self");
		}
	}
}