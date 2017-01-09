package application.components.lifeindicator
{
	import flash.display.Sprite;
	
	public class LifeIndicator extends Sprite
	{
		public var WIDTH:Number = 30;
		public var HEIGHT:Number = 4;
		public var WEIGHT:Number = 1;
		
		public var borderColor:uint = 0x028701;
		public var bgColor:uint = 0x00CC33;
		
		public function LifeIndicator()
		{
			super();
			draw(1);
		}
		
		public function draw(percent:Number):void{
			this.graphics.clear();
				
			this.graphics.beginFill(borderColor, 1);
			this.graphics.drawRect(0, 0, WIDTH , WEIGHT);
			this.graphics.endFill();
			
			this.graphics.beginFill(borderColor, 1);
			this.graphics.drawRect(0, HEIGHT - WEIGHT, WIDTH , WEIGHT);
			this.graphics.endFill();
			
			this.graphics.beginFill(borderColor, 1);
			this.graphics.drawRect(0, 0, WEIGHT , HEIGHT);
			this.graphics.endFill();
			
			this.graphics.beginFill(borderColor, 1);
			this.graphics.drawRect(WIDTH - WEIGHT, 0, WEIGHT , HEIGHT);
			this.graphics.endFill();
			
			this.graphics.beginFill(bgColor, 1);
			this.graphics.drawRect(WEIGHT, WEIGHT, (WIDTH - WEIGHT * 2) * percent, HEIGHT - WEIGHT * 2);
			this.graphics.endFill();
		}
	}
}