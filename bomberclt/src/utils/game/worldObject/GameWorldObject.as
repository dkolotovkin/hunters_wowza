package utils.game.worldObject
{
	public class GameWorldObject extends Object
	{
		public var id:int;
		public var x:Number;
		public var y:Number;
		public var lvx:Number;
		public var lvy:Number;
		public var av:Number;
		public var angle:Number
		
		public function GameWorldObject(id:int, x:Number, y:Number, lvx:Number, lvy:Number, av:Number, angle:Number)
		{
			super();
			this.id = id;
			this.x = x;
			this.y = y;
			this.lvx = lvx;
			this.lvy = lvy;
			this.av = av;
			this.angle = angle;
		}
		
		public static function createFromObject(obj:Object):GameWorldObject{
			var gwo:GameWorldObject = new GameWorldObject(obj["id"], obj["x"], obj["y"], obj["lvx"], obj["lvy"], obj["av"], obj["angle"]);
			return gwo;
		}
	}
}