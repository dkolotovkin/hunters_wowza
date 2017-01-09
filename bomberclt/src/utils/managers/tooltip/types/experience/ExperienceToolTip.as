package utils.managers.tooltip.types.experience
{
	import application.gamecontainer.persinfobar.experience.ExperienceIndicator;
	
	import mx.core.IToolTip;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.types.siimple.ToolTip;
	
	public class ExperienceToolTip extends ToolTip implements IToolTip
	{		
		private var _experience:uint;
		private var _maxexperience:uint;
		
		public function ExperienceToolTip()
		{
			super();
			setStyle("skinClass", ExperienceToolTipSkin);
		}
		
		override public function updateState() : void {
			if (initialized) {
				(skin as ExperienceToolTipSkin).exp.text = _experience + "/" + _maxexperience;				
			}
		}
		
		
		override public function set target(value : IToolTiped) : void {			
			
			if (value is ExperienceIndicator){				
				_experience = ExperienceIndicator(value).experience;
				_maxexperience = ExperienceIndicator(value).maxexperience;
				updateState();
			}
		}
	}
}