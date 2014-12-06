import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class AnimUDPClient1 {
	public static void main(String[] args){
		AppFrame3 f = new AppFrame3(args[0]);
		f.setSize(640,480);
		f.addWindowListener(new WindowAdapter(){
				@Override public void windowClosing(WindowEvent e){
				System.exit(0);
				}});
		f.setVisible(true);
	}
}

class AppFrame3 extends Frame {
	String hostname;
	ImageSocket2 imgsock = null;
	int count = 0;
	AppFrame3(String hostname){
		this.hostname = hostname;
	}
	@Override public void update(Graphics g){
		paint(g);
	}
	@Override public void paint(Graphics g){
		if(imgsock != null){
			Image img = imgsock.loadNextFrame();
			if(img != null){
				g.drawImage(img, 10, 50, 480, 360, this);
				count = count +1;
			} else {
				System.out.println(count + "枚");
			}
		} else {
			imgsock = new ImageSocket2(hostname);
		}
		repaint(1);
	}
}

class ImageSocket2 {
	DatagramSocket socket; // 通信用ソケット
	InetAddress serverAddress; // サーバアドレス保管
	BufferedImage bImage; // イメージ
	byte buf[]; // バッファ
	int port = 8000;
	DatagramPacket receivePacket; // 受信データ
	boolean fin = false; /**********/ // 終了フラグ
	byte ack[] = "Ack".getBytes();
	DatagramPacket ackPacket;
	ImageSocket2(String hostname){
		buf = new byte[160*120];
		bImage = new BufferedImage(160, 120, BufferedImage.TYPE_BYTE_GRAY);
		byte request[] = "REQUEST".getBytes();
		try {
			socket = new DatagramSocket(); // ソケットの作成
			// 送信データ用 DatagramPacket の作成
			serverAddress = InetAddress.getByName(hostname);
			DatagramPacket sendPacket = new DatagramPacket(request,
					request.length, serverAddress, port);
			// 受信データ用 DatagramPacket の作成
			receivePacket = new DatagramPacket(buf, 160*120);

			socket.setSoTimeout(3000); // タイムアウトの設定 (3 秒)
			socket.send(sendPacket); // REQUEST の送信
			socket.receive(receivePacket); // 応答の受信
			receivePacket.setLength(160*120); // 受信可能サイズの再設定
			ackPacket=new DatagramPacket(ack,ack.length,serverAddress,port);
		}
		catch(IOException e){
			System.out.println("Exception : " + e);
		}
	}

	Image loadNextFrame(){
		if(fin) return null; /**********/
		try {
			socket.receive(receivePacket); // 画像データの受信
			int x,y,pixel;
			for(y = 0; y < 120; y++){
				for(x = 0; x < 160; x++){
					pixel = (int)buf[y * 160 + x] * 2;
					if(pixel < 0) {
						socket.send(ackPacket);
						socket.close();
						System.out.println("Done.");
						fin = true; /**********/
						return null;
					}
					pixel = new Color(pixel,pixel,pixel).getRGB();
					bImage.setRGB(x, y, pixel);
				}
			}
			socket.send(ackPacket);
		}
		catch(Exception e){
			System.err.println("Exception2 : " + e);
		}
		return bImage;
	}
}
