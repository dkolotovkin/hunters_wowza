package app.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.ServerApplication;


public class MyLogger {
	public BufferedWriter writer;	
	public String className;
	File f;
	
	public MyLogger(String className){
		String fileName = "bomber_server_log.txt";
		this.className = className;
		
		f = new File("../logs/" + fileName);
		if (!f.exists()){
			
			try {
				f.createNewFile();
			} catch (Exception e) {	
				ServerApplication.application.getLoggerInstance().info("don't create log file: " + e.getMessage());				 
			}
		}
	}
	
	public void log(String msg){		
		try{
			Date d = new Date();
			Locale local = new Locale("ru","RU");
			DateFormat df = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd", local);
			df.setTimeZone(TimeZone.getTimeZone("EAT"));			
			String date = df.format(d);
			String classn = "[" + className + "]";
			
			writer = new BufferedWriter(new FileWriter(f.getPath(), true));
			if (f.length() > 0){
				writer.newLine();
			}else{
				writer.write("START LOG...");
				writer.newLine();
			}
			writer.write(date + "  " + classn + " [LOG] " + msg);			
			writer.close();
			
			System.out.println(date + "  " + classn + "  " + msg);
	    }
	    catch (IOException e){	    	
	    }
	}
	
	public void error(String msg){
		try{
			Date d = new Date();
			Locale local = new Locale("ru","RU");
			DateFormat df = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd", local);
			df.setTimeZone(TimeZone.getTimeZone("EAT"));			
			String date = df.format(d);
			String classn = "[" + className + "]";
			
			writer = new BufferedWriter(new FileWriter(f.getPath(), true));
			if (f.length() > 0){
				writer.newLine();
			}else{
				writer.write("START LOG...");
				writer.newLine();
			}
			writer.write(date + "  " + classn + " [ERR] " + msg);			
			writer.close();
			
			System.out.println(date + "  " + classn + "  " + msg);
	    }
	    catch (IOException e){	    	
	    }
	}
}
