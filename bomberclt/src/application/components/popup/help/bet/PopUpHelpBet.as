package application.components.popup.help.bet
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpBet extends PopUpTitled
	{
		private var _help:HelpBet = new HelpBet();
		public function PopUpHelpBet()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}