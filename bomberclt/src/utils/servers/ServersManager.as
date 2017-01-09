package utils.servers
{
	import application.GameApplication;
	import application.GameMode;
	
	import flash.events.NetStatusEvent;
	import flash.net.NetConnection;
	import flash.net.ObjectEncoding;
	import flash.net.Responder;
	
	import mx.controls.Alert;
	
	import utils.connection.ConnectionStatus;

	public class ServersManager
	{
		public static var MIN_USERS:uint = 0;
		public static var MAX_USERS:uint = int.MAX_VALUE;
		
		public var connection:NetConnection;		
		public var servers:Vector.<Server> = new Vector.<Server>();
		public var currentServerIndex:uint;		
		[Bindable]
		public var currentServerName:String;
		
		public function ServersManager(){
			//первый локальный сервер
			servers.push(new Server("rtmp://localhost:1935/bombersrvw", "Город-Test"));
			servers.push(new Server("rtmp://37.200.71.155:1935/bombersrvw", "Город-A"));
			servers.push(new Server("rtmp://37.200.71.155:1935/bombersrvw", "Город-B"));
//			servers.push(new Server("rtmp://31.186.103.117:1935/bombersrvw", "Город-A"));
//			servers.push(new Server("rtmp://31.186.103.117:1935/bombersrvw", "Город-B"));
		}
		
		public function checkServers():void{			
			if(!GameApplication.app.contains(GameApplication.app.connectPreLoader)) GameApplication.app.addElement(GameApplication.app.connectPreLoader);
			
			if(GameApplication.app.config.mode == GameMode.DEBUG){
				currentServerIndex = 0;
				GameApplication.app.connect();
				return;
			}
			currentServerIndex = 1;
			checkNextServer();			
		}
		
		private function checkNextServer():void{			
			if(connection != null && connection.connected){
				connection.close();
				connection = null;
			}
			if(currentServerIndex < servers.length){
				try{
					connection = new NetConnection();				
					connection.objectEncoding = ObjectEncoding.AMF3;
					connection.addEventListener(NetStatusEvent.NET_STATUS, onnetStatus, false, 0, true);
				}catch(e:*){
					currentServerIndex++;
					checkNextServer();
				}				
				connection.connect(servers[currentServerIndex].url);
			}else{
				var minUsers:int = int.MAX_VALUE;
				currentServerIndex = 1;
				for(var i:uint = currentServerIndex; i < servers.length; i++){
					if(servers[i].usersonline < minUsers && servers[i].usersonline >= MIN_USERS && servers[i].usersonline <= MAX_USERS){
						minUsers = servers[i].usersonline;
						currentServerIndex = i;
					}
				}				
				GameApplication.app.connect();
			}
		}
		
		private function onnetStatus ( event:NetStatusEvent ):void{			
			connection.removeEventListener(NetStatusEvent.NET_STATUS, onnetStatus);
			if(event.info.code == ConnectionStatus.SUCCESS){
				connection.call("getOnlineUsersCount", new Responder(onGetOnlineUsersCount, onGetOnlineUsersCountError));
			}else{
				currentServerIndex++;
				checkNextServer();
			}
		}
		
		private function onGetOnlineUsersCount(countusers:uint):void{			
			servers[currentServerIndex].usersonline = countusers;
			
			currentServerIndex++;
			checkNextServer();
		}
		private function onGetOnlineUsersCountError(e:Object):void{
			currentServerIndex++;
			checkNextServer();
		}
	}
}