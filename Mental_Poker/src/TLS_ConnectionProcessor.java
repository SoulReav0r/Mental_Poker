import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class TLS_ConnectionProcessor implements Runnable
{
  /**
   * The Server we are running inside
   */
  private TLS_Server server;

  /**
   * The socket connected to our client
   */
  private Socket socket;

  /**
   * Connection to the client
   */
  private DataInputStream din;

  /**
   * Connection to the client
   */
  private DataOutputStream dout;
  
  private byte [] Message = null;

  /**
   * Create a new ConnectionProcessor
   */
  public TLS_ConnectionProcessor( TLS_Server server, Socket socket ) {
    this.server = server;
    this.socket = socket;

    new Thread( this ).start();
  }

  public void run() {
    try {
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      din = new DataInputStream( in );
      dout = new DataOutputStream( out );

      while (true) { 
        	 System.out.println(din.readUTF()); // TODO Message is read continuously --> better choose non Blocking data structures like Queue 
      }

    } catch( IOException ie ) {
      try {
        socket.close();
      } catch( IOException ie2 ) {
        System.out.println( "Error closing socket "+socket );
      }

      server.removeConnection( this );

      System.out.println( "Closed connection from socket "+socket );
    }
  }

  }
