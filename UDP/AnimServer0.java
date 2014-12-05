import java.io.*;
import java.net.*;

public class AnimServer0 {
	public static void main(String args[]){
		try{
			byte buf[] = new byte[160*120];
			int i;
			BufferedInputStream biStream;
			int port = 8000; // ポートは 8000
			// サーバソケットの作成
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Running...");

			while(true){
				Socket s = ss.accept();
				biStream=new BufferedInputStream(
						new FileInputStream("bane.raw"));
				DataOutputStream dos=new DataOutputStream(s.getOutputStream());

				for(i = 0; i <= 200; i++){ // "<="に注意
					biStream.read(buf, 0, 160*120);
					dos.write(buf, 0, 160*120);
					if (buf[0] < 0){
						break;
					}
				}
				s.close();
			}
		}
		catch(Exception e){
			System.out.println("Exception : " + e);
		}
	}
}
