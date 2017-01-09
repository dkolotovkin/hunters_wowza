package utils.chat.formats {

	/**
	 * @author dkolotovkin
	 */
	public class ChatSmiles {
		private static var _hash:Object;
		
		public static function init():void{
			_hash = new Object();
			_hash = new Object();
			_hash["*angel*"] = angel;			 
			_hash[":-)"] = smile; 
			_hash[":-("] = sad;  
			_hash["*yahoo*"] = yahoo;
			_hash[":-P"] = blum;
			_hash["8-)"] = dirol;
			_hash[":-D"] = bigsmile;
			
			_hash[":-["] = blush;
			_hash["*yes*"] = yes;
			_hash[":-*"] = kiss;
			_hash[":'("] = cray;
			_hash[":-X"] = secret;
			_hash["*good*"] = good;
			_hash["*fool*"] = fool;
			
			_hash["*bee*"] = bee;			 
			_hash["*mosking*"] = mosking;
			_hash["]:->"] = diablo;
			_hash["[:-}"] = music;
			_hash["*airkiss*"] = airkiss;
			_hash["*bad*"] = bad;
			_hash["*tired*"] = tired;
			
			_hash["*stop*"] = stop;
			_hash["*kissing*"] = mankiss;
			_hash["@}->--"] = rose;
			_hash[";D"] = acute;
			_hash["*drink*"] = drink;
			_hash["*stratch*"] = stratch;
			_hash["*dance*"] = dance;
			
			_hash["*help*"] = help;
			_hash["*dash*"] = dash;
			_hash["*wacko*"] = wacko;
			_hash["*ok*"] = ok;
			_hash["*mamba*"] = mamba;
			_hash["*sorry*"] = sorry;
			_hash["*clapping*"] = clapping;
			
			_hash["*rolf*"] = rolf;
			_hash["*pardon*"] = pardon;
			_hash["*no*"] = no;
			_hash["*crazy*"] = crazy;
			_hash["*dntknw*"] = dntknw;
		}
		
		public static function getSmile(str:String):Class{
			if (!_hash) init();			
			return _hash[str];
		}
	}
}
