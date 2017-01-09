package application.components.popup.help.clan
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpClan extends PopUpTitled
	{
		private var _help:HelpClan = new HelpClan();
		public function PopUpHelpClan()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}