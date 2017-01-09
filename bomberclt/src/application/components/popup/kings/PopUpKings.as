package application.components.popup.kings
{
	import application.GameApplication;
	import application.components.popup.PopUpTitled;
	
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.collections.ArrayCollection;
	
	import utils.user.User;
	
	public class PopUpKings extends PopUpTitled
	{
		private var _content:KingsInfo = new KingsInfo();
		private var _time:Number;
		private var _sid:int = -1;
		
		public function PopUpKings(passtime:Number, king:User, queen:User, aspirantsCollection:ArrayCollection, iisaspirant:Boolean)
		{
			super();
			title = "Выборы королей";
			_content.aspirants = aspirantsCollection;
			_content.king = king;
			_content.queen = queen;
			
			if(!iisaspirant && GameApplication.app.userinfomanager.myuser.level >= 5){
				_content.requestvisible = true;
			}else{
				_content.requestvisible = false;
			}
			
			if(_sid != -1){
				clearInterval(_sid);
				_sid = -1;
			}
			_time = passtime;
			_sid = setInterval(updateTime, 1000); 
			updateTime();
		}
		
		override public function onHide():Boolean{
			if(_sid != -1){
				clearInterval(_sid);
				_sid = -1;
			}
			return super.onHide();
		}
		
		private function updateTime():void{
			if(_time > 0){
				var second:int = 1;
				var minute:int = second * 60;
				var hour:int = minute * 60;
				var day:int = hour * 24;
				
				var days:int = Math.floor(_time / (day));
				var hours:int =  Math.floor((_time - days * day) / (hour));
				var minuts:int = Math.floor((_time - days * day - hours * hour) / minute);
				var seconds:int = _time - days * day - hours * hour - minuts * minute;
				
				_content.timetext = "До коронации: " + days + "дн. " + fillZero(hours) + " ч. " + fillZero(minuts) + " мин. " + fillZero(seconds) + " с.";
				_time--;
			}else{
				if(_sid != -1){
					clearInterval(_sid);
					_sid = -1;
				}
			}
		}
		
		private function fillZero(value:int):String{
			if(value <= 9) return String("0" + value);
			return String(value);
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}