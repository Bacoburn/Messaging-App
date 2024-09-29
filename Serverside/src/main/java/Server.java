import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 0;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;

	ArrayList<String> usernameList = new ArrayList<String>();

	ArrayList<String> privateChatters;

	String WhoLeft;
	ArrayList<String> groupnameList = new ArrayList<>();
	HashMap<String,ArrayList<String>> GroupDetails = new HashMap<>();
	ArrayList<String> GroupBelonging;
	String tempGroupName;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(Message message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					if (message.whattodo.equals("UpdateChat")) {
						try {
							t.out.writeObject(message);
						} catch (Exception e) {
						}
					}
					else if (message.whattodo.equals("GlobalChat")) {
						try {
							t.out.writeObject(message);
						} catch (Exception e) {
						}
					}
					else if (message.whattodo.equals("UpdatePrivateChat")) {

						if (privateChatters.contains(usernameList.get(i))) {
							try {
								t.out.writeObject(message);
							} catch (Exception e) {
							}
						}
					}
					else if (message.whattodo.equals("UserLeft")) {
							try {
								message.LeaveTheServer = WhoLeft;
								t.out.writeObject(message);
							} catch (Exception e) {
							}
					}
					else if (message.whattodo.equals("UpdateGroupChat")) {
						try {
							if (GroupBelonging.contains(usernameList.get(i))) {
								Message drunk = new Message();
								drunk.username = message.username;
								drunk.whattodo = "UpdateGroupChat";
								drunk.GroupName = tempGroupName;
								drunk.ListOGMUsers.add(tempGroupName);
								drunk.AllGroups.addAll(message.AllGroups);
								t.out.writeObject(drunk);
							}
							else {
								t.out.writeObject(message);
							}
						} catch (Exception e) {
						}
					}
					else if (message.whattodo.equals("UpdateViewGroups")) {
						try {
							t.out.writeObject(message);
						} catch (Exception e) {
						}
					}
					else if (message.whattodo.equals("GroupChatMessaggers")) {
						String temper = message.GroupName;
						if ((GroupDetails.get(temper)).contains(usernameList.get(i))) {
							try {
								t.out.writeObject(message);
							} catch (Exception e) {
							}
						}
					}
				}
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				//updateClients("new client on server: client #"+count);
					
				 while(true) {
					    try {
					    	Message ServerIn = (Message) in.readObject();
							if (ServerIn.whattodo.equals("Uniqueness")) {
								String userr = ServerIn.username;
								if (usernameList.contains(userr)) {
									ServerIn.isUnique = false;
									out.writeObject(ServerIn);
								} else {
									usernameList.add(userr);
									ServerIn.isUnique = true;
									callback.accept("client #" + count + " is " + userr);
									out.writeObject(ServerIn);
								}
							}
							else if (ServerIn.whattodo.equals("UpdateChat")) {
								Message m1 = new Message();
								m1.username = ServerIn.username;
								m1.ListOUsers.addAll(usernameList);
								m1.whattodo = "UpdateChat";
								updateClients(m1);
							}
							else if (ServerIn.whattodo.equals("GlobalChat")) {
								Message GlobalOut = new Message();
								GlobalOut.username = ServerIn.username;
								GlobalOut.whattodo = "GlobalChat";
								GlobalOut.message = ServerIn.message;
								callback.accept(ServerIn.username + " says: " + ServerIn.message);
								updateClients(GlobalOut);
							}
							else if (ServerIn.whattodo.equals("UpdatePrivateChat")) {
								Message PrivateOut = new Message();
								PrivateOut.username = ServerIn.username;
								PrivateOut.recipient = ServerIn.recipient;
								PrivateOut.message = ServerIn.message;
								privateChatters = new ArrayList<String>();
								privateChatters.addAll(ServerIn.ListOPUsers);
								PrivateOut.whattodo = "UpdatePrivateChat";
								callback.accept(ServerIn.username + " is private messaging " + ServerIn.recipient);
								updateClients(PrivateOut);
							}
							else if (ServerIn.whattodo.equals("UserLeft")) {
								usernameList.remove(ServerIn.username);
								WhoLeft = ServerIn.username;
								Message Leave = new Message();
								Leave.username = ServerIn.username;
								Leave.whattodo = "UserLeft";
								Leave.ListOUsers.addAll(usernameList);
								//callback.accept(ServerIn.username + " left the server");
								updateClients(Leave);
							}
							else if (ServerIn.whattodo.equals("UpdateGroupChat")) {
								groupnameList.add(ServerIn.GroupName);
								GroupDetails.putAll(ServerIn.GroupMembers);
								GroupBelonging = new ArrayList<>();
								tempGroupName = ServerIn.GroupName;
								GroupBelonging.addAll(ServerIn.GroupMembers.get(ServerIn.GroupName));

								Message GroupIn = new Message();
								GroupIn.username = ServerIn.username;
								GroupIn.whattodo = "UpdateGroupChat";
								GroupIn.GroupName = ServerIn.GroupName;
								GroupIn.GroupMembers.putAll(ServerIn.GroupMembers);
								GroupIn.AllGroups.addAll(groupnameList);
								callback.accept(ServerIn.username + " has created a group called " + ServerIn.GroupName);
								updateClients(GroupIn);
							}
							else if (ServerIn.whattodo.equals("UpdateViewGroups")) {
								Message Methane = new Message();
								Methane.username = ServerIn.username;
								Methane.ListOGUsers.addAll(groupnameList);
								Methane.whattodo = "UpdateViewGroups";
								updateClients(Methane);

							}
							else if (ServerIn.whattodo.equals("GroupChatMessaggers")) {

								Message Iron = new Message();
								Iron.username = ServerIn.username;
								Iron.GroupName = ServerIn.GroupName;
								Iron.message = ServerIn.message;
								Iron.whattodo = "GroupChatMessaggers";
								callback.accept(ServerIn.username + " is chatting in " + ServerIn.GroupName);
								updateClients(Iron);
							}


					    	
					    	}
					    catch(Exception e) {
					    	callback.accept("Client #" + count + " has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
