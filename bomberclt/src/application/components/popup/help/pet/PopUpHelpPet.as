package application.components.popup.help.pet
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpPet extends PopUpTitled
	{
		private var _help:HelpPet = new HelpPet();
		public function PopUpHelpPet()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}