package app.user.chage;

import app.user.User;

public class ChangeResult {
	public static byte OK = 1;
	public static byte NO_MONEY = 2;
	public static byte USER_EXIST = 3;
	public static byte UNDEFINED = 4;
	
	public byte errorCode = 0;
	public User user;
}
