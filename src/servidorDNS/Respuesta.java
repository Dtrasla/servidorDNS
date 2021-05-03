package servidorDNS;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

/*
All RRs (Resource Record) have the same top level format shown below:

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
    
	NAME            a domain name to which this resource record pertains.
	
	TYPE            two octets containing one of the RR type codes.  This
	                field specifies the meaning of the data in the RDATA
	                field.
	
	CLASS           two octets which specify the class of the data in the
	                RDATA field.
	
	TTL             a 32 bit unsigned integer that specifies the time
	                interval (in seconds) that the resource record may be
	                cached before it should be discarded.  Zero values are
	                interpreted to mean that the RR can only be used for the
	                transaction in progress, and should not be cached.
	
	RDLENGTH        an unsigned 16 bit integer that specifies the length in
	                octets of the RDATA field.
	
	RDATA           a variable length string of octets that describes the
	                resource.  The format of this information varies
	                according to the TYPE and CLASS of the resource record.
	                For example, the if the TYPE is A and the CLASS is IN,
	                the RDATA field is a 4 octet ARPA Internet address.
	
	     *
  */


public class Respuesta {
	private short name;
	private short type;
	private short clase;
	private int ttl; //Java no tiene unsigned
	private short rdlength;
	private InetAddress direccion;
	
	public Respuesta(short name, short type, short clase, int ttl, short rdlength, InetAddress address) {
		this.name = name;
		this.type = type;
		this.clase = clase;
		this.ttl = ttl;
		this.rdlength = rdlength;
		this.direccion = address;
	}
	
	public byte[] devolverStream(){
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(o);
		try {
			stream.writeShort(this.name);
			stream.writeShort(this.type);
			stream.writeShort(this.clase);
			stream.writeInt(this.ttl);
			stream.writeShort(this.rdlength);
			stream.write(this.direccion.getAddress());
			
			return o.toByteArray();
		}
		catch(IOException e) {
			System.out.println("Error creando stream");
			return null;
		}
		
		
	}


	
	
}
