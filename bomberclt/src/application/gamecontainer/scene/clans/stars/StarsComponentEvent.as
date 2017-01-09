package application.gamecontainer.scene.clans.stars
{
	import flash.events.Event;
	
	public class StarsComponentEvent extends Event
	{
		public static var CHANGE_ROLE:String = "CHANGE_ROLE";
		public var role:int;
		
		public function StarsComponentEvent(type:String, role:int)
		{
			super(type, false, false);
			this.role = role;
		}
	}
}