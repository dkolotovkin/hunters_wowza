package utils.servers
{
	public class Server
	{	
		public var url:String;		
		public var name:String;
		public var usersonline:int;		
		
		public function Server(u:String, n:String){
			this.url = u;			
			this.name = n;
			this.usersonline = int.MAX_VALUE;
		}
	}
}