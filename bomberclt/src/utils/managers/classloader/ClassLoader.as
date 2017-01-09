package utils.managers.classloader
{
	import flash.display.Loader;
	
	public class ClassLoader extends Loader
	{
		public var classname:String;
		
		private var _callbacks:Vector.<Function>;
		private var _callbacksClassName:Vector.<Function>;
		private var _errorCallbacks:Vector.<Function>;
		
		public function ClassLoader() {
		}
		
		public function start(classname:String,onLoadComplete:Function, onLoadCompleteCN:Function, onLoadError:Function):void {
			this.classname = classname;
			_callbacks = new Vector.<Function>();
			addCallback(onLoadComplete);
			
			_callbacksClassName = new Vector.<Function>();
			addCallbackClassName(onLoadCompleteCN);
			
			_errorCallbacks = new Vector.<Function>();
			addErrorCallback(onLoadError);
		}
		
		public function get callbacks ():Vector.<Function>{
			return _callbacks;
		}
		
		public function get callbacksClassName ():Vector.<Function>{
			return _callbacksClassName;
		}
		
		public function get errorCallbacks ():Vector.<Function>{
			return _errorCallbacks;
		}
		
		public function removeCallback(onLoad : Function) : void {
			for (var i : uint = 0, len:uint = _callbacks.length; i <  len; i++) {
				if (_callbacks[i] == onLoad) {
					_callbacks.splice(i, 1);
					break;
				}
			}
		}
		
		public function removeCallbackClassName(onLoad : Function) : void {
			for (var i : uint = 0, len:uint = _callbacksClassName.length; i <  len; i++) {
				if (_callbacksClassName[i] == onLoad) {
					_callbacksClassName.splice(i, 1);
					break;
				}
			}
		}
		
		public function removeErrorCallback(onLoad : Function) : void {
			for (var i : uint = 0, len:uint = _errorCallbacks.length; i <  len; i++) {
				if (_errorCallbacks[i] == onLoad) {
					_errorCallbacks.splice(i, 1);
					break;
				}
			}
		}
		
		public function addCallback (func:Function):void{			
			func && _callbacks.push (func);
		}
		public function addCallbackClassName (func:Function):void{			
			func && _callbacksClassName.push (func);
		}
		public function addErrorCallback (func:Function):void{
			func && _errorCallbacks.push (func);
		}
		
		public function clear ():void {
			_callbacks = null;
			_callbacksClassName = null;
			_errorCallbacks = null;
		}
	}
}