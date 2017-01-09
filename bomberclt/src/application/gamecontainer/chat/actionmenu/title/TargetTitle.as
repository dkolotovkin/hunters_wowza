package application.gamecontainer.chat.actionmenu.title {

	import mx.utils.StringUtil;
	
	import spark.components.Label;
	import spark.components.RichText;
	import spark.components.supportClasses.SkinnableComponent;
	import spark.utils.TextFlowUtil;

	/**
	 * @author dkolotovkin
	 */
	
	public class TargetTitle extends SkinnableComponent {	
		
		public function TargetTitle() {
			setStyle("skinClass", TargetTitleSkin);
		}
		
		public function setTitle(title:String, isPers:Boolean, level:uint = 0):void{			
			var _title:String;
			var ind:int = title.indexOf("\"");
			if (ind == -1) ind = title.indexOf("«");
			if (ind > 0){				
				if (title.charAt(ind - 1) == " "){
					_title = title.slice(0, ind - 1) + "\n" + title.slice(ind, title.length);
				}else{
					_title = title.slice(0, ind) + "\n" + title.slice(ind, title.length);
				}
			}else _title = title;

			if (skin){		
				(skin["title"] as Label).text = title;
				(skin["level"] as Label).text = "[" + String(level) + "]";
			}	
		}
	}
}