package servidorDNS;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Mensajes extends Thread{

	private int puerto;
	private DatagramPacket peticion;
	private InetAddress cliente;
	private HashMap<String, ArrayList<Respuesta>> masterFile;
	private DatagramSocket socket;

	public Mensajes(int puerto, InetAddress cliente, DatagramPacket peticion, DatagramSocket UDP,
			HashMap<String, ArrayList<Respuesta>> masterFile) {
		this.peticion = peticion;
		this.puerto = puerto;
		this.cliente = cliente;
		this.socket = UDP;
		this.masterFile = masterFile;
	}

	public void run() {
		byte[] resp = new byte[1024];
		DNS query = new DNS(peticion.getData());
		if (this.masterFile.containsKey(query.getDominio())) {
			resp = query.consultaI(peticion.getData(), this.masterFile);
			System.out.println("Respuesta encontrada en el cache");
		} else {
			resp = query.consultaE(peticion.getData(), this.masterFile);
			System.out.println("Respuesta encontrada con resolver externo");
		}
		DatagramPacket paquete = new DatagramPacket(resp, resp.length, this.cliente, this.puerto);
		try {
			socket.send(paquete);
			this.socket.close();
		} catch (Exception e) {
			System.out.println("Se esta enviando...");
		}
	}
}
