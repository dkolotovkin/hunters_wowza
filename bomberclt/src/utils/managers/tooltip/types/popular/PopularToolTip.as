package utils.managers.tooltip.types.popular
{
	import application.GameApplication;
	import application.gamecontainer.persinfobar.popular.PopularPart;
	
	import mx.core.IToolTip;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.types.siimple.ToolTip;
	
	public class PopularToolTip extends ToolTip implements IToolTip
	{
		private var _popular:uint;
		private var _title:String;
		private var _min:uint;
		private var _max:uint;
		
		public function PopularToolTip()
		{
			super();
			setStyle("skinClass", PopularToolTipSkin);
		}
		
		override public function updateState() : void {
			if (initialized) {				
				(skin as PopularToolTipSkin).minmax.text = _title + " (" + _min + " - " + _max + ")";
				(skin as PopularToolTipSkin).mypopular.text = "Популярность: " + GameApplication.app.userinfomanager.getPopularTitle(_popular) + " (" + _popular + ")";
				if(_popular > _min && _popular < _max){
					(skin as PopularToolTipSkin).nextlevelgr.visible = (skin as PopularToolTipSkin).nextlevelgr.includeInLayout = true;
					(skin as PopularToolTipSkin).nextlevel.text = String(_max - _popular);
				}else{
					(skin as PopularToolTipSkin).nextlevelgr.visible = (skin as PopularToolTipSkin).nextlevelgr.includeInLayout = false;
				}
			}
		}
		
		
		override public function set target(value : IToolTiped) : void {
			if (value is PopularPart){				
				_min = PopularPart(value).min;
				_max = PopularPart(value).max;
				_title = PopularPart(value).title;
				_popular = PopularPart(value).popular;
				updateState();
			}
		}
	}
}