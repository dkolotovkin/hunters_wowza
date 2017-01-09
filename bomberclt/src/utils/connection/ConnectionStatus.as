package utils.connection
{
	public class ConnectionStatus
	{
		public static var REJECT:String = "NetConnection.Connect.Rejected"; 		//отклонено сервером
		public static var CLOSED:String = "NetConnection.Connect.Closed"; 			//успешно разорвано
		public static var FAILED:String = "NetConnection.Connect.Failed"; 			//подключение не удалось
		public static var SUCCESS:String = "NetConnection.Connect.Success"; 		//успешное подключение
		public static var INVALID:String = "NetConnection.Connect.InvalidApp"; 		//при подключении указано не допустимое имя приложения
		
		public function ConnectionStatus()
		{
		}
	}
}