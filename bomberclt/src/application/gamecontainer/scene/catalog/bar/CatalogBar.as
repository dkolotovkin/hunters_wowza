package application.gamecontainer.scene.catalog.bar
{
	import application.gamecontainer.scene.catalog.bar.tab.CatalogTab;
	import application.gamecontainer.scene.catalog.bar.tab.CatalogTabEvent;
	import application.components.separator.SeparatorMXML;
	import utils.selector.Selector;
	
	import spark.components.HGroup;
	import spark.layouts.VerticalAlign;
	
	import utils.selector.SelectorEvent;
	import utils.shop.category.CatalogBarCategory;
	
	public class CatalogBar extends HGroup
	{	
		public var hash:Object = new Object ();
		private var _tabs:Array = new Array();
		private var _selector:Selector = new Selector ();
		
		private var _tabsGroup:HGroup = new HGroup();
		
		public var selectedTab:CatalogTab;
		
		public function get tabs():Array{
			return _tabs;
		}
		
		public function CatalogBar() {
			verticalAlign = VerticalAlign.BOTTOM;
			
			_selector.addEventListener(SelectorEvent.SELECTED, onSelected, false, 0, true);
			_selector.addEventListener(SelectorEvent.UNSELECTED, onUnselected, false, 0, true);
		}
		
		override protected function createChildren():void {
			super.createChildren();
			_tabsGroup.gap = 0;
			_tabsGroup.verticalAlign = VerticalAlign.BOTTOM;
			
			var underLine:SeparatorMXML = new SeparatorMXML();
			underLine.percentWidth = 100;
			underLine.height = 1;
			
			addElement(_tabsGroup);
			addElement(underLine);
		}
		
		private function onUnselected(event : SelectorEvent) : void {
		}
		
		private function onSelected(event : SelectorEvent) : void {					
		}
		
		public function addCategories(list:Array):void{		
			for each (var category : CatalogBarCategory in list) {				
				var tab:CatalogTab = new CatalogTab();
				tab.init(category);
				addTab(tab);
			}
		}
		
		public function addTab (tab:CatalogTab):void {
			if (!hash[tab.category.id]){
				hash[tab.category.id] = tab;
				_tabs.push(tab);
				tab.addEventListener(CatalogTabEvent.SELECTED, onTabSelected, false, 0, true);
				tab.addEventListener(CatalogTabEvent.UNSELECTED, onTabUnselected, false, 0, true);
				_tabsGroup.addElement(tab);
			}
		}
		
		private function onTabSelected(event : CatalogTabEvent) : void {
			selectedTab = event.tab;
			
			_selector.selected(event.tab);
			dispatchEvent(event.clone());
		}
		
		private function onTabUnselected(event : CatalogTabEvent) : void {
			_selector.unselected(event.tab);
			dispatchEvent(event.clone());
		}
		
		public function removeTab (id:int):void {
			var tab:CatalogTab = hash[id];
			if (tab){
				for (var i : uint = 0, len:uint = _tabs.length; i <  len; i++) {
					if ((_tabs[i] as CatalogTab).category.id == id){
						_tabs.splice(i, 1);
						break;
					}
				}
				tab.removeEventListener(CatalogTabEvent.SELECTED, onTabSelected);
				tab.removeEventListener(CatalogTabEvent.UNSELECTED, onTabUnselected);
				_tabsGroup.removeElement(tab);
			}
		}
	}
}