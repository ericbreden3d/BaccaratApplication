import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

// Templated Client code
public class Client extends Thread{

    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    String ip;
    Integer port;
    private Consumer<Serializable> callback;

    BaccaratInfo gameInfo;

    Client(Consumer<Serializable> call, String ip, Integer port){
        // Sets up a new object with ip and port
        callback = call;
        gameInfo = new BaccaratInfo();
        this.ip = ip;
        this.port = port;
    }

    public void run() {

        try {
            socketClient= new Socket(ip,port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {}

        while(true) {
            try {
                // Information is read in from server
                gameInfo = (BaccaratInfo)in.readObject();
                callback.accept(gameInfo);
            }
            catch(Exception e) { }
        }
    }

    public void send(BaccaratInfo pgameInfo) throws Exception {
        try {
            // Information is wrote to server
            out.writeObject(pgameInfo);
        } catch (IOException e) {
            // This is not ideal, but we let the client know about an issue contacting the server
            e.printStackTrace();
            throw new Exception();
        }

    }
}