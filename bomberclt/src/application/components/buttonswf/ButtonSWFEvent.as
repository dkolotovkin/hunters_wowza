package application.components.buttonswf
{
	import flash.events.Event;
	
	public class ButtonSWFEvent extends Event
	{
		public static const SELECTED:String = "selected"; 
		public static const UNSELECTED:String = "unselected"; 
		public static const CLICK:String = "BUTTON_CLICK"; 
		
		
		public var button:ButtonSWF;
		
		public function ButtonSWFEvent(type : String,button:ButtonSWF) {
			super(type, false,false);
			this.button = button;
		}
		
		override public function clone() : Event {
			return new ButtonSWFEvent(type, button);
		}
	}
}