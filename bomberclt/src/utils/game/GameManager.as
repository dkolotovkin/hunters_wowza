package utils.game
{
	import application.GameApplication;
	import application.components.popup.extraction.PopUpExtraction;
	import application.components.popup.extraction.bets.PopUpExtractionBets;
	import application.components.popup.help.startbonus.PopUpStartBonus;
	import application.gamecontainer.scene.game.GameWorld;
	import application.gamecontainer.scene.minigames.betsroom.BetsRoom;
	
	import flash.events.EventDispatcher;
	import flash.events.TimerEvent;
	import flash.net.Responder;
	import flash.utils.Timer;
	
	import mx.controls.Alert;
	
	import org.hamcrest.core.throws;
	
	import utils.copyproperties.CopyProperties;
	import utils.game.action.GameActionSubType;
	import utils.game.action.GameActionType;
	import utils.game.betroominfo.BetResult;
	import utils.game.extraction.ExtractionData;
	import utils.shop.category.ShopCategory;
	import utils.shop.item.Item;
	import utils.shop.itemprototype.ItemPrototype;
	import utils.user.User;
	import utils.user.UserRole;

	public class GameManager extends EventDispatcher
	{
		public var timer:Timer;
		public var guntimer:Timer;
		public var timeround:int;
		public var roomID:int;		
		public var gameworld:GameWorld;
		[Bindable]
		public var gameMode:Boolean = false;
		[Bindable]
		public var creatorMapText:String;
		[Bindable]
		public var mapName:String;
		[Bindable]
		public var hunter:User;
		[Bindable]
		public var hunterId:int;
		[Bindable]
		public var countNuts:int;
		[Bindable]
		public var countBullets:int;
		[Bindable]
		public var countKilled:int;
		
		private var _hunterGun:Item;		
		
		private var _callBackBetGames:Function;
		private var _callBackBetsInfo:Function;
		
		public var sendRequest:Boolean = false;
		
		public function GameManager(){
		}
		
		//войти в игру(в забег)
		public function sendStartRequest():void{
			if(!sendRequest){
				sendRequest = true;
				var startResponder:Responder = new Responder(onStart, onStartError);			
				GameApplication.app.connection.call("gameStartRequest", startResponder);
			}
		}
		private function onStart(result:Object):void{
			if (result["added"] && !gameMode){
				GameApplication.app.popuper.hidePopUp();
				GameApplication.app.navigator.goFindUsersScreen(result["waittime"]);
			}
			sendRequest = false;
		}
		private function onStartError(err:Object):void{
			GameApplication.app.navigator.goHome();
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу. Код ошибки 111. Сообщите об этом разработчикам!");
			sendRequest = false;
		}		
		
		public function processGameAction(action:Object):void{
			if(action){					
				if (action["type"] == GameActionType.NOT_ENOUGH_USERS){
					GameApplication.app.navigator.goHome();
					GameApplication.app.popuper.showInfoPopUp("Нет желающих поиграть прямо сейчас. Повторите попытку немного позже.");
				}else if (action["type"] == GameActionType.NOT_ENOUGH_ENERGY){					
					GameApplication.app.popuper.showInfoPopUp("У вас недостаточно энергии для игры (необходимо " + action["needEnergy"]+ "). Вы можете немного подождать (энергия восстановится со временем) или купить в магазине что-нибудь для восстановления.");
				}else if (action["type"] == GameActionType.NOT_ENOUGH_MONEY){					
					GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег для игры.");
				}else if (action["type"] == GameActionType.NO_ROOM){					
					GameApplication.app.popuper.showInfoPopUp("Игровая комната не найдена, возможно игра уже началась");
				}else if (action["type"] == GameActionType.NO_SEATS){					
					GameApplication.app.popuper.showInfoPopUp("Нет свободных мест");
				}else if(action["type"] == GameActionType.START){					
					gameMode = true;
					
					var creatorName:String = action["creatorName"];
					var locationFile:String = action["locationFile"];
					if(creatorName && creatorName.length){
						creatorMapText = "Создатель карты: " + action["creatorName"];
						var startIndex:uint;
						while(locationFile.indexOf("\\") != -1){
							startIndex = locationFile.indexOf("\\") + 1;
							locationFile = locationFile.substr(startIndex, locationFile.length - startIndex);
						}
						while(locationFile.indexOf("/") != -1){
							startIndex = locationFile.indexOf("/") + 1;
							locationFile = locationFile.substr(startIndex, locationFile.length - startIndex);
						}
						mapName = locationFile;
					}else{
						creatorMapText = "";
						mapName = "";
					}

					hunterId = action["hunterId"];
					var tempHunterUser:User = GameApplication.app.gameContainer.chat.getUserByID(hunterId);
					if(tempHunterUser == null){
						tempHunterUser = new User();
						tempHunterUser.title = "вышел из игры";
					}
					hunter = tempHunterUser;
					tempHunterUser = null;
					
					countBullets = 0;
					countKilled = 0;
					countNuts = 0;
					gameworld = GameApplication.app.navigator.goGameWorld(action["roomId"], XML(action["locationXML"]), (action["users"] as Array), hunterId, action["gametype"]);
					
					timeround = action["time"];
					timer = new Timer(1000, timeround);
					timer.start();
					timer.addEventListener(TimerEvent.TIMER, timerHandler);
					dispatchEvent(new GameManagerTimerEvent(GameManagerTimerEvent.TIMER_UPDATE, timeround));
					
					var iishunter:Boolean = gameworld.hunterId == GameApplication.app.userinfomanager.myuser.id;
					GameApplication.app.gameContainer.chat.bagingamesmall.update(iishunter);
					
					if(iishunter && GameApplication.app.userinfomanager.myuser.bullets == 0){
						GameApplication.app.gameContainer.chat.bagingamesmall.blockAllAndSelectDefault();
					}
					if(iishunter){
						GameApplication.app.userinfomanager.myuser.bullets = Math.max(0, GameApplication.app.userinfomanager.myuser.bullets - 1);
					}
					
					var item:Item;
					var dress:Array = GameApplication.app.userinfomanager.myuser.itemsArr;
					for(var i:uint = 0; i < dress.length; i++){
						item = dress[i];
						if(item != null && item.onuser == 1){
							if((iishunter && item.param1 == ShopCategory.ARSENAL) ||
								(!iishunter && item.param1 == ShopCategory.THINGS)){
								item.quality = Math.max(0, item.quality - 1);
							}
						}
					}
					item = null;
				}else if(action["type"] == GameActionType.ACTION){
					if (gameworld != null && gameworld.roomID == int(action["roomId"])){
						if (action["subtype"] == GameActionSubType.GOTOLEFT){
							gameworld.userGotoLeft(action["initiatorID"], action["down"], action["userx"], action["usery"], action["lvx"], action["lvy"]);
						}else if (action["subtype"] == GameActionSubType.GOTORIGHT){
							gameworld.userGotoRight(action["initiatorID"], action["down"], action["userx"], action["usery"], action["lvx"], action["lvy"]);
						}else if (action["subtype"] == GameActionSubType.JUMP){
							gameworld.userJump(action["initiatorID"], action["down"], action["userx"], action["usery"], action["lvx"], action["lvy"]);
						}else if (action["subtype"] == GameActionSubType.MOVE_TARGET){
							//обновление местоположения прицела
							gameworld.moveTarget(action["tx"], action["ty"]);
						}else if (action["subtype"] == GameActionSubType.SELECT_GUN){
							//для обновления прицела, при изменении вида оружия (sourceId - id прототипа орижия, т.к. item оружия есть только у владельца)
							var itempGun:ItemPrototype = GameApplication.app.shopmanager.itemPrototypesObj[action["sourceId"]];
														
							var accuracy:int = GameConfig.MIN_ACCURACY;
							if(itempGun){
								accuracy = itempGun.param2;
							}
							gameworld.selectGun(accuracy);
							GameApplication.app.gameContainer.chat.bagingamesmall.updateHunterGun(itempGun);
						}else if (action["subtype"] == GameActionSubType.GET_SOURCE){
							//взяли орех
							if(action["userId"] == GameApplication.app.userinfomanager.myuser.id){
								countNuts++;
							}
							gameworld.getSource(action["sourceId"]);
						}else if (action["subtype"] == GameActionSubType.GET_BULLET){
							//взяли патрон
							if(action["userId"] == GameApplication.app.userinfomanager.myuser.id){
								countBullets++;
							}
							gameworld.getSource(action["sourceId"]);
						}else if (action["subtype"] == GameActionSubType.SHOT){
							gameworld.shot(action["tx"], action["ty"]);
						}else if (action["subtype"] == GameActionSubType.HUNTER_SHOT){
							gameworld.shot(action["tx"], action["ty"]);
							GameApplication.app.userinfomanager.myuser.bullets = action["bullets"];
							if(GameApplication.app.userinfomanager.myuser.bullets == 0){
								GameApplication.app.gameContainer.chat.bagingamesmall.blockAllAndSelectDefault();
							}
						}else if (action["subtype"] == GameActionSubType.DODGE){
							gameworld.dodge(action["tx"], action["ty"]);
						}else if (action["subtype"] == GameActionSubType.WOUND){
							gameworld.wound(action["userId"], action["sourceId"]);
							if(action["sourceId"] == 0){
								countKilled++;
							}
						}else if (action["subtype"] == GameActionSubType.RESTORE_SOURCE){
							gameworld.restoreSource(action["sourceId"]);
						}else if (action["subtype"] == GameActionSubType.EXIT){
							gameworld.userExit(action["userId"]);
						}else if (action["subtype"] == GameActionSubType.OUT){
							gameworld.userOut(action["userId"]);
						}
					}
				}else if (action["type"] == GameActionType.FINISH){					
					if(gameworld != null && gameworld.roomID == int(action["roomId"])){
						exitGame();
						var extractonData:ExtractionData = new ExtractionData();
						CopyProperties.copy(action["extraction"], extractonData);
						var popUp:PopUpExtraction = new PopUpExtraction(extractonData);
						GameApplication.app.popuper.show(popUp);
						
						hunter = null;
					}
				}else if (action["type"] == GameActionType.WAIT_START){					
					if (!gameMode){
						GameApplication.app.popuper.hidePopUp();
						GameApplication.app.navigator.goFindUsersScreen(action["waittime"]);
					}
				}else if (action["type"] == GameActionType.BETS_CONTENT){
					if(GameApplication.app.navigator.currentSceneContent != null &&
						GameApplication.app.navigator.currentSceneContent is BetsRoom){
						(GameApplication.app.navigator.currentSceneContent as BetsRoom).updateBetsInfo(action);
					}					
				}else if (action["type"] == GameActionType.FINISH_BETS){
					if(action["returnmoney"] > 0 || action["prizemoney"] > 0)
					{
						var pp:PopUpExtractionBets = new PopUpExtractionBets(action["returnmoney"], action["section"], action["prizemoney"]);			
						GameApplication.app.popuper.show(pp);
					}
					
					if(GameApplication.app.navigator.currentSceneContent != null &&
						GameApplication.app.navigator.currentSceneContent is BetsRoom){
						(GameApplication.app.navigator.currentSceneContent as BetsRoom).selectWinnerSection(action["section"]);
					}
				}
			}else{
				GameApplication.app.navigator.goHome();
				GameApplication.app.popuper.showInfoPopUp("Произошла ошибка. Код ошибки 222. Сообщите об этом разработчикам!");
			}
		}
		
		public function exitGame():void{
			if(gameworld){
				GameApplication.app.actionShowerMenu.hideMenu();
				GameApplication.app.useitemingamemanager.clear();
				
				if(timer){
					timer.removeEventListener(TimerEvent.TIMER, timerHandler);
					timer.stop();
				}
				
				gameworld.destroyWorld();
				GameApplication.app.navigator.goHome();
				gameworld = null;
				
				gameMode = false;
			}
		}
		
		private function timerHandler(e:TimerEvent):void{
			dispatchEvent(new GameManagerTimerEvent(GameManagerTimerEvent.TIMER_UPDATE, (timeround * 1000 - (e.target as Timer).currentCount * (e.target as Timer).delay) / 1000));
		}
		
		private function guntimerHandler(e:TimerEvent):void{
			if((e.target as Timer).currentCount == (e.target as Timer).repeatCount){
				guntimer.removeEventListener(TimerEvent.TIMER, guntimerHandler);
				guntimer.stop();
				GameApplication.app.gameContainer.chat.bagingamesmall.updateCooldown(0);
			}else{
				GameApplication.app.gameContainer.chat.bagingamesmall.updateCooldown((e.target as Timer).currentCount / (e.target as Timer).repeatCount);
			}
		}
		
		public function goToLeft(down:Boolean, _x:Number, _y:Number, _lvx:Number, _lvy:Number):void{			
			GameApplication.app.connection.call("gameGotoleft", null, gameworld.roomID, down, _x, _y, _lvx, _lvy);
		}
		public function goToRight(down:Boolean, _x:Number, _y:Number, _lvx:Number, _lvy:Number):void{			
			GameApplication.app.connection.call("gameGotoright", null, gameworld.roomID, down, _x, _y, _lvx, _lvy);
		}
		public function jump(down:Boolean, _x:Number, _y:Number, _lvx:Number, _lvy:Number):void{
			GameApplication.app.connection.call("gameJump", null, gameworld.roomID, down, _x, _y, _lvx, _lvy);
		}
		public function moveTarget(_x:Number, _y:Number):void{
			GameApplication.app.connection.call("moveTarget", null, gameworld.roomID, _x, _y);
		}
		public function shot(_x:Number, _y:Number):void{			
			if(_hunterGun && (guntimer == null || (guntimer && !guntimer.running))){
				guntimer = new Timer(10, (_hunterGun.param4 * GameConfig.countMiliSecondByOnePoint) / 10);
				guntimer.start();
				guntimer.addEventListener(TimerEvent.TIMER, guntimerHandler);
				
				GameApplication.app.connection.call("shot", null, gameworld.roomID, _x, _y);
			}
		}
		public function dodge(_x:Number, _y:Number):void{
			GameApplication.app.connection.call("dodge", null, gameworld.roomID, _x, _y);
		}
		public function wound():void{
			GameApplication.app.connection.call("wound", null, gameworld.roomID);
		}
		public function getsource(id:uint):void{
			GameApplication.app.connection.call("gameGetSource", null, gameworld.roomID, id);
		}
		public function getbullet(id:uint):void{
			GameApplication.app.connection.call("gameGetBullet", null, gameworld.roomID, id);
		}
		public function userout():void{	
			GameApplication.app.connection.call("gameUserout", null, gameworld.roomID);
		}
		public function userexit():void{	
			GameApplication.app.connection.call("gameUserexit", null, gameworld.roomID);
		}
		public function selectGun(item:Item):void{
			_hunterGun = item;
			
			GameApplication.app.connection.call("selectGun", null, gameworld.roomID, item.id);
			dispatchEvent(new SelectGunEvent(SelectGunEvent.SELECT, item));
		}
		
		public function endTestGame():void{		
			GameApplication.app.navigator.goHome();
			GameApplication.app.connection.call("startbonusGetStartBonus", new Responder(onGetStartBonus, null));			
		}
		private function onGetStartBonus(e:*):void{
			GameApplication.app.navigator.goHome();
			GameApplication.app.popuper.show(new PopUpStartBonus());
		}
		
		public function addToBetsMiniGame():void{
			GameApplication.app.connection.call("minigameAddToBetsGame", new Responder(onAddToBetsMiniGame, null));
		}
		private function onAddToBetsMiniGame(action:Object):void{
			if(GameApplication.app.navigator.currentSceneContent != null &&
				GameApplication.app.navigator.currentSceneContent is BetsRoom){
				(GameApplication.app.navigator.currentSceneContent as BetsRoom).updateBetsInfo(action);
			}
		}
		
		public function exitBetsMiniGame():void{
			GameApplication.app.connection.call("minigameExitBetsGame", new Responder(null, null));
		}
		
		public function betMiniGame(section:uint, bet:uint):void{
			GameApplication.app.connection.call("minigameBet", new Responder(onBetMiniGame, null), section, bet);
		}
		private function onBetMiniGame(result:int):void{
			if(result == BetResult.OK){
				GameApplication.app.popuper.hidePopUp();
			}else if(result == BetResult.NO_ROOM){
				GameApplication.app.popuper.showInfoPopUp("Невозможно сделать ставку. Возможно игра уже началась или организатор вышел из игры.");
			}else if(result == BetResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег для ставки.");
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно сделать ставку.");
			}
		}
	}
}