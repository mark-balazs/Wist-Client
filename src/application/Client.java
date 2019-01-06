package application;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Controller{
    protected Socket connection;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    
}
