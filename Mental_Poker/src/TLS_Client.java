import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;

public class TLS_Client implements Runnable
{
  /**
   * Connection to the client
   */
  private DataInputStream din;

  /**
   * Connection to the client
   */
  private DataOutputStream dout;

  /**
   * KeyStore for storing our public/private key pair
   */
  private KeyStore clientKeyStore;
  
  /**
   * KeyStore for storing the server's public key
   */
  private KeyStore serverKeyStore;

  /**
   * Used to generate a SocketFactory
   */
  private SSLContext sslContext;
  
  /**
   * JSON Object for sending
   */
  private JSONObject Message = new JSONObject("{Message : Test}");

    /**
   * Passphrase for accessing our authentication keystore
   */
  static private final String passphrase = "clientpw";

  /**
   * A source of secure random numbers
   */
  static private SecureRandom secureRandom;

  public TLS_Client( String host, int port ) {
    connect( host, port );
    new Thread( this ).start();
  }

  private void setupServerKeystore() throws GeneralSecurityException, IOException {
    serverKeyStore = KeyStore.getInstance( "JKS" );
    serverKeyStore.load( new FileInputStream( "D:/JavaWorkspace/Mental_Poker/Mental_Poker/src/server.public" ), 
                        "public".toCharArray() );
  }

  private void setupClientKeyStore() throws GeneralSecurityException, IOException {
    clientKeyStore = KeyStore.getInstance( "JKS" );
    clientKeyStore.load( new FileInputStream( "D:/JavaWorkspace/Mental_Poker/Mental_Poker/src/client.private" ),
                       passphrase.toCharArray() );
  }

  private void setupSSLContext() throws GeneralSecurityException, IOException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
    tmf.init( serverKeyStore );

    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( clientKeyStore, passphrase.toCharArray() );

    sslContext = SSLContext.getInstance( "TLS" );
    sslContext.init( kmf.getKeyManagers(),
                     tmf.getTrustManagers(),
                     secureRandom );
  }

  private void connect( String host, int port ) {
    try {
      setupServerKeystore();
      setupClientKeyStore();
      setupSSLContext();

      SSLSocketFactory sf = sslContext.getSocketFactory();
      SSLSocket socket = (SSLSocket)sf.createSocket( host, port );

      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      din = new DataInputStream( in );
      dout = new DataOutputStream( out );
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  public void run() {
    try {
      while (true) {      
        	dout.writeUTF(Message.getString("Message")); // TODO Message is send continuously --> better choose non Blocking data structures like Queue 
    	  ;      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }
  
  static public void main( String args[] ) {
    if (args.length != 2) {
      System.err.println( "Usage: java Client [hostname] [port number]" );
      System.exit( 1 );
    }

    String host = args[0];
    int port = Integer.parseInt( args[1] );

    System.out.println( "Wait while secure random numbers are initialized...." );
    secureRandom = new SecureRandom();
    secureRandom.nextInt();
    System.out.println( "Done." );

    new TLS_Client( host, port );
  }
}
