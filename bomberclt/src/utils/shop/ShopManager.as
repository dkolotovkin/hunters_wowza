package utils.shop
{
	import application.GameApplication;
	import application.components.popup.banoff.PopUpBanOff;
	import application.components.popup.buy.PopUpBuy;
	import application.components.popup.buy.present.PopUpPresentBuy;
	import application.gamecontainer.scene.bag.article.BagInGameSmallArticle;
	
	import flash.events.EventDispatcher;
	import flash.net.Responder;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	
	import utils.copyproperties.CopyProperties;
	import utils.managers.useitem.UseItemEvent;
	import utils.shop.category.CatalogBarCategory;
	import utils.shop.category.ShopCategory;
	import utils.shop.item.Item;
	import utils.shop.item.ItemPresent;
	import utils.shop.itemprototype.ItemPrototype;
	import utils.shop.presentprototype.PresentPrototype;

	public class ShopManager extends EventDispatcher
	{
		private var _itemprototypesArr:Array;
		private var _itemprototypesObj:Object;
		
		private var _presentprototypesArr:Array;
		private var _presentprototypesObj:Object;
				
		private var _callBackItemPrototypes:Function;
		private var _callBackPresentPrototype:Function;
		private var _callBackItems:Function;
		private var _callBackPresents:Function;
		
		private var _currentUrl:String;
		
		public function get presentprototypesObj():Object{
			return _presentprototypesObj;
		}
		
		public function ShopManager(){						
		}
		
		//инициализация данных для кэширования
		public function init():void{			
			getMyItems();
		}
		
		//получение прототипов вещей
		public function getItemPrototypes(callback:Function = null):void{
			if(_itemprototypesArr){
				callback && callback(_itemprototypesArr);
			}else{
				_callBackItemPrototypes = callback;
				GameApplication.app.connection.call("shopGetItemPrototypes", new Responder(ongetItemPrototypes, ongetItemPrototypesError));
			}
		}
		private function ongetItemPrototypes(items:Array):void{			
			_itemprototypesArr = new Array();
			_itemprototypesObj = new Object();
			for(var i:uint = 0; i < items.length; i++){
				var itemprototype:ItemPrototype = new ItemPrototype();				
				CopyProperties.copy(items[i], itemprototype);
				_itemprototypesArr.push(itemprototype);
				_itemprototypesObj[itemprototype.id] = itemprototype;
			}			
			_callBackItemPrototypes && _callBackItemPrototypes(_itemprototypesArr);
			_callBackItemPrototypes = null;
		}
		private function ongetItemPrototypesError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при получении вещей. Сообщите об ошибке разработчикам.");
		}
		public function get itemPrototypesObj():Object{
			return _itemprototypesObj;
		}
		public function getItemPrototypeById(id:int):ItemPrototype{
			return _itemprototypesObj[id];
		}
		
		//получение прототипов подарков
		public function getPresentPrototypes(callback:Function = null):void{
			if(_presentprototypesArr){
				callback && callback(_presentprototypesArr);
			}else{
				_callBackPresentPrototype = callback;
				GameApplication.app.connection.call("shopGetPresentPrototypes", new Responder(ongetPresentPrototypes, ongetPresentPrototypesError));
			}
		}
		private function ongetPresentPrototypes(items:Array):void{			
			_presentprototypesArr = new Array();
			_presentprototypesObj = new Object();
			for(var i:uint = 0; i < items.length; i++){
				var prototype:PresentPrototype = new PresentPrototype();				
				CopyProperties.copy(items[i], prototype);
				_presentprototypesArr.push(prototype);
				_presentprototypesObj[prototype.id] = prototype;
			}			
			_callBackPresentPrototype && _callBackPresentPrototype(_presentprototypesArr);
			_callBackPresentPrototype = null;
		}
		private function ongetPresentPrototypesError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при получении подарков. Сообщите об ошибке разработчикам.");
		}
		
		//получение списка вещей пользователя
		public function getMyItems(callBackItems:Function = null):void{
			if(GameApplication.app.userinfomanager.myuser.itemsArr){
				callBackItems && callBackItems(GameApplication.app.userinfomanager.myuser.itemsArr);
			}
		}
		
		//получение списка подарков пользователя
		public function getMyPresents(callBackItems:Function = null):void{
			_callBackPresents = callBackItems;
			GameApplication.app.connection.call("shopGetUserPresents", new Responder(ongetUserPresents, ongetUserPresentsError));
		}
		private function ongetUserPresents(items:Array):void{
			var presents:Array = new Array();
			
			for(var i:uint = 0; i < items.length; i++){				
				var present:ItemPresent = new ItemPresent();
				CopyProperties.copy(items[i], present);
				presents.push(present);
			}			
			_callBackPresents && _callBackPresents(presents);
			_callBackPresents = null;
		}
		private function ongetUserPresentsError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при получении подарков пользователя. Сообщите об ошибке разработчикам.");
		}
		
		//покупка вещи
		public function buyItem(prototype:ItemPrototype):void{	
			if(prototype.param1 == ShopCategory.THINGS || prototype.param1 == ShopCategory.ARSENAL){
				GameApplication.app.connection.call("shopBuyItem", new Responder(onBuyItem, onBuyItemError), prototype.id);				
			}else if(prototype.param1 == ShopCategory.BULLETS){
				GameApplication.app.connection.call("shopBuyBullets", new Responder(onBuyBullets, onBuyBulletsError));				
			} 
		}		
		private function onBuyItem(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				var item:Item = new Item();				
				CopyProperties.copy(buyresult["item"], item);
				GameApplication.app.popuper.show(new PopUpBuy(item));
				
				var itemsArr:Array = GameApplication.app.userinfomanager.myuser.itemsArr;
				var itemsObj:Object = GameApplication.app.userinfomanager.myuser.itemsObj;
				if(itemsArr) itemsArr.push(item);
				if(itemsObj) itemsObj[item.id] = item;
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.NOT_PROTOTYPE){
				GameApplication.app.popuper.showInfoPopUp("Невозможно купить эту вещь.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 756. Сообщите об ошибке разработчикам.");
			}else if (buyresult["error"] == BuyResultCode.LOW_LEVEL){
				GameApplication.app.popuper.showInfoPopUp("Эта вещь вам недоступна (по уровню персонажа).");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 796. Сообщите об ошибке разработчикам.");
			}
		}
		private function onBuyItemError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке вещи. Сообщите об ошибке разработчикам.");
		}
		private function onBuyBullets(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				GameApplication.app.popuper.showInfoPopUp("Поздравляем с удачной покупкой патронов!");		
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 756. Сообщите об ошибке разработчикам.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 791. Сообщите об ошибке разработчикам.");
			}
		}
		private function onBuyBulletsError(result:Object):void{
			_currentUrl = null;
			GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке патронов.");
		}
		
		//покупка подарка
		public function buyPresent(prototypeID:int, userID:int):void{			
			GameApplication.app.connection.call("shopBuyPresent", new Responder(onBuyPresent, onBuyPresentError), prototypeID, userID);
		}		
		private function onBuyPresent(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				var prototype:PresentPrototype = new PresentPrototype();
				CopyProperties.copy(buyresult["prototype"], prototype);
				GameApplication.app.popuper.show(new PopUpPresentBuy(prototype, "Подарок доставлен!"));
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.NOT_PROTOTYPE){
				GameApplication.app.popuper.showInfoPopUp("Невозможно купить эту вещь.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при отправке подарка.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при отправке подарка.");
			}
		}
		private function onBuyPresentError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при отправке подарка.");
		}
		
		//продать подарок
		public function salePresent(item:ItemPresent):void{
			GameApplication.app.connection.call("shopSalePresent", new Responder(onSalePresent, onSalePresentError), item.id);
		}
		private function onSalePresent(result:Object):void{
			if (result["error"] == UseResultCode.GAMEACTION_OK){
				var item:Item = new Item();
				item.id = result["itemid"];
				dispatchEvent(new UseItemEvent(UseItemEvent.SALE_PRESENT, item));
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно продать вещь");
			}	
		}
		private function onSalePresentError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно продать вещь");
		}
		
		//починить вещь
		public function fixItem(item:Item):void{
			GameApplication.app.connection.call("shopFixItem", new Responder(onFixItem, onFixItemError), item.id);
		}
		private function onFixItem(result:Object):void{
			if (result["error"] == BuyResultCode.OK){
				var itemFix:Item = new Item();				
				CopyProperties.copy(result["item"], itemFix);
				
				var itemsObj:Object = GameApplication.app.userinfomanager.myuser.itemsObj;
				var items:Array = GameApplication.app.userinfomanager.myuser.itemsArr;
				var item:Item;
				for(var i:int = 0; i < items.length; i++){
					item = (items[i] as Item);
					if(item.id == itemFix.id){
						item.quality = itemFix.quality;
						item.maxquality = itemFix.maxquality;
						break;
					}
				}
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно починить вещь");
			}
		}
		private function onFixItemError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно починить вещь");
		}
		
		//выбросить вещь
		public function dropItem(item:Item):void{
			GameApplication.app.connection.call("shopDropItem", new Responder(onDropItem, onDropItemError), item.id);
		}
		private function onDropItem(result:Object):void{
			if (result["error"] == UseResultCode.GAMEACTION_OK){				
				var items:Array = GameApplication.app.userinfomanager.myuser.itemsArr;
				var itemsObj:Object = GameApplication.app.userinfomanager.myuser.itemsObj;
				
				var item:Item = itemsObj[result["itemid"]];
				if(item == null) return;
				
				dispatchEvent(new UseItemEvent(UseItemEvent.DROP, item));				
				
				var existItem:Item;
				for(var i:int = 0; i < items.length; i++){
					existItem = (items[i] as Item);
					if(existItem.id == result["itemid"]){
						items.splice(i, 1);
						break;
					}
				}
				delete itemsObj[result["itemid"]];
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно выбросить вещь");
			}
		}
		private function onDropItemError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно выбросить вещь");
		}
		
		//надеть вещь
		public function putItem(item:Item):void{
			GameApplication.app.connection.call("shopPutItem", new Responder(onPutItem, onPutItemError), item.id);
		}
		private function onPutItem(result:Object):void{			
			if (result["error"] == UseResultCode.GAMEACTION_OK){
				var itemsObj:Object = GameApplication.app.userinfomanager.myuser.itemsObj;
				
				var item:Item = itemsObj[result["itemid"]];
				if(item == null) return;
				
				item.onuser = 1;
				dispatchEvent(new UseItemEvent(UseItemEvent.PUT, item));
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно надеть вещь");
			}
		}
		private function onPutItemError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно надеть вещь");
		}
		
		//снять вещь
		public function takeOffItem(item:Item):void{
			GameApplication.app.connection.call("shopTakeOffItem", new Responder(onTakeOffItem, onTakeOffItemError), item.id);
		}
		private function onTakeOffItem(result:Object):void{
			if (result["error"] == UseResultCode.GAMEACTION_OK){
				var itemsObj:Object = GameApplication.app.userinfomanager.myuser.itemsObj;
				
				var item:Item = itemsObj[result["itemid"]];
				if(item == null) return;
				
				item.onuser = 0;
				dispatchEvent(new UseItemEvent(UseItemEvent.TAKEOFF, item));
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно снять вещь");
			}
		}
		private function onTakeOffItemError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно снять вещь");
		}
		
		//купить(показать) страничку пользователя
		public function buyLink(url:String):void{
			_currentUrl = url;
			GameApplication.app.connection.call("shopBuyLink", new Responder(onBuyLink, onBuyLinkError));
		}		
		private function onBuyLink(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				if (_currentUrl && _currentUrl.length){
					var request:URLRequest = new URLRequest(_currentUrl);
					try {
						navigateToURL(request, '_blank');
					} catch (e:Error) {
						trace("Error occurred!");
					}
					_currentUrl = null;
				}				
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 756. Сообщите об ошибке разработчикам.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 791. Сообщите об ошибке разработчикам.");
			}
		}
		private function onBuyLinkError(result:Object):void{
			_currentUrl = null;
			GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке ссылки.");
		}		
		
		//РАБОТА С БАНОМ
		public function showBanPrice():void{			
			GameApplication.app.connection.call("shopGetPriceBanOff", new Responder(ongetPriceBanOff, ongetPriceBanOffError));
		}		
		private function ongetPriceBanOff(price:int):void{
			GameApplication.app.popuper.show(new PopUpBanOff(price));
		}
		private function ongetPriceBanOffError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при запросе цены на выход из бана.");
		}
		
		public function buyBanOff():void{
			GameApplication.app.connection.call("shopBuyBanOff", new Responder(onbuyBanOff, onbuyBanOffError));
		}
		
		private function onbuyBanOff(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				GameApplication.app.banmanager.banoff();	
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 756. Сообщите об ошибке разработчикам.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 791. Сообщите об ошибке разработчикам.");
			}
		}
		private function onbuyBanOffError(result:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке выхода из бана.");
		}
	}
}