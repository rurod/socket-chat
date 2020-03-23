package server.chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.io.IOException;

import server.chat.ClientHandler;

class ChatService {


    private Vector<ClientHandler> clients;
	private int port;
	
	
	public ChatService(int port) {
        this.clients = new Vector<ClientHandler>();
     	this.port = port;
	}

	public void run() {
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(this.port);
			System.out.println("[LOG] - Server started - Waiting for connections");
			while (true) {
				ClientHandler ch = new ClientHandler(ss.accept(), this.clients);
                this.clients.add(ch);
				Thread t = new Thread(ch);
				t.start();
				System.out.println("[LOG] - Client connected - Thread created");
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		finally {
			if (ss != null) {
				try { 
					ss.close();
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	public static void main(String[] args) {
	    ChatService cs = new ChatService(16523);
        cs.run();
    }

}
