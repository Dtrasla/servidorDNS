package servidorDNS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DNS {

	
    private int puerto;
    private String dnsExterno;
    private Inet4Address ip;
    private short TID;
    private short flags;
    private short question;
    private short answer;
    private short authority;
    private short aditional;
    private byte[] query;
    private String dominio = "";
    private short type;
    private short clase;
    private Set<Respuesta> records = new HashSet<Respuesta>();

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
    public DNS(byte[] paquete) {
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(paquete));
            this.TID = din.readShort();
            this.flags = din.readShort();
            this.question = din.readShort();
            this.answer = din.readShort();
            this.authority = din.readShort();
            this.aditional = din.readShort();
            int recLen = 0;
            //Obtener el dominio
            while ((recLen = din.readByte()) > 0) {
                this.query = new byte[recLen];
                for (int i = 0; i < recLen; i++) {
                    query[i] += din.readByte();
                }
                dominio += new String(query, "UTF-8");
                if (new String(query, "UTF-8").equals("com") || new String(query, "UTF-8").equals("co") || new String(query, "UTF-8").equals("org")) {
                } else {
                    dominio += ".";
                }
            }
            System.out.println("++++++++++++DOMINIO: " + dominio);
            this.type = din.readShort();
            this.clase = din.readShort();
            this.puerto = 53;
            this.dnsExterno = "8.8.8.8";
            try {
                this.ip = (Inet4Address) Inet4Address.getByName(this.dnsExterno);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("ERROR GENERANDO QUERY");
        }
    }
	
	
	public String getDominio() {
	return this.dominio;
}




public void setDireccion(String direccion) {
	this.dominio = direccion;
}




	//Lee la respuesta de un dns externo y la pasa a los RRs
	public void leerRR(byte[] respuesta) {	
        int respuestas;
        short dom;
        short tip;
        short aClase;
        int ttl;
        short addrLen;
        String Address = "";
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(respuesta));

            String.format("%x", din.readShort());
            String.format("%x", din.readShort());
            String.format("%x", din.readShort());
            respuestas = din.readShort();
            String.format("%x", din.readShort());
            String.format("%x", din.readShort());
            int recLen = 0;
            while ((recLen = din.readByte()) > 0) {
                byte[] record = new byte[recLen];
                for (int i = 0; i < recLen; i++) {
                    record[i] = din.readByte();
                }
            }

            din.readShort();
            din.readShort();

            for (int j = 0; j < respuestas; j++) {
                Address = "";
                System.out.println("\n++++++++++++RESPUESTA " + (j + 1) + "++++++++++++");
                dom = din.readShort();
                System.out.println("NOMBRE: " + String.format("%x", dom));
                tip = din.readShort();
                System.out.println("TIPO: 0x" + String.format("%x", tip));
                aClase = din.readShort();
                System.out.println("CLASE: 0x" + String.format("%x", aClase));
                ttl = din.readInt();
                System.out.println("TTL: 0x" + String.format("%x", ttl));
                addrLen = din.readShort();
                System.out.println("TAMANO: 0x" + String.format("%x", addrLen));
                System.out.print("DIRECCION: ");
                for (int i = 0; i < addrLen; i++) {
                    if (i < 3) {
                        Address += (din.readByte() & 0xFF) + ".";
                    } else {
                        Address += (din.readByte() & 0xFF);
                    }
                }
                System.out.println(Address);
                Respuesta nueva = new Respuesta(dom, tip, aClase, ttl, addrLen, InetAddress.getByName(Address));
                records.add(nueva);
                System.out.println("+++++++++++++++++++++++++++++++++++\n");
            }
        } catch (IOException e) {
            System.out.println("ERROR IMPRIMIENDO DATOS");
        }
	}
	
	
	
	public byte[] realizarConsultaExterna(byte[] pack) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] bytesRespuesta = new byte[1024];

            DatagramPacket paqueteEnvio = new DatagramPacket(pack, pack.length, this.ip, this.puerto);
            socket.send(paqueteEnvio);
            
            DatagramPacket paqueteRespuesta = new DatagramPacket(bytesRespuesta, bytesRespuesta.length);
            socket.receive(paqueteRespuesta);
            socket.close();
            leerRR(paqueteRespuesta.getData());
            return generarRespuestaExterna();
        } catch (IOException e) {
            System.out.println("Error generando la consulta externa.");
            return null;
        }
	}
	
	
	public byte[] realizarConsultaInterna(byte[] paquete, HashMap<String, Set<Respuesta>> masterF) {
        byte[] respuesta = generarRespuestaInterna(masterF);
        leerRR(respuesta);
        return respuesta;
    }
	


public byte[] generarRespuestaInterna(HashMap<String, Set<Respuesta>> masterF) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(out);
    String[] nombreDom = this.dominio.split("\\.");
    try {
        data.writeShort(TID);
        data.writeShort(0x8180);
        data.writeShort(question);
        data.writeShort(masterF.get(this.dominio).size());
        data.writeShort(authority);
        data.writeShort(aditional);
        //Formato Query 
        for (int i = 0; i < nombreDom.length; i++) {
            byte[] bytesDom = nombreDom[i].getBytes();
            data.writeByte(bytesDom.length);
            data.write(bytesDom);
        }
        data.writeByte(0x00);
        data.writeShort(0x0001);
        data.writeShort(0x0001);
        //Formato answers
        if (masterF.containsKey(this.dominio)) {
            Set<Respuesta> rec = masterF.get(this.dominio);
            for (Respuesta actual : rec) {
                data.write(actual.devolverStream());
            }
        }
        return out.toByteArray();
    } catch (IOException e) {
        System.out.println("ERROR GENERANDO RESPUESTA INTERNA");
        return null;
    }
}
	
public byte[] generarRespuestaExterna() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(out);
    String[] nombreDom = this.dominio.split("\\.");
    try {
        data.writeShort(TID);
        data.writeShort(0x8180);
        data.writeShort(question);
        data.writeShort(records.size());
        data.writeShort(authority);
        data.writeShort(aditional);
        for (int i = 0; i < nombreDom.length; i++) {
            byte[] bytesDom = nombreDom[i].getBytes();
            data.writeByte(bytesDom.length);
            data.write(bytesDom);
        }
        data.writeByte(0x00);
        data.writeShort(0x0001);
        data.writeShort(0x0001);
        for (Respuesta r : records) {
            data.write(r.devolverStream());
        }
        return out.toByteArray();
    } catch (IOException e) {
        System.out.println("ERROR GENERANDO RESPUESTA EXTERNA");
        return null;
    }	
}
	
	
	
	
}
