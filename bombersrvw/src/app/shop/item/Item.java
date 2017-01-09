package app.shop.item;

public class Item {
	public int id;
	public int pid;
	public String title;
	public String description;
	public int param1;
	public int param2;
	public int param3;
	public int param4;
	public int param5;
	public int param6;
	public int param7;	
	public int price;
	public int maxquality;
	public int quality;
	public int onuser;
	public String url;
	public Boolean change;			//флаг для обновления на userUpdate
	
	public Item(int id, int pid, String title, String description, 
			int param1, int param2, int param3, int param4, int param5, int param6, int param7, 
			int price, int maxquality, int quality, int onuser, String url){
		this.id = id;
		this.pid = pid;
		this.title = title;
		this.description = description;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
		this.param5 = param5;
		this.param6 = param6;
		this.param7 = param7;
		this.price = price;
		this.maxquality = maxquality;
		this.quality = quality;
		this.onuser = onuser;
		this.url = url;
		
		this.change = false;
	}
}
