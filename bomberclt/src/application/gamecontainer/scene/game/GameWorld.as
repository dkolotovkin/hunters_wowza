package application.gamecontainer.scene.game
{
	import Box2D.Collision.Shapes.b2CircleDef;
	import Box2D.Collision.Shapes.b2MassData;
	import Box2D.Collision.Shapes.b2PolygonDef;
	import Box2D.Collision.Shapes.b2PolygonShape;
	import Box2D.Collision.Shapes.b2Shape;
	import Box2D.Collision.b2AABB;
	import Box2D.Common.Math.b2Vec2;
	import Box2D.Common.Math.b2XForm;
	import Box2D.Dynamics.Contacts.b2ContactEdge;
	import Box2D.Dynamics.Joints.b2Joint;
	import Box2D.Dynamics.Joints.b2MouseJoint;
	import Box2D.Dynamics.Joints.b2MouseJointDef;
	import Box2D.Dynamics.Joints.b2PrismaticJoint;
	import Box2D.Dynamics.b2Body;
	import Box2D.Dynamics.b2BodyDef;
	import Box2D.Dynamics.b2ContactListener;
	import Box2D.Dynamics.b2DebugDraw;
	import Box2D.Dynamics.b2World;
	
	import application.GameApplication;
	import application.GameMode;
	import application.components.findflash.FindFlash;
	import application.components.lifeindicator.LifeIndicator;
	import application.components.usertitle.UserTitle;
	
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.text.StaticText;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.text.engine.FontWeight;
	import flash.text.engine.Kerning;
	import flash.ui.Mouse;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import flashx.textLayout.formats.TextAlign;
	
	import mx.core.UIComponent;
	import mx.effects.Tween;
	import mx.managers.CursorManager;
	
	import spark.components.Application;
	import spark.components.Group;
	import spark.components.VGroup;
	import spark.layouts.HorizontalAlign;
	import spark.primitives.Rect;
	
	import utils.game.GameConfig;
	import utils.game.GameContactListener;
	import utils.game.GameContactListenerEvent;
	import utils.game.PersFrameLabel;
	import utils.game.SelectGunEvent;
	import utils.game.worldObject.GameWorldObject;
	import utils.interfaces.ISceneContent;
	import utils.managers.flashing.FlashingManager;
	import utils.user.User;
	
	public class GameWorld extends VGroup implements ISceneContent
	{
		private var _commongr:UIComponent = new UIComponent();
		private var _gameUI:UIComponent = new UIComponent();
		private var _mask:UIComponent = new UIComponent();
		private var _targetmc:MovieClip;
		private var _gamebouns:b2AABB;
		private var _world:b2World;		
		private var kScale:Number = 1;
		private var _debugDraw:b2DebugDraw;		
		private var _doSleep:Boolean = true;		
		private var _m_iterations:int = 10;
		private var _m_timeStep:Number = 1.0 / 5;		
		private var _gravity:b2Vec2 = new b2Vec2(0, 20);
		private var _mouseVec:b2Vec2 = new b2Vec2();		
		private var _mouseJoint:b2MouseJoint;		
		private var _contactListener:GameContactListener = new GameContactListener();		
		private var _rightForceApllyed:Object = new Object();
		private var _leftForceApplyed:Object = new Object();		
		private var _creator:GameWorldCreator;		
		private var _urlLoader:URLLoader;
		private var _urlRequest:URLRequest;		
		protected var _locationXML:XML;
		
		public var getSourceIds:Object = new Object();
		
		public var users:Object = new Object(); 				//все b2body пользователей
		private var userIDs:Array = new Array();				//все id пользователей
		public var userstitles:Object = new Object();
		public var lifeindicators:Object = new Object();
		
		public var roomID:int;		
		public var hunterId:int;
		public var gametype:int;
		
		private var _heroPoints:Array = new Array();
		private var _myuserout:Boolean = false;
		private var _wrect:Rectangle = new Rectangle();
		
		private var _pressButtons:Object = new Object();
				
		private var _rightForce:int = 900;		
		private var _jumpForce:int = -1500 * 120;
		
		private var _ismember:Boolean = false;
		private var _ishunter:Boolean = false;
		private var _killme:Boolean = false;
		
		//флаг для слежения за состоянием курсора
		private var _hideCursor:Boolean = false;
		//id интервала для обновления курсора
		private var _ciid:int = -1;
		private var _tweenTargetX:Tween;
		private var _tweenTargetY:Tween;
		private var _lastTargetX:Number = 0;
		private var _lastTargetY:Number = 0;
		
		private var _flashingManager:FlashingManager;
		
		public function get gameUI():UIComponent{
			return _gameUI;
		}
		
		public function get locationXML():XML{
			return _locationXML;
		}
		
		public function GameWorld(roomID:int, wrect:Rectangle, locationXML:XML, usrs:Array, hunterId:int, gt:int):void
		{
			super();
			this.roomID = roomID;
			this.hunterId = hunterId;
			this.gametype = gt;			
			_locationXML = locationXML;
			_wrect.x = wrect.x - 500;
			_wrect.y = wrect.y - 500;
			_wrect.width = wrect.width + 1000;
			_wrect.height = wrect.height + 1000;
			
			createWorld(_wrect.x, _wrect.y, _wrect.width, _wrect.height);
			_contactListener.world = _world;
			_creator = new GameWorldCreator(_world, _gameUI, userstitles, lifeindicators);
			if (_locationXML){
				createWorldFromXML(_locationXML);
			}
			if(hunterId == GameApplication.app.userinfomanager.myuser.id){
				_ishunter = true;
				_ciid = setInterval(updateTargetXY, 1000);
			}
			
			var user:User;			
			for(var i:int = 0; i < usrs.length; i++){
				var _ismyuser:Boolean = false;
				
				if(usrs[i]["id"] == GameApplication.app.userinfomanager.myuser.id){
					_ismember = true;
				}
				if(this.gametype == -1 || this.gametype == -2){										//test
					user = GameApplication.app.gameContainer.chat.getUserByID(usrs[i]["id"]);
				}else{
					user = GameApplication.app.gameContainer.chat.activeRoom.getUser(usrs[i]["id"]);
				}
				if(user != null && user.id != hunterId){
					
					var p:Point;
					if(usrs[i]["seatIndex"] > 0 && usrs[i]["seatIndex"] < _heroPoints.length){
						p = _heroPoints[usrs[i]["seatIndex"]];
					}else{
						p = _heroPoints[0];
					}
					users[int(user.id)] = _creator.createPers(p.x, p.y, 30, 30, user, usrs[i]["pids"]);
					userIDs.push(int(user.id));
				}
			}
			user = null;
			
			if(_ismember && !_ishunter){
				var _myuserMC:MovieClip = (users[GameApplication.app.userinfomanager.myuser.id] as b2Body).m_userData;
				_gameUI.addChild(_myuserMC);
				_gameUI.addChild((userstitles[GameApplication.app.userinfomanager.myuser.id] as UserTitle));
				_contactListener.myusermc = (users[GameApplication.app.userinfomanager.myuser.id] as b2Body).m_userData;
			}
			
//			createDebugDraw(kScale, 1, 1, 0xFFFFFF);	
			
			_targetmc = new Target();
			_targetmc.x = _targetmc.y = -100;
			_gameUI.addChild(_targetmc);
			
			_mask.graphics.beginFill(0x000000, .5);
			_mask.graphics.drawRect(0, 0, 740, 380);
			_mask.graphics.endFill();
			_gameUI.mask = _mask;
			
			_commongr.width = _gameUI.width = 740;
			_commongr.height = _gameUI.height = 380;
			addEventListener(Event.ADDED_TO_STAGE, addListeners, false, 0, true);
			
			_flashingManager = new FlashingManager(100);
			
			GameApplication.app.gamemanager.addEventListener(SelectGunEvent.SELECT, onSelectGun);
		}
		
		private function updateTargetXY():void{
			if(mouseX > 0 && mouseX < 740 && mouseY > 0 && mouseY < 380){
				var d:Number = Math.sqrt(Math.pow(_lastTargetX - mouseX, 2) + Math.pow(_lastTargetY - mouseY, 2));
				if(d > 10){
					_lastTargetX = mouseX;
					_lastTargetY = mouseY;
					GameApplication.app.gamemanager.moveTarget(mouseX, mouseY);
				}
			}
		}
		
		private function onSelectGun(e:SelectGunEvent):void{
			if(_targetmc){
				_targetmc.gotoAndStop(e.item.param2);
			}
		}
		
		private function addListeners(e:Event):void{				
			removeEventListener(Event.ADDED_TO_STAGE, addListeners);
			stage.addEventListener(Event.ENTER_FRAME, update, false, 0, true);
			if(GameApplication.app.config.mode == GameMode.DEBUG){
				stage.addEventListener(MouseEvent.MOUSE_DOWN, createMouseJoing);
			}
			stage.addEventListener(MouseEvent.MOUSE_UP, destroyMouseJoing);
			
			if(!_ishunter){
				if(_ismember){
					stage.addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
					stage.addEventListener(KeyboardEvent.KEY_UP, onKeyUp);
				}
				
				_world.SetContactListener(_contactListener);
				_contactListener.addEventListener(GameContactListenerEvent.SOURCE, onSourceContact);
				_contactListener.addEventListener(GameContactListenerEvent.BULLET, onBulletContact);
			}else{
				stage.addEventListener(MouseEvent.CLICK, onShot, false, 0, true);
				stage.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove, false, 0, true);
			}
		}
		
		private function onMouseMove(e:MouseEvent):void{
			if (_targetmc){
				_targetmc.x = this.mouseX;
				_targetmc.y = this.mouseY;
				if(mouseX > 0 && mouseX < 740 && mouseY > 0 && mouseY < 380){
					if(!_hideCursor){
						_hideCursor = true;
						Mouse.hide();
					}
				}else{
					if(_hideCursor){
						_hideCursor = false;
						Mouse.show();
					}
				}
			}
		}
		
		private function onShot(e:MouseEvent):void{
			GameApplication.app.gamemanager.shot(mouseX, mouseY);
		}
		
		private function onSourceContact(e:GameContactListenerEvent):void{
			if (!getSourceIds[e.id]){
				getSourceIds[e.id] = e.id;
				
				var myuserbody:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
				if(this.gametype != -1 && this.gametype != -2){		//test
					GameApplication.app.gamemanager.getsource(e.id);
				}else{
//					GameApplication.app.gamemanager.endTestGame();
				}
			}
		}
		private function onBulletContact(e:GameContactListenerEvent):void{			
			if (!getSourceIds[e.id]){			
				getSourceIds[e.id] = e.id;
				
				var myuserbody:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
				if(this.gametype != -1 && this.gametype != -2){		//test
					GameApplication.app.gamemanager.getbullet(e.id);
				}else{
//					userFindSource(GameApplication.app.userinfomanager.myuser.id, myuserbody.GetXForm().position.x, myuserbody.GetXForm().position.y);
				}
			}
		}
		
		protected function onKeyDown(event:KeyboardEvent):void 
		{
			if(!_killme){
				switch(int(event.keyCode))
				{
					case 65:
					case 37:{ 			//left
						if (!_pressButtons[event.keyCode]){
							_pressButtons[event.keyCode] = event.keyCode;
							var userb2bodyleft:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
							if (userb2bodyleft){
								if(this.gametype != -1 && this.gametype != -2) GameApplication.app.gamemanager.goToLeft(true, userb2bodyleft.GetXForm().position.x, userb2bodyleft.GetXForm().position.y, userb2bodyleft.GetLinearVelocity().x, userb2bodyleft.GetLinearVelocity().y);
								userGotoLeft(GameApplication.app.userinfomanager.myuser.id, true, 0, 0, 0, 0, true);
							}
						}
						break;
					}
					case 68:
					case 39:{ 			//right
						if (!_pressButtons[event.keyCode]){						
							_pressButtons[event.keyCode] = event.keyCode;
							var userb2bodyright:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
							if (userb2bodyright){
								if(this.gametype != -1 && this.gametype != -2) GameApplication.app.gamemanager.goToRight(true, userb2bodyright.GetXForm().position.x, userb2bodyright.GetXForm().position.y, userb2bodyright.GetLinearVelocity().x, userb2bodyright.GetLinearVelocity().y);
								userGotoRight(GameApplication.app.userinfomanager.myuser.id, true, 0, 0, 0, 0, true);						
							}
						}
						break;					
					}
					case 87:
					case 38:{ 			//up		
						if (!_pressButtons[event.keyCode]){
							_pressButtons[event.keyCode] = event.keyCode;
							var userb2body:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
							if (userb2body){
								if(this.gametype != -1 && this.gametype != -2) GameApplication.app.gamemanager.jump(true, userb2body.GetXForm().position.x, userb2body.GetXForm().position.y, userb2body.GetLinearVelocity().x, userb2body.GetLinearVelocity().y);
								userJump(GameApplication.app.userinfomanager.myuser.id, true, 0, 0, 0, 0, true);						
							}
						}
					}
					case 40: break;{	//down
					}
					case 32:{ 			//space
						break;	
					}
					case 17: break;		//ctrl
					default: break;
				}
			}			
		}
		
		protected function onKeyUp(event:KeyboardEvent):void
		{
			if(!_killme){
				if(_pressButtons[event.keyCode]){
					delete _pressButtons[event.keyCode];
					switch(int(event.keyCode))
					{
						case 65:
						case 37:{ 			//left					
							var userb2bodyleft:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
							if (userb2bodyleft){						
								if(this.gametype != -1 && this.gametype != -2) GameApplication.app.gamemanager.goToLeft(false, userb2bodyleft.GetXForm().position.x, userb2bodyleft.GetXForm().position.y, userb2bodyleft.GetLinearVelocity().x, userb2bodyleft.GetLinearVelocity().y);
								userGotoLeft(GameApplication.app.userinfomanager.myuser.id, false, 0, 0, 0, 0, true);						
							}					
							break;
						}
						case 68:
						case 39:{ 			//right
							var userb2bodyright:b2Body = users[GameApplication.app.userinfomanager.myuser.id];
							if (userb2bodyright){
								if(this.gametype != -1 && this.gametype != -2) GameApplication.app.gamemanager.goToRight(false, userb2bodyright.GetXForm().position.x, userb2bodyright.GetXForm().position.y, userb2bodyright.GetLinearVelocity().x, userb2bodyright.GetLinearVelocity().y);						
								userGotoRight(GameApplication.app.userinfomanager.myuser.id, false, 0, 0, 0, 0, true);						
							}
							break;
						}
						default:break;
					}
				}
			}			
		}
		
		public function userGotoLeft(userID:int, down:Boolean, userx:Number, usery:Number, lvx:Number, lxy:Number, ismyuser:Boolean = false):void{
			var userb2body:b2Body;
			var position:b2Vec2;
			if (down){
				if (_rightForceApllyed[userID]){
					delete _rightForceApllyed[userID];
				}
				if (!_leftForceApplyed[userID]){
					userb2body = users[userID];
					
					if (userb2body){
						position = new b2Vec2(userx, usery);						
						if (!ismyuser){
							userb2body.SetXForm(position, 0);
							userb2body.SetLinearVelocity(new b2Vec2(lvx, lxy));
						}
						
						_leftForceApplyed[userID] = userb2body;
						userb2body.m_userData.scaleX = -1;
					}
				}
			}else{
				delete _leftForceApplyed[userID];
				userb2body = users[userID];
				if(userb2body){
					position = new b2Vec2(userx, usery);						
					if (!ismyuser){
						userb2body.SetXForm(position, 0);
						userb2body.SetLinearVelocity(new b2Vec2(lvx, lxy));
					}
					
					userb2body.SetLinearVelocity(new b2Vec2(-15, userb2body.GetLinearVelocity().y));
					if (isGroundFromBody(userb2body, _world)){
						userb2body.m_userData.gotoAndStop(PersFrameLabel.STAND);
					}
				}
			}
		}
		
		public function userGotoRight(userID:int, down:Boolean, userx:Number, usery:Number, lvx:Number, lxy:Number, ismyuser:Boolean = false):void{
			var userb2body:b2Body;
			var position:b2Vec2;
			if (down){
				if (_leftForceApplyed[userID]){
					delete _leftForceApplyed[userID];
				}
				if (!_rightForceApllyed[userID]){
					userb2body = users[userID];
					if (userb2body){
						position = new b2Vec2(userx, usery);						
						if (!ismyuser){
							userb2body.SetXForm(position, 0);
							userb2body.SetLinearVelocity(new b2Vec2(lvx, lxy));
						}
						
						_rightForceApllyed[userID] = userb2body;
						userb2body.m_userData.scaleX = 1;
					}				
				}
			}else{
				delete _rightForceApllyed[userID];
				userb2body = users[userID];
				if(userb2body){
					position = new b2Vec2(userx, usery);						
					if (!ismyuser){
						userb2body.SetXForm(position, 0);
						userb2body.SetLinearVelocity(new b2Vec2(lvx, lxy));
					}
					
					userb2body.SetLinearVelocity(new b2Vec2(15, userb2body.GetLinearVelocity().y));
					if (isGroundFromBody(userb2body, _world)){
						userb2body.m_userData.gotoAndStop(PersFrameLabel.STAND);
					}
				}		
			}
		}
		
		public function userJump(userID:int, down:Boolean, userx:Number, usery:Number, lvx:Number, lxy:Number, ismyuser:Boolean = false):void{
			if (down){
				var userb2body:b2Body = users[userID];
				if(userb2body){
					var position:b2Vec2 = new b2Vec2(userx, usery);						
					if (!ismyuser){
						userb2body.SetXForm(position, 0);
						userb2body.SetLinearVelocity(new b2Vec2(lvx, lxy));
					}
					trace("=======> " + isGroundFromBody(userb2body, _world));
					if (isGroundFromBody(userb2body, _world)){
						userb2body.SetLinearVelocity(new b2Vec2(userb2body.GetLinearVelocity().x, 0));
						userb2body.m_force.y = 0;
						userb2body.ApplyForce(new b2Vec2(0, _jumpForce * userb2body.m_userData["kjump"]), userb2body.GetLocalCenter());
						userb2body.m_userData.gotoAndStop(PersFrameLabel.JUMP);	
					}
				}			
			}
		}
		
		public function userExit(userID:uint):void{
			hideUserById(userID);
		}
		
		public function userOut(userID:uint):void{
			hideUserById(userID);
		}
		
		private function hideUserById(userID:uint):void{
			var lifeindicator:LifeIndicator = lifeindicators[userID];
			var usertitle:UserTitle = userstitles[userID];
			var userbody:b2Body = users[userID];
			
			for(var i:int = 0; i < userIDs.length; i++){
				if(userIDs[i] == userID){
					userIDs[i] = -1;
					break;
				}
			}
			
			if(userbody.m_userData && _gameUI.contains(userbody.m_userData)){
				_gameUI.removeChild(userbody.m_userData);
				userbody.m_userData = null;
			}
			userbody.SetXForm(new b2Vec2(int.MAX_VALUE, int.MAX_VALUE), 0);
			if(_gameUI.contains(lifeindicator)) _gameUI.removeChild(lifeindicator);
			if(_gameUI.contains(usertitle)) _gameUI.removeChild(usertitle);
		}
		
		public function getSource(sourceId:int):void{
			var p:Point = hideBodyByID(sourceId);
			var flash:FindFlash = new FindFlash(Flash);
			_gameUI.addChild(flash);
			flash.x = p.x;
			flash.y = p.y;
		}
		
		public function restoreSource(sourceId:int):void{
			showBodyByID(sourceId);
			delete getSourceIds[sourceId];
		}
		
		public function moveTarget(tx:Number, ty:Number):void{
			if(_targetmc && !_ishunter){
				if(_tweenTargetX) _tweenTargetX.endTween();
				if(_tweenTargetY) _tweenTargetY.endTween();
				_tweenTargetX = new Tween(_targetmc, _targetmc.x, tx, 300, -1, updateTargerX, endupdateTargerX);
				_tweenTargetY = new Tween(_targetmc, _targetmc.y, ty, 300, -1, updateTargerY, endupdateTargerY);
			}
		}
		private function updateTargerX(value:Number):void{
			_targetmc.x = value;
		}
		private function endupdateTargerX(value:Number):void{
		}
		private function updateTargerY(value:Number):void{
			_targetmc.y = value;
		}
		private function endupdateTargerY(value:Number):void{
		}
		
		public function selectGun(targetFrame:int):void{
			if(_targetmc){
				_targetmc.gotoAndStop(targetFrame);
			}
		}
		
		public function shot(sx:Number, sy:Number):void{
			if(_tweenTargetX) _tweenTargetX.endTween();
			if(_tweenTargetY) _tweenTargetY.endTween();
			
			if(_targetmc){
				_targetmc.x = sx;
				_targetmc.y = sy;
			}
			var flash:FindFlash = new FindFlash(Explot);
			_gameUI.addChild(flash);
			flash.x = sx;
			flash.y = sy;			
			
			if(!_ishunter){
				var _myuserMC:MovieClip = (users[GameApplication.app.userinfomanager.myuser.id] as b2Body).m_userData;
				if(_myuserMC){
					//диаметр попадания
					var d:Number = Math.sqrt(Math.pow(_myuserMC.x - sx, 2) + Math.pow(_myuserMC.y - sy, 2));
					if(d < GameConfig.radiusHit){
						var skills:int = int(_myuserMC["skills"]) * 5;
						var rnd:Number = Math.round(Math.random() * 100);  
						if(rnd < skills){
							GameApplication.app.gamemanager.dodge(_myuserMC.x, _myuserMC.y);						
						}else{
							GameApplication.app.gamemanager.wound();
						}
					}
				}				
			}			
		}
		
		public function dodge(sx:Number, sy:Number):void{
			var flash:FindFlash = new FindFlash(ShotResultIcon);
			_gameUI.addChild(flash);
			flash.x = sx;
			flash.y = sy;
		}
		
		public function wound(userID:int, lifes:int):void{
			var flash:FindFlash;
			
			if(users[userID] is b2Body){				
				var userbody:b2Body = users[userID];
				if(lifes > 0){
					var lifeindicator:LifeIndicator = lifeindicators[userID];
					var usertitle:UserTitle = userstitles[userID];
					
					lifeindicator.draw(lifes/3);
					_flashingManager.addFlashingElement(userbody.m_userData, 10);
					
					flash = new FindFlash(OnePoint);
					_gameUI.addChild(flash);
					flash.x = userbody.m_userData.x;
					flash.y = userbody.m_userData.y - 20;
					
					var wuser:User = GameApplication.app.gameContainer.chat.getUserByID(userID);
					if(wuser){
						var woundMessageText:String = wuser.title + " ранен";
						GameApplication.app.gameContainer.chat.sendSystemMessage(int(GameApplication.app.gameContainer.chat.activeRoom.id), woundMessageText);
						wuser = null;
					}
				}else{
					if(userID == GameApplication.app.userinfomanager.myuser.id){
						_killme = true;
					}
					
					flash = new FindFlash(FivePoint);
					_gameUI.addChild(flash);
					flash.x = userbody.m_userData.x;
					flash.y = userbody.m_userData.y - 20;
					
					hideUserById(userID);
					
					var kuser:User = GameApplication.app.gameContainer.chat.getUserByID(userID);
					if(kuser){
						var killMessageText:String = "Точное попадание! Охотник убил " + kuser.title;
						GameApplication.app.gameContainer.chat.sendSystemMessage(int(GameApplication.app.gameContainer.chat.activeRoom.id), killMessageText);
						kuser = null;
					}					
				}
			}			
		}
		
		public static function isGroundFromBody(body:b2Body, w:b2World):Boolean{			
			
			for (var cc:b2ContactEdge = body.m_contactList; cc; cc = cc.next) 
			{				
				if (!body.GetShapeList().IsSensor() && !cc.other.GetShapeList().IsSensor()){					
					var b_mc:MovieClip = (body.GetUserData() as MovieClip);
					var o_mc:MovieClip = (cc.other.GetUserData() as MovieClip);				
					var bs_mc:MovieClip;
					b_mc && (bs_mc = b_mc["bodysensor"]);
					
					if (b_mc && o_mc && bs_mc){
						var b_r:Rectangle = bs_mc.getBounds(b_mc.parent);
						var o_r:Rectangle = o_mc.getBounds(o_mc.parent);
						
						if (b_r.x < (o_r.x + o_r.width - 7) && (b_r.x + b_r.width) > o_r.x + 7){						
							var aabb:b2AABB = new b2AABB();
							aabb.lowerBound.Set(b_r.x + 7, b_r.y + b_r.height + 1);
							aabb.upperBound.Set(b_r.x + b_r.width - 7, b_r.y + b_r.height + 2);
							
							var count:int = w.Query(aabb,new Array(), int.MAX_VALUE);						
							if (count > 0) return true;
						}
					}
				}
			}			
			return false;
		}	
		
		private function createWorld(minX:Number, minY:Number, maxX:Number, maxY:Number):void{
			_gamebouns = new b2AABB();
			_gamebouns.lowerBound.Set(minX, minY);
			_gamebouns.upperBound.Set(maxX, maxX);
			
			_world = new b2World(_gamebouns, _gravity, _doSleep);				
		}
		
		private function createWorldFromXML(xmlContent:XML):void
		{
			var list:XMLList = xmlContent.elements("*");			
			var p:Point;
			
			for(var i:uint = 0; i < list.length(); i++)
			{
				if (list[i].name() == SceneElements.STATIC){
					_creator.createStaticBox(list[i].@x, list[i].@y, list[i].@w, list[i].@h, StaticSkin);
				}else if (list[i].name() == SceneElements.STATICRED){
					_creator.createStaticRedBox(list[i].@x, list[i].@y, list[i].@w, list[i].@h, StaticRedSkin);
				}else if (list[i].name() == SceneElements.STATICBLUE){
					_creator.createStaticBlueBox(list[i].@x, list[i].@y, list[i].@w, list[i].@h, StaticBlueSkin);
				}else if (list[i].name() == SceneElements.HEAVYBOX){
					_creator.createHeavyBox(list[i].@x, list[i].@y, list[i].@w, list[i].@h, HeavyBoxSkin);
				}else if (list[i].name() == SceneElements.BOX){
					_creator.createBox(list[i].@x, list[i].@y, list[i].@w, list[i].@h, BoxSkin);
				}else if (list[i].name() == SceneElements.STICK){
					_creator.createStick(list[i].@x, list[i].@y, list[i].@w, list[i].@h, StickSkin);
				}else if (list[i].name() == SceneElements.BALL){
					_creator.createBall(list[i].@x, list[i].@y, list[i].@w / 2, BallSkin);
				}else if (list[i].name() == SceneElements.SPRINGBROAD){
					_creator.CreateSpringboard(list[i].@x, list[i].@y, list[i].@w, list[i].@h, SpringboardSkin);
				}else if (list[i].name() == SceneElements.SOURCE){
					_creator.CreateSource(list[i].@x, list[i].@y, list[i].@w, list[i].@h, Nut);
				}else if (list[i].name() == SceneElements.BULLET){
					_creator.CreateBullet(list[i].@x, list[i].@y, list[i].@w, list[i].@h, Bullet);
				}else if (list[i].name() == SceneElements.CARRIERH){
					_creator.createCarrierH(list[i].@x, list[i].@y, list[i].@lw , list[i].@bw, list[i].@bh, StaticBlackSkin);
				}else if (list[i].name() == SceneElements.CARRIERV){
					_creator.createCarrierV(list[i].@x, list[i].@y, list[i].@lh , list[i].@bw, list[i].@bh, StaticBlackSkin);
				}else if (list[i].name() == SceneElements.HERO){
					p = new Point(list[i].@x, list[i].@y);
					_heroPoints.push(p);
				}
			}
		}
		
		private function createDebugDraw(drawScale:Number, fillAlpha:Number, linethckness:Number, drawFlags:uint):void{
			_debugDraw = new b2DebugDraw();
			var dbgSprite:Sprite = new Sprite();			
			_gameUI.addChild(dbgSprite);
			_debugDraw.m_sprite = dbgSprite;			
			_debugDraw.m_drawScale = drawScale;			
			_debugDraw.m_fillAlpha = fillAlpha;
			_debugDraw.m_lineThickness = linethckness;
			_debugDraw.m_drawFlags = drawFlags;
			_world.SetDebugDraw(_debugDraw);
		}	
		
		public function createMouseJoing(evt:MouseEvent):void {	
			var body:b2Body = GetBodyAtMouse();
			
			if (body) {
				var mouseJointDef:b2MouseJointDef = new b2MouseJointDef();
				mouseJointDef.body1 = _world.GetGroundBody();
				mouseJointDef.body2 = body;
				mouseJointDef.target.Set(mouseX / kScale, mouseY / kScale);
				mouseJointDef.maxForce = 3000000;
				mouseJointDef.timeStep = _m_timeStep;
				_mouseJoint = _world.CreateJoint(mouseJointDef) as b2MouseJoint;
			}
		}
		public function destroyMouseJoing(evt:MouseEvent):void {
			if (_mouseJoint) {
				_world.DestroyJoint(_mouseJoint);
				_mouseJoint = null;
			}
		}
		
		public function GetBodyAtMouse(includeStatic:Boolean=false):b2Body
		{
			var mouseXWorldPhys:Number = (mouseX) / kScale;
			var mouseYWorldPhys:Number = (mouseY) / kScale;
			_mouseVec.Set(mouseXWorldPhys, mouseYWorldPhys);
			var aabb:b2AABB = new b2AABB();
			aabb.lowerBound.Set(mouseXWorldPhys - 0.001, mouseYWorldPhys - 0.001);
			aabb.upperBound.Set(mouseXWorldPhys + 0.001, mouseYWorldPhys + 0.001);
			var k_maxCount:int = 10;
			var shapes:Array = new Array();
			var count:int = _world.Query(aabb,shapes,k_maxCount);
			var body:b2Body=null;
			
			for (var i:int = 0; i < count; ++i) {
				if (shapes[i].GetBody().IsStatic()==false||includeStatic) {
					var tShape:b2Shape = shapes[i] as b2Shape;
					var inside:Boolean = tShape.TestPoint(tShape.GetBody().GetXForm(),_mouseVec);
					if (inside) {
						body = tShape.GetBody();
						break;
					}
				}
			}
			return body;
		}
		
		public function destroyWorld():void
		{
			_flashingManager.clear();
			
			GameApplication.app.gamemanager.removeEventListener(SelectGunEvent.SELECT, onSelectGun);
			
			if(GameApplication.app.config.mode == GameMode.DEBUG){
				stage.removeEventListener(Event.ENTER_FRAME, update);
			}
			
			stage.removeEventListener(MouseEvent.MOUSE_DOWN,createMouseJoing);
			stage.removeEventListener(MouseEvent.MOUSE_UP,destroyMouseJoing);
			if(!_ishunter){				
				if(_ismember){
					stage.removeEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
					stage.removeEventListener(KeyboardEvent.KEY_UP, onKeyUp);
				}				
			
				_contactListener.removeEventListener(GameContactListenerEvent.SOURCE, onSourceContact);
				_contactListener.removeEventListener(GameContactListenerEvent.BULLET, onBulletContact);
			}else{
				stage.removeEventListener(MouseEvent.CLICK, onShot);
				stage.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);				
				
				Mouse.show();
				
				clearInterval(_ciid);
			}
			
			_rightForceApllyed = null;
			_leftForceApplyed = null;	
			
			for (var bb:b2Body = _world.m_bodyList; bb; bb = bb.m_next)
			{
				destroyBody(bb);
			}
			for (var jj:b2Joint = _world.m_jointList; jj; jj = jj.m_next){
				_world.DestroyJoint(jj);
			}
			
			for each(var elem:UserTitle in _gameUI){
				_gameUI.removeChild(elem);
			}
			_commongr.removeChild(_gameUI);
			_commongr.removeChild(_mask);
			
			removeElement(_commongr);
		}
		
		public function destroyBody(bb:b2Body): void 
		{
			if(bb.m_userData && _gameUI.contains(bb.m_userData))
			{
				_gameUI.removeChild(bb.m_userData);
				bb.m_userData = null;
			}
			_world.DestroyBody(bb);
		}
		
		public function destroyBodyByID(sourceId:int):Point 
		{
			var p:Point = new Point();
			for (var bb:b2Body = _world.m_bodyList; bb; bb = bb.m_next)
			{
				if (bb.m_userData is Sprite)
				{
					if(bb.m_userData["id"] == sourceId){
						p.x = bb.m_userData.x;
						p.y = bb.m_userData.y;
						
						_gameUI.removeChild(bb.m_userData);
						bb.m_userData = null;
						
						_world.DestroyBody(bb);
						break;
					}
				}
			}
			return p;
		}
		
		public function hideBodyByID(sourceId:int):Point 
		{
			var p:Point = new Point();
			for (var bb:b2Body = _world.m_bodyList; bb; bb = bb.m_next)
			{
				if (bb.m_userData is Sprite)
				{
					if(bb.m_userData["id"] == sourceId){
						p.x = bb.m_userData.x;
						p.y = bb.m_userData.y;
						
						(bb.m_userData as Sprite).visible = false;
						break;
					}
				}
			}
			return p;
		}
		
		public function showBodyByID(sourceId:int):Point 
		{
			var p:Point = new Point();
			for (var bb:b2Body = _world.m_bodyList; bb; bb = bb.m_next)
			{
				if (bb.m_userData is Sprite)
				{
					if(bb.m_userData is MovieClip && bb.m_userData["id"] == sourceId){
						p.x = bb.m_userData.x;
						p.y = bb.m_userData.y;
						
						(bb.m_userData as Sprite).visible = true;
						break;
					}
				}
			}
			return p;
		}
		
		public function update(e:Event):void
		{			
			//ПРОВЕРКА НЕ УПАЛ ЛИ ИГРОК
			if (!_myuserout && _ismember && !_ishunter && !_killme){
				if ((users[GameApplication.app.userinfomanager.myuser.id] as b2Body).GetXForm().position.y > _wrect.y + _wrect.height ||
					(users[GameApplication.app.userinfomanager.myuser.id] as b2Body).GetXForm().position.y < _wrect.y ||
					(users[GameApplication.app.userinfomanager.myuser.id] as b2Body).GetXForm().position.x > _wrect.x + _wrect.width ||
					(users[GameApplication.app.userinfomanager.myuser.id] as b2Body).GetXForm().position.x < _wrect.x){
					_myuserout = true;
					GameApplication.app.gamemanager.userout();
					if(_ismember){
						stage.removeEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
						stage.removeEventListener(KeyboardEvent.KEY_UP, onKeyUp);
					}
				}
			}
			
			if (_mouseJoint)
			{
				var mouseXWorldPhys:Number = mouseX / kScale;
				var mouseYWorldPhys:Number = mouseY / kScale;
				var p2:b2Vec2 = new b2Vec2(mouseXWorldPhys,mouseYWorldPhys);
				_mouseJoint.SetTarget(p2);
			}
			
			for each(var bodyr:b2Body in _rightForceApllyed){				
				if (bodyr && bodyr.m_userData){				
					var force_r:int = _rightForce * bodyr.m_userData["kspeed"];
					if (!isGroundFromBody(bodyr, _world)) force_r /= 5;
					
					bodyr.ApplyForce(new b2Vec2((30 - bodyr.GetLinearVelocity().x) * force_r, bodyr.GetLinearVelocity().y), bodyr.GetLocalCenter());
					if (isGroundFromBody(bodyr, _world) && (bodyr.m_userData as MovieClip).currentFrameLabel == PersFrameLabel.STAND){
						bodyr.m_userData.gotoAndPlay(PersFrameLabel.RUN);
					}
				}
			}
			for each(var bodyl:b2Body in _leftForceApplyed){
				if (bodyl && bodyl.m_userData){
					var force_l:int = -_rightForce * bodyl.m_userData["kspeed"];
					if (!isGroundFromBody(bodyl, _world)) force_l /= 5;
					
					bodyl.ApplyForce(new b2Vec2((30 - Math.abs(bodyl.GetLinearVelocity().x)) * force_l, bodyl.GetLinearVelocity().y), bodyl.GetLocalCenter());
					if (isGroundFromBody(bodyl, _world) && (bodyl.m_userData as MovieClip).currentFrameLabel == PersFrameLabel.STAND){
						bodyl.m_userData.gotoAndPlay(PersFrameLabel.RUN);
					}
				}
			}
			
			_world.Step(_m_timeStep, _m_iterations);	
			for (var bb:b2Body = _world.m_bodyList; bb; bb = bb.m_next)
			{
				if (bb.m_userData && bb.m_userData is Sprite)
				{
					bb.m_userData.x = Math.round(bb.GetPosition().x * kScale);
					bb.m_userData.y = Math.round(bb.GetPosition().y * kScale);
					bb.m_userData.rotation = bb.GetAngle() * (180 / Math.PI);
					bb.WakeUp();
				}
			}
			
			for (var jj:b2Joint = _world.m_jointList; jj; jj = jj.m_next){
				if(jj is b2PrismaticJoint){
					var absdelda:Number;
					var realdelta:Number;
					
					//if horizontal carrier
					if(jj.m_body2.GetUserData().name == "toright" || jj.m_body2.GetUserData().name == "toleft"){
						absdelda = Math.abs((jj as b2PrismaticJoint).GetAnchor1().x - (jj as b2PrismaticJoint).GetAnchor2().x);
						realdelta = (jj as b2PrismaticJoint).GetUpperLimit();
						if(absdelda < 1){
							jj.m_body2.GetUserData().name = "toright";
							jj.m_body2.SetLinearVelocity(new b2Vec2(20, 0));
						}else if(Math.abs(absdelda - realdelta) < 1){
							jj.m_body2.GetUserData().name = "toleft";
							jj.m_body2.SetLinearVelocity(new b2Vec2(-20, 0));
						}else{
							if(jj.m_body2.GetUserData().name == "toright"){								
								jj.m_body2.SetLinearVelocity(new b2Vec2(20, 0));
							}else{
								jj.m_body2.SetLinearVelocity(new b2Vec2(-20, 0));
							}
						}							
					}else{
						//vertical carrier						
						absdelda = Math.abs((jj as b2PrismaticJoint).GetAnchor1().y - (jj as b2PrismaticJoint).GetAnchor2().y);
						realdelta = (jj as b2PrismaticJoint).GetUpperLimit();
						if(absdelda < 1){
							jj.m_body2.GetUserData().name = "totop";
							jj.m_body2.SetLinearVelocity(new b2Vec2(0, 10));
						}else if(Math.abs(absdelda - realdelta) < 1){
							jj.m_body2.GetUserData().name = "tobottom";
							jj.m_body2.SetLinearVelocity(new b2Vec2(0, -15));
						}else{
							if(jj.m_body2.GetUserData().name == "totop"){								
								jj.m_body2.SetLinearVelocity(new b2Vec2(0, 10));
							}else{
								jj.m_body2.SetLinearVelocity(new b2Vec2(0, -15));
							}
						}
					}
				}
			}
			
			for(var k:int = 0; k < userIDs.length; k++){
				var userbody:b2Body = users[userIDs[k]];
				if (userbody && userbody.m_userData){
					(userstitles[userIDs[k]] as UserTitle).x = -((userstitles[userIDs[k]] as UserTitle).width / 2) + userbody.m_userData.x;
					(userstitles[userIDs[k]] as UserTitle).y = userbody.m_userData.y - 50;
					
					(lifeindicators[userIDs[k]] as LifeIndicator).x = -((lifeindicators[userIDs[k]] as LifeIndicator).width / 2) + userbody.m_userData.x;
					(lifeindicators[userIDs[k]] as LifeIndicator).y = userbody.m_userData.y - 30;
				}
			}
		}
		
		override protected function createChildren():void{
			super.createChildren();			
			
			this.percentWidth = 100;
			horizontalAlign = HorizontalAlign.CENTER;
			
			var random:Number = Math.random();			
			if(random > 0 && random <= 0.33){
				_commongr.addChild(new BackGroundDay());
			}else if(random > 0.33 && random <= 0.66){
				_commongr.addChild(new BackGroundEvening());
			}else{
				_commongr.addChild(new BackGroundNight());
			}
			
			_commongr.addChild(_gameUI);
			_commongr.addChild(_mask);
			addElement(_commongr);
		}
		
		public function onHide():void{			
		}
	}
}