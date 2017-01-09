package utils.shop.item
{
	import utils.shop.itemprototype.ItemPrototype;

	public class Item
	{
		public var id:int;
		public var title:String;
		public var description:String;
		public var param1:int;
		public var param2:int;
		public var param3:int;
		public var param4:int;
		public var param5:int;
		public var param6:int;
		public var param7:int;
		public var price:int;
		public var maxquality:int;
		public var quality:int;
		public var onuser:int;
		public var url:String;
		
		public function Item():void{			
		}
		
		public function init(id:int, title:String, description:String, 
							 param1:int, param2:int, param3:int, param4:int, param5:int, param6:int, param7:int,
							 price:int, maxquality:int, quality:int, onuser:int, url:String):void{
			this.id = id;
			this.title = title;
			this.description = description;
			this.param1 = param1;
			this.param2 = param2;
			this.param3 = param3;
			this.param4 = param4;
			this.param5 = param5;
			this.param6 = param6;
			this.param7 = param7;
			this.price = price;
			this.maxquality = maxquality;
			this.quality = quality;
			this.onuser = onuser;
			this.url = url;
		}
		
		public static function createFromItemPrototype(ip:ItemPrototype):Item{
			var item:Item = new Item();
			item.init(ip.id, ip.title, ip.description, ip.param1, ip.param2, ip.param3, ip.param4, ip.param5, ip.param6, ip.param7, ip.price, ip.maxquality, ip.maxquality, 0, ip.url);
			return item;
		}
		
		public function clone():Item{
			var item:Item = new Item();
			item.init(id, title, description, param1, param2, param3, param4, param5, param6, param7, price, maxquality, quality, onuser, url);
			return item;
		}
	}
}