package application.components.popup.changeInfo
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpChangeInfo extends PopUpTitled
	{
		private var _changeinfo:ChangeInfo = new ChangeInfo();
		
		public function PopUpChangeInfo()
		{
			super();
			title = "Параметры персонажа";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_changeinfo);
		}
	}
}