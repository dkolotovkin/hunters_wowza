package application.components.popup.winjp
{
	import application.components.popup.PopUpTitled;

	public class PopUpWinJackPot extends PopUpTitled
	{
		private var _content:WinJPContent = new WinJPContent();
		
		public function PopUpWinJackPot(money:int, winner:String)
		{
			super();
			title = "";
			_content.money = money;
			_content.winner = winner;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}