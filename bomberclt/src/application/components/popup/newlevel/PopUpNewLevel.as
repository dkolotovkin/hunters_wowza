package application.components.popup.newlevel
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpNewLevel extends PopUpTitled
	{
		private var _content:NewLevelContent = new NewLevelContent();
		
		public function PopUpNewLevel(level:int, prize:int)
		{
			super();
			priority = 100;
			title = "Поздравляем!";
			_content.init(level, prize);
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}