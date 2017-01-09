package utils.chat.actionshower {
	
	import application.GameApplication;
	import application.gamecontainer.chat.actionmenu.ActionMenu;
	import application.gamecontainer.chat.actionmenu.ActionMenuEvent;
	
	import flash.events.MouseEvent;
	
	import mx.events.FlexEvent;
	
	import spark.components.Group;

	/**
	 * @author dkolotovkin
	 */
	public class ActionsShowerMenu extends Group {		
		private var  _currentMenu:ActionMenu;	
		
		public function get currentmenu ():ActionMenu {
			return _currentMenu;
		}	
		
		public function ActionsShowerMenu() {				
		}
		
		public function showMenu (menu:ActionMenu,dx:Number = 10,dy:Number = 10):void {			
			if (_currentMenu != menu)
			{				
				_currentMenu && hideMenu();
				_currentMenu = menu;
				_currentMenu.addEventListener(ActionMenuEvent.ACTION, onAction, false, 0, true);
				_currentMenu.addEventListener(ActionMenuEvent.SHOW, onShowMenu, false, 0, true);
				_currentMenu.addEventListener(ActionMenuEvent.HIDE, onHideMenu, false, 0, true);				
				
				if (_currentMenu.initialized){
					correctPosition();
				}else{
					_currentMenu.addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete, false, 0, true);				
				}
				addElement(_currentMenu);
				_currentMenu.onShow();
				_currentMenu.setXY();								
			}
		}
		
		public function openMenu():void{
			showMenu(_currentMenu);
		}
		
		private function onCreationComplete(event : FlexEvent) : void {
			correctPosition();
			correctMenuPosition();
		}
		
		private function correctMenuPosition ():void {
			if (_currentMenu){
				if (_currentMenu.x < 0){
					_currentMenu.x = 0;
				}
				if(_currentMenu.x + _currentMenu.width > parent.width){
					_currentMenu.x = parent.width - _currentMenu.width;
				}
				if (_currentMenu.y < 0){
					_currentMenu.y = 0;
				}
				if(_currentMenu.y + _currentMenu.height > parent.height){
					_currentMenu.y = parent.height - _currentMenu.height;
				}
			}
		}
		
		private function correctPosition ():void {
			if (_currentMenu){
				GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_UP, onStageMouseUp, false, 0, true);
			}
		}

		private function onHideMenu(event : ActionMenuEvent) : void {
			dispatchEvent(event.clone());
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_UP, onStageMouseUp);
		}

		private function onShowMenu(event : ActionMenuEvent) : void {
			dispatchEvent(event.clone());
		}

		private function onStageMouseUp(event : MouseEvent) : void {
			if (_currentMenu){
				if (mouseX < _currentMenu.x || mouseX > _currentMenu.x +_currentMenu.width || mouseY < _currentMenu.y || mouseY > _currentMenu.y +_currentMenu.height + 5){
					hideMenu();
				}
			}
		}

		private function onAction(event : ActionMenuEvent) : void {
			hideMenu();
			dispatchEvent(event.clone());
		}

		public function hideMenu ():void {
			if (_currentMenu){
				
				if (this.contains(_currentMenu)) removeElement(_currentMenu);
				_currentMenu.onHide();
				_currentMenu.removeEventListener(ActionMenuEvent.ACTION, onAction);
				_currentMenu.removeEventListener(ActionMenuEvent.SHOW, onShowMenu);
				_currentMenu.removeEventListener(ActionMenuEvent.HIDE, onHideMenu);
				_currentMenu.removeEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
				_currentMenu = null;
			}
		}
	}
}
