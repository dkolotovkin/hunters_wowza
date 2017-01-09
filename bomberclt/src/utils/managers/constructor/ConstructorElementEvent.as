package utils.managers.constructor
{
	import flash.events.Event;
	
	import utils.constructor.ConstructorElement;
	
	public class ConstructorElementEvent extends Event
	{
		public static const SELECTED:String = "selected";
		public static const UNSELECTED:String = "unselected";
		
		public var element:ConstructorElement;
		
		public function ConstructorElementEvent(type : String, element:ConstructorElement) {
			super(type, false, false);
			this.element = element;
		}
		
		override public function clone() : Event {
			return new ConstructorElementEvent(type, element);
		}
	}
}