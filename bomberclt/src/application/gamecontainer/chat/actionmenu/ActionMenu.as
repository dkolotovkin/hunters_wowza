package application.gamecontainer.chat.actionmenu {	
	
	import application.GameApplication;
	import application.gamecontainer.chat.actionmenu.*;
	import application.gamecontainer.chat.actionmenu.button.ActionBarButtonEvent;
	import application.gamecontainer.chat.actionmenu.button.ActionMenuButton;
	import application.gamecontainer.chat.actionmenu.button.ActionMenuButtonEvent;
	import application.gamecontainer.chat.actionmenu.title.TargetTitle;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import spark.components.SkinnableContainer;
	import spark.components.VGroup;
	
	import utils.hashlist.HashList;

	/**
	 * @author dkolotovkin
	 */
	public class ActionMenu extends SkinnableContainer {

		private var _controls : HashList = new HashList();		private var _title : String;		private var _description : String;		
		protected var _bar : ActionMenuBar = new ActionMenuBar();	
		protected var _titleComponent : TargetTitle = getTitleComponent();

		public function get title() : String {
			return _title;
		}

		public function set title(value : String) : void {
			if (_title != value) {
				_title = value;
				initialized && updateDisplayList(width, height);
			}	
		}

		public function get description() : String {
			return _description;
		}

		public function set description(value : String) : void {
			if (_description != value) {
				_description = value;
				initialized && updateDisplayList(width, height);
			}	
		}
		
		protected function getTitleComponent ():TargetTitle{
			return new TargetTitle ();
		}
		

		public function onShow() : void {
			this.visible = true;
		}

		public function onHide() : void {			
		}

		public function ActionMenu() {
			this.visible = false;			
			setStyle("skinClass", ActionMenuSkin);			
			_bar.addEventListener(ActionBarButtonEvent.CLICK_BUTTON, onClickButtonBar, false, 0, true);
		}
		

		private function onClickButtonBar(event : ActionBarButtonEvent) : void {
			onAction(event.button.type);
		}

		public function setMenuVisible(v : Boolean) : void {
			this.visible = v;
		}

		public function createControl(id : String,title : String) : ActionMenuButton {
			var button : ActionMenuButton = new ActionMenuButton();
			button.title = title;			button.id = id;
			button.percentWidth = 100;
			return button;
		}
		
		public function addButtonBar(type : String,text : String, index : int = -1) : void {
			_bar.addButton(type, text, index);
		}

		public function removeButtonBar(type : String) : void {
			_bar.removeButton(type);
		}

		
		public function addControl(control : ActionMenuButton) : ActionMenuButton {			
			return addControlAt(control, _controls.length);
		}		

		public function addAndCreateControl(id : String,title : String) : ActionMenuButton {			
			return addControlAt(createControl(id, title), _controls.length);
		}

		public function addControlAt(control : ActionMenuButton,index : uint) : ActionMenuButton {			
			if (!_controls.getItem(id)) {
				control.addEventListener(ActionMenuButtonEvent.CLICK_ACTION, onClickAction, false, 0, true);
				_controls.addItemAt(control, index);				addElement(control);
				return control;
			}
			return null;
		}

		private function onClickAction(event : ActionMenuButtonEvent) : void {
			dispatchEvent(new ActionMenuEvent(ActionMenuEvent.ACTION, this, event.button.id));
			onAction(event.button.id);
		}		

		protected function onAction(id : String) : void {
		}

		override protected function createChildren() : void {
			super.createChildren();						
			_bar.percentWidth = 100;
			_titleComponent.percentHeight = 100;
			_titleComponent.percentWidth = 100;
			
			(skin["title"] as VGroup).addElementAt(_titleComponent,0);
		}		
		
		public function removeControl(id : String) : void {			
			var button : ActionMenuButton = _controls.getItem(id) as ActionMenuButton;			
			if (button) {
				_controls.removeItem(id);
				button.removeEventListener(MouseEvent.CLICK, onClickAction);
				removeElement(button);
			}
		}		
		
		private function onAddToStage(e:Event):void{
			e.target.removeEventListener(Event.ADDED_TO_STAGE, onAddToStage);
			this.visible = true;
			setXY();
		}

		public function setXY() : void {			
			this.x = GameApplication.app.actionShowerMenu.mouseX;
			this.y = GameApplication.app.actionShowerMenu.mouseY;			
		}			
	}
}
