import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReceiver {
	public static void main(String[] args) throws Exception {
		// ����ָ���Ķ˿�
		int port = 9050;
		ServerSocket server = new ServerSocket(port);

		// server��һֱ�ȴ����ӵĵ���
		System.out.println("server��һֱ�ȴ����ӵĵ���");
		while (true) {
			 //�ȴ�client������
            System.out.println("waiting...");
            Socket socket = server.accept();
            // ���տͻ��˵�����
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String string = in.readUTF();
            System.out.println("client:" + string);
            // ���͸��ͻ�������
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("hi,i am hserver!i say:" + string);
            socket.close();
		}
	}
}