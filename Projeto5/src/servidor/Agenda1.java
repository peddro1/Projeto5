package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Agenda1 {
	public static int qtdClients = 0;
	
	public static void main(String[] args) throws IOException {
		try {
			ServerSocket serverSocket =  new ServerSocket(54321);
			System.out.println("A porta 54321 foi aberta!");
	        System.out.println("Servidor, com Thread, esperando receber mensagens...");
	        
			
	        while(true) {
	        	Socket socket;
	        	socket = serverSocket.accept();
	        	
	        	qtdClients++;
	        	
	        	//Endereço IP do cliente conectado
	            System.out.println("Cliente " + socket.getInetAddress().getHostAddress() + " conectado");
	            System.out.println(qtdClients);
	        
	            ThreadAgenda thread =  new ThreadAgenda(socket, qtdClients);
	            thread.setName("Thread Servidor: " + String.valueOf(qtdClients));
	            thread.start();
	            
	            // chat ok
	        }
	        
		}finally{
			  System.out.println("Caiu");
		}
		
	}
}
