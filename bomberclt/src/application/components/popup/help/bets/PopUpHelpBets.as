package application.components.popup.help.bets
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpBets extends PopUpTitled
	{
		private var _help:HelpBets = new HelpBets();
		public function PopUpHelpBets()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}