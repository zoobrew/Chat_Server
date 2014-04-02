package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/* Main Class of the Chat Server */
public class Server {

	public int portNumber;
	Hashtable<String, ChatConnection> connections =
			new Hashtable<String, ChatConnection>(32);

	//up to 32 concurrent clients
	//hashmap with usernames?


	public static void main(String[] args) {
		if (args.length < 1) {
            System.err.println("Usage: ChatServer <port>");
            System.exit(1);
        }
		System.out.print(args[0]);
		String portNumber = args[0];
		int port = Integer.parseInt(portNumber);
		try (
				//create socket with arg port number
				ServerSocket chatServerSocket = new ServerSocket(port);
				/* block until a client is connnected */
				Socket clientSocket = chatServerSocket.accept();
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
			) {
			System.out.print("Accepted client socket");
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
			out.println(inputLine);
			}
		} catch (IOException exception) {
			System.out.println("IOException caught while listening on port " + portNumber);
			System.out.println(exception.getMessage());
		}
	}

}
