package application.gamecontainer.chat.actionmenu.title
{
	import spark.components.Label;
	import spark.components.supportClasses.SkinnableComponent;
	
	public class ArticleTitle extends TargetTitle
	{
		public function ArticleTitle() {
			setStyle("skinClass", TargetTitleSkin);
		}
		
		override public function setTitle(title:String, isPers:Boolean, level:uint = 0):void{			
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
			}	
		}
	}
}