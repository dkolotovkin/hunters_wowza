package application.components.popup.help.popular
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpPopular extends PopUpTitled
	{
		private var _help:HelpPopular = new HelpPopular();
		public function PopUpHelpPopular()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}