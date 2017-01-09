package application.components.popup.help
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelp extends PopUpTitled
	{
		private var _help:Help = new Help();
		public function PopUpHelp()
		{
			super();			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}