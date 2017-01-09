package utils.game
{
	import flash.events.Event;
	
	import utils.shop.item.Item;
	
	public class SelectGunEvent extends Event
	{
		public static var SELECT:String = "SELECT";
		
		public var item:Item;
		
		public function SelectGunEvent(type:String, item:Item)
		{
			super(type, false, false);
			this.item = item;
		}
	}
}