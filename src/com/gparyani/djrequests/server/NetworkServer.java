package com.gparyani.djrequests.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkServer extends Thread {
	private ServerSocket serverSock;
	private Queue<String> requests = new ConcurrentLinkedQueue<>(),
			handledRequests = new ConcurrentLinkedQueue<>();
	private Set<IndividualClient> runningClients = Collections.synchronizedSet(new HashSet<IndividualClient>());
	
	/**
	 * Creates a new NetworkServer
	 * @param port the port
	 * @throws IOException if an I/O error occurs when instantiating the underlying server socket
	 */
	public NetworkServer(int port) throws IOException {
		serverSock = new ServerSocket(port);
	}

	@Override
	public void run() {
		try {
			while(true) {
				Socket newClient = serverSock.accept();
				IndividualClient theClient = new IndividualClient(newClient);
				runningClients.add(theClient);
				new Thread(theClient).start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts accepting connections.
	 * 
	 * @throws IllegalStateException if this server has already been started
	 */
	@Override
	public void start() {
		if(getState() != State.NEW)
			throw new IllegalStateException("This server has already been started");
		else
			super.start();
	}
	
	/**
	 * Returns the new requests since this method or the {@link #start()} method was invoked.
	 * Subsequent calls will include only those requests sent after the last call to this method
	 * @return the new requests since this call
	 */
	public String[] newRequests() {
		String[] toReturn = requests.toArray(new String[0]);
		requests.clear();
		return toReturn;
	}
	
	public String getIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "Error obtaining";
		}
	}
	
	public void addHandledRequest(String req) {
		handledRequests.add(req);
	}
	
	public int numClients() {
		return runningClients.size();
	}
	
	private class IndividualClient implements Runnable {
		private Socket client;
		
		IndividualClient(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
				while(true) {
					@SuppressWarnings("resource")
					Scanner request = new Scanner(input.readLine());
					if(!request.hasNext())
						output.write("Invalid request");
					else
						switch(request.next()) {
						case "request":
							if(!request.hasNextLine())
								output.write("Invalid request");
							else {
								requests.add(request.nextLine());
								output.write("Request successful");
							}
						case "gethandled":
							output.write(handledRequests.toString());
						}
					output.newLine();
					output.flush();
				}
				
			} catch (IOException e) {	//this exception will occur if a client disconnects, or for another reason
				e.printStackTrace();
			} finally {
				runningClients.remove(this);	//update the number of running clients
			}
		}
		
	}

}
