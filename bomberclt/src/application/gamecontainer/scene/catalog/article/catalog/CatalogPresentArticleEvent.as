package application.gamecontainer.scene.catalog.article.catalog
{
	import flash.events.Event;
	
	public class CatalogPresentArticleEvent extends Event
	{
		public static const SELECTED:String = "selected";
		public static const UNSELECTED:String = "unselected";
		
		public var article:CatalogPresentArticle;
		
		public function CatalogPresentArticleEvent(type : String, article:CatalogPresentArticle){
			super(type, false, false);
			this.article = article;
		}
		
		override public function clone() : Event {
			return new CatalogPresentArticleEvent(type, article);
		}
	}
}