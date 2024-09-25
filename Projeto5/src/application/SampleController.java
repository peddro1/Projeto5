package application;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import object.Mensagem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class SampleController {
	@FXML
	private CheckBox agenda1;
	
	@FXML
	private CheckBox agenda2;
	
	@FXML
	private CheckBox agenda3;
	
	@FXML
	private ListView<String> listContacts;
	
	@FXML
	private TextField name;
	
	@FXML
	private TextField number;
	
    @FXML
    private Button connect;
    
    private String remetente;
    
    private Socket socket;
    
	@FXML
	void connect(ActionEvent event) {

		if(this.getSelectedAgenda() !=0 ) {
			try {
	            //Conectando ao Servidor do Chat
	            socket = new Socket("127.0.0.1", this.getSelectedAgenda());
	            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

	            //Enviando a primeira mensagem informando conexão (apenas para passar o nome do cliente)
	            Mensagem mensagem = new Mensagem();
	           
	            mensagem.setAction(Mensagem.Action.CONNECT);

	            //Instanciando uma ThreadCliente para ficar recebendo mensagens do Servidor
	            ThreadClient thread = new ThreadClient(remetente, socket, listContacts);
	            thread.setName("Thread Cliente " + remetente);
	            thread.start();

	            //Saída de Dados do Cliente
	            saida.writeObject(mensagem); //Enviando mensagem para Servidor
	            
	            this.connect.setDisable(true);
	        } catch (IOException ex) {
	            //Logger.getLogger(FXMLChatClienteController.class.getName()).log(Level.SEVERE, null, ex);
	        }
		
		}
	
	}
	
	@FXML
	void addContact(ActionEvent event) {
		try {
			
	            Mensagem mensagem = new Mensagem();
	            mensagem.setName(this.name.getText());
	            mensagem.setPhoneNumber(this.number.getText());

	            //Caso tenha selecionado algum usuário
	            
                mensagem.setAction(Mensagem.Action.ADD_CONTACT);

	            //Saída de Dados do Cliente
	            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
	            saida.writeObject(mensagem); //Enviando mensagem para Servidor

	            this.name.setText("");
	            this.number.setText("");
	         
	        } catch (IOException ex) {
	            //Logger.getLogger(FXMLChatClienteController.class.getName()).log(Level.SEVERE, null, ex);
	        }
	}
	
	@FXML
	void removeContact(ActionEvent event) {
		try {
			if (this.listContacts.getSelectionModel().getSelectedItem() != null) {
	            Mensagem mensagem = new Mensagem();
	            //Caso tenha selecionado algum usuário
	            
	            mensagem.setAction(Mensagem.Action.REMOVE_CONTACT);
	            mensagem.setName((String)listContacts.getSelectionModel().getSelectedItem().split(":")[0]);
	
	            //Saída de Dados do Cliente
	            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
	            saida.writeObject(mensagem); //Enviando mensagem para Servidor
	
		 	}
	
	    } catch (IOException ex) {
	        //Logger.getLogger(FXMLChatClienteController.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	
	private int getSelectedAgenda() {
		
		if(this.agenda1.isSelected()) {
			return 54321;
		}else if(this.agenda2.isSelected()) {
			return 54322;
		}else if(this.agenda3.isSelected()) {
			return 54323;
		}
		
		return 0;
	}

}
