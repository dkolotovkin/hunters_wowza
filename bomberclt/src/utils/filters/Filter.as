package utils.filters{
	import flash.display.DisplayObject;
	import flash.filters.BitmapFilter;
	import flash.filters.ColorMatrixFilter;
	import flash.utils.ByteArray;
	
	import utils.filters.adjustcolor.AdjustColor;

	/**
	 * @author dkolotovkin
	 */
	public class Filter {
		public var brightness:int = 0;
		public var contrast:int = 0;
		public var saturation:int = 0;
		public var source:int = 0;
		public var hue:int = 0;		
		
		public function Filter(_brightness:int, _contrast:int, _saturation:int, _hue:int,source:int)
		{
			brightness = _brightness;
			contrast = _contrast;
			saturation = _saturation;
			hue = _hue;
			this.source = source;
		}
		
		static public function getFilterByInt(colorFilter:int):Filter
		{			
			var byteArr:ByteArray = new ByteArray ();
			byteArr.writeInt(colorFilter);
			
			//проверка на отрицательность чисел
			var _brightness:int = byteArr[3];
			if (_brightness > 127) _brightness -= 256;
			var _contrast:int = byteArr[2];
			if (_contrast > 127) _contrast -= 256;
			var _saturation:int = byteArr[1];
			if (_saturation > 127) _saturation -= 256;
			var _hue:int = byteArr[0];
			if (_hue > 127) _hue -= 256;
			_hue *= 2;
			return new Filter(_brightness, _contrast, _saturation, _hue,colorFilter);			
		}
		
		static public function BrushThing(_clip:DisplayObject, _filter : Filter):void
		{
			if (_filter)
			{
				var _mColor : AdjustColor = new AdjustColor();
				_mColor.brightness = _filter.brightness;
				_mColor.contrast = _filter.contrast;				
				_mColor.saturation = _filter.saturation;
				_mColor.hue = _filter.hue;		
				_clip.filters = [new ColorMatrixFilter(_mColor.CalculateFinalFlatArray())];								
			}
		}
		
		static public function unBrushThing(_clip:DisplayObject):void
		{			
			_clip.filters = new Array ();	
		}
		
		public function clone():Filter
		{
			return (new Filter(brightness, contrast, saturation, hue,source));
		}
	}
}
