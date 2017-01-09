package utils.game
{
	import flash.events.Event;
	
	public class GameManagerTimerEvent extends Event
	{
		public static var TIMER_UPDATE:String = "TIMER_UPDATE";
		
		public var time:int;
		
		public function GameManagerTimerEvent(type:String, time:int)
		{
			super(type, false, false);
			this.time = time;
		}
		
		override public function clone() : Event {
			return new GameManagerTimerEvent(type, time);
		}
	}
}