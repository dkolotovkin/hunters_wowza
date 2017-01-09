package utils.shop.item
{
	import utils.shop.presentprototype.PresentPrototype;

	public class ItemPresent
	{
		public var id:int;
		public var title:String;
		public var iduser:int;
		public var presenter:String;
		public var price:int;
		public var showed:int;
		public var url:String;
		
		public function ItemPresent():void{
		}
		
		public function init(id:int, iduser:int, presenter:String, title:String, price:int, showed:int, url:String):void{
			this.id = id;
			this.iduser = iduser;
			this.presenter = presenter;
			this.title = title;		
			this.price = price;
			this.showed = showed;
			this.url = url;
		}
		
		public static function createFromPresentPrototype(pp:PresentPrototype, iduser:uint = 0, presenter:String = ""):ItemPresent{
			var itemp:ItemPresent = new ItemPresent();
			itemp.init(pp.id, iduser, presenter, pp.title, pp.price, pp.showed, pp.url);
			return itemp;
		}
		
		public function clone():ItemPresent{
			var itemp:ItemPresent = new ItemPresent();
			itemp.init(id, iduser, presenter, title, price, showed, url);
			return itemp;
		}
	}
}