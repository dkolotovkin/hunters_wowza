package utils.managers.tooltip.types.clanrole
{
	import application.gamecontainer.scene.clans.stars.StarMXML;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.types.siimple.ToolTip;
	
	public class ClanRoleToolTip extends ToolTip
	{
		private var _title:String;
		private var _description:String;
		
		public function ClanRoleToolTip()
		{
			super();
			setStyle("skinClass", ClanRoleToolTipSkin);
		}
		
		override public function updateState() : void {
			if (initialized) {
				(skin as ClanRoleToolTipSkin).title.text = _title;
				(skin as ClanRoleToolTipSkin).description.text = _description;
			}
		}		
		
		override public function set target(value : IToolTiped) : void {			
			if (value is StarMXML){				
				_title = StarMXML(value).title;
				_description = StarMXML(value).description;
				updateState();
			}
		}
	}
}