package servidorDNS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class DNS {

	
private HashMap<String, ArrayList<Respuesta> > cache;
ArrayList<Respuesta> rrs;
private byte[] entrada;
private InetAddress dnsExterno;


private short id;
private short flags;
private short question;
private short answer; //Java no tiene unsigned
private short authority;
private short additional;
private String direccion;
private short type;
private short clase;

private int cantidadRrs;

public DNS(HashMap<String, ArrayList<Respuesta>> cache, byte[] entrada) {
	this.cache = cache;
	this.entrada = entrada;
	try {
		this.dnsExterno = InetAddress.getByName("8.8.8.8");
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	
	DataInputStream s = new DataInputStream(new ByteArrayInputStream(entrada));
	
	try {
		this.id = s.readShort();
		this.flags = s.readShort();
		this.question= s.readShort();
		this.answer = s.readShort();
		this.authority= s.readShort();
		this.additional= s.readShort();
		
		byte[] q;
		int l;
		while ((l = s.readByte()) > 0) {
            q = new byte[l];
            for (int i = 0; i < l; i++) {
                q[i] += s.readByte();
            }
            direccion += new String(q, "UTF-8");
            if (new String(q, "UTF-8").equals("com") || new String(q, "UTF-8").equals("co") || new String(q, "UTF-8").equals("org")) {
            } else {
                direccion += ".";
            }
        }
		this.rrs = new ArrayList<>();
		
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	
}
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
	
	
	
	
	public String getDireccion() {
	return this.direccion;
}




public void setDireccion(String direccion) {
	this.direccion = direccion;
}




	//Lee la respuesta de un dns externo y la pasa a los RRs
	public void leerRR(byte[] respuesta) {	
		short ancount;
		short qdcount;
		
		//HEADER -> ID 16 
		//QUESTION
		//ANSWER
		//AUTHORITY
		//ADDITIONAL
		
		
		try {

			DataInputStream lectura = new DataInputStream(new ByteArrayInputStream(respuesta));

			lectura.readShort(); //ID
			lectura.readShort(); //FLAGS
			qdcount = lectura.readShort(); //QDCOUNT
			ancount = lectura.readShort(); //ANCOUNT
			lectura.readShort(); //NSCOUNT
			lectura.readShort(); //ARCOUNT
			
			
			for(int i = 0; i< (int)qdcount; i++) {
				lectura.readShort();
				lectura.readShort();
				lectura.readShort();
			}



			for (int i = 0; i < (int)ancount; i++) {
				short name = lectura.readShort();
				short type = lectura.readShort();
				short clase = lectura.readShort();
				int ttl = lectura.readInt();
				short rdlength = lectura.readShort();
				//short rdata = lectura.readShort();
				String direccion = "";
		
				


				for (int j = 0; j < rdlength; j++ ) {
					if(j<3) {
						direccion += (lectura.readByte() & 0xFF) + ".";	
					}else {
						direccion += (lectura.readByte() & 0xFF);
					}
				}
				Respuesta rr = new Respuesta(name, type, clase, ttl, rdlength, InetAddress.getByName(direccion));
				this.rrs.add(rr);
				this.cantidadRrs = (int)ancount;
			}

		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public byte[] realizarConsultaExterna(byte[] pack) {

		try {

			DatagramSocket socket = new DatagramSocket();
			byte[] bytesRespuesta = new byte[1024];

			//Envio a servidor externo
			DatagramPacket packemisor = new DatagramPacket(pack, pack.length, this.dnsExterno, 53);
			socket.send(packemisor);

			//Recibo de servidor externo osea recibe respuesta de servidor DNS
			DatagramPacket respuesta = new DatagramPacket(bytesRespuesta, bytesRespuesta.length);
			socket.receive(respuesta);
			socket.close();
			leerRR(respuesta.getData());
			return generarRespuestaExterna();

		} catch (IOException e) {
			System.out.println("consulta externa erronea");
			return null;
		}
	}
	
	
	public byte[] realizarConsultaInterna(byte[] paquete) {

		byte[] respuesta = generarRespuestaInterna();
		leerRR(respuesta);
		return respuesta;
	}
	
public byte[] generarRespuestaInterna() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);
		String[] nombreDom = this.direccion.split("\\.");
		try {
			data.writeShort(this.id);
			data.writeShort(0x8180);
			data.writeShort(this.question);
			data.writeShort(cache.get(this.direccion).size());
			data.writeShort(this.authority);
			data.writeShort(this.additional);

			//Formato Query 
			for(int i = 0; i < nombreDom.length; i++) {
				byte[] bytesDom = nombreDom[i].getBytes();
				data.writeByte(bytesDom.length);
				data.write(bytesDom);
			}

			data.writeByte(0x00);
			data.writeShort(0x0001);
			data.writeShort(0x0001);

			//Formato answers
			if(cache.containsKey(this.direccion)) {
				ArrayList<Respuesta> rec = cache.get(this.direccion);
				for(Respuesta actual: rec) {
					data.write(actual.devolverStream());
				}
			}

			return out.toByteArray();

		} catch (IOException e) {
			System.out.println("Error generando la consulta interna.");
			return null;
		}
	}
	
public byte[] generarRespuestaExterna() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);
		String[] nombreDom = this.direccion.split("\\.");
		try {
			data.writeShort(this.id);
			data.writeShort(0x8180);
			data.writeShort(question);
			data.writeShort(cantidadRrs);
			data.writeShort(authority);
			data.writeShort(additional);

			//Formato Query 
			for(int i = 0; i < nombreDom.length; i++) {
				byte[] bytesDom = nombreDom[i].getBytes();
				data.writeByte(bytesDom.length);
				data.write(bytesDom);
			}

			data.writeByte(0x00);
			data.writeShort(0x0001);
			data.writeShort(0x0001);

			//Formato respuestas
			for (Respuesta r: rrs) {
				data.write(r.devolverStream());
			}
			return out.toByteArray();

		} catch (IOException e) {
			System.out.println("Error generando el paquete externo.");
			return null;
		}
	}
	
	
	
	
}
