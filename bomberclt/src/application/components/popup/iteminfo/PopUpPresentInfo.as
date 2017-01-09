package application.components.popup.iteminfo
{
	import application.components.popup.PopUpTitled;
	
	import utils.shop.item.ItemPresent;
	
	public class PopUpPresentInfo extends PopUpTitled
	{
		private var _content:PresentInfo;
		private var _item:ItemPresent;
		
		public function PopUpPresentInfo(item:ItemPresent)
		{
			super();
			_item = item;
			_content = new PresentInfo();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			_content.title.text = _item.title;
			_content.description.text = "отправитель: " + _item.presenter;
			_content.article.init(_item);
			addElement(_content);
		}
	}
}