package application.components.popup.buy.present
{
	import application.components.popup.PopUpTitled;
	
	import utils.shop.item.ItemPresent;
	import utils.shop.presentprototype.PresentPrototype;
	
	public class PopUpPresentBuy extends PopUpTitled
	{
		private var _buyInfo:BuyPresentInfo;
		private var _prototype:PresentPrototype;
		
		public function PopUpPresentBuy(prototype:PresentPrototype, description:String = "Поздравляем с успешной покупкой!"){
			super();
			priority = 31;
			_prototype = prototype;
			_buyInfo = new BuyPresentInfo();
			_buyInfo.description.text = description;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			_buyInfo.article.init(ItemPresent.createFromPresentPrototype(_prototype));
			addElement(_buyInfo);
		}
	}
}