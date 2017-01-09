package application.components.popup.createbetgame
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpCreateBetGame extends PopUpTitled
	{
		private var _info:CreateBetInfo = new CreateBetInfo();
		
		public function PopUpCreateBetGame()
		{
			super();
			title = "Создание забега";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_info);
		}
	}
}