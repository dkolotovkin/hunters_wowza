package utils.managers.constructor
{
	import application.GameApplication;
	import application.components.popup.savemap.PopUpSaveMap;
	import application.gamecontainer.scene.constructor.Constructor;
	import application.gamecontainer.scene.game.SceneElements;
	
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.net.Responder;
	
	import utils.constructor.ConstructorCreateMode;
	import utils.constructor.ConstructorElement;
	import utils.constructor.ConstructorState;
	import utils.selector.Selector;
	import utils.shop.BuyResultCode;

	public class ConstructorManager extends EventDispatcher
	{
		public var constructor:Constructor;
		
		public const CARRIER_BOX_WIDTH:Number = 50;
		public const CARRIER_BOX_HEIGHT:Number = 20;
		
		[Bindable]
		public var constructormode:Boolean = false;
		[Bindable]
		public var testMode:Boolean = false;
		[Bindable]
		public var createmode:uint;
		[Bindable]
		public var state:uint;
		
		private var _down:Boolean = false;
		private var _up:Boolean = false;
		private var _move:Boolean = false;
		
		public var currentElement:ConstructorElement;
		
		private var _startCreatedPoint:Point;
		private var _elementIdCounter:uint = 0;
		
		private var _selectorElement:Selector = new Selector();
		private var _fileRef:FileReference;
		private var _creatorID:uint;
		
		public var elementsObj:Object = new Object();
		
		public function ConstructorManager()
		{
		}
		
		public function init(xmlContent:XML):void{
			if(constructor && xmlContent){
				
				var list:XMLList = xmlContent.elements("*");			
				for(var i:uint = 0; i < list.length(); i++)
				{	
					var element:ConstructorElement;
					if (list[i].name() == SceneElements.STATIC){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorStatic);
						element.x = list[i].@x - list[i].@w / 2;
						element.y = list[i].@y - list[i].@h / 2;
						element.width = list[i].@w;
						element.height = list[i].@h; 
					}else if (list[i].name() == SceneElements.STATICRED){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorStaticRed);
						element.x = list[i].@x - list[i].@w / 2;
						element.y = list[i].@y - list[i].@h / 2;
						element.width = list[i].@w;
						element.height = list[i].@h;
					}else if (list[i].name() == SceneElements.STATICBLUE){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorStaticBlue);
						element.x = list[i].@x - list[i].@w / 2;
						element.y = list[i].@y - list[i].@h / 2;
						element.width = list[i].@w;
						element.height = list[i].@h;
					}else if (list[i].name() == SceneElements.HEAVYBOX){
						element = createElement(_elementIdCounter++, list[i].name(), HeavyBoxSkin);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.BOX){
						element = createElement(_elementIdCounter++, list[i].name(), BoxSkin);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.BALL){
						element = createElement(_elementIdCounter++, list[i].name(), BallSkin);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.SPRINGBROAD){
						element = createElement(_elementIdCounter++, list[i].name(), SpringboardSkin);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.SOURCE){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorSource);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.BULLET){
						element = createElement(_elementIdCounter++, list[i].name(), Bullet);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.CARRIERH){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorCarrierHorizontal);
						element.x = int(list[i].@x) + CARRIER_BOX_WIDTH / 2;
						element.y = list[i].@y;	
					}else if (list[i].name() == SceneElements.CARRIERV){
						element = createElement(_elementIdCounter++, list[i].name(), ConstructorCarrierVertical);
						element.x = list[i].@x;
						element.y = int(list[i].@y) + CARRIER_BOX_HEIGHT / 2;
					}else if (list[i].name() == SceneElements.HERO){
						element = createElement(_elementIdCounter++, list[i].name(), Hero);
						element.x = list[i].@x;
						element.y = list[i].@y;	
					}else if(list[i].name() == "creator"){
						_creatorID = list[i].@id;
					}
				} 
			}
		}
		
		private function createElement(eid:uint, tagName:String, iconClass:Class):ConstructorElement{
			var element:ConstructorElement = new ConstructorElement();
			element.tagName = tagName;
			element.iconClass = iconClass;				
			element.elementId = eid;
			
			element.addEventListener(ConstructorElementEvent.SELECTED, onElementSelected, false, 0, true);
			element.addEventListener(ConstructorElementEvent.UNSELECTED, onElementUnSelected, false, 0, true);
			
			constructor.content.addChild(element);
			elementsObj[element.elementId] = element;			
			return element;
		}
		
		public function startCreate():void{
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown, false, 0, true);
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp, false, 0, true);
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove, false, 0, true);			
			
			constructormode = true;
			state = ConstructorState.NONE;
		}
		
		public function stopCreate():void{
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
			
			_selectorElement.clear();
			
			constructormode = false;
			
			currentElement = null;
			elementsObj = new Object();
			constructor = null;
		}
		
		public function setMode(mode:uint):void{
			createmode = mode;		
			state = ConstructorState.CREATE;
		}
		
		private function onMouseDown(e:MouseEvent):void{
			if(state == ConstructorState.CREATE){			
				if(constructor.content.mouseX >= 0 && constructor.content.mouseX <= constructor.width &&
					constructor.content.mouseY >= 0 && constructor.content.mouseY <= constructor.height){
					if(constructor){
						currentElement = new ConstructorElement();
						
						if(createmode == ConstructorCreateMode.STATIC){
							currentElement.tagName = "stat";
							currentElement.iconClass = ConstructorStatic;
							currentElement.width = currentElement.height = 0;
							
							state = ConstructorState.RESIZE;							
						}else if(createmode == ConstructorCreateMode.STATIC_BLUE){
							currentElement.tagName = "sblu";
							currentElement.iconClass = ConstructorStaticBlue;
							currentElement.width = currentElement.height = 0;
							
							state = ConstructorState.RESIZE;							
						}else if(createmode == ConstructorCreateMode.STATIC_RED){
							currentElement.tagName = "sred";
							currentElement.iconClass = ConstructorStaticRed;
							currentElement.width = currentElement.height = 0;
							
							state = ConstructorState.RESIZE;
						}else if(createmode == ConstructorCreateMode.BOX){
							currentElement.tagName = "box";
							currentElement.iconClass = BoxSkin;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.BALL){
							currentElement.tagName = "ball";
							currentElement.iconClass = BallSkin;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.SPRING_BOARD){
							currentElement.tagName = "sprb";
							currentElement.iconClass = SpringboardSkin;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.HEAVY_BOX){
							currentElement.tagName = "hbox";
							currentElement.iconClass = HeavyBoxSkin;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.SOURCE){
							currentElement.tagName = "src";
							currentElement.iconClass = ConstructorSource;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.BULLET){
							currentElement.tagName = "bull";
							currentElement.iconClass = Bullet;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.HERO){
							currentElement.tagName = "hero";
							currentElement.iconClass = Hero;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.CARRIER_V){
							currentElement.tagName = "carrv";
							currentElement.iconClass = ConstructorCarrierVertical;
							
							state = ConstructorState.NONE;
						}else if(createmode == ConstructorCreateMode.CARRIER_H){
							currentElement.tagName = "carrh";
							currentElement.iconClass = ConstructorCarrierHorizontal;
							
							state = ConstructorState.NONE;
						}
							
						currentElement.elementId = _elementIdCounter++;
						
						currentElement.x = constructor.content.mouseX;
						currentElement.y = constructor.content.mouseY;						
						
						currentElement.addEventListener(ConstructorElementEvent.SELECTED, onElementSelected, false, 0, true);
						currentElement.addEventListener(ConstructorElementEvent.UNSELECTED, onElementUnSelected, false, 0, true);
						
						constructor.content.addChild(currentElement);
						elementsObj[currentElement.elementId] = currentElement;
						
						_startCreatedPoint = new Point(constructor.content.mouseX, constructor.content.mouseY);
					}
					
				}
				
			}
		}
		
		private function onMouseUp(e:MouseEvent):void{
			if(currentElement){
				if(currentElement.width < 5 || currentElement.height < 5){
					GameApplication.app.popuper.showInfoPopUp("Недостаточная ширина или высота предмета.");
					_selectorElement.selected(currentElement);
					deleteSelectObject();
				}
			}
			currentElement = null;
			_startCreatedPoint = null;
			createmode = ConstructorCreateMode.NONE;
			state = ConstructorState.NONE;
			
			dispatchEvent(new ConstructorManagerEvent(ConstructorManagerEvent.ELEMENT_CREATED));
		}
		
		private function onMouseMove(e:MouseEvent):void{
			if(state == ConstructorState.RESIZE){
				if(_startCreatedPoint){
					var _startX:Number = Math.min(constructor.content.mouseX, _startCreatedPoint.x);
					var _startY:Number = Math.min(constructor.content.mouseY, _startCreatedPoint.y);
					
					var _elementW:Number = Math.abs(constructor.content.mouseX - _startCreatedPoint.x);
					var _elementH:Number = Math.abs(constructor.content.mouseY - _startCreatedPoint.y);
					
					currentElement.x = _startX;
					currentElement.y = _startY;
					
					currentElement.width = _elementW;
					currentElement.height = _elementH;
				}				
			}
		}
		
		private function onElementSelected(event : ConstructorElementEvent) : void {
			_selectorElement.selected(event.element);
		}
		
		private function onElementUnSelected(event : ConstructorElementEvent) : void {
			_selectorElement.unselected(event.element);
		}
		
		public function deleteSelectObject():void{
			var selectedElement:ConstructorElement = _selectorElement.selection as ConstructorElement;
			if(selectedElement){
				if(constructor){
					_selectorElement.clear();
					delete elementsObj[selectedElement.elementId];
					if(constructor.content.contains(selectedElement)){
						constructor.content.removeChild(selectedElement);
					}
				}
			}else{
				GameApplication.app.popuper.showInfoPopUp("Не выделен элемент! Если вы хотите удалить элемент, то выделите его, а затем нажмите на кнопку удаления.");
			}
		}
		
		public function play():void{
			var xml:XML = getLocationXML();
			if(xml){
				var users:Array = new Array();
				var user:Object = new Object();
				user["id"] = GameApplication.app.userinfomanager.myuser.id
				users.push(user);
				GameApplication.app.gamemanager.gameworld = GameApplication.app.navigator.goGameWorld(1024, xml, users, 0, -2);
				
				testMode = true;
			}
		}
		
		public function save():void{
			var xml:XML = getLocationXML();
			if(xml){				
				var fileRef:FileReference = new FileReference();				
				fileRef.save(xml);
			}
		}
		
		public function shopSaveOnServerPopUp():void{
			var xml:XML = getLocationXML();
			if(xml){
				GameApplication.app.popuper.show(new PopUpSaveMap(xml));
			}
		}
		
		public function saveOnServer(fileName:String, content:String):void{
			GameApplication.app.connection.call("shopSaveMap", new Responder(onSaveMap, onSaveMapError), fileName, content);
		}
		
		private function onSaveMap(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				GameApplication.app.popuper.showInfoPopUp("Поздравляем! Ваша карта успешно сохранена на сервере!");
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег.");
			}else if (buyresult["error"] == BuyResultCode.LOW_LEVEL){
				GameApplication.app.popuper.showInfoPopUp("У вас маленький уровень.");
			}else if (buyresult["error"] == BuyResultCode.EXIST){
				GameApplication.app.popuper.showInfoPopUp("Файл с таким именем уже существует на сервере. Выберите другое имя файла.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно сохранить карту. Вы можете сохранить максимум 3 карты в день.");
			}
		}
		private function onSaveMapError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно сохранить карту.");
		}
		
		public function open():void{
			_fileRef = new FileReference();
			_fileRef.browse([new FileFilter("Все файлы","*.*")]);
			
			_fileRef.addEventListener(Event.SELECT, onSelect, false, 0, true);			
		}
		
		private function onSelect(e:Event):void{
			_fileRef.removeEventListener(Event.SELECT, onSelect);
			_fileRef.addEventListener(Event.COMPLETE, onLoadComplete, false, 0, true);
			_fileRef.load();
		}
		
		private function onLoadComplete(e:Event):void{
			_fileRef.removeEventListener(Event.COMPLETE, onLoadComplete);
			GameApplication.app.navigator.goConstructor(new XML(e.target.data));
		}
		
		public function getLocationXML():XML{		
			var heroPointCount:uint = 0;
			var countSource:uint = 0;
			var countBullets:uint = 0;
			var maxBullets:int = 7;
			var minBullets:int = 4;
			var maxSource:int = 12;
			var minSource:int = 7;
			var maxHeroPoint:int = 5;
			var countElement:uint = 0;
			
			var time:uint = 30 + Math.round(Math.random() * 50);
			
			var xmlString:String = "<?xml version='1.0' encoding='UTF-8'?><scene>";
			xmlString += "<timer time='" + time + "'/>";
			if(_creatorID == 0){
				_creatorID = GameApplication.app.userinfomanager.myuser.id;
			}
			xmlString += "<creator id='" + _creatorID + "'/>";
			for each(var element:ConstructorElement in elementsObj){
				countElement++;
				if(element.tagName == "carrv" || element.tagName == "carrh"){
					var _carrX:Number = element.x;
					var _carrY:Number = element.y;
					if(element.tagName == "carrh"){
						_carrX = element.x - CARRIER_BOX_WIDTH / 2;
					}
					if(element.tagName == "carrv"){
						_carrY = element.y - CARRIER_BOX_HEIGHT / 2;
					}
					
					xmlString += "<" + element.tagName + " x='" + _carrX + "' y='" + _carrY +
								"' lw='" + element.width + "' lh='" + element.height +
								"' bw='" + CARRIER_BOX_WIDTH + "' bh='" + CARRIER_BOX_HEIGHT + "'/>";
				}else{
					var _x:Number = element.x;
					var _y:Number = element.y;
					if(element.tagName == "stat" || element.tagName == "sblu" || element.tagName == "sred"){
						_x = element.x + element.width / 2;
						_y = element.y + element.height / 2;
					}
					xmlString += "<" + element.tagName + " x='" + _x + "' y='" + _y + 
								"' w='" + element.width + "' h='" + element.height + "'/>";
				}
				if(element.tagName == "hero"){
					heroPointCount++;
				}
				if(element.tagName == "src"){
					countSource++;				
				}
				if(element.tagName == "bull"){
					countBullets++;					
				}
			}
			xmlString += "</scene>";
			
			if(countElement > 70){
				GameApplication.app.popuper.showInfoPopUp("Слишком много элементов на карте!");
				return null;
			}			
			if(heroPointCount <= 0){
				GameApplication.app.popuper.showInfoPopUp("На карте обязательно должно быть хотя бы одно место появления персонажа!");
				return null;
			}
			if(heroPointCount > maxHeroPoint){
				GameApplication.app.popuper.showInfoPopUp("На карте слишком много мест появления персонажа! Максимально: " + maxHeroPoint + ".");
				return null;
			}
			if(countBullets < minBullets){
				GameApplication.app.popuper.showInfoPopUp("На карте слишком мало патронов! Минимально: " + minBullets + " шт.");
				return null;
			}
			if(countBullets > maxBullets){
				GameApplication.app.popuper.showInfoPopUp("На карте слишком много патронов! Максимально: " + maxBullets + " шт.");
				return null;
			}
			if(countSource > maxSource){
				GameApplication.app.popuper.showInfoPopUp("На карте слишком много орехов! Максимально: " + maxSource + " шт.");
				return null;
			}
			if(countSource < minSource){
				GameApplication.app.popuper.showInfoPopUp("На карте слишком мало орехов! Минимально: " + minSource + " шт.");
				return null;
			}
			
			if(heroPointCount > 0){
				return new XML(xmlString);
			}
			return null;			
		}
	}
}