package unimelb.bitbox;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.sql.ConnectionEvent;

public class Client extends Thread {
	
	// IP and port
	private String ip;
	private int port;
	private ArrayList<String> iplist = new ArrayList<String>();
	private Socket socket;
	
	public Client(ArrayList<String> iplist, int port){

		this.iplist = iplist;
		this.ip = iplist.get(0);
		this.port = port;
	}
	
	public void run() {
		try{
			Socket socket = new Socket(ip, port);
				this.socket = socket;
				//Get the input/output streams for reading/writing data from/to the socket
	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

	            Document handshake = new Document();
	            handshake.append("command","HANDSHAKE_REQUEST");
				HostPort hostport = new HostPort(socket.getInetAddress().toString(),port);
				handshake.append("hostPort",hostport.toDoc());
				out.write(handshake.toJson()+"\n");
				out.flush();

	            //While the user input differs from "exit"
	            while (true) {
	                // Receive the reply from the server by reading from the socket input stream
	                String received = in.readLine(); // This method blocks until there
					received = received+ "\n";
					System.out.print("clients received from server: " + received +"\n");

					if(received.contains("_")){
						Document received_message = Document.parse(received);
						System.out.println("CLIENT: " + Peer.operation(received_message) + "\n");


						if(Peer.operation(received_message).contains("longgenb1995")){

							String[] message = new String[2];
							message[0] = Peer.operation(received_message).split("longgenb1995")[0];
							message[1] = Peer.operation(received_message).split("longgenb1995")[1];

							Document received1 = Document.parse(message[0]);
							Document received2 = Document.parse(message[1]);
							System.out.println(message[0]);
							System.out.println(message[1]);

							out.write(received1.toJson()+"\n");
							out.write(received2.toJson()+"\n");
							out.flush();

						}
						else {
							if(!Peer.operation(received_message).equals("nothing")) {
								out.write(Peer.operation(received_message) + "\n");
								out.flush();
							}
						}
					}else{
						System.out.println("NO Json File Received");
					}
	            }
		    
		} catch (ConnectException e) {
			try {
				if(iplist.indexOf(ip)!= iplist.size()-1) {
					ip = iplist.get(iplist.indexOf(ip) + 1);
					System.out.println("this peer not online, finding next ...."+ e.toString());
				}
				else
					ip = iplist.get(0);
				Thread.sleep(5*1000);

				run();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		catch (SocketException  e) {
			try {
				if(iplist.indexOf(ip)!= iplist.size()-1) {
					ip = iplist.get(iplist.indexOf(ip) + 1);
					System.out.println("this peer not online, finding next ...."+ e.toString());
				}
				else
					ip = iplist.get(0);
				Thread.sleep(5*1000);

				run();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		catch(IOException| NoSuchAlgorithmException e){

		}

	}

	public void sendtoServer(String message){

	    if(socket != null){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                out.write(message+"\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


	}

}
