package utils.managers.tooltip {	
	import mx.core.IToolTip;
	
	import utils.managers.tooltip.types.clanrole.ClanRoleToolTip;
	import utils.managers.tooltip.types.energy.EnergyToolTip;
	import utils.managers.tooltip.types.experience.ExperienceToolTip;
	import utils.managers.tooltip.types.popular.PopularToolTip;
	import utils.managers.tooltip.types.siimple.ToolTip;
	import utils.managers.tooltip.types.titleanddesc.TitleAndDescriptionToolTip;

	public class ToolTipType {		
		public static const SIMPLE:int = 0;		public static const EXPERIENCE:int = 1;		public static const ENERGY:int = 2;		
		public static const CLANROLE:int = 3;
		public static const TITLEANDDESCRIPTION:int = 4;
		public static const BETGAME:int = 5;
		public static const POPULAR:int = 6;
				
		public static function createToolTip (type:int):IToolTip {
			if (type == EXPERIENCE){				return new ExperienceToolTip();
			}else if (type == ENERGY){
				return new EnergyToolTip();
			}else if (type == CLANROLE){
				return new ClanRoleToolTip();
			}else if (type == TITLEANDDESCRIPTION){
				return new TitleAndDescriptionToolTip();
			}else if (type == POPULAR){
				return new PopularToolTip();
			}
			return new ToolTip();
		}		
	}
}
