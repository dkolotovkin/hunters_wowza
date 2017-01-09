package application.components.popup.pay
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpPay extends PopUpTitled
	{
		private var _content:PayContent = new PayContent();
		
		public function PopUpPay(money:int)
		{
			super();
			title = "Зарплата";
			_content.money = money;
			_content.closeFunction = onHide;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}