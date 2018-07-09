import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReceiver {
	public static void main(String[] args) throws Exception {
		// 监听指定的端口
		int port = 9050;
		ServerSocket server = new ServerSocket(port);

		// server将一直等待连接的到来
		System.out.println("server将一直等待连接的到来");
		while (true) {
			 //等待client的请求
            System.out.println("waiting...");
            Socket socket = server.accept();
            // 接收客户端的数据
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String string = in.readUTF();
            System.out.println("client:" + string);
            // 发送给客户端数据
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("hi,i am hserver!i say:" + string);
            socket.close();
		}
	}
}