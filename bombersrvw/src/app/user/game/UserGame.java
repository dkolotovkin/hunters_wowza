package app.user.game;

import java.util.ArrayList;
import java.util.List;

public class UserGame {
	public int id;													//id пользователя
	public int seatIndex;											//индекс места появления
	public List<Integer> pids = new ArrayList<Integer>();			//список id прототипов одетых вещей
}
