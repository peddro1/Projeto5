package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import object.Mensagem;
import object.Mensagem.Action;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ThreadClient extends Thread {
	private Socket socket;
    private String remetente;
    private ListView list;
    private Button button;
    boolean sair = false;

    public ThreadClient(String r, Socket s, ListView list) {
        this.remetente = r;
        this.socket = s;
        this.list = list;
        //this.button = button;
    }

    @Override
    public void run() {
        try {
            while (!sair) {
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                Mensagem mensagem = (Mensagem) entrada.readObject();
                Action action = mensagem.getAction();

                switch (action) {
                  
                    case USERS_ONLINE:
                        atualizarUsuarios(mensagem);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            //Logger.getLogger(ThreadServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

    public void atualizarUsuarios(Mensagem mensagem) {
        ArrayList<String> usuariosOnline = mensagem.getUsuariosOnline();
      
        Platform.runLater(() -> {
            list.getItems().clear();
            list.getItems().addAll(usuariosOnline);
        });

    }
}
