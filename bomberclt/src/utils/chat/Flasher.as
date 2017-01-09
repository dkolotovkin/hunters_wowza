package utils.chat
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	public class Flasher extends EventDispatcher
	{
		private var _target:IFlashing;			
		private var _intervalId:int;
		private var _delay:int;
		private var _replayCount:int = -1;			
		
		public function Flasher(target : IFlashing) {
			_target = target;
		}			
		
		public function start (delay:int,replayCount:int = -1):void{
			_delay = delay;
			replayCount > -1 && (_replayCount = replayCount+1);
			_intervalId > -1 && clearInterval(_intervalId);
			_intervalId = setInterval(onUpdateFlash, delay);
		}
		
		private function onUpdateFlash() : void {
			_target.flash = !_target.flash;
			if (_replayCount > 0){
				_replayCount--;
			}else if (_replayCount == 0){
				stop();
			}			
		}
		
		
		public function stop ():void{
			_intervalId > -1 && clearInterval(_intervalId);
			_target.flash = false;
		}		
	}
}