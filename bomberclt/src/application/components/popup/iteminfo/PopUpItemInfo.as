package application.components.popup.iteminfo
{
	import application.components.popup.PopUpTitled;
	
	import utils.game.GameConfig;
	import utils.shop.category.ShopCategory;
	import utils.shop.item.Item;
	
	public class PopUpItemInfo extends PopUpTitled
	{
		private var _content:ItemInfo;
		private var _item:Item;
		
		public function PopUpItemInfo(item:Item)
		{
			super();
			_item = item;
			_content = new ItemInfo();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			_content.title.text = _item.title;
			
			var percent:int = Math.round((_item.quality / _item.maxquality) * 100);
			
			if(_item.description && _item.description.length) _content.description.text = _item.description;
			else _content.description.text = "";
			if(_item.maxquality > 0) _content.description.text += "\nИзнос: " + (100 - percent) +  "% (" + _item.quality + " из " + _item.maxquality + ")";
			else _content.description.text += "\nКачество: 0%";
			if(_item.param1 == ShopCategory.THINGS){
				if(_item.param3 > 0) _content.description.text += "\nБонус к опыту: +" + _item.param3;
				if(_item.param4 > 0) _content.description.text += "\nБонус к опыту альянса: +" + _item.param4;
				if(_item.param5 > 0) _content.description.text += "\nСкорость: +" + _item.param5;
				if(_item.param6 > 0) _content.description.text += "\nСила прыжка: +" + _item.param6;
				if(_item.param7 > 0) _content.description.text += "\nУворотливость: " + _item.param7 * GameConfig.percentSkillByOnePoint + "%";
			}else if(_item.param1 == ShopCategory.ARSENAL){
				_content.description.text += "\nТочность: " + _item.param2;
				_content.description.text += "\nУрон: " + _item.param3;
				_content.description.text += "\nСкорость перезаряда: " + Math.round(((_item.param4 * GameConfig.countMiliSecondByOnePoint) / 1000) * 100) / 100 + " сек.";
			}
			_content.article.init(_item);
			addElement(_content);
		}
	}
}