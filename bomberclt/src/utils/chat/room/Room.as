package utils.chat.room
{
	import application.GameApplication;
	import application.components.popup.buy.PopUpBuy;
	import application.components.popup.buy.present.PopUpPresentBuy;
	import application.gamecontainer.chat.actionmenu.chatuser.ActionMenuChatUser;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.MovieClip;
	import flash.events.EventDispatcher;
	import flash.text.engine.FontWeight;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import flashx.textLayout.elements.FlowElement;
	import flashx.textLayout.elements.InlineGraphicElement;
	import flashx.textLayout.elements.LinkElement;
	import flashx.textLayout.elements.ParagraphElement;
	import flashx.textLayout.elements.SpanElement;
	import flashx.textLayout.elements.TextFlow;
	import flashx.textLayout.events.FlowElementMouseEvent;
	
	import utils.access.AccessManager;
	import utils.chat.UserSprite;
	import utils.chat.formats.ChatActionFormat;
	import utils.chat.formats.ChatRegExp;
	import utils.chat.formats.ChatSmiles;
	import utils.chat.message.MessageType;
	import utils.clan.ClanError;
	import utils.managers.ban.BanType;
	import utils.selector.ISelected;
	import utils.shop.itemprototype.ItemPrototype;
	import utils.shop.presentprototype.PresentPrototype;
	import utils.user.User;
	import utils.user.UserRole;

	public class Room extends EventDispatcher implements ISelected
	{
		public var title : String;
		protected var _id : int;
		protected var _selected : Boolean;
		protected var _messages : int = 0;		
		protected var _cache : TextFlow;
		protected var _users : Object = new Object();
		private static var _urlFinder:RegExp = /(http:\/\/){0,1}(\S*\.ru)(\/\S*){0,1}/g;
		
		private var _stacksid:int = -1;
		private var _stackmessages:Array = new Array();
		
		private var _linksToPicture:Object = new Object();
		
		public function get id() : String {
			return _id.toString();
		}	
		
		public function get selected() : Boolean {
			return _selected;
		}
		
		public function set selected(value : Boolean) : void {
			if (_selected != value) {
				_selected = value;
			}	
		}
		
		public function get users():Object{
			return _users;
		}
		
		public function Room(id : int, title : String, users:Array = null, messages:Array = null)
		{
			this.title = title;			
			_id = id;
			_cache = new TextFlow();
			
			//ДЛЯ РАБОТЫ ССЫЛОК ПРИ ПУСТОМ TextFlow ПРИ ИНИЦИАОИЗАЦИИ (неизвестно почему так, возможно баг flex sdk)
			addAndRemoveSimpleLink();			
			
			if(users && users.length){
				for(var i:uint = 0; i < users.length; i++){
					addUser(users[i]);
				}	
			}
			
			if(messages && messages.length){				
				for(var j:uint = 0; j < messages.length; j++){	
					addMessage(messages[j], true, true);
				}
			}			
		}
		
		private function addAndRemoveSimpleLink():void{
			var link : LinkElement = new LinkElement();
			var p:ParagraphElement = new ParagraphElement();
			p.addChild(link);
			_cache.addChild(p);
			_cache.removeChild(p);
		}
		
		public function addUser(u : Object) : Boolean {
			var user:User;
			if(u is User){
				user = (u as User).clone();
			}else{
				user = createUser(u);
			}
			user.isonline = true;
			
			if (!_users[user.id]) {
				_users[user.id] = user;
				dispatchEvent(new RoomEvent(RoomEvent.ADD_USER, user, this));
				return true;
			}
			return false;
		}
		private function createUser(u:Object):User{			
			if(u){
				var user:User = new User();
				if (!_users[int(u["id"])]){
					user.id = int(u["id"]);
					user.sex = int(u["sex"]);
					user.king = int(u["king"]);
					user.title = String(u["title"]);	
					user.level = int(u["level"]);
					user.role = int(u["role"]);
					user.url = String(u["url"]);
					user.popular = int(u["popular"]);
				}else{
					user = _users[int(u["id"])];
				}
				return user;
			}
			return null;
		}
		
		public function removeUser(id : int) : User {			
			var user : User = _users[id];
			if (user) {
				dispatchEvent(new RoomEvent(RoomEvent.REMOVE_USER, user, this));
				delete _users[id];
			}
			return user;
		}
		
		public function getUser(id : int) : User {
			return _users[id];
		}
		
		public function addMessage(message : Object, dispach : Boolean = true, ishistory:Boolean = false) : void {			
			if (ishistory){
				addM(message, dispach, ishistory);
			}else{
				_stackmessages.push(message);
				if(_stacksid == -1){
					_stacksid = setInterval(addStackMessage, 500);
					addM(_stackmessages.shift());
				}
			}
		}
		
		private function addM(message : Object, dispach : Boolean = true, ishistory:Boolean = false):void{
			var p : ParagraphElement = getMessageString(message, ishistory);	
			
			if ((_cache.getChildAt(0) as ParagraphElement) && 
				((_cache.getChildAt(0) as ParagraphElement).getChildAt(0) as SpanElement) && 
				(((_cache.getChildAt(0) as ParagraphElement).getChildAt(0) as SpanElement).text == "")){
				_cache.removeChildAt(0);
			} 
			if (_cache.numChildren > GameApplication.app.config.maxMessagesInRoom) {
				_cache.removeChildAt(0);				
			}
			dispach && _cache.addChild(p);			
			dispatchEvent(new RoomEvent(RoomEvent.ADD_MESSAGE, null, this));
		}
		
		private function addStackMessage():void{			
			if(_stackmessages.length == 0){
				if(_stacksid != -1){
					clearInterval(_stacksid);
					_stacksid = -1;
				}
			}else{
				addM(_stackmessages.shift());
			}
		}
		
		private function getMessageString(m : Object, ishistory:Boolean = false) : ParagraphElement {
			var from:User;
			if(GameApplication.app.chatmanager.commonroom){
				from = GameApplication.app.chatmanager.commonroom.getUser(m["fromId"]);
			}else{
				from = users[m["fromId"]];
			}
			if(from == null && m["type"] != MessageType.SYSTEM && m["type"] != MessageType.JACK_POT_WIN  && m["type"] != MessageType.VICTORINA) return null;
			var to:User;
			if(GameApplication.app.chatmanager.commonroom){
				to = GameApplication.app.chatmanager.commonroom.getUser(m["toId"]); 		//createUser(m["to"]);
			}else{
				to = users[m["toId"]];
			}
			
			var p : ParagraphElement = new ParagraphElement();
			var ui : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x028AE3, 0x000000, FontWeight.NORMAL, false);
			var ban : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x9E256F, 0x000000, FontWeight.NORMAL, false);
			var tf : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x000000, 0x000000, FontWeight.NORMAL, false);
			//system
			var stf : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x9E256F, 0x000000, FontWeight.NORMAL, false);
			//jackpot
			var jptf : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x9E256F, 0x000000, FontWeight.NORMAL, false);
			//offline
			var otf : ChatActionFormat = new ChatActionFormat("_sans", 12, 0x666666, 0x000000, FontWeight.NORMAL, false);					
			
			var space_span:SpanElement = new SpanElement();
			space_span.text = " ";
			space_span.fontSize = tf.size;
			space_span.fontFamily = tf.font;
			
			var points_space_span : SpanElement = new SpanElement();
			points_space_span.text = ": ";
			points_space_span.color = tf.color;
			points_space_span.fontSize = tf.size;
			points_space_span.fontFamily = tf.font;
			
			var for_span:SpanElement = new SpanElement();
			for_span.text = " для ";            
			for_span.color = otf.color;
			for_span.fontSize = otf.size;			
			for_span.fontFamily = otf.font;			
			
			var warning_span:SpanElement = new SpanElement();
			warning_span.text = "Неверная команда: ";
			warning_span.fontSize = stf.size;
			warning_span.color = stf.color;			
			warning_span.fontFamily = stf.font;
			
			var present_span:SpanElement = new SpanElement();
			present_span.text = " подарил ";            
			present_span.color = ban.color;
			present_span.fontSize = ban.size;			
			present_span.fontFamily = ban.font;
				
			if (!ishistory){
				p = createParagraphTime(p);
			}
			
			if (m["type"] == MessageType.SYSTEM || m["type"] == MessageType.VICTORINA) {
				p = createParagraphMessage(p, m, stf);				
				return p;				
			}else if (m["type"] == MessageType.JACK_POT_WIN) {
				p = createParagraphMessage(p, m, jptf);				
				return p;				
			}else if(from == null){
				var m:Object = new Object();
				m["type"] = MessageType.SYSTEM;
				m["text"] = "Пользователь вышел из игры";				
				p = createParagraphMessage(p, m, stf);
				return p;
			}else if (m["type"] == MessageType.MESSAGE && to) {
				createNickIcon(p, from.popular);
				p.addChild(createHtmlNick(from, true, tf.bgc));
				p.addChild(createUserSprite(from));
				p.addChild(for_span);
				createNickIcon(p, to.popular);
				p.addChild(createHtmlNick(to, true, tf.bgc));
				p.addChild(createUserSprite(to));	
				p.addChild(points_space_span);
				p = createParagraphMessage(p, m, tf);													
				return p;					
			}else if(m.flags == MessageType.PRIVATE) {				
			}else if(m["type"] == MessageType.USEITEM) {
				createNickIcon(p, from.popular);
				p.addChild(createHtmlNick(from, true, tf.bgc));	
				p.addChild(createUserSprite(from));
				p.addChild(points_space_span);
				p = createParagraphMessage(p, m, ui);				
				return p;
			}else if(m["type"] == MessageType.BAN) {	
				var time:String;
				var t:int;				
				if (m["bantype"] == BanType.MINUT5){
					time = " на 5 минут";
					t = 5 * 60;
				}else if (m["bantype"] == BanType.MINUT15){
					time = " на 15 минут";
					t = 15 * 60;
				}else if (m["bantype"] == BanType.MINUT30){
					time = " на 30 минут";
					t = 30 * 60;
				}else if (m["bantype"] == BanType.HOUR1){
					time = " на 1 час";
					t = 60 * 60;
				}else if (m["bantype"] == BanType.DAY1){
					time = " на 1 день";
					t = 60 * 60 * 60;
				}
				if(to && to.id == GameApplication.app.userinfomanager.myuser.id && !ishistory){
					GameApplication.app.banmanager.setBanTime(t);
				}
				
				createNickIcon(p, from.popular);
				p.addChild(createHtmlNick(from, true, tf.bgc));
				p.addChild(createUserSprite(from));
				
				var ban1_span:SpanElement = new SpanElement();
				ban1_span.text = " отправил в бан ";            
				ban1_span.color = ban.color;
				ban1_span.fontSize = ban.size;			
				ban1_span.fontFamily = ban.font;
				p.addChild(ban1_span);
				
				var ban_uout:SpanElement = new SpanElement();
				ban_uout.text = "вышедшего из игры пользователя ";            
				ban_uout.color = ban.color;
				ban_uout.fontSize = ban.size;			
				ban_uout.fontFamily = ban.font;				
				
				if (to != null){
					createNickIcon(p, to.popular);
					p.addChild(createHtmlNick(to, true, tf.bgc));
					p.addChild(createUserSprite(to));
				}else{
					p.addChild(ban_uout);
				}
				
				var ban2_span:SpanElement = new SpanElement();
				ban2_span.text = time;            
				ban2_span.color = ban.color;
				ban2_span.fontSize = ban.size;			
				ban2_span.fontFamily = ban.font;
				p.addChild(ban2_span);	
				return p;
			}else if(m["type"] == MessageType.BAN_OUT) {
				createNickIcon(p, from.popular);
				p.addChild(createHtmlNick(from, true, tf.bgc));
				p.addChild(createUserSprite(from));
				
				var banout_span:SpanElement = new SpanElement();
				banout_span.text = " вышел из бана";            
				banout_span.color = ban.color;
				banout_span.fontSize = ban.size;			
				banout_span.fontFamily = ban.font;
				p.addChild(banout_span);
				return p;
			}else if(m["type"] == MessageType.PRESENT) {
				if(GameApplication.app.shopmanager.presentprototypesObj == null){
					GameApplication.app.shopmanager.getPresentPrototypes(null);
					return null;
				}
				
				createNickIcon(p, from.popular);
				p.addChild(createHtmlNick(from, true, tf.bgc));
				p.addChild(createUserSprite(from));
				
				var prototype:PresentPrototype = GameApplication.app.shopmanager.presentprototypesObj[m["prototypeid"]];
				
				if(to){
					p.addChild(present_span);
					createNickIcon(p, to.popular);
					p.addChild(createHtmlNick(to, true, tf.bgc));
					p.addChild(createUserSprite(to));
					p.addChild(space_span);					
					
					if(to.id == GameApplication.app.userinfomanager.myuser.id && !ishistory){						
						GameApplication.app.popuper.show(new PopUpPresentBuy(prototype, "Пользователь " + from.title + " преподнес вам подарок!"));
					}
				}else{
					var presentout_span:SpanElement = new SpanElement();
					presentout_span.text = " подарил вышедшему из игры пользователю ";            
					presentout_span.color = ban.color;
					presentout_span.fontSize = ban.size;			
					presentout_span.fontFamily = ban.font;
					p.addChild(presentout_span);
				}		
				
				if(AccessManager.checkAccessPresent(m["prototypeid"])){
					createPresentMessage(p, prototype.url);
				}else{
					var not_access_present_span:SpanElement = new SpanElement();
					not_access_present_span.text = "подарок!";            
					not_access_present_span.color = ban.color;
					not_access_present_span.fontSize = ban.size;			
					not_access_present_span.fontFamily = ban.font;
					p.addChild(not_access_present_span);
				}				
				return p;
			}
			
			createNickIcon(p, from.popular);
			p.addChild(createHtmlNick(from, true, tf.bgc));	
			p.addChild(createUserSprite(from));
			p.addChild(points_space_span);
			p = createParagraphMessage(p, m, tf);
			var inf : Object = {color:stf.color};
			p.linkHoverFormat = inf;			
			return p;
		}
		
		private function createParagraphTime(p : ParagraphElement) : ParagraphElement {
			var date : Date = new Date();
			var hours : String;
			var minutes : String;
			var seconds : String;				
			if (date.getHours() < 10) hours = "0" + date.getHours();
			else hours = "" + date.getHours();
			if (date.getMinutes() < 10) minutes = "0" + date.getMinutes();
			else minutes = "" + date.getMinutes();
			if (date.getSeconds() < 10) seconds = "0" + date.getSeconds();
			else seconds = "" + date.getSeconds();
			var time : SpanElement = new SpanElement();
			time.text = hours + ":" + minutes + ":" + seconds + " ";
			time.fontSize = 10;
			time.color = 0xA2A2A2;
			time.fontFamily = '_sans';				
			
			p.addChild(time);
			return p;
		}
		
		private function createUserSprite(user : User) : InlineGraphicElement {
			var sige : InlineGraphicElement;
			sige = new InlineGraphicElement();
			var _spr : UserSprite = new UserSprite();
			_spr.user = user;									
			sige.source = _spr;
			return sige;
		}
		
		private function createHtmlNick (user:User, active:Boolean = true, bgc:uint = 0x666666):FlowElement{
			if (user){
				var link:LinkElement = new LinkElement();
				var linkSpan:SpanElement = new SpanElement();
				linkSpan.fontSize = 12;
				linkSpan.fontFamily = '_sans';
				
				if (active && (user.id != GameApplication.app.userinfomanager.myuser.id)){										
					link.href = "event:" + user.id;					
					link.id = user.title;					
					
					var overf : Object = {color:0xD86B76};
					var normalf : Object = {color:0xCD3F44};
					
					if(user.king == 1){
						normalf = {color:0xff0000};
					}else if(user.role == UserRole.MODERATOR){
						normalf = {color:0x028AE3};
					}else if(user.role == UserRole.ADMINISTRATOR || user.role == UserRole.ADMINISTRATOR_MAIN){
						normalf = {color:0x003399};
					}
					
					link.linkHoverFormat = overf;
					link.linkNormalFormat = normalf;
					
					link.addEventListener(FlowElementMouseEvent.CLICK, onLinkPress, false, 0, true);
					
					linkSpan.text = user.title;
					linkSpan.textDecoration = 'none';            	           		
					link.addChild(linkSpan);            	            		
					
					return link;				
				}			
				linkSpan = new SpanElement();
				linkSpan.text = user.title;
				linkSpan.color = 0x666666;
				linkSpan.fontSize = 12;
				linkSpan.fontFamily = '_sans';						
				return linkSpan;
			}else{
				return null;
			}
		}
		
		private function onLinkPress(e:FlowElementMouseEvent):void{
			var grElem:InlineGraphicElement = (e.flowElement as FlowElement).getNextSibling() as InlineGraphicElement;
			if(grElem){
				var u:User = (grElem.source as UserSprite).user;
				GameApplication.app.actionShowerMenu.showMenu(new ActionMenuChatUser(u));
			}
		}
		
		private function createPresentMessage(p : ParagraphElement, url:String):ParagraphElement{			
			var fullUrl:String = GameApplication.app.config.swfDataAddress + "/" + url;
			var presentClass:Class = GameApplication.app.classloader.hashmap[url];
			if(presentClass){
				addPresentMovie(presentClass, p);
			}else{
				_linksToPicture[url] = p;
				GameApplication.app.classloader.load(fullUrl, null, onLoadPresentMovieClass, null);
				//если будут утечки памяти проверить этот кусок кода
				//сохраняем ссылки на параграфы, в которые нужно добавлять картинки после загрузки 
				//(сделано так, потому что нет картинок в данный момент, их нужно грузить)
				//после загрузки добавляем картинки, удаляем ссылки				
			}
			return p;
		}
		private function onLoadPresentMovieClass(movieClass:Class, className:String):void{
			var p:ParagraphElement = _linksToPicture[className];
			if (movieClass && p) addPresentMovie(movieClass, p);
			delete _linksToPicture[className];
		}
		private function addPresentMovie(movieClass:Class, p:ParagraphElement):void{
			var inlineGraphicElement : InlineGraphicElement = new InlineGraphicElement();
			inlineGraphicElement.source = new movieClass();	
			
			var needHeight:Number = 30;
			var k:Number;
			k = needHeight / inlineGraphicElement.source.height;
			inlineGraphicElement.source.height *= k;
			inlineGraphicElement.source.width *= k;
			
			p.addChild(inlineGraphicElement);
		}
		
		private function createNickIcon(p : ParagraphElement, popular:int):ParagraphElement{
			var inlineGraphicElement : InlineGraphicElement = new InlineGraphicElement();			
			var Icon : Class = GameApplication.app.userinfomanager.getPopularIconClass(popular);												 
			if (Icon) {					
				inlineGraphicElement = new InlineGraphicElement();
				inlineGraphicElement.source = new Icon();
				
				var needHeight:Number = 15;
				var k:Number;
				k = needHeight / inlineGraphicElement.source.height;
				inlineGraphicElement.source.height *= k;
				inlineGraphicElement.source.width *= k;
				
				var space_span:SpanElement = new SpanElement();
				space_span.text = " ";				
				
				p.addChild(inlineGraphicElement);
				p.addChild(space_span);
			}		
			return p;
		}
		
		private function createParagraphMessage(p : ParagraphElement, m : Object, tf : ChatActionFormat) : ParagraphElement {
			var inlineGraphicElement : InlineGraphicElement = new InlineGraphicElement();			
			var span : SpanElement = new SpanElement();
			var arrElems : Array = new Array();
			var ms : String = m["text"];			
			
			if (ms && ms.length > 0) {	
				arrElems = ms.split(ChatRegExp.seprator);
				for each (var e : String in arrElems) {
					var smile : Class = ChatSmiles.getSmile(e);
					var arrExec:Object = _urlFinder.exec(e);									 
					if (smile) {					
						inlineGraphicElement = new InlineGraphicElement();
						inlineGraphicElement.source = new smile();
						setSmoothing(inlineGraphicElement.source as MovieClip);
						p.addChild(inlineGraphicElement);
					}/*else if(arrExec) {						
						var linkElem:LinkElement = new LinkElement();
						var linkSpan:SpanElement = new SpanElement();
						linkSpan.fontSize = 12;
						linkSpan.color = 0x1C60DB;
						linkSpan.fontFamily = tf.font;
						linkElem.href = "http://" + (arrExec as Array)[2] + (arrExec as Array)[4];
						linkSpan.text = e;						
						
						linkSpan.textDecoration = 'none';
						linkElem.addChild(linkSpan);
						p.addChild(linkElem);
					}*/else if (e.length) {
						span = new SpanElement();
						span.text = e;                                   
						span.color = tf.color;						
						span.fontFamily = tf.font;
						span.fontWeight = tf.bold;
						p.addChild(span);
					}
				}
			}			
			return p;
		}
		
		private function setSmoothing(parent:MovieClip):void{
			for each(var obj:* in parent){
				if(obj is Bitmap){
					trace("BITMAP");
					(obj as Bitmap).smoothing = true;
				}
			}
		}
		
		public function getText() : TextFlow {						
			return _cache;
		}
		
		public function getUserByNick(nick : String) : User {
			for each (var user : User in _users) {
				if (user.title == nick) {
					return user;
				}
			}
			return null;
		}
		
		public function clear() : void {
			_messages = 0;
			_cache = new TextFlow();			
			for each (var user : User in _users) {
				dispatchEvent(new RoomEvent(RoomEvent.REMOVE_USER, user, this));
			}
			_users = new Object();			
		}
	}
}