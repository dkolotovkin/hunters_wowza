package application.components.popup.betminigame
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpBet extends PopUpTitled
	{
		private var _content:BetInfo = new BetInfo();
		
		public function PopUpBet(section:uint)
		{
			super();
			title = "Ставка";
			_content.section = section;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}