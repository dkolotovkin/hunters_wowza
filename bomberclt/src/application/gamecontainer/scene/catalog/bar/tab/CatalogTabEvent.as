package application.gamecontainer.scene.catalog.bar.tab
{
	import flash.events.Event;
	
	public class CatalogTabEvent extends Event
	{
		public static const SELECTED:String = "selected";
		public static const UNSELECTED:String = "unselected";
		
		public var tab:CatalogTab;
		
		public function CatalogTabEvent(type : String, tab:CatalogTab) {
			super(type, false, false);
			this.tab = tab;
		}
		
		override public function clone() : Event {
			return new CatalogTabEvent(type, tab);
		}
	}
}