import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * 使用socket发送数据
 */
public class SocketSender {
	
	private static JsonParser parser=new JsonParser();
	private static JsonArray array;
	
	public static void ParseInputData(){
		try{
			//JsonObject object= parser.parse(new FileReader("test_data_0.json"));

			array= (JsonArray) parser.parse(new FileReader("test_data_0.json"));
			/*for (int i=0;i<array.size() && i<2;i++){
				JsonObject subObject= array.get(i).getAsJsonObject();
				System.out.println(subObject);
				
			}*/
			
		}catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
	}
	
	
	
    public static void main(String[] args) {
        try {
            ParseInputData();
            
            int i=0;
            int size=array.size();
            while (true) {
                try {
                    Socket socket = new Socket("127.0.0.1", 9050);

                    String data="";
                    
                    for (int j=0;j<4 && j<size ;j++){
                    	JsonObject subObject= array.get(i).getAsJsonObject();
                    	data+=subObject;
                    	i++;
                    }
                    
                    System.out.println(data);
                    // 发送给服务器的数据
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(data);
                    // 接收服务器的返回数据
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    System.out.println("server:" + in.readUTF());
                    socket.close();
                    
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}