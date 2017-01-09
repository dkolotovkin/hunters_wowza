package application.components.popup.buymoney.od
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpBuyMoneyOD extends PopUpTitled
	{
		private var _bumoneyodInfo:BuyMoneyODInfo = new BuyMoneyODInfo();
		
		public function PopUpBuyMoneyOD()
		{
			super();
			title = "Купить валюту";	
			_bumoneyodInfo.closeFunction = closePopUp;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_bumoneyodInfo);
		}
		
		public function closePopUp():void{
			onHide();
		}
	}
}