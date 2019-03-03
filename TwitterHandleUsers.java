/*******************************************************************************************
 * Class: TwitterHandleUsers()
 * Purpose: Values for the users attained from the XML file.
 * Params: N/A
 * *******************************************************************************************/
public class TwitterHandleUsers {

	private String userName;
	private String twitterHandle;
	private String userStatus;
	
	//GETTERS AND SETTERS
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String name){
		userName = name;
	}
	
	public String getTwitterHandle(){
		return twitterHandle;
	}
	
	public void setTwitterHandle(String handle){
		twitterHandle = handle;
	}
	
	public String getUserStatus(){
		return userStatus;
	}
	
	public void setUserStatus(String status){
		userStatus = status;
	}
}
