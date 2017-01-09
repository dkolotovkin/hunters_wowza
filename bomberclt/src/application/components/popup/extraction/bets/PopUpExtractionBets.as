package application.components.popup.extraction.bets
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpExtractionBets extends PopUpTitled
	{
		private var _content:ExtractionBetsContent = new ExtractionBetsContent();
		
		public function PopUpExtractionBets(returnmoney:int, section:int, prizemoney:int)
		{
			super();
			_content.returnmoney = returnmoney;
			_content.section = section;
			_content.prizemoney = prizemoney;
			
			title = "Итоги розыгрыша";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}