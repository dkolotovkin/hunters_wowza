package application.components.popup.help.rule
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpRule extends PopUpTitled
	{
		private var _content:RuleContent = new RuleContent();
		public function PopUpRule()
		{
			super();
			title = "Правила общения в общем чате";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}