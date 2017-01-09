package utils.managers.tooltip {
		
	import flash.display.DisplayObject;
	import flash.display.DisplayObjectContainer;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.core.IToolTip;
	import mx.core.IVisualElement;
	
	import spark.components.Application;
	

	/**
	 * @author dkolotovkin
	 */
	public class GameToolTipManager {

		private var _host : Application;
		private var _toolTips : Object = new Object();
		private var _intervalId : int = -1;
		private var _target : utils.managers.tooltip.IToolTiped;
		private var _tooltipElem : DisplayObjectContainer;
		private var _currentToolTip : IToolTip;

		
		public function get currentToolTip() : IToolTip {
			return _currentToolTip;
		}

		public function set currentToolTip(value : IToolTip) : void {
			if (_currentToolTip != value) {
				
				if (_currentToolTip) {
					_host.removeElement(IVisualElement(_currentToolTip));
				}
				
				_currentToolTip = value;
				
				if (_currentToolTip) {
					_currentToolTip.visible = false;
					_host.addElement(IVisualElement(_currentToolTip));	
				}
			}	
		}		
		
		private function getToolTip(type : int) : IToolTip {
			return _toolTips[type] || (_toolTips[type] = ToolTipType.createToolTip(type));
		}

		private function updateCurrentToolTip(type : int) : void {
			currentToolTip = getToolTip(type);
		}	
		
		private function hideToolTip() : void {
			currentToolTip = null;
		}

		public function append(host : Application) : void {
			if (!_host) {
				_host = host;
				_host.addEventListener(MouseEvent.ROLL_OVER, onRollOver, true);
				_host.addEventListener(MouseEvent.ROLL_OUT, onRollOut, true);				hideToolTip();
			}
		}

		private function onRollOut(event : MouseEvent) : void {
			hideToolTip();
			if (_intervalId > -1) {
				_tooltipElem = null;
				clearInterval(_intervalId);
				_intervalId = -1;
			}
		}

		private function onRollOver(event : MouseEvent) : void {	
			if (event.target is IToolTiped) {
				if (_tooltipElem && (event.target as DisplayObjectContainer).contains(_tooltipElem)) {
					_tooltipElem = event.target as DisplayObjectContainer;
					return;
				}
				var txt : String = IToolTiped(event.target).toolTip;
				if (txt && txt.length || IToolTiped(event.target).toolTipType != ToolTipType.SIMPLE) {
					_tooltipElem = event.target as DisplayObjectContainer;
					if (_intervalId > -1) {
						_tooltipElem = null;
						clearInterval(_intervalId);
					}
					_intervalId = setInterval(onShowInterval, (event.target as IToolTiped).toolTipDelay, event.target);
					setToolTipText((event.target as IToolTiped));
				}
			}
		}

		private function setToolTipText(target : IToolTiped) : void {
			
			updateCurrentToolTip(target.toolTipType);
			
			if (_currentToolTip is IToolTipExt) {
				(_currentToolTip as IToolTipExt).target = target;
			}else{
				_currentToolTip.text = target.toolTip;
			}
			_target = target;
		}	

		public function toolTipUpdate() : void {
			if(_target) {
				setToolTipText(_target);
			}
		}

		private function onShowInterval(target : IToolTiped) : void {
			if (_intervalId > -1) {
				_tooltipElem = null;
				clearInterval(_intervalId);
				_intervalId = -1;
			}
			
			if (_currentToolTip is IToolTipExt) {
				IToolTipExt(_currentToolTip).show(target.toolTipDX, target.toolTipDY);
			}
		}

		public function remove() : void {
			if (_host) {
				_currentToolTip = null;
				_host.removeEventListener(MouseEvent.ROLL_OVER, onRollOver, true);
				_host.removeEventListener(MouseEvent.ROLL_OUT, onRollOut, true);
			}
		}
	}
}
