package utils.managers.classloader
{
	import application.GameApplication;
	import application.GameMode;
	
	import flash.display.LoaderInfo;
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.SecurityErrorEvent;
	import flash.net.URLRequest;
	import flash.system.ApplicationDomain;
	import flash.system.LoaderContext;
	import flash.system.SecurityDomain;
	import flash.utils.getDefinitionByName;
	import flash.utils.setTimeout;
	
	import mx.controls.Alert;

	public class ClassLoaderManager
	{
		public var hashmap:Object = new Object();		
		private var _queue:Object = new Object();		
		private var lc:LoaderContext;
		
		public function ClassLoaderManager() {
			hashmap = new Object();
			lc = new LoaderContext(true, ApplicationDomain.currentDomain, SecurityDomain.currentDomain);
		}
		
		public function load(url:String, onLoadComplete:Function, onLoadCompleteCN:Function, onLoadError:Function):void{
			var cl:Class = hashmap[url];
			if(url){
				var arr:Array = url.split("/");
				if(arr && arr.length){
					var swfName:String = arr[arr.length - 1];
					var arr2:Array = swfName.split(".");
					if(arr2 && arr2.length){
						var className:String = arr2[0] as String;
					 	try{
							var ClassReference:Class = getDefinitionByName(className) as Class;
							onLoadComplete && onLoadComplete(ClassReference);	
							onLoadCompleteCN && onLoadCompleteCN(ClassReference, swfName);
						}catch(e:*){
							
						}
					}
				}
			}
			return;
			
			if (cl){
				onLoadComplete(cl);
			}else{
				var loader:ClassLoader = _queue[url];
				if (loader){
					loader.addCallback(onLoadComplete);
					loader.addCallbackClassName(onLoadCompleteCN);
					loader.addErrorCallback(onLoadError);
				}else{					
					loader = new ClassLoader();
					
					loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onInit, false, 0, true);
					loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onIOError, false, 0, true);
					loader.contentLoaderInfo.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onSequrityError, false, 0, true);
					loader.start(url, onLoadComplete, onLoadCompleteCN, onLoadError);
					_queue[url] = loader;
					
					if(GameApplication.app.config.mode != GameMode.DEBUG){
						loader.load(new URLRequest(url), lc);
					}else{
						loader.load(new URLRequest(url));
					}
				}
			}
		}		
		
		public function removeListener(url:String, onLoad:Function, onLoadCN:Function, onErrorLoad:Function):void{
			var loader:ClassLoader = _queue[url];
			if (loader){
				loader.removeCallback(onLoad);
				loader.removeCallbackClassName(onLoadCN);
				loader.removeErrorCallback(onErrorLoad);
			}
		}
		
		private function onSequrityError(event : SecurityErrorEvent) : void {
			var li:LoaderInfo = event.target as LoaderInfo;
			var loader:ClassLoader = li.loader as ClassLoader;
			
			for each(var func:Function in loader.errorCallbacks){
				func();
			}
			removeLoader(loader);
		}
		
		private function onIOError(event : IOErrorEvent) : void {
			var li:LoaderInfo = event.target as LoaderInfo;
			var loader:ClassLoader = li.loader as ClassLoader;
			for each (var func:Function in loader.errorCallbacks){
				func && func();
			}
			removeLoader (loader);
		}
		
		private function onInit(event : Event) : void {
			var li:LoaderInfo = event.target as LoaderInfo;
			var loader:ClassLoader = li.loader as ClassLoader;
			
			var loadClass:Class = Object(Sprite(loader.content).getChildAt(0)).constructor as Class;
			hashmap[loader.classname] = loadClass;
			
			setTimeout(onNeedClear,100,loader,loadClass);
		}
		
		private function removeLoader(classLoader:ClassLoader):void {
			classLoader.contentLoaderInfo.removeEventListener(Event.COMPLETE, onInit);
			classLoader.contentLoaderInfo.removeEventListener(IOErrorEvent.IO_ERROR, onIOError);
			classLoader.contentLoaderInfo.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onSequrityError);
			
			delete _queue[classLoader.classname];
			classLoader.clear();
			classLoader.unload();
		}
		
		private function onNeedClear(loader:ClassLoader, loadClass:Class):void {
			loader.unloadAndStop();
			for each(var func:Function in loader.callbacks){
				func(loadClass);
			}
			for each(var funcCN:Function in loader.callbacksClassName){
				funcCN(loadClass, loader.classname);
			}
			removeLoader(loader);
		}
	}
}