package application.components.popup.help.testgame
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpTestGame extends PopUpTitled
	{
		private var _content:TestGameContent = new TestGameContent();
		
		public function PopUpTestGame(){
			super();	
			priority = 1000;
			_closeBt.visible = false;
			title = "Охота";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}