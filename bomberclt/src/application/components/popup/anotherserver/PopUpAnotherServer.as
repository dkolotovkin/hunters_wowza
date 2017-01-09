package application.components.popup.anotherserver
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpAnotherServer extends PopUpTitled
	{
		private var _content:AnotherServerContent = new AnotherServerContent();
		
		public function PopUpAnotherServer()
		{
			super();
			title = "Перейти в другой город";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}