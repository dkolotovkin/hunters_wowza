package application.components.popup.enemies
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpEnemies extends PopUpTitled
	{
		private var _content:EnemiesContent = new EnemiesContent();
		
		public function PopUpEnemies()
		{
			super();
			title = "Мои враги";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}