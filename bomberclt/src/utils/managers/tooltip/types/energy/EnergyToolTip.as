package utils.managers.tooltip.types.energy
{
	import application.gamecontainer.persinfobar.energy.EnergyIndicator;
	
	import mx.core.IToolTip;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.types.siimple.ToolTip;
	
	public class EnergyToolTip extends ToolTip implements IToolTip
	{
		private var _energy:uint;
		private var _maxenergy:uint;
		
		public function EnergyToolTip()
		{
			super();
			setStyle("skinClass", EnergyToolTipSkin);
		}
		
		override public function updateState() : void {
			if (initialized) {
				(skin as EnergyToolTipSkin).energy.text = _energy + "/" + _maxenergy;				
			}
		}		
		
		override public function set target(value : IToolTiped) : void {
			if (value is EnergyIndicator){				
				_energy = EnergyIndicator(value).energy;
				_maxenergy = EnergyIndicator(value).maxenergy;
				updateState();
			}
		}
	}
}