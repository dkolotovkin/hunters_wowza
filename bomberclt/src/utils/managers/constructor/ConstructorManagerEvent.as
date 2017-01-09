package utils.managers.constructor
{
	import flash.events.Event;
	
	public class ConstructorManagerEvent extends Event
	{
		public static var ELEMENT_CREATED:String = "ELEMENT_CREATED";
		
		public function ConstructorManagerEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}