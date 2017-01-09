package application.gamecontainer.scene.clans.stars
{
	import application.components.iconswf.IconMXML;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.ToolTipType;
	
	public class StarMXML extends IconMXML implements IToolTiped
	{
		public var title:String;
		public var description:String;
		
		public function StarMXML()
		{
			super();
		}
		
		public function get toolTipDelay() : int {				
			return 400;
		}
		
		public function get toolTipDX() : int {
			return 10;
		}
		
		public function get toolTipDY() : int {
			return 2;
		}
		
		public function get toolTipType() : int {
			return ToolTipType.CLANROLE;
		}
	}
}