/*
4. MESSAGES

4.1. Format

All communications inside of the domain protocol are carried in a single
format called a message.  The top level format of message is divided
into 5 sections (some of which are empty in certain cases) shown below:

    +---------------------+
    |        Header       |
    +---------------------+
    |       Question      | the question for the name server
    +---------------------+
    |        Answer       | RRs answering the question
    +---------------------+
    |      Authority      | RRs pointing toward an authority
    +---------------------+
    |      Additional     | RRs holding additional information
    +---------------------+
 */


package servidorDNS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class Servidor {
	public static final int PUERTO = 53;
	private String direccionMaster;
	private HashMap<String, ArrayList<Respuesta> > cache;
	private int tam;
	
	public Servidor(String master, int tam) {
		this.direccionMaster = master;
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
				
				
				//falta crear respuestas
			}
		} catch(IOException e) {
			
		}
		
		socket.close();
	}
}
