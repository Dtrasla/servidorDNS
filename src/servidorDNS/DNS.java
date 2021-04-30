package servidorDNS;

import java.net.SocketException;

public class DNS {

	public static void main(String[] args) {
		Servidor serv = new Servidor("",512);
		try {
			serv.servidor();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
