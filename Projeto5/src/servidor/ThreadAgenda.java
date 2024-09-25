package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import object.Mensagem;
import object.Mensagem.Action;

public class ThreadAgenda extends Thread {

	private static Map<String, Socket> clientsMap = new HashMap<>();
	private static Map<String, String> usersMapNumber = new HashMap<>();
	private Socket socket;
	private ArrayList<Integer> listPorts = new ArrayList();
	private int numberClient = 0;

	public ThreadAgenda(Socket s, int numberClient) {
		this.socket = s;
		this.numberClient = numberClient;
		this.getPortsAgendas();
		//if(usersMapNumber.isEmpty()) this.getContactosMensagem();

	}
	
	@Override
	public void run() {
		
		boolean sair = false;

		while (!sair) {
			
			try {
				ObjectInputStream input =  new ObjectInputStream(socket.getInputStream());
				Mensagem mensagem = (Mensagem) input.readObject();
				Action action = mensagem.getAction();
				
				switch (action) {
		            case CONNECT:
		                conectar(mensagem);
		                //enviarMensagemTodos(mensagem);
		                break;
		            case DISCONNECT:
		            	desconectar(mensagem);
		                //enviarMensagemTodos(mensagem);
		                sair = true;
		                break;
		            case USERS_ONLINE:
		            	enviarLista();
		            	break;
		            case ADD_CONTACT:
		            	addContact(mensagem);
		            	break;
		            case ADD_CONTACT_AGEN:
		            	addContactAgenda(mensagem);
		            	break;
		            case REMOVE_CONTACT:
		            	removeContact(mensagem);
		            	break;
		            case REMOVE_CONTACT_AGEN:
		            	removeContactAgenda(mensagem);
		            	break;
		            case GET_CONTACTS:
		            	getContatos(mensagem);
		            	break;
		            case SET_CONTACTS:
		            	setContatos(mensagem);
		            	break;
		            default:
		                break;
				}
				
				
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
				//Logger.getLogger(ThreadServidor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		
		
	}
	
	public void conectar(Mensagem mensagem) {
        clientsMap.put("Client" + this.numberClient + ":" + this.socket.getLocalPort(), socket);
        
        // se a mensagem tem remetente, propaga para as outras agendas com a acao CONNECT_AGENDAS
        // se nao tem remetente nao propaga para as agendas
    }
	
	 public void desconectar(Mensagem mensagem) throws IOException {
        clientsMap.remove("Client" + this.numberClient + "" + this.socket.getLocalPort()); 
        // se a mensagem tem remetente, propaga para as outras agendas com a acao DISCONNECT_AGENDAS
        // se nao tem remetente nao propaga para as agendas
    }

    public void enviarMensagemTodos(Mensagem mensagem) throws IOException {

        for (Map.Entry<String, Socket> cliente : clientsMap.entrySet()) {
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
            mensagem.setNameThreadServer(this.getName());
            saida.writeObject(mensagem);
            
        }
    }

    public void addContact(Mensagem mensagem) throws IOException {
    	if(!checarNomeContatos(mensagem.getName())) {
    		usersMapNumber.put(mensagem.getName(), mensagem.getPhoneNumber());
        	enviarLista();
        	mensagem.setAction(Action.ADD_CONTACT_AGEN);
            
            listPorts.forEach(port -> {
            	try {
    				Socket socketA = new Socket("127.0.0.1", port);
    				ObjectOutputStream saida = new ObjectOutputStream(socketA.getOutputStream());
    				saida.writeObject(mensagem);
    				
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            });
    	}
    	
    }
    
    public void addContactAgenda(Mensagem mensagem) throws IOException {
    	usersMapNumber.put(mensagem.getName(), mensagem.getPhoneNumber());
    	enviarLista();
    }
    
    public void removeContact(Mensagem mensagem) throws IOException {
    	usersMapNumber.remove(mensagem.getName());
    	enviarLista();
    	mensagem.setAction(Action.REMOVE_CONTACT_AGEN);
        
        listPorts.forEach(port -> {
        	try {
				Socket socketA = new Socket("127.0.0.1", port);
				ObjectOutputStream saida = new ObjectOutputStream(socketA.getOutputStream());
				saida.writeObject(mensagem);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
    }
    
    public void removeContactAgenda(Mensagem mensagem) throws IOException {
    	usersMapNumber.remove(mensagem.getName());
    	enviarLista();
    }
    
    public void enviarLista() throws IOException {
    	//usersMapNumber.put(mensagem.getName(), mensagem.getPhoneNumber());
        ArrayList<String> usuariosOnline = new ArrayList();
        
        for (Map.Entry<String, String> cliente : usersMapNumber.entrySet()) {
            usuariosOnline.add(cliente.getKey() + ": " + cliente.getValue());
        }
        
        Mensagem mensagem = new Mensagem();
        mensagem.setAction(Action.USERS_ONLINE);
        mensagem.setUsuariosOnline(usuariosOnline);
        
        for (Map.Entry<String, Socket> cliente : clientsMap.entrySet()) {
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
            saida.writeObject(mensagem);
        }
    }
    
    private void getPortsAgendas(){    	
    	if(this.socket.getLocalPort() == 54321) {
    		this.listPorts.add(54322);
    		this.listPorts.add(54323);
    	} else if(this.socket.getLocalPort() == 54322) {
    		this.listPorts.add(54321);
    		this.listPorts.add(54323);
    	}else if(this.socket.getLocalPort() == 54323) {
    		this.listPorts.add(54321);
    		this.listPorts.add(54322);
    	}
    }
    
    private Boolean checarNomeContatos(String nome) {
    	
    	Boolean response = false;
    	
    	 for (Map.Entry<String, String> cliente : usersMapNumber.entrySet()) {
             if(cliente.getKey().equals(nome)) {
            	 response = true;
             }
         }
    	
    	return response;
    }
    
    private void setContatos(Mensagem mensagem) {
    	if(usersMapNumber.isEmpty()) {
    		mensagem.getUsuariosOnline().forEach(user ->{
        		usersMapNumber.put(user.split(": ")[0], user.split(": ")[1]);
        	});
    	}
    	
    }
    
    private void getContatos(Mensagem msg) throws IOException {
    	if(!usersMapNumber.isEmpty()) {
    		ArrayList<String> usuariosOnline = new ArrayList();
            
            for (Map.Entry<String, String> cliente : usersMapNumber.entrySet()) {
                usuariosOnline.add(cliente.getKey() + ": " + cliente.getValue());
            }
            
            Mensagem mensagem = new Mensagem();
            mensagem.setAction(Action.SET_CONTACTS);
            mensagem.setUsuariosOnline(usuariosOnline);
            
            
            	try {
    				Socket socketA = new Socket("127.0.0.1", Integer.valueOf(msg.getPhoneNumber()));
    				ObjectOutputStream saida = new ObjectOutputStream(socketA.getOutputStream());
    				saida.writeObject(mensagem);
    				
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	}
    	 
        
    }
    
    private void getContactosMensagem() {
    	
    		Mensagem mensagem = new Mensagem();
            mensagem.setAction(Action.GET_CONTACTS);
            mensagem.setPhoneNumber(Integer.toString(socket.getLocalPort()));
             
        	listPorts.forEach(port -> {
            	try {
    				Socket socketA = new Socket("127.0.0.1", port);
    				ObjectOutputStream saida = new ObjectOutputStream(socketA.getOutputStream());
    				saida.writeObject(mensagem);
    				
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            });
    	
    	
    }
   
}
