package utils.shop.itemprototype
{
	public class ItemPrototype
	{
		public var id:int;
		public var title:String;
		public var description:String;
		public var param1:int;					//категория вещи
		public var param2:int;					//слот(глубина)
		public var param3:int;					//бонус к опыту
		public var param4:int;					//бонус к опыту альянса
		public var param5:int;					//бонус к скорости
		public var param6:int;					//бонус к прыжку
		public var param7:int;					//уворотливость
		public var price:int;
		public var showed:int;
		public var maxquality:int;
		public var minlevel:int;
		public var url:String;
		
		public function ItemPrototype():void{			
		}
		
		public function init(id:int, title:String, description:String, 
									  param1:int, param2:int, param3:int, param4:int, param5:int, param6:int, param7:int,
									  price:int, showed:int, maxquality:int, minlevel:int, url:String):void{
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
			this.minlevel = minlevel;
			this.showed = showed;
			this.url = url;
		}
	}
}