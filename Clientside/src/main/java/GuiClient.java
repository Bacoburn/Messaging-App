
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GuiClient extends Application{

	

	Button b1, DMs, SEND, BackPMs, SENDPM, SENDGM, Group, GroupView, GroupChatting, ADDMEMBERS, DONE, BACKG1, BACKG2;
	HashMap<String, Scene> sceneMap;

	Client clientConnection;
	TextField t1, EnterMessage, usernameField, EnterPMMessage, groupnameField, groupnameField1, groupmembersField, EnterGCMessage;
	Label ErrorUsername, CurrentChatter;
	AnchorPane screeen;
	ListView<String> chat = new ListView<>();
	ListView<String> AllUsers = new ListView<>();
	ListView<String> AllGUsers = new ListView<>();
	ArrayList<String> GlobalUsers = new ArrayList<>();
	ArrayList<String> GlobalGroups = new ArrayList<>();
	ArrayList<String> UserGroups = new ArrayList<>();


	String CurrUser;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		sceneMap = new HashMap<String, Scene>();

		clientConnection = new Client(data->{
			Message messageObj = (Message) data;
			if (messageObj.whattodo.equals("Uniqueness")) {
				Platform.runLater(()->{
					if (!messageObj.isUnique) {
						ErrorUsername.setVisible(true);
					}
					else {
						ErrorUsername.setVisible(false);
						sceneMap.put("KnockOffChat",  createChatScreen());
						primaryStage.setScene(sceneMap.get("KnockOffChat"));
						primaryStage.setTitle("KnockOffChat");
						primaryStage.show();
						messageObj.whattodo = "UpdateChat";
						CurrUser = messageObj.username;
						CurrentChatter = new Label("Current user: " + CurrUser);
						CurrentChatter.setFont(Font.font("Itim", FontWeight.BOLD, 20));
						CurrentChatter.setStyle("-fx-text-fill: yellow;");
						AnchorPane.setTopAnchor(CurrentChatter, 10.0);
						AnchorPane.setLeftAnchor(CurrentChatter, 20.0);
						screeen.getChildren().add(CurrentChatter);

						//messageObj.ListOUsers.add(messageObj.username);
						clientConnection.send(messageObj);

						SEND.setOnAction(e->{
							Message UserMessage = new Message();
							UserMessage.username = messageObj.username;
							UserMessage.message = EnterMessage.getText();
							UserMessage.whattodo = "GlobalChat";
							clientConnection.send(UserMessage);

						});
						DMs.setOnAction(e->{
							usernameField = new TextField();

							DialogPane dialogPane = new DialogPane();
							dialogPane.setHeaderText("Enter recipient username:");
							dialogPane.setContent(usernameField);
							dialogPane.setPrefWidth(500);
							dialogPane.setPrefHeight(100);

							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setTitle("Username Input");
							alert.setDialogPane(dialogPane);

							alert.getButtonTypes().clear();
							alert.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

							alert.showAndWait().ifPresent(buttonType -> {
								if (buttonType == ButtonType.OK) {
									String reciever = usernameField.getText();
									if (!reciever.isEmpty()) {
										if (reciever.equals(messageObj.username)) {
											Alert error1 = new Alert(Alert.AlertType.ERROR);
											error1.setTitle("Error");
											error1.setHeaderText(null);
											error1.setContentText("MESSAGING TO YOURSELF!?!?!?!?");
											error1.showAndWait().ifPresent(response -> {
												if (response == ButtonType.OK) {

												}
											});
										}
										else if (!GlobalUsers.contains(reciever)) {
											Alert error2 = new Alert(Alert.AlertType.ERROR);
											error2.setTitle("Error");
											error2.setHeaderText(null);
											error2.setContentText("Username does NOT exist!!!");
											error2.showAndWait().ifPresent(response -> {
												if (response == ButtonType.OK) {

												}
											});
										}
										else {
											EnterPMMessage = new TextField();
											EnterPMMessage.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
											EnterPMMessage.setPromptText("Enter your message to " + reciever);
											AnchorPane.setTopAnchor(EnterPMMessage, 130.0);
											AnchorPane.setLeftAnchor(EnterPMMessage, 20.0);
											EnterPMMessage.setPrefWidth(400);
											screeen.getChildren().add(EnterPMMessage);

											SENDPM = new Button("PM SEND");
											SENDPM.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
											AnchorPane.setTopAnchor(SENDPM, 130.0);
											AnchorPane.setLeftAnchor(SENDPM, 450.0);
											screeen.getChildren().add(SENDPM);

											Button Back = new Button("Back");
											Back.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
											AnchorPane.setTopAnchor(Back, 130.0);
											AnchorPane.setLeftAnchor(Back, 540.0);
											screeen.getChildren().add(Back);

											System.out.println(reciever);
											SENDPM.setOnAction(f->{
												Message DirectContact = new Message();
												DirectContact.username = messageObj.username;
												DirectContact.recipient = reciever;
												System.out.println(DirectContact.username);
												System.out.println(DirectContact.recipient);
												DirectContact.message = EnterPMMessage.getText();
												DirectContact.ListOPUsers.add(messageObj.username);
												DirectContact.ListOPUsers.add(reciever);
												System.out.println(DirectContact.ListOPUsers);
												DirectContact.whattodo = "UpdatePrivateChat";
												clientConnection.send(DirectContact);
											});

											Back.setOnAction(g->{
												EnterPMMessage.setVisible(false);
												SENDPM.setVisible(false);
												Back.setVisible(false);
											});

										}
									}

								}
							});
						});

						Group.setOnAction(h->{
							groupnameField = new TextField();
							DialogPane dialogPane = new DialogPane();
							dialogPane.setHeaderText("Enter a unique group name");
							dialogPane.setContent(groupnameField);
							dialogPane.setPrefWidth(500);
							dialogPane.setPrefHeight(100);

							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setTitle("UNIQUE Group Name Input");
							alert.setDialogPane(dialogPane);

							alert.getButtonTypes().clear();
							alert.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

							alert.showAndWait().ifPresent(buttonType -> {
								if (buttonType == ButtonType.OK) {
									String reciever = groupnameField.getText();
									if (!reciever.isEmpty()) {
										 if (GlobalGroups.contains(reciever)) {
											Alert error = new Alert(Alert.AlertType.ERROR);
											error.setTitle("Error");
											error.setHeaderText(null);
											error.setContentText("In this app, group name are UNIQUE!");
											error.showAndWait().ifPresent(response -> {
												if (response == ButtonType.OK) {

												}
											});
										}
										 else {
											 sceneMap.put("EnterGroupMembers",  createGroupMembers());
											 primaryStage.setScene(sceneMap.get("EnterGroupMembers"));
											 primaryStage.setTitle("EnterGroupMembers");
											 primaryStage.show();
											 HashMap<String,ArrayList<String>> tempGroupMem = new HashMap<>();
											 ArrayList<String> Carbon = new ArrayList<>();
											 ADDMEMBERS.setOnAction(a -> {
												 if (!groupmembersField.getText().isEmpty()) {
													 String reciever1 = groupmembersField.getText();
													 if (reciever1.equals(messageObj.username)) {
														 Alert error1 = new Alert(Alert.AlertType.ERROR);
														 error1.setTitle("Error");
														 error1.setHeaderText(null);
														 error1.setContentText("CAN'T ADD YOURSELF SMH!");
														 error1.showAndWait().ifPresent(response -> {
															 if (response == ButtonType.OK) {

															 }
														 });
													 } else if (!GlobalUsers.contains(reciever1)) {
														 Alert error2 = new Alert(Alert.AlertType.ERROR);
														 error2.setTitle("Error");
														 error2.setHeaderText(null);
														 error2.setContentText("Username does NOT exist!!!");
														 error2.showAndWait().ifPresent(response -> {
															 if (response == ButtonType.OK) {

															 }
														 });
													 }

													 else {
														 Carbon.add(reciever1);
														 groupmembersField.clear();
													 }
												 }

											 });
											 DONE.setOnAction(b-> {
												 Carbon.add(messageObj.username);
												 tempGroupMem.put(reciever, Carbon);
												 primaryStage.setScene(sceneMap.get("KnockOffChat"));
												 primaryStage.setTitle("KnockOffChat");
												 primaryStage.show();
												 Message UpdateGEveryone = new Message();
												 UpdateGEveryone.username = messageObj.username;
												 UpdateGEveryone.GroupName = reciever;
												 UpdateGEveryone.GroupMembers.putAll(tempGroupMem);
												 UpdateGEveryone.whattodo = "UpdateGroupChat";
												 clientConnection.send(UpdateGEveryone);

											 });
										 }
									}
								}
							});

						});
						GroupView.setOnAction(m-> {
							sceneMap.put("ViewGroup",  createViewGroup());
							primaryStage.setScene(sceneMap.get("ViewGroup"));
							primaryStage.setTitle("ViewGroup");
							primaryStage.show();
							Message Butane = new Message();
							Butane.username = messageObj.username;
							Butane.whattodo = "UpdateViewGroups";
							clientConnection.send(Butane);

							BACKG1.setOnAction(a-> {
								primaryStage.setScene(sceneMap.get("KnockOffChat"));
								primaryStage.setTitle("KnockOffChat");
								primaryStage.show();
							});
						});

						GroupChatting.setOnAction(n-> {
							groupnameField1 = new TextField();
							DialogPane dialogPane = new DialogPane();
							dialogPane.setHeaderText("Enter Group Name:");
							dialogPane.setContent(groupnameField1);
							dialogPane.setPrefWidth(500);
							dialogPane.setPrefHeight(100);

							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setTitle("Group Name Input");
							alert.setDialogPane(dialogPane);

							alert.getButtonTypes().clear();
							alert.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
							alert.showAndWait().ifPresent(buttonType -> {
								if (buttonType == ButtonType.OK) {
									String banana = groupnameField1.getText();
									if (!banana.isEmpty()) {
										if (!UserGroups.contains(banana)) {
											Alert error2 = new Alert(Alert.AlertType.ERROR);
											error2.setTitle("Error");
											error2.setHeaderText(null);
											error2.setContentText("Group Name does NOT exist!!!");
											error2.showAndWait().ifPresent(response -> {
												if (response == ButtonType.OK) {

												}
											});
										}
										else {
											EnterGCMessage = new TextField();
											EnterGCMessage.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
											EnterGCMessage.setPromptText("Enter your message to " + banana + " members");
											AnchorPane.setTopAnchor(EnterGCMessage, 100.0);
											AnchorPane.setLeftAnchor(EnterGCMessage, 20.0);
											EnterGCMessage.setPrefWidth(400);
											screeen.getChildren().add(EnterGCMessage);

											SENDGM = new Button("GC SEND");
											SENDGM.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
											AnchorPane.setTopAnchor(SENDGM, 100.0);
											AnchorPane.setLeftAnchor(SENDGM, 450.0);
											screeen.getChildren().add(SENDGM);


											Button Back = new Button("Back");
											Back.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
											AnchorPane.setTopAnchor(Back, 100.0);
											AnchorPane.setLeftAnchor(Back, 540.0);
											screeen.getChildren().add(Back);


											SENDGM.setOnAction(z->{
												Message InDirectContact = new Message();
												InDirectContact.username = messageObj.username;
												InDirectContact.GroupName = banana;
												InDirectContact.message = EnterGCMessage.getText();
												InDirectContact.whattodo = "GroupChatMessaggers";
												clientConnection.send(InDirectContact);
											});

											Back.setOnAction(g->{
												EnterGCMessage.setVisible(false);
												SENDGM.setVisible(false);
												Back.setVisible(false);
											});

										}
									}

								}
							});

						});



					}
				});
			}
			else if (messageObj.whattodo.equals("UpdateChat")) {
				Platform.runLater(()->{
					String clientee = messageObj.username;
					chat.getItems().add("Welcome " + clientee + " to Messanger! :)");
					AllUsers.setItems(FXCollections.observableArrayList(messageObj.ListOUsers));
					GlobalUsers = messageObj.ListOUsers;

				});
			}
			else if (messageObj.whattodo.equals("GlobalChat")) {
				Platform.runLater(()->{
					String sender = messageObj.username;
					String saywhat = messageObj.message;
					chat.getItems().add(sender + " says: " + saywhat);
				});
			}
			else if (messageObj.whattodo.equals("UpdatePrivateChat")) {
				Platform.runLater(()-> {
					String sender = messageObj.username;
					String saywhat = messageObj.message;
					String reciever = messageObj.recipient;
					chat.getItems().add(sender + " to " + reciever + ": " + saywhat);
				});
			}
			else if (messageObj.whattodo.equals("UserLeft")) {
				Platform.runLater(()-> {
					String clientee = messageObj.LeaveTheServer;
					chat.getItems().add(clientee + " has left Messanger! :(");
					AllUsers.setItems(FXCollections.observableArrayList(messageObj.ListOUsers));
					GlobalUsers = messageObj.ListOUsers;
				});

			}
			else if (messageObj.whattodo.equals("UpdateGroupChat")) {
				Platform.runLater(()-> {
					UserGroups.addAll(messageObj.ListOGMUsers);
					GlobalGroups.addAll(messageObj.AllGroups);
					System.out.println(GlobalGroups);
					chat.getItems().add(messageObj.username + " has created a group: " + messageObj.GroupName + "!");
				});
			}
			else if (messageObj.whattodo.equals("UpdateViewGroups")) {
				Platform.runLater(()->{
					AllGUsers.setItems(FXCollections.observableArrayList(messageObj.ListOGUsers));

						});
			}
			else if (messageObj.whattodo.equals("GroupChatMessaggers")) {
				Platform.runLater(()-> {
					String sender = messageObj.username;
					String saywhat = messageObj.message;
					String reciever = messageObj.GroupName;
					chat.getItems().add(sender + " to " + reciever + ": " + saywhat);
				});
			}





		});
							
		clientConnection.start();


		sceneMap.put("signup",  createSignUpScreen());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
				Message UserGone = new Message();
				UserGone.username = CurrUser;
				UserGone.whattodo = "UserLeft";
				clientConnection.send(UserGone);
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("signup"));
		primaryStage.setTitle("Sign Up");
		primaryStage.show();

		b1.setOnAction(e->{
			if (!t1.getText().isEmpty()) {
				Message message = new Message();
				message.username = t1.getText();
				message.whattodo = "Uniqueness";
				clientConnection.send(message);
			}

		});

	}
	

	
	public Scene createSignUpScreen() {
		screeen = new AnchorPane();
		Image backgroundImage = new Image("night-sky.jpg");
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.fitWidthProperty().bind(screeen.widthProperty());
		backgroundImageView.fitHeightProperty().bind(screeen.heightProperty());
		screeen.getChildren().add(backgroundImageView);


		Label label = new Label("WELCOME TO MESSANGER!");
		label.setFont(Font.font("Itim", FontWeight.BOLD, 40));
		label.setStyle("-fx-text-fill: linear-gradient(to right, red, orange, yellow, green, blue, indigo, violet);");
		AnchorPane.setTopAnchor(label, 225.0);
		AnchorPane.setLeftAnchor(label, 150.0);
		screeen.getChildren().add(label);

		ErrorUsername = new Label("Username already taken!");
		ErrorUsername.setFont(Font.font("Itim", FontWeight.BOLD, 20));
		ErrorUsername.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
		AnchorPane.setTopAnchor(ErrorUsername, 470.0);
		AnchorPane.setLeftAnchor(ErrorUsername, 180.0);
		ErrorUsername.setVisible(false);
		screeen.getChildren().add(ErrorUsername);

		t1 = new TextField();
		t1.setStyle("-fx-text-fill: purple; -fx-font-weight: bold; -fx-min-width: 200px; -fx-min-height: 40px;");
		t1.setPromptText("Enter your username");
		AnchorPane.setTopAnchor(t1, 500.0);
		AnchorPane.setLeftAnchor(t1, 200.0);
		screeen.getChildren().add(t1);

		b1 = new Button("Sign up");
		b1.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-min-width: 150px; -fx-min-height: 50px;");
		AnchorPane.setTopAnchor(b1, 500.0);
		AnchorPane.setLeftAnchor(b1, 500.0);
		screeen.getChildren().add(b1);


		return new Scene(screeen, 800, 800);
	}

	public Scene createChatScreen() {
		screeen = new AnchorPane();
		Image backgroundImage = new Image("night-sky.jpg");
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.fitWidthProperty().bind(screeen.widthProperty());
		backgroundImageView.fitHeightProperty().bind(screeen.heightProperty());

		screeen.getChildren().add(backgroundImageView);
		chat = new ListView<>();
		AllUsers = new ListView<>();
		chat.setPrefSize(600, 600);
		AllUsers.setPrefSize(200, 600);

		AnchorPane.setTopAnchor(chat, 200.0);
		AnchorPane.setLeftAnchor(chat, 0.0);
		AnchorPane.setTopAnchor(AllUsers, 200.0);
		AnchorPane.setRightAnchor(AllUsers, 0.0);
		screeen.getChildren().add(chat);
		screeen.getChildren().add(AllUsers);

		Label label = new Label("All Users");
		label.setFont(Font.font("Itim", FontWeight.BOLD, 20));
		label.setStyle("-fx-text-fill: orange;");
		AnchorPane.setTopAnchor(label, 160.0);
		AnchorPane.setRightAnchor(label, 60.0);
		screeen.getChildren().add(label);

		DMs = new Button("Private Message");
		DMs.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(DMs, 10.0);
		AnchorPane.setRightAnchor(DMs, 50.0);
		screeen.getChildren().add(DMs);

		Group = new Button("Create Group");
		Group.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(Group, 50.0);
		AnchorPane.setRightAnchor(Group, 50.0);
		screeen.getChildren().add(Group);

		GroupView = new Button("All Groups");
		GroupView.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(GroupView, 90.0);
		AnchorPane.setRightAnchor(GroupView, 50.0);
		screeen.getChildren().add(GroupView);

		GroupChatting = new Button("Group Message");
		GroupChatting.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(GroupChatting, 130.0);
		AnchorPane.setRightAnchor(GroupChatting, 50.0);
		screeen.getChildren().add(GroupChatting);

		EnterMessage = new TextField();
		EnterMessage.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		EnterMessage.setPromptText("Enter your message");
		AnchorPane.setTopAnchor(EnterMessage, 160.0);
		AnchorPane.setLeftAnchor(EnterMessage, 20.0);
		EnterMessage.setPrefWidth(400);
		screeen.getChildren().add(EnterMessage);

		SEND = new Button("SEND");
		SEND.setStyle("-fx-text-fill: #568203; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(SEND, 160.0);
		AnchorPane.setLeftAnchor(SEND, 450.0);
		screeen.getChildren().add(SEND);
		return new Scene(screeen,800, 800);
	}

	public Scene createGroupMembers() {
		AnchorPane screeen5 = new AnchorPane();
		Image backgroundImage = new Image("night-sky.jpg");
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.fitWidthProperty().bind(screeen5.widthProperty());
		backgroundImageView.fitHeightProperty().bind(screeen5.heightProperty());
		screeen5.getChildren().add(backgroundImageView);

		groupmembersField = new TextField();
		groupmembersField.setStyle("-fx-text-fill: purple; -fx-font-weight: bold; -fx-min-width: 200px;");
		groupmembersField.setPromptText("Enter username");
		AnchorPane.setTopAnchor(groupmembersField, 75.0);
		AnchorPane.setLeftAnchor(groupmembersField, 75.0);
		screeen5.getChildren().add(groupmembersField);

		ADDMEMBERS = new Button("Add");
		ADDMEMBERS.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
		AnchorPane.setTopAnchor(ADDMEMBERS, 50.0);
		AnchorPane.setRightAnchor(ADDMEMBERS, 125.0);
		screeen5.getChildren().add(ADDMEMBERS);


		DONE = new Button("Done");
		DONE.setStyle("-fx-text-fill: red; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(DONE, 100.0);
		AnchorPane.setRightAnchor(DONE, 125.0);
		screeen5.getChildren().add(DONE);

		return new Scene(screeen5, 500, 200);
	}

	public Scene createViewGroup() {
		AnchorPane screeen6 = new AnchorPane();
		Image backgroundImage = new Image("night-sky.jpg");
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.fitWidthProperty().bind(screeen6.widthProperty());
		backgroundImageView.fitHeightProperty().bind(screeen6.heightProperty());
		screeen6.getChildren().add(backgroundImageView);

		AllGUsers = new ListView<>();
		AllGUsers.setPrefSize(300, 500);
		AnchorPane.setTopAnchor(AllGUsers, 50.0);
		AnchorPane.setLeftAnchor(AllGUsers, 50.0);
		screeen6.getChildren().add(AllGUsers);

		BACKG1 = new Button("Back");
		BACKG1.setStyle("-fx-text-fill: red; -fx-font-weight: bold; ");
		AnchorPane.setTopAnchor(BACKG1, 600.0);
		AnchorPane.setRightAnchor(BACKG1, 190.0);
		screeen6.getChildren().add(BACKG1);

		return new Scene(screeen6, 400, 650);
	}



}
