package application.components.popup {
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.engine.FontWeight;
	
	import flashx.textLayout.formats.TextAlign;
	
	import mx.graphics.GradientEntry;
	import mx.graphics.IFill;
	import mx.graphics.LinearGradient;
	
	import spark.components.Group;
	import spark.components.Label;

	/**
	 * @author ivaskov
	 */
	public class TitlePopUp extends Group {
		protected var _fill : IFill;
		private var _title:String = "";
		private var _stext : Label = new Label();
		
		
		public function get title ():String {
			return _title;
		}
		
		public function set title (value:String):void {
			if (_title != value){
				_title = value;
				_stext.text = value;
			}
		}
		
		public function TitlePopUp() {
			
			_fill = createFill();
			
			_stext.setStyle("color", 0xFFFFCC);
			_stext.setStyle("fontSize", 18);			_stext.setStyle("paddingLeft", 3);			_stext.setStyle("paddingRight", 3);
			//_stext.setStyle("fontWeight", FontWeight.BOLD);			_stext.setStyle("textAlign", TextAlign.CENTER);
		}
		
		
		override protected function createChildren ():void{
			super.createChildren ();
			addElement(_stext);
			
			
		}
		
		
		/**
		 * переопределять в случае если требуется поменять цвет фона
		 */
		protected function createFill() : IFill {
			
			var fill : LinearGradient = new LinearGradient();
			var g1 : GradientEntry = new GradientEntry(0xDAE6F1, 0, 1);
			var g2 : GradientEntry = new GradientEntry(0x8BB3D9, 1, 1);
    		
			fill.entries = [g1, g2];
			
			return  fill;
		}
		
		
		override protected function updateDisplayList(w : Number,h : Number) : void {
			super.updateDisplayList(w, h);
  
  			/*graphics.clear();
			_fill.begin(graphics, new Rectangle(0, 0, w - 44, h), new Point(0, 0));
			graphics.drawRoundRectComplex(0, 0, w - 44, h, 0, 0, 0, 6);
			_fill.end(graphics);*/
			
			_stext.y = (h - _stext.height)/2+1;
			_stext.width = w;			
		}		
	}
}
