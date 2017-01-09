package app.utils.jsonutil;

public class JSONUtil {
	public static String getValueByName(String str, String field){
		if(str != null && str.length() > 2){
			String content = str.substring(1, str.length() - 2);			
			String[] arr3 = content.split(",");
			for(int i = 0; i < arr3.length; i++){				
				String[] arr4 = arr3[i].split(":");
				if(arr4 != null && arr4[0].indexOf(field) != -1){
					return new String(arr4[1]);
				}						
			}
		}
		return null;
	}
}
