package utils.managers.admin
{
	import application.GameApplication;
	import application.components.popup.PopUp;
	import application.components.popup.userinfo.PopUpUserInfo;
	
	import flash.net.Responder;
	
	import utils.user.User;

	public class AdministratorManager
	{
		public var lastUserID:uint;
		
		public function AdministratorManager(){
		}
		
		public function updateAllUsersParams():void{
			GameApplication.app.connection.call("adminUpdateAllUsersParams", new Responder(onUpdateAllUsersParams, onError));
		}
		private function onUpdateAllUsersParams(result:int):void{
			if(result == 1){				
				GameApplication.app.popuper.showInfoPopUp("Действие выполнено!");
			}else if(result == 0){
				GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие. Вожможно у вас нет прав доступа.");
			}
		}
		
		public function setModerator(userID:int):void{
			GameApplication.app.connection.call("adminSetModerator", new Responder(onSetParam, onError), userID);
		}
		
		public function deleteModerator(userID:int):void{
			GameApplication.app.connection.call("adminDeleteModerator", new Responder(onSetParam, onError), userID);
		}
		
		public function deleteUser(userID:int):void{
			GameApplication.app.connection.call("adminDeleteUser", new Responder(onSetParam, onError), userID);
		}
		
		public function setParam(userID:int, param:int, value:*):void{
			GameApplication.app.connection.call("adminSetParam", new Responder(onSetParam, onError), userID, param, value);
		}
		public function setNameParam(userID:int, param:int, value:*):void{
			GameApplication.app.connection.call("adminSetNameParam", new Responder(onSetParam, onError), userID, param, value);
		}
		
		private function onSetParam(result:int):void{
			if(result == 1){				
				GameApplication.app.popuper.showInfoPopUp("Действие выполнено!");
			}else if(result == 0){
				GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие. Вожможно у вас нет прав доступа.");
			}
		}
		
		public function showInfo(userID:int):void{
			GameApplication.app.connection.call("adminShowInfo", new Responder(onShowInfo, onError), userID);
		}
		
		private function onShowInfo(u:Object):void{
			if(u != null){
				var popUp:PopUp;
				popUp = new PopUpUserInfo(User.createFromObject(u));
				GameApplication.app.popuper.show(popUp);
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие. Вожможно у вас нет прав доступа.");
			}
		}
		
		public function sendNotification(msg:String):void{
			if(msg && msg.length >= 5){
				GameApplication.app.connection.call("adminSendNotification", new Responder(onSendNotification, onError), msg);
			}else{
				GameApplication.app.popuper.showInfoPopUp("Слишком короткий текст сообщения.");
			}
		}
		
		private function onSendNotification(result:int):void{
			if(result == 1){				
				GameApplication.app.popuper.showInfoPopUp("Действие выполнено!");
			}else if(result == 0){
				GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие. Вожможно у вас нет прав доступа.");
			}
		}
		
		private function onError(err:Object):void{			
		}
	}
}