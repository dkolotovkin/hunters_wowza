package utils.shop.presentprototype
{
	public class PresentPrototype
	{
		public var id:int;
		public var title:String;
		public var price:int;
		public var showed:int;
		public var url:String;
		
		public function PresentPrototype():void{			
		}
		
		public function init(id:int, title:String, price:int, showed:int, url:String):void{
			this.id = id;
			this.title = title;
			this.price = price;
			this.showed = showed;
			this.url = url;
		}
	}
}