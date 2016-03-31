import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserProfileGUI extends JFrame{
	private Connection con;
	private JFrame frame;
	private JPanel panel;
	
	private String username;
	
	public UserProfileGUI(Connection sqlConnection, String user){
		con = sqlConnection;
		username = user;
	}
	
	private UserProfileGUI(){
		setSize(500,400); // default size is 0,0
		setLocation(400,200); // default is 0,0 (top left corner)
	}
	
	public void run(){
		frame = new UserProfileGUI();
		panel = new JPanel();
		
		
	}
	

}
