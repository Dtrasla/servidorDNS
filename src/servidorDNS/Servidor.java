package servidorDNS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Servidor {
	public static final int PUERTO = 53;
	String master;
	private int tam;
	
	public Servidor(String master, int tam) {
		this.master = master;
		this.tam = tam;
	}
	
	
	public void servidor() throws SocketException {
		
		byte[] entrada = new byte[tam];
		byte[] salida = new byte[tam];
		DatagramSocket socket = new DatagramSocket(Servidor.PUERTO);
		try {
			while(true) {
				DatagramPacket solicitud = new DatagramPacket(entrada,tam);
				socket.receive(solicitud);
				System.out.println(solicitud.getAddress().getHostAddress() + ": " + solicitud.getPort());
				System.out.println("tam : " + solicitud.getLength());
				
			}
		} catch(IOException e) {
			
		}
	}
}
