import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/***********************************************************************************************
 * Program: BufferingPiRetweeter
 ***********************************************************************************************/
public class Main extends Application{
	
	/*******************************************************************************************
	 * Fields used throughout the entire program
	 *******************************************************************************************/
	//Keys to Twitter 
	public static String consumerKey = "";
	public static String consumerSecret = "";
	public static String accessToken = "";
	public static String accessSecret = "";
	
	//Other Fields
	public static Twitter twitter;
	public static ArrayList<TwitterHandleUsers> userList = new ArrayList<TwitterHandleUsers>();
	public static int arrayIncrement = 0;
	public static int delay = 1000; //Milliseconds
	public static int period = 1000 * 60 * 5; //Milliseconds converted to Minutes (5 minutes)
	public static TableView<TwitterHandleUsers> tblVw_Users;
	public static Timer timer;
	public static int countdown = 140;
	public static TextField txtFld_RetweetTags;
	
	//Logger to catch, log, and display errors when they occur.
	public static Logger logger = Logger.getLogger(Main.class.getName());
	
	
	/*******************************************************************************************
	 * Method: main()
	 * Purpose: Launches the program. Attains the users to retweet from an XML file. Then logs In to Twitter. Then
	 * 			it starts a timer to run every 5 minutes. Then it launches the GUI interface. 
	 * Params: N/A
	 *******************************************************************************************/
	public static void main(String[] args) {
		getUsersToRetweet();
		logIn();
		retweetingTimer();
		launch(args);
	}
	
	
	/*******************************************************************************************
	 * Method: retweetingTimer()
	 * Purpose: Launches a timer that runs every 5 minutes that will look at the user list, and look for tweets to retweet. 
	 * Params: N/A
	 *******************************************************************************************/
	public static void retweetingTimer(){
		
		//Create the timer.
		timer = new Timer();
		
		//Create the timer task at a scheduled rate.
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				//Iterate through the list of users, and retweet their status.
				for (TwitterHandleUsers user : userList){
					System.out.println(user.getTwitterHandle());
					getTwitterHandleAndRetweet(user.getTwitterHandle(), user.getUserStatus());
				}
			}
		}, delay, period); 	
	}
	
	/*******************************************************************************************
	 * Method: getUserList()
	 * Purpose: Create an ObservableList of users. Users are attained from an XML file. Use this list to display the
	 * 			users on a GUI table.
	 * Params: N/A
	 *******************************************************************************************/
	public static ObservableList<TwitterHandleUsers> getUserList(){
		ObservableList<TwitterHandleUsers> users = FXCollections.observableArrayList();
		
		for(int i = 0; i < userList.size(); i++){
        	users.add(userList.get(i));
        }
		return users;
	}
	
	/*******************************************************************************************
	 * Method: refreshTable()
	 * Purpose: Sets the table with the values from the getUserList(). This method clears the User classes, and clears the current list
	 * 			to be repopulated.
	 * Params: N/A
	 *******************************************************************************************/
	public static void refreshTable(){
		tblVw_Users.setItems(getUserList());
	}
	
	/*******************************************************************************************
	 * Method: start()
	 * Purpose: This is the GUI criteria. It sets all the parameters for the GUI and launches it. Also gives the buttons their functionality.
	 * Params: N/A
	 *******************************************************************************************/
	@Override
    public void start(Stage primaryStage) {
        
		//The following criteria sets the title, and builds the TableView tblVw_Users.
		primaryStage.setTitle("Welcome to the Retweet Machine for @BadCity_Gaming!");
        
        tblVw_Users = new TableView<TwitterHandleUsers>();
        tblVw_Users.setMaxSize(600, 600);
        
        TableColumn<TwitterHandleUsers, String> col1 = new TableColumn<>("User");
        tblVw_Users.getColumns().add(col1);
        col1.setMinWidth(120);
        
        TableColumn<TwitterHandleUsers, String> col2 = new TableColumn<>("Twitter Handle");
        tblVw_Users.getColumns().add(col2);
        col2.setMinWidth(150);
        
        TableColumn<TwitterHandleUsers, String> col3 = new TableColumn<>("Retweet Status");
        tblVw_Users.getColumns().add(col3);
        col3.setMinWidth(100);
        
        //Bind the columns of the table with User values,.
        col1.setCellValueFactory(new PropertyValueFactory<>("userName"));
        col2.setCellValueFactory(new PropertyValueFactory<>("twitterHandle"));
        col3.setCellValueFactory(new PropertyValueFactory<>("userStatus"));
        
        //Populate the table with the list created from the XML file.
        tblVw_Users.setItems(getUserList());
        
        Label lblUserTable = new Label("Select user to: Edit Info, Remove from List, or Choose to Retweet Them.");
        
        //Create and add functionality to a button to Add a new user.
        Button btn_Add = new Button();
        btn_Add.setText("Add New User");
        btn_Add.setMinWidth(120);
        btn_Add.setOnAction(new EventHandler<ActionEvent>() {
        	//Launch the AddEditWindow to add a new user.
            @Override
            public void handle(ActionEvent event) {
                AddEditWindow a = new AddEditWindow();
                a.start(true, "", "", "");
            }
        });
        
        //Create and add functionality to a button to edit a user.
        Button btn_Edit = new Button();
        btn_Edit.setText("Edit User");
        btn_Edit.setMinWidth(120);
        btn_Edit.setOnAction(new EventHandler<ActionEvent>() {
        	//Launch the AddEditWindow to edit a user.
            @Override
            public void handle(ActionEvent event) {
            	TwitterHandleUsers selectedUser = tblVw_Users.getSelectionModel().getSelectedItem();
            	AddEditWindow a = new AddEditWindow();
                a.start(false, selectedUser.getUserName(), selectedUser.getTwitterHandle(), selectedUser.getUserStatus());
            }
        });
        
        //Create and add functionality to a button to remove a user.
        Button btn_Remove = new Button();
        btn_Remove.setText("Remove User");
        btn_Remove.setMinWidth(120);
        btn_Remove.setOnAction(new EventHandler<ActionEvent>() {
        	//Remove the user.
            @Override
            public void handle(ActionEvent event) {
            	//Attain user to remove by selecting them from the table.
            	TwitterHandleUsers selectedUser = tblVw_Users.getSelectionModel().getSelectedItem();
            	
            	//If no user is selected, prompt the user.
            	if(selectedUser == null){
            		Alert alert = new Alert(AlertType.INFORMATION);
            		alert.setContentText("Please Select A User To Remove.");
                	alert.showAndWait();
                	return;
            	}
            	
            	//Remove the user from the XML file and table, and attain the new XML file values and add them to the table.
            	removeUser(selectedUser.getUserName());
            	getUsersToRetweet();
            }
        });
        
        //Labels for the retweeting messages.
        Label lbl_RetweetTags = new Label("What do you want to say in your retweet?");
        final Label lbl_LettersRemaining = new Label("Characters Left: 140");
        
        //Allow users to insert their own retweet messages. Put a counter in that ensures users stay within twitters 140 character limit.
        txtFld_RetweetTags = new TextField ();
        txtFld_RetweetTags.setMinWidth(500);
        txtFld_RetweetTags.setOnKeyPressed(new EventHandler<KeyEvent>(){
        	@Override
            public void handle(KeyEvent ke)
            {
        		String txt = txtFld_RetweetTags.getText();
        		char[] c = txt.toCharArray();
        		countdown = 140 - (c.length + 1);
        		lbl_LettersRemaining.setText("Characters Left: " + countdown);
            }
        	
        });
        
        //Set up the root portion of the GUI and add the GUI params.
        AnchorPane root = new AnchorPane();
        root.getChildren().add(tblVw_Users);
        root.getChildren().add(btn_Add);
        root.getChildren().add(btn_Edit);
        root.getChildren().add(btn_Remove);
        root.getChildren().add(lblUserTable);
        root.getChildren().add(txtFld_RetweetTags);
        root.getChildren().add(lbl_RetweetTags);
        root.getChildren().add(lbl_LettersRemaining);
        
        //TableView tblVw_Users Location
        root.getChildren().get(0).setLayoutX(10);
        root.getChildren().get(0).setLayoutY(25);
        
        //Button btn_Add Location
        root.getChildren().get(1).setLayoutX(395);
        root.getChildren().get(1).setLayoutY(25);
        
        //Button btn_Edit Location
        root.getChildren().get(2).setLayoutX(395);
        root.getChildren().get(2).setLayoutY(55);
        
        //Button btn_Remove Location
        root.getChildren().get(3).setLayoutX(395);
        root.getChildren().get(3).setLayoutY(85);
        
        //Label lblUserTable Location
        root.getChildren().get(4).setLayoutX(10);
        root.getChildren().get(4).setLayoutY(5);
        
        //Text Field txtFld_RetweetTags Location
        root.getChildren().get(5).setLayoutX(10);
        root.getChildren().get(5).setLayoutY(470);
        
        //Label lbl_RetweetTags Location
        root.getChildren().get(6).setLayoutX(10);
        root.getChildren().get(6).setLayoutY(450);
        
        //Label lbl_RetweetTags Location
        root.getChildren().get(7).setLayoutX(405);
        root.getChildren().get(7).setLayoutY(450);
        
        
        
        //Set the primary stage, and show it.
        primaryStage.setScene(new Scene(root, 530, 520));
        primaryStage.show();
        
        //Ensure the program actual stops all threads when the program is closed.
        //This code stops an issue I was having earlier where the program was closed, but the operation remained.
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
    }
	
	/*******************************************************************************************
	 * Method: removeUser()
	 * Purpose: Removes the user from the XML file.
	 * Params: userName -> User to remove.
	 *******************************************************************************************/
	public void removeUser(String userName){
		try {
			//Create the file and params needed to remove the user from the XML file.
			File fXmlFile = new File(GlobalVars.xmlUserListLocation);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			//Find the root node.
			NodeList mainNode = doc.getElementsByTagName("twitter");
			
			//Attain the user nodes.
			NodeList nList = doc.getElementsByTagName("user");
			
			//Iterate through the file and find the username to remove.
			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				//Current user node to compare
				Node nNode = nList.item(temp);
				
				//If the node is the right node, continue on
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					//If the node value equals the username value, remove the node and its sub contents.
					if(eElement.getElementsByTagName("userName").item(0).getTextContent().equals(userName)){
						//Remove the node
						mainNode.item(0).removeChild(nNode);
						doc.getDocumentElement().normalize();
						
						//Save the changes to the XML file.
						DOMSource source = new DOMSource(doc);
				        TransformerFactory transformerFactory = TransformerFactory.newInstance();
				        Transformer transformer = transformerFactory.newTransformer();
				        StreamResult result = new StreamResult(GlobalVars.xmlUserListLocation);
				        transformer.transform(source, result);
				        return;
					}
				}
			}
		//Catch exceptions but simply ignore them.
	    } catch (Exception e) {
	    }
	}
	
	/*******************************************************************************************
	 * Method: getTwitterHandleAndRetweet()
	 * Purpose: Iterates through the user list, then looks at their tweeting history. If the tweet criteria matches, retweet them.
	 * Params: twitterHandle -> twitter handle to find
	 * 		   retweetStatus -> status of whether or not to retweet the user.
	 *******************************************************************************************/
	public static void getTwitterHandleAndRetweet(String twitterHandle, String retweetStatus){
		
		//Create a list to store the users tweet history.
		List<Status> statusList = null;

		//Attain users status from their timeline.
	   try {
	        statusList = twitter.getUserTimeline(twitterHandle);
	        
//	        if(twitterHandle.equals("@Fr3oN17")){
//	        	//System.out.println(statusList.get(1).get));
//	        	System.out.println(statusList.get(1).getText());
//	        }
	   //Do nothing if an error occurs. 
	   } catch (TwitterException e) {
	    }

	   //Iterate through their history. I believe twitter attains the last 15 tweets from the user.
	    for (Status status : statusList) {
	    	//Create a calendar instance for time subtraction
	    	Calendar now = Calendar.getInstance();
            now.setTime(new java.util.Date());
            
            //Create a date for the tweets post time
            Date createdDate = status.getCreatedAt();
            Calendar postTime = Calendar.getInstance();
            postTime.setTime(createdDate);
            
            //Subtract the current time and the tweets posted time. We are looking for < 5 minutes.
            long diff = now.getTimeInMillis() - postTime.getTimeInMillis();
            diff = diff / 1000; //Millis to Seconds
            diff = diff / 60; //Seconds to Minutes
            
            //Find the tagToLookFor by breaking apart the status text using a String Tokenizer
            String delim = " \n\t"; 
            StringTokenizer st = new StringTokenizer(status.getText(),delim);
            Boolean found = false;
            while (st.hasMoreTokens()) {
                if(st.nextToken().equals(GlobalVars.tagToLookFor)){
                	found = true;
                }
            	
            }
            //If the diff is < 5 minutes, and the user retweet status is Active, and they have the @BadCity_Gaming in their tweet.
            if(diff < 5 && retweetStatus.equals("Active") && found){
            	if(countdown < 0){
            		Alert alert = new Alert(AlertType.INFORMATION);
                	alert.setTitle("Uh-Oh");
                	alert.setHeaderText(null);
                	alert.setContentText("Cannot Retweet. Please Ensure the Character Limit is 140 Characters or Less.");
                	alert.showAndWait();
                	return;
            	}
            	
            	//Retweet their status with the following values.
            	try {
            		
            		Status tempStatus = twitter.updateStatus(txtFld_RetweetTags.getText()
            				+ " https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());
				//Catch and log any errors
				} catch (TwitterException e) {
					logger.log(Level.WARNING, "Failure with Retweeting: " + e.getMessage());
				}
            }else{
            	break;
            }
	    }  
	}
	
	
	/*******************************************************************************************
	 * Method: getUsersToRetweet()
	 * Purpose: Attain the users to retweet by attaining their info from the XML file.
	 * Params: N/A
	 *******************************************************************************************/
	public static void getUsersToRetweet(){
		
		//Var that determines if the Table View for the users needs to be refreshed.
		boolean isRefreshTable = false;
		
		//If the user list is populated, clear the list and remove any user objects.
		if(userList.size() > 0){
			for(int i = 0; i < userList.size(); i++){
				userList.get(i).equals(null);
			}
			
			isRefreshTable = true;
			userList.clear();
		}
		
		//Find the XML file, and read the values from it. Find the user's and create TwitterHandleUsers objects from them.
		try {
			//Read the file
			File fXmlFile = new File(GlobalVars.xmlUserListLocation);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			//Look at the user nodes and iterate through them.
			NodeList nList = doc.getElementsByTagName("user");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				//Instance of the user node.
				Node nNode = nList.item(temp);
				
				//Ensure the nNode is indeed an Element Node
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					//Create the TwitterHandleUsers object
					TwitterHandleUsers user = new TwitterHandleUsers();
					user.setUserName(eElement.getElementsByTagName("userName").item(0).getTextContent());
					user.setTwitterHandle(eElement.getElementsByTagName("twitterHandle").item(0).getTextContent());
					user.setUserStatus(eElement.getElementsByTagName("userStatus").item(0).getTextContent());
					
					//Add the new object to the userList.
					userList.add(user);
				}
				
			}
		//Catch and log any exceptions	
	    } catch (Exception e) {
	    	logger.log(Level.WARNING, "Failure with XML Data: " + e.getMessage());
	    }
		
		//If the list was populated and cleared, then the table view will need to be refreshed. Do this here.
		if(isRefreshTable){
			refreshTable();
		}
	}
	
	
	/*******************************************************************************************
	 * Method: logIn()
	 * Purpose: This method will contact the Twitter service, and allow us to login using the credentials
	 * 			stored in the fields section of this class.
	 * Params: N/A
	 * *******************************************************************************************/
	public static void logIn(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(consumerKey)
		.setOAuthConsumerSecret(consumerSecret)
		.setOAuthAccessToken(accessToken)
		.setOAuthAccessTokenSecret(accessSecret)
		.setTweetModeExtended(true);
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		
	}
}
