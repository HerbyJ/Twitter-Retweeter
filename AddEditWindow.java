import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******************************************************************************************
 * Class: AddEditWindow()
 * Purpose: Creates a GUI for adding/editing a user.
 * Params: N/A
 * *******************************************************************************************/
public class AddEditWindow {
	
	/*******************************************************************************************
	 * Class: start()
	 * Purpose: Creates a GUI for adding/editing a user.
	 * Params: isAdd -> this param indicates if this is a new user to add
	 * 		   userName -> username to edit or add
	 * 		   twitterHandle -> twitterHandle to edit or add
	 * 		   userStatus -> determines whether or not to retweet the user based on values: Active or NotActive
	 * *******************************************************************************************/
	public void start(boolean isAdd, String userName, String twitterHandle, String userStatus) {
        //All the following is GUI variables
		Stage primaryStage = new Stage();
        Label lbl_Header;
        Label lbl_UserName;
        Label lbl_Handle;
        Label lbl_Status;
        
        String oldName;
        String oldHandle;
        String oldStatus;
        
        TextField txtFld_UserName = new TextField();
        TextField txtFld_Handle = new TextField();
        
        ComboBox cBx_Status = new ComboBox();
        cBx_Status.getItems().addAll("Active", "Inactive");
        
        if(isAdd){
        	primaryStage.setTitle("Add New Twitter User");
        	lbl_Header = new Label("Add User Information");
        }
        else{
        	primaryStage.setTitle("Edit Selected Twitter User");
        	lbl_Header = new Label("Edit User Information.");
        }
        
        lbl_Header.setFont(Font.font(16));
        
        lbl_UserName = new Label("Enter Twitter Name:");
        lbl_Handle = new Label("Enter Twitter Handle:");
        lbl_Status = new Label("Start Retweeting Now?");
        
        txtFld_UserName.setText(userName);
        txtFld_Handle.setText(twitterHandle);
        
        if(!userStatus.equals("")){
        	cBx_Status.setValue(userStatus);
        }
        
        //Vars if the isAdd is false.
        oldName = userName;
        oldHandle = twitterHandle;
        oldStatus = userStatus;
        
        //Submit button to save the changes of the user information.
        Button btn_Submit = new Button();
        btn_Submit.setText("SUBMIT");
        btn_Submit.setMinWidth(80);
        btn_Submit.setOnAction(new EventHandler<ActionEvent>() {
        	//Button action
            @Override
            public void handle(ActionEvent event) {
                
            	//Create an alert and look for specific criteria before submitting the changes.
            	Alert alert = new Alert(AlertType.INFORMATION);
            	alert.setTitle("Uh-Oh");
            	alert.setHeaderText(null);
                
            	//Look at the twitter handle and determine if it is populated. This will help with any errors later.
            	String handle = txtFld_Handle.getText().toString();
            	char c = 0;
            	if(!handle.equals("")){
            		c = handle.charAt(0);
            	}
            	
            	//Ensure the username is filled in
                if(txtFld_UserName.getText().equals("")){
                	alert.setContentText("Please Enter the Twitter Name.");
                	alert.showAndWait();
                	return;
                }
                
                //Ensure the handle is filled in
                if(txtFld_Handle.getText().equals("")){
                	alert.setContentText("Please Enter the Twitter Handle.");
                	alert.showAndWait();
                	return;
                }
                
                //Ensure the handle starts with an @
                if(c != '@'){
                	alert.setContentText("Twitter Handle Needs To Have '@' In The Front.");
                	alert.showAndWait();
                	return;
                }
                
                //Ensure retweeting status is selected
                try{
                	if(cBx_Status.getValue().equals(null)){
                    	alert.setContentText("Please Select Retweeting Status.");
                    	alert.showAndWait();
                    	return;
                    }
                }
                catch(NullPointerException e){
                	alert.setContentText("Please Select Retweeting Status.");
                	alert.showAndWait();
                	return;
                }
                
                //Add the user if isAdd is true, otherwise change the already existing user information
                if(isAdd){
                	addUser(txtFld_UserName.getText(), txtFld_Handle.getText(), cBx_Status.getValue().toString());
                	alert.setContentText("Twitter User Added Successfully.");
                	alert.showAndWait();
                }
                else{
                	editUser(oldName, txtFld_UserName.getText(), oldHandle, txtFld_Handle.getText(), oldStatus, cBx_Status.getValue().toString());
                	alert.setContentText("Twitter User Edited Successfully.");
                	alert.showAndWait();
                }
                
                //Once the changes were made, re-attain the user status from the XML file. Close this GUI.
                Main.getUsersToRetweet();
            	primaryStage.close();
            }
        });
        
        //Add functionality to the Cancel button. Simply closes the GUI.
        Button btn_Cancel = new Button();
        btn_Cancel.setText("Cancel");
        btn_Cancel.setMinWidth(80);
        btn_Cancel.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        
        //Add the params to the GUI
        AnchorPane root = new AnchorPane();
        root.getChildren().add(lbl_Header);
        root.getChildren().add(lbl_UserName);
        root.getChildren().add(lbl_Handle);
        root.getChildren().add(lbl_Status);
        root.getChildren().add(txtFld_UserName);
        root.getChildren().add(txtFld_Handle);
        root.getChildren().add(cBx_Status);
        root.getChildren().add(btn_Submit);
        root.getChildren().add(btn_Cancel);
        
        //Label lbl_Header Position
        root.getChildren().get(0).setLayoutX(75);
        root.getChildren().get(0).setLayoutY(5);
        
        //Label lbl_UserName Position
        root.getChildren().get(1).setLayoutX(24);
        root.getChildren().get(1).setLayoutY(50);
        
        //Label lbl_Handle Position
        root.getChildren().get(2).setLayoutX(19);
        root.getChildren().get(2).setLayoutY(90);
        
        //Label lbl_Status Position
        root.getChildren().get(3).setLayoutX(10);
        root.getChildren().get(3).setLayoutY(130);
        
        //TextField txtFld_UserName Position
        root.getChildren().get(4).setLayoutX(135);
        root.getChildren().get(4).setLayoutY(47);
        
        //TextField txtFld_Handle Position
        root.getChildren().get(5).setLayoutX(135);
        root.getChildren().get(5).setLayoutY(87);
        
        //ComboBox cBx_Status Position
        root.getChildren().get(6).setLayoutX(135);
        root.getChildren().get(6).setLayoutY(127);
        
        //Button btn_Submit Position
        root.getChildren().get(7).setLayoutX(60);
        root.getChildren().get(7).setLayoutY(180);
        
        //Button btn_Cancel Position
        root.getChildren().get(8).setLayoutX(160);
        root.getChildren().get(8).setLayoutY(180);
        
        primaryStage.setScene(new Scene(root, 300, 220));
        primaryStage.show();
    }
	
	/*******************************************************************************************
	 * Class: addUser()
	 * Purpose: Add the user by adding them to the XML 
	 * Params: userName -> username to add
	 * 		   twitterHandle -> twitterHandle to add
	 * 		   userStatus -> determines whether or not to retweet the user based on values: Active or NotActive
	 * *******************************************************************************************/
	public void addUser(String userName, String twitterHandle, String userStatus){
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder;
	        
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(GlobalVars.xmlUserListLocation);
	        Element root = document.getDocumentElement();

	        Element user = document.createElement("user");

	        Element name = document.createElement("userName");
	        name.appendChild(document.createTextNode(userName));
	        user.appendChild(name);

	        Element handle = document.createElement("twitterHandle");
	        handle.appendChild(document.createTextNode(twitterHandle));
	        user.appendChild(handle);

	        Element status = document.createElement("userStatus");
	        status.appendChild(document.createTextNode(userStatus));
	        user.appendChild(status);
	        
	        root.appendChild(user);

	        DOMSource source = new DOMSource(document);

	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        StreamResult result = new StreamResult(GlobalVars.xmlUserListLocation);
	        transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	public void editUser(String oldUserName, String newUserName, String oldHandle, String newHandle, String oldStatus, String newStatus){
		try {
			File fXmlFile = new File(GlobalVars.xmlUserListLocation);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("user");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					if(eElement.getElementsByTagName("userName").item(0).getTextContent().equals(oldUserName)){
						//System.out.println("YAYAYAYA!!!!");
						eElement.getElementsByTagName("userName").item(0).setTextContent(newUserName);
						eElement.getElementsByTagName("twitterHandle").item(0).setTextContent(newHandle);
						eElement.getElementsByTagName("userStatus").item(0).setTextContent(newStatus);
						
						DOMSource source = new DOMSource(doc);

				        TransformerFactory transformerFactory = TransformerFactory.newInstance();
				        Transformer transformer = transformerFactory.newTransformer();
				        StreamResult result = new StreamResult(GlobalVars.xmlUserListLocation);
				        transformer.transform(source, result);
				        
				        return;
					}
					
					
				}
				
			}
			
			
			
	    } catch (Exception e) {
	    	//logger.log(Level.WARNING, "Failure with XML Data: " + e.getMessage());
	    }
	}
}
