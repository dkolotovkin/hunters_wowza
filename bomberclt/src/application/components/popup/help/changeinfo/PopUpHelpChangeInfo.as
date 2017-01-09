package application.components.popup.help.changeinfo
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpHelpChangeInfo extends PopUpTitled
	{
		private var _help:HelpChangeInfo = new HelpChangeInfo();
		public function PopUpHelpChangeInfo()
		{
			super();	
			priority = 1000;
			_closeBt.visible = false;
			title = "Привет!";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_help);
		}
	}
}