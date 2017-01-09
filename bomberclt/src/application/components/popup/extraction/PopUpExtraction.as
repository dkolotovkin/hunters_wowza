package application.components.popup.extraction
{
	import application.GameApplication;
	import application.components.popup.PopUpTitled;
	
	import utils.game.extraction.ExtractionData;
	
	public class PopUpExtraction extends PopUpTitled
	{
		private var _extractionInfo:ExtractionInfo;
		
		public function PopUpExtraction(extraction:ExtractionData)
		{
			super();
			
			_extractionInfo = new ExtractionInfo();
			
			title = "Окончание охоты";
			if (extraction.experience > 0 || extraction.cexperience > 0 || extraction.money > 0 || extraction.bullets > 0){
				_extractionInfo.description = "Охота окончена. Ваша награда:";
			}else if (extraction.experience < 0){
				_extractionInfo.description = "Охота окончена! К сожалению Вы потеряли опыт (" + extraction.experience + ").";
			}else{
				_extractionInfo.description = "Охота окончена! К сожалению Вы ничего не заработали. Попробуйте еще.";
			}
			_extractionInfo.experience = extraction.experience;
			_extractionInfo.cexperience = extraction.cexperience;
			_extractionInfo.money = extraction.money;
			_extractionInfo.experienceBonus = extraction.experiencebonus;
			_extractionInfo.cexperienceBonus = extraction.cexperiencebonus;
			_extractionInfo.bullets = extraction.bullets;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_extractionInfo);
		}
	}
}