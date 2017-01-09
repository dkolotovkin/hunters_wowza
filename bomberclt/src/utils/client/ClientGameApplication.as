package utils.client
{
	import application.GameApplication;
	
	import flash.events.EventDispatcher;
	import flash.utils.setInterval;
	
	import mx.controls.Alert;
	
	import utils.chat.message.MessageType;
	import utils.chat.room.Room;

	public class ClientGameApplication extends EventDispatcher
	{		
		public function ClientGameApplication(){
		}		
		
		public function close():void{			
		}
		
		public function initPersParams(obj:Object):void{			
			GameApplication.app.userinfomanager.initPersParams(obj);
			GameApplication.app.navigator.goHome();
			GameApplication.app.userinfomanager.getEnemies(null);
		}
		
		public function processMassage(message:Object):void{			
			GameApplication.app.chatmanager.processMassage(message);
		}
		
		public function processGameAction(action:Object):void{
			GameApplication.app.gamemanager.processGameAction(action);
		}
	}
}