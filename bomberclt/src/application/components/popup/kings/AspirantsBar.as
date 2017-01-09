package application.components.popup.kings
{
	import application.gamecontainer.scene.catalog.bar.tab.CatalogTab;
	import application.gamecontainer.scene.catalog.bar.tab.CatalogTabEvent;
	
	import spark.components.HGroup;
	import spark.layouts.VerticalAlign;
	
	import utils.selector.Selector;
	import utils.selector.SelectorEvent;
	import utils.shop.category.CatalogBarCategory;
	import utils.user.Sex;

	public class AspirantsBar extends HGroup
	{
		private var _hash:Object = new Object ();
		private var _tabs:Array = new Array();
		private var _selector:Selector = new Selector ();
		
		public function AspirantsBar() {
			verticalAlign = VerticalAlign.BOTTOM;
			
			_selector.addEventListener(SelectorEvent.SELECTED, onSelected, false, 0, true);
			_selector.addEventListener(SelectorEvent.UNSELECTED, onUnselected, false, 0, true);
		}
		
		private function onUnselected(event : SelectorEvent) : void {
		}
		
		private function onSelected(event : SelectorEvent) : void {			
		}
		
		public function showGroups() : void {	
			var tabwidth:int = 150;
			
			var sc1:CatalogBarCategory = new CatalogBarCategory(Sex.MALE, "Король");
			var tab1:CatalogTab = new CatalogTab();
			tab1.width = tabwidth;
			tab1.init(sc1);
			
			var sc2:CatalogBarCategory = new CatalogBarCategory(Sex.FEMALE, "Королева");
			var tab2:CatalogTab = new CatalogTab();
			tab2.width = tabwidth;
			tab2.init(sc2);		
			
			addTab(tab1);
			addTab(tab2);
			
			(_tabs[0] as CatalogTab).selected = true;
		}
		
		public function addTab (tab:CatalogTab):void {
			if (!_hash[tab.category.id]){
				_hash[tab.category.id] = tab;
				_tabs.push(tab);
				tab.addEventListener(CatalogTabEvent.SELECTED, onTabSelected, false, 0, true);
				tab.addEventListener(CatalogTabEvent.UNSELECTED, onTabUnselected, false, 0, true);
				addElement(tab);
			}
		}
		
		private function onTabSelected(event : CatalogTabEvent) : void {
			_selector.selected(event.tab);
			dispatchEvent(event.clone());
		}
		
		private function onTabUnselected(event : CatalogTabEvent) : void {
			_selector.unselected(event.tab);
			dispatchEvent(event.clone());
		}
		
		public function removeTab (id:int):void {
			var tab:CatalogTab = _hash[id];
			if (tab){
				for (var i : uint = 0, len:uint = _tabs.length; i <  len; i++) {
					if ((_tabs[i] as CatalogTab).category.id == id){
						_tabs.splice(i, 1);
						break;
					}
				}
				tab.removeEventListener(CatalogTabEvent.SELECTED, onTabSelected);
				tab.removeEventListener(CatalogTabEvent.UNSELECTED, onTabUnselected);
				removeElement(tab);
			}
		}
	}
}