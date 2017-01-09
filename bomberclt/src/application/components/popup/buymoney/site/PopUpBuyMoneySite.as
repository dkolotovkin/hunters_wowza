package application.components.popup.buymoney.site
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpBuyMoneySite extends PopUpTitled
	{
		private var _content:BuyMoneySiteInfo = new BuyMoneySiteInfo();
		
		public function PopUpBuyMoneySite()
		{
			super();
			title = "";	
			_content.closeFunction = closePopUp;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
		
		public function closePopUp():void{
			onHide();
		}
	}
}