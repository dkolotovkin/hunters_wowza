package utils.managers.ban
{
	import application.GameApplication;
	
	import flash.events.EventDispatcher;
	import flash.events.TimerEvent;
	import flash.net.Responder;
	import flash.utils.Timer;

	public class BanManager extends EventDispatcher
	{
		[Bindable]
		public var bantime:int = 0;
		public var timer:Timer;
		
		public function BanManager(){
		}
		
		public function setBanTime(time:int):void{
			bantime = time;			
			clearTimer();
			
			if(bantime > 0){
				timer = new Timer(1000, bantime + 5);
				timer.start();
				timer.addEventListener(TimerEvent.TIMER, timerHandler);
			}
		}
		
		private function clearTimer():void{
			if(timer != null){
				timer.stop();
				timer.removeEventListener(TimerEvent.TIMER, timerHandler);
				timer = null;
			}
		}
		
		private function timerHandler(e:TimerEvent):void{			
			bantime--;
			dispatchEvent(new BanManagerEvent(BanManagerEvent.TIME_UPDATE, bantime));
			if (bantime <= 0){
				clearTimer();
				bantime == 0;
			}
		}
		
		public function ban(userID:int, type:int, byip:Boolean):void{			
			GameApplication.app.connection.call("ban", new Responder(onBan, onBanError), userID, type, byip);
		}
		
		private function onBan(result:int):void{
			if (result == BanResult.OK){
				
			}else if (result == BanResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас недостаточно денег для этого действия");
			}else if (result == BanResult.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие");
			}
		}
		private function onBanError(e:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно выполнить действие");
		}
		
		public function banoff():void{
			clearTimer();
			bantime = 0;
		}
	}
}