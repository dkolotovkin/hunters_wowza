package application.components.popup.help.startbonus
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpStartBonus extends PopUpTitled
	{
		private var _content:StartBonusContent = new StartBonusContent();
		
		public function PopUpStartBonus(){
			super();
			priority = 1000;
			title = "Поздравляем!";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}