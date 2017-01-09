package utils.managers.useitem
{
	import flash.events.Event;
	
	import utils.shop.item.Item;
	
	public class UseItemEvent extends Event
	{
		public static var SALE_PRESENT:String = "SALE_PRESENT";
		public static var FIX_ITEM:String = "FIX_ITEM";
		public static var DROP:String = "DROP";
		public static var PUT:String = "PUT";
		public static var TAKEOFF:String = "TAKEOFF";
		
		public var item:Item;
		
		public function UseItemEvent(type:String, item:Item)
		{
			super(type, false, false);
			this.item = item;
		}
		
		override public function clone() : Event {
			return new UseItemEvent(type, item);
		}
	}
}