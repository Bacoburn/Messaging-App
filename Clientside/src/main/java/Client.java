import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
	
		callback = call;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {

		}
		
		while(true) {
			 
			try {
				// Read an object (message) from the input stream
				Message message = (Message) in.readObject();
				if (message.whattodo.equals("Uniqueness")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UpdateChat")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("GlobalChat")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UpdatePrivateChat")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UserLeft")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UpdateGroupChat")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UpdateViewGroups")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("UpdateGroupMembers")) {
					callback.accept(message);
				}
				else if (message.whattodo.equals("GroupChatMessaggers")) {
					callback.accept(message);
				}
				// Call the callback function to handle the received message
				//callback.accept(message);

			}
			catch(Exception e) {}
		}
	
    }
	
	public void send(Message data) {
		
		try {
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
