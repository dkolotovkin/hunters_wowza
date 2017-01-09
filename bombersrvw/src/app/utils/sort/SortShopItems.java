package app.utils.sort;

import java.util.Comparator;

import app.shop.itemprototype.ItemPrototype;

public class SortShopItems implements Comparator<ItemPrototype> {
	public int compare(ItemPrototype item1, ItemPrototype item2){
		if(item1.price > item2.price){
			return 1;
		}else if(item1.price < item2.price){
			return -1;
		}
		return 0;
	}
}
