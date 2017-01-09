package application.components.popup.help.experience
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpExperience extends PopUpTitled
	{
		private var _help:HelpExperience = new HelpExperience();
		public function PopUpHelpExperience()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}