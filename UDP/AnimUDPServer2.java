import java.io.*;
import java.net.*;

public class AnimUDPServer1 {
	public static void main(String args[]){
		try{
			BufferedInputStream biStream;
			int port = 8000; // ポートは 8000
			InetAddress clientAddress; // クライアントの IP アドレス
			int clientPort; // クライアントのポート番号
			int sleeptime =  Integer.parseInt(args[0]);

			// 送信用 DatagramPacket
			byte buf[] = new byte[160*120];
			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

			// 受信用 DatagramPacket
			byte req[] = new byte[32];
			DatagramPacket receivePacket = new DatagramPacket(req,req.length);

			// ソケットの作成 (Port 8000)
			DatagramSocket socket = new DatagramSocket(port);
			System.out.println("Running...");

			while(true){
				socket.receive(receivePacket); // Request の受信
				clientAddress = receivePacket.getAddress();
				clientPort = receivePacket.getPort();
				socket.send(receivePacket); // Echo back（Ack）

				// sendPacket の IPaddress，PortNo，データ長設定
				sendPacket.setAddress(clientAddress);
				sendPacket.setPort(clientPort);
				sendPacket.setLength(160*120);

				biStream = new BufferedInputStream(new FileInputStream("bane.raw"));

				for(int i = 0; i <= 200; i++){ // "<="に注意
					biStream.read(buf, 0, 160*120); // ファイルから読み込み
					socket.send(sendPacket); // クライアントに送信
					socket.receive(receivePacket);

				}
				biStream.close();
			}
			// socket.close();
		}
		catch(Exception e){
			System.out.println("Exception : " + e);
		}
	}
}
