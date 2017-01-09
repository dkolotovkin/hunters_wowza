package app.utils.sort;

import java.util.Comparator;

import app.shop.presentprototype.PresentPrototype;

public class SortPresents implements Comparator<PresentPrototype> {
	public int compare(PresentPrototype present1, PresentPrototype present2){
		if(present1.price > present2.price){
			return 1;
		}else if(present1.price < present2.price){
			return -1;
		}
		return 0;
	}
}