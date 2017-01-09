package application.gamecontainer.chat.useronline {
	import application.gamecontainer.chat.useronline.useritem.UserItem;
	
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.core.UIComponent;
	
	import spark.components.Group;
	import spark.core.NavigationUnit;
	
	import utils.showinterval.ShowInterval;	

	/**
	 * @author dkoltovkin
	 */
	public class ChatUserListBase extends Group {

		private var _queue : Vector.<UserItem> = new Vector.<UserItem>();
		private var _itemHeight : int = 20;		
		private var _resortIntervalId : int = -1;
		private var _bg : UIComponent = new UIComponent();		private var _showInterval : ShowInterval = new ShowInterval(-1, 0);
		private var _queueChange : Boolean;
		private var _needShowInterval: ShowInterval = new ShowInterval(0, 0);

		public function ChatUserListBase() {
		}

		override public function getVerticalScrollPositionDelta(navigationUnit : uint) : Number {
			if (navigationUnit == NavigationUnit.DOWN){
				return _itemHeight;
			}
			return -_itemHeight;
		}

		
		override public function set verticalScrollPosition(value : Number) : void {

				var min : int = Math.floor(value / _itemHeight);
				var max : int = min + Math.ceil(height / _itemHeight);					
				onResort(min, max);				

			super.verticalScrollPosition = value;
		}

		
		public function addItem(element : UserItem) : UserItem {
			return addItemAt(element, _queue.length);
		}

		public function addItemAt(element : UserItem, index : int) : UserItem {
			element.includeInLayout = false;
			if (index < _queue.length - 1) {
				_queue.splice(index, 0, element);
			} else {
				_queue.push(element);
			}
			
			_bg.height = _queue.length * _itemHeight;
			
			_queueChange = true;
			resort(_showInterval.min, _showInterval.max);
			return element;
		}
		
		public function removeItem(item : UserItem) : Boolean {
			for (var i : uint = 0, len : uint = _queue.length;i < len;i++) {
				if (_queue[i] == item) {					_queue.splice(i, 1);
					_bg.height = _queue.length * _itemHeight;
					_queueChange = true;
					resort(_showInterval.min, _showInterval.max);
					
					return true;
				}
			}
			
			return false;
		}

		private function onResort(min : int,max : int) : void {
			var i:int;
			var len : uint;
			var item : UserItem;
			if (initialized) {
			
//				if (_queue.length) {
					if (min < 0) {
						min = 0;
					}
					if (min > _queue.length - 1) {
						min = _queue.length - 1;
					}
					if (max < 0) {
						max = 0;
					}
					if (max > _queue.length) {
						max = _queue.length;
					}
					
					
					if (_showInterval.min > _queue.length - 1) {
						_showInterval.min = _queue.length - 1;
					}
					if (_showInterval.max > _queue.length) {
						_showInterval.max = _queue.length;
					}
			
			
					if (_queueChange) {
						_queueChange = false;
						removeAllElements();
						addElement(_bg);
							
						for (i = min;i < max;i++) {
							item = _queue[i];
							item.y = _itemHeight * i;
//							item.height = _itemHeight;
							//item.width = width;
							addElement(item);
						}
					} else {

						if (_showInterval.min != min || _showInterval.max != max) {
							var topDelta : int = _showInterval.min - min;
					
							if (topDelta > 0) {
								for (i = min, len = _showInterval.min;i < len;i++) {
									item = _queue[i];
									item.y = _itemHeight * i;
									addElement(item);
								}
							} else {
								for (i = _showInterval.min, len = Math.min(_showInterval.max,min);i < len;i++) {
									item = _queue[i];
									removeElement(item);
								}
							}
						
						
							var bottomDelta : int = _showInterval.max - max;
							if (bottomDelta < 0) {
								for (i = _showInterval.max, len = max;i < len;i++) {
									item = _queue[i];
									item.y = _itemHeight * i;
									item.height = _itemHeight;
									item.width = width;
									addElement(item);
								}
							} else {
								for (i = Math.max(_showInterval.min,max), len = _showInterval.max;i < len;i++) {
									item = _queue[i];
									removeElement(item);
								}
							}
						}
					}
					_showInterval.min = min;
					_showInterval.max = max;
				}
			
			_resortIntervalId > -1 && clearInterval(_resortIntervalId);
			_resortIntervalId = -1;
		}
		
		private function onResortInterval() : void {
			onResort(_needShowInterval.min,_needShowInterval.max);
		}

		private function resort(min : int,max : int) : void {
			_needShowInterval.min = min;			_needShowInterval.max = max;
			if (_resortIntervalId == -1) {
				_resortIntervalId = setInterval(onResortInterval, 100);
			}
		}

		
		override protected function updateDisplayList(w : Number,h : Number) : void {
			super.updateDisplayList(w, h);
		}

		
		
		override public function set initialized(value : Boolean) : void {
			super.initialized = value;
			_queueChange = true;
			_showInterval.min = 0;			_showInterval.max = Math.ceil(height / _itemHeight);
			resort(_showInterval.min, _showInterval.max);
		}
	}
}
