package app.shop.item;

public class ItemPresent{
	public int id;
	public int iduser;
	public String presenter;
	public String title;	
	public int price;
	public int showed;
	public String url;
	
	public ItemPresent(int id, int iduser, String presenter, String title, int price, int showed, String url){
		this.id = id;
		this.iduser = iduser;
		this.presenter = presenter;
		this.title = title;		
		this.price = price;
		this.showed = showed;
		this.url = url;
	}
}
