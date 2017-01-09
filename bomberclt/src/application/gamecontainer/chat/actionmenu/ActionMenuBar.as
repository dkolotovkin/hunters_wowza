package application.gamecontainer.chat.actionmenu{
	
	import spark.components.HGroup;
	import application.gamecontainer.chat.actionmenu.ActionMenuBarSkin;
	import application.gamecontainer.chat.actionmenu.button.ActionBarButton;
	import application.gamecontainer.chat.actionmenu.button.ActionBarButtonEvent;
	import application.gamecontainer.chat.actionmenu.button.ActionBarButtons;
	
	import spark.components.SkinnableContainer;
	
	public class ActionMenuBar extends SkinnableContainer
	{
		private var _buttons : HGroup = new HGroup();
		private var _buttonsHash : Object = new Object();
		
		public function ActionMenuBar()
		{
			super();
			_buttons.gap = 5;
			setStyle("skinClass", ActionMenuBarSkin);
		}
		
		public function updateToolTip (type:String,text:String):void {
			
			var b:ActionBarButton = getButton(type);
			if (b){
				b.toolTip = text;
			}
		}
		
		public function getButton (type:String):ActionBarButton {
			return _buttonsHash[type];
		}
		
		override protected function createChildren() : void {
			super.createChildren();
			addElement(_buttons);						
		}
		
		public function addButton(type : String,text : String, index:int = -1) : Boolean {
			if (!_buttonsHash[type]) {
				var b : ActionBarButton = createButton(type);
				if (b) {
					_buttonsHash[type] = b;
					b.toolTip = text;
					b.addEventListener(ActionBarButtonEvent.CLICK_BUTTON, onClickButton, false, 0, true);
					if (index > -1){
						_buttons.addElementAt(b, index);
					}else _buttons.addElement(b);
					return true;
				}
			}
			return false;
		}
		
		public function removeButton(type:String) : Boolean {
			var b:ActionBarButton = _buttonsHash[type];
			if (b){
				delete _buttonsHash[type];
				b.removeEventListener(ActionBarButtonEvent.CLICK_BUTTON, onClickButton);
				_buttons.removeElement(b);
				return true;
			}
			return false;
		}
		
		private function onClickButton(event : ActionBarButtonEvent) : void {
			dispatchEvent(event.clone());
		}
		
		public function createButton(type : String) : ActionBarButton{
			return new (ActionBarButtons.getClassByType(type));
		}
	}
}