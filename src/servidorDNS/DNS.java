package servidorDNS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DNS {

	

/*
 * All communications inside of the domain protocol are carried in a single
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
    
    
    4.1.1. Header section format
The header contains the following fields:
                                    1  1  1  1  1  1
      0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      ID                       |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    QDCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    ANCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    NSCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    ARCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    
    4.1.2. Question section format
The question section is used to carry the "question" in most queries,
i.e., the parameters that define what is being asked.  The section
contains QDCOUNT (usually 1) entries, each of the following format:
                                    1  1  1  1  1  1
      0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                                               |
    /                     QNAME                     /
    /                                               /
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                     QTYPE                     |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                     QCLASS                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    
    4.1.3. Resource record format
The answer, authority, and additional sections all share the same
format: a variable number of resource records, where the number of
records is specified in the corresponding count field in the header.
Each resource record has the following format:
                                    1  1  1  1  1  1
      0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                                               |
    /                                               /
    /                      NAME                     /
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      TYPE                     |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                     CLASS                     |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      TTL                      |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                   RDLENGTH                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
    /                     RDATA                     /
    /                                               /
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * */
	
	
	
	
private int puerto;
private Inet4Address ip;
private short id;
private short flags;
private short qdcount;
private short ancount;
private short nscount;
private short arcount;
private byte[] query;
private String dominio = "";
private short type;
private short clase;
private List<Respuesta> resp = new ArrayList<Respuesta>();

public DNS(byte[] paquete) {
	try {
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(paquete));
		this.id = din.readShort();
		this.flags = din.readShort();
		this.qdcount = din.readShort();
		this.ancount = din.readShort();
		this.nscount = din.readShort();
		this.arcount = din.readShort();
		int tamR = 0;
		while ((tamR = din.readByte()) > 0) {
			this.query = new byte[tamR];
			for (int i = 0; i < tamR; i++) {
				query[i] += din.readByte();
			}
			String c = new String(query, "UTF-8");
			dominio += c;
			if (!c.equals("com") && !c.equals("co") && !c.equals("org")){
				dominio += ".";
			}
		}
		System.out.println("Dominio: " + dominio);
		System.out.println("----------------------------------");
		this.type = din.readShort();
		this.clase = din.readShort();
		this.puerto = 53;
		try {
			this.ip = (Inet4Address) Inet4Address.getByName("8.8.8.8");
		} catch (UnknownHostException e) {
			
		}
	} catch (IOException e) {
		System.out.println("Error generando el Query.");
	}
}

public String getDominio() {
	return dominio;
}

public void setDominio(String dominio) {
	this.dominio = dominio;
}

public byte[] consultaI(byte[] paquete, HashMap<String, ArrayList<Respuesta>> masterF) {
	byte[] respuesta = respuestaI(masterF);
	ArrayList<Respuesta> nuevos = obtRegistros(respuesta);
	if(nuevos != null)
		masterF.put(dominio, nuevos);
	return respuesta;
}

public byte[] consultaE(byte[] paquete, HashMap<String,ArrayList<Respuesta>> masterFile) {
	try {
		DatagramSocket socket = new DatagramSocket();
		byte[] bytesRespuesta = new byte[1024];
		DatagramPacket paqueteEnvio = new DatagramPacket(paquete, paquete.length, this.ip, this.puerto);
		socket.send(paqueteEnvio);
		DatagramPacket paqueteRespuesta = new DatagramPacket(bytesRespuesta, bytesRespuesta.length);
		socket.receive(paqueteRespuesta);
		socket.close();
		ArrayList<Respuesta> nuevos = obtRegistros(paqueteRespuesta.getData());
		if(nuevos != null)
			masterFile.put(dominio, nuevos);
		return respuestaE();
	} catch (IOException e) {
		System.out.println("Error generando la consulta externa.");
		return null;
	}
}

public ArrayList<Respuesta> obtRegistros(byte[] respuestaPaq) {
	int respuestas;
	short dom;
	short tipo;
	short clase;
	int ttl;
	short tam;
	boolean existe = false;
	String dir = "";
	ArrayList<Respuesta> nuevos = null;
	try {
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(respuestaPaq));
		din.readShort();
		din.readShort();
		din.readShort();
		respuestas = din.readShort();
		din.readShort();
		din.readShort();
		int tamR = 0;
		while ((tamR = din.readByte()) > 0) {
			for (int i = 0; i < tamR; i++) 
				din.readByte();
		}
		din.readShort();
		din.readShort();
		try {
			String linea;
			Scanner sc = new Scanner(new File("src/MasterFile.txt"));
			while(sc.hasNext() && !existe) {
				linea = sc.nextLine();
				if(linea.contains(dominio))
					existe = true;
			}
			sc.close();
		}catch(Exception ex) {}
		FileWriter fw = new FileWriter("src/MasterFile.txt",true);
		if(!existe) {
			fw.write("\n$ORIGIN " + dominio);
			nuevos = new ArrayList<>();
		}
		for (int j = 0; j < respuestas; j++) {
			dir = "";
			dom = din.readShort();
			tipo = din.readShort();
			clase = din.readShort();
			ttl = din.readInt();
			tam = din.readShort();
			System.out.print("\tIP: ");
			for (int i = 0; i < tam - 1; i++)
					dir += (din.readByte() & 0xFF) + ".";
			dir += (din.readByte() & 0xFF);
			System.out.println(dir);
			//public Respuesta(short name, short type, short clase, int ttl, short rdlength, InetAddress address) {
			Respuesta nueva = new Respuesta(dom,tipo,clase, ttl, tam,ip);
			if(!existe) {
				fw.write("\n" + dominio + " " + ttl + " IN A " + dir);
				nuevos.add(nueva);
			}
			resp.add(nueva);
			System.out.println("----------------------------------");
		}
		fw.close();
	} catch (IOException e) {
		System.out.println("Error imprimiendo los registros.");
	}
	return nuevos;
}

public byte[] respuestaI(HashMap<String, ArrayList<Respuesta>> masterFile) {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	DataOutputStream dos = new DataOutputStream(bytes);
	String[] dom = this.dominio.split("\\.");
	try {
		dos.writeShort(id);
		dos.writeShort(0x8180);
		dos.writeShort(qdcount);
		dos.writeShort(masterFile.get(this.dominio).size());
		dos.writeShort(nscount);
		dos.writeShort(arcount);
		for (int i = 0; i < dom.length; i++) {
			byte[] bytesDom = dom[i].getBytes();
			dos.writeByte(bytesDom.length);
			dos.write(bytesDom);
		}
		dos.writeByte(0x00);
		dos.writeShort(0x0001);
		dos.writeShort(0x0001);
		if (masterFile.containsKey(this.dominio)) {
			ArrayList<Respuesta> rec = masterFile.get(this.dominio);
			for (Respuesta actual : rec) {
				dos.write(actual.devolverStream());
			}
		}
		return bytes.toByteArray();
	} catch (IOException e) {
		System.out.println("Error generando la consulta interna.");
		return null;
	}
}

public byte[] respuestaE() {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	DataOutputStream dos = new DataOutputStream(out);
	String[] nombreDom = this.dominio.split("\\.");
	try {
		dos.writeShort(id);
		dos.writeShort(0x8180);
		dos.writeShort(qdcount);
		dos.writeShort(resp.size());
		dos.writeShort(nscount);
		dos.writeShort(arcount);
		for (int i = 0; i < nombreDom.length; i++) {
			byte[] bytesDom = nombreDom[i].getBytes();
			dos.writeByte(bytesDom.length);
			dos.write(bytesDom);
		}
		dos.writeByte(0x00);
		dos.writeShort(0x0001);
		dos.writeShort(0x0001);
		for (Respuesta r : resp) {
			dos.write(r.devolverStream());
		}
		return out.toByteArray();
	} catch (IOException e) {
		System.out.println("Error generando el paquete de bytes.");
		return null;
	}
}
	
}