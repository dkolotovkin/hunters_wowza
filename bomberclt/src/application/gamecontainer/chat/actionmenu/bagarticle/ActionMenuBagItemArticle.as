package application.gamecontainer.chat.actionmenu.bagarticle
{
	import application.GameApplication;
	import application.components.popup.fix.PopUpFix;
	import application.components.popup.iteminfo.PopUpItemInfo;
	import application.gamecontainer.chat.actionmenu.ActionMenu;
	import application.gamecontainer.chat.actionmenu.Actions;
	import application.gamecontainer.chat.actionmenu.title.ArticleTitle;
	import application.gamecontainer.chat.actionmenu.title.TargetTitle;
	
	import utils.shop.item.Item;
	
	public class ActionMenuBagItemArticle extends ActionMenu
	{
		private var _item : Item;
		
		public function ActionMenuBagItemArticle(item : Item) {
			super();
			_item = item;			
			title = _item.title;
		}
		
		override protected function createChildren() : void {
			super.createChildren();
			_titleComponent.setTitle("", true, 0);
			addAndCreateControl(Actions.INFO, "Информация");
			if(_item.onuser){
				addAndCreateControl(Actions.TAKEOFF, "Снять");
			}else{
				addAndCreateControl(Actions.PUT, "Надеть");
			}
			addAndCreateControl(Actions.FIX, "Починить");
			addAndCreateControl(Actions.DROP, "Выбросить");
		}
		
		override protected function getTitleComponent ():TargetTitle{
			return new ArticleTitle();
		}
		
		override protected function onAction(id : String) : void {
			super.onAction(id);			
			if (id == Actions.INFO){
				GameApplication.app.popuper.show(new PopUpItemInfo(_item));
			}else if(id == Actions.FIX){
				GameApplication.app.popuper.show(new PopUpFix(_item));
			}else if(id == Actions.DROP){
				GameApplication.app.shopmanager.dropItem(_item);
			}else if(id == Actions.PUT){
				GameApplication.app.shopmanager.putItem(_item);
			}else if(id == Actions.TAKEOFF){
				GameApplication.app.shopmanager.takeOffItem(_item);
			}
		}
	}
}