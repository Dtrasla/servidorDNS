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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class Servidor {
	private int port;
	private int udpSize;
	private HashMap<String, ArrayList<Respuesta>> masterFile;

	public Servidor() {
		this.port = 53;
		this.udpSize = 512;
		this.masterFile = new HashMap<String, ArrayList<Respuesta>>();
		try {
			llenarMasterFile();
		} catch (Exception e) {
		}
	}

	public void llenarMasterFile() throws Exception {
		Scanner sc = new Scanner(new File("src/MasterFile.txt"));
		String linea, dom = "";
		InetAddress ip;
		byte[] nom;
		int ttl;
		short tipo, clase, tam = 4;
		while (sc.hasNextLine()) {
			linea = sc.nextLine();
			String[] tok = linea.split(" ");
			if (!tok[0].equalsIgnoreCase("$ORIGIN")) {
				ArrayList<Respuesta> ips = new ArrayList<>();
				nom = tok[0].getBytes();
				ttl = Integer.parseInt(tok[1]);
				tipo = 0x0001;
				clase = 0x0001;
				ip = InetAddress.getByName(tok[4]);
				//public Respuesta(short name, short type, short clase, int ttl, short rdlength, InetAddress address) {
				Respuesta resp = new Respuesta(ByteBuffer.wrap(nom).getShort(), tipo,clase, ttl, tam, ip);
				if (this.masterFile.containsKey(dom)) {
					this.masterFile.get(dom).add(resp);
				} else {
					ips.add(resp);
					this.masterFile.put(dom, ips);
				}
			} else
				dom = tok[1];
		}
		sc.close();
	}

	public static void main(String[] args) {
		System.out.println("Ejecutando Servidor DNS...");
		while (true) {
			Servidor sv = new Servidor();
			try {
				byte[] bufer = new byte[sv.udpSize];
				while (true) {
					DatagramSocket socket = new DatagramSocket(sv.port);
					DatagramPacket peticion = new DatagramPacket(bufer, bufer.length);
					socket.receive(peticion);
					socket.setSoTimeout(5000);
					System.out.print("\nPeticion desde el host: " + peticion.getAddress());
					System.out.println(" Puerto No: " + peticion.getPort());
					Mensajes control = new Mensajes(sv.getPort(), peticion.getAddress(), peticion,
							socket, sv.getMasterFile());
					control.start();
				}
			} catch (Exception e) {
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getUdpSize() {
		return udpSize;
	}

	public void setUdpSize(int udpSize) {
		this.udpSize = udpSize;
	}

	public HashMap<String, ArrayList<Respuesta>> getMasterFile() {
		return masterFile;
	}

	public void setMasterFile(HashMap<String, ArrayList<Respuesta>> masterFile) {
		this.masterFile = masterFile;
	}
}