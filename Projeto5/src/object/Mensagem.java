package object;

import java.io.Serializable;
import java.util.ArrayList;


public class Mensagem implements Serializable {

	
    private String phoneNumber;
    private String name;
    private Action action;
    private ArrayList<String> usuariosOnline;
    private boolean isPlayer1 = true;
    private String nameThreadServer;


    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }



    public ArrayList<String> getUsuariosOnline() {
        return usuariosOnline;
    }

    public void setUsuariosOnline(ArrayList<String> usuariosOnline) {
        this.usuariosOnline = usuariosOnline;
    }

	public boolean isPlayer1() {
		return isPlayer1;
	}

	public void setPlayer1(boolean isPlayer1) {
		this.isPlayer1 = isPlayer1;
	}

	public String getNameThreadServer() {
		return nameThreadServer;
	}

	public void setNameThreadServer(String nameThreadServer) {
		this.nameThreadServer = nameThreadServer;
	}


	public enum Action {
        CONNECT, DISCONNECT, USERS_ONLINE, ADD_CONTACT, REMOVE_CONTACT, ADD_CONTACT_AGEN, REMOVE_CONTACT_AGEN, GET_CONTACTS, SET_CONTACTS
    }
}
