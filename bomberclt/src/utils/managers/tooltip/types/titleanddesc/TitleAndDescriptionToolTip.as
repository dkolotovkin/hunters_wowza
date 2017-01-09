package utils.managers.tooltip.types.titleanddesc
{
	import application.components.dressingroom.DressHero;
	import application.gamecontainer.scene.bag.article.BagItemArticle;
	import application.gamecontainer.scene.bag.article.BagPresentArticle;
	import application.gamecontainer.scene.bag.article.DressingRoomItemArticle;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.types.siimple.ToolTip;
	
	public class TitleAndDescriptionToolTip extends ToolTip
	{
		private var _title:String;
		private var _description:String;
		
		public function TitleAndDescriptionToolTip()
		{
			super();
			setStyle("skinClass", TitleAndDescriptionToolTipSkin);
		}
		
		override public function updateState() : void {
			if (initialized) {
				(skin as TitleAndDescriptionToolTipSkin).title.text = _title;
				(skin as TitleAndDescriptionToolTipSkin).description.text = _description;
			}
		}
		
		override public function set target(value : IToolTiped) : void {
			var percent:int;
			if (value is BagPresentArticle){				
				_title = BagPresentArticle(value).tooltiptitle;
				_description = BagPresentArticle(value).tooltipdescription;
				updateState();
			}else if (value is BagItemArticle){
				percent = Math.round((BagItemArticle(value).item.quality / BagItemArticle(value).item.maxquality) * 100);
				_title = BagItemArticle(value).item.title;
				_description = "качество: " + percent + "%";
				updateState();
			}else if (value is DressHero){	
				percent = Math.round((DressHero(value).item.quality / DressHero(value).item.maxquality) * 100);
				_title = DressHero(value).item.title;
				_description = "качество: " + percent + "%";
				updateState();
			}else if (value is DressingRoomItemArticle){
				if(DressingRoomItemArticle(value).article.item == null){
					_title = "Слот для оружия";
					_description = "которое используется на охоте";
				}else{
					percent = Math.round((DressingRoomItemArticle(value).article.item.quality / DressingRoomItemArticle(value).article.item.maxquality) * 100);
					_title = DressingRoomItemArticle(value).article.item.title;
					_description = "качество: " + percent + "%";
				}
				updateState();
			}
		}
	}
}