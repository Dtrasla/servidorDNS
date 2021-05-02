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
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class Servidor {
	public static final int PUERTO = 53;
	private String direccionMaster;
	private HashMap<String, Set<Respuesta>> cache;
	private int tam;
	//DNS dns;
	
	public Servidor(String master, int tam) {
		this.direccionMaster = master;
		this.tam = tam;
		this.cache = new HashMap<String, Set<Respuesta>>();
		//this.dns = new DNS(cache);
	}
	
	
	public void servidor() throws SocketException {
		
		byte[] entrada = new byte[tam];
		DatagramSocket socket = new DatagramSocket(Servidor.PUERTO);
		try {
			while(true) {
				byte[] salida = new byte[tam];
				DatagramPacket solicitud = new DatagramPacket(entrada,tam);
				socket.receive(solicitud);
				System.out.println(solicitud.getAddress().getHostAddress() + ": " + solicitud.getPort());
				System.out.println("tam : " + solicitud.getLength());
				
				DNS dns = new DNS(solicitud.getData());
				
				if(this.cache.containsKey( dns.getDominio() )) {
					salida = dns.realizarConsultaInterna(solicitud.getData(), this.cache );	
				}
				else {
					salida = dns.realizarConsultaExterna(entrada);
				}
				
				DatagramPacket paquete = new DatagramPacket(salida,salida.length, solicitud.getAddress(),53);
				try {
			            socket.send(paquete);
				} catch (Exception e) {
			            System.out.println("Enviando...");
				}
				
			}
		} catch(IOException e) {
			
		}
		
		socket.close();
	}
	
	
	
	public static void main(String[] args) {
		Servidor serv = new Servidor("src\\masterfile.txt",1024);
		try {
			serv.servidor();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	 public void llenarMasterFile() throws Exception {
	        BufferedReader br = new BufferedReader(new FileReader("src\\MasterFile.txt"));
	        String linea,dominio ="";
	        InetAddress ip;
	        byte[] name;
	        int ttl;
	        short tipo,clase,len =4;
	        while ((linea = br.readLine()) != null) {
	            String[] datos = linea.split(" ");
	            if (!datos[0].equalsIgnoreCase("$ORIGIN")) {
	                Set<Respuesta> ips = new HashSet<Respuesta>();
	                dominio = datos[0];
	                name = datos[0].getBytes();
	                ttl = Integer.parseInt(datos[1]);
	                tipo = 0x0001;
	                clase = 0x0001;
	                ip = InetAddress.getByName(datos[4]);
	                Respuesta resp = new Respuesta(ByteBuffer.wrap(name).getShort(), tipo, clase, ttl, len, ip);
	                if (this.cache.containsKey(dominio)) {
	                    this.cache.get(dominio).add(resp);
	                } else {
	                    ips.add(resp);
	                    this.cache.put(dominio, ips);
	                }
	            }
	        }
	        br.close();
	    }
}
