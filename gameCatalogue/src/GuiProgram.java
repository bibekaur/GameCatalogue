import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GuiProgram extends JFrame{
	private Connection con;
	
	private JFrame frame;
	private JPanel panel;
	private JTextField loginNameBox;
	private JTextField loginPasswordBox;
	private JButton loginButton;
	private JButton signUpButton;
	private JButton searchButton;
	
	private JTextField searchField;
	
	public GuiProgram() {
		setTitle("Game Catalogue");
		setSize(500,400); // default size is 0,0
		setLocation(400,200); // default is 0,0 (top left corner)
	}
	
	//Splitting it into initializing what the buttons do
	public void init(){
		
	    loginButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
			    String loginName = loginNameBox.getText();
			    String loginPassword = loginPasswordBox.getText();
			    System.out.println("logged in as "+loginName+" with password: "+loginPassword);
			    if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
			    	//TODO See if the user exists from the database
					System.out.println("Login Got here!");
					
					try {
						Statement s = con.createStatement();
						String query = "SELECT username, password FROM user WHERE username IS " + loginName + "AND password IS " + loginPassword;
						ResultSet rs = s.executeQuery(query);
						if (!rs.isBeforeFirst() ) {    
							 System.out.println("No username/password combo found"); 
						} 
						else {
							frame.remove(panel);
							JButton box = new JButton("hello");
							panel = new JPanel();
							panel.add(box);
			                frame.setContentPane(panel);
			                
							frame.revalidate();	
							frame.repaint();	
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
	    });
	    signUpButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
				String loginName = loginNameBox.getText();
				String loginPassword = loginPasswordBox.getText();
				System.out.println("signed up as "+loginName+" with password: "+loginPassword);
				if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
					//TODO add user info in the database
					System.out.println("Signup Got here!");
				}
	    	}
	    });
	    
	    searchButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		String search = searchField.getText();
	    		System.out.println("Attempting to search for ");
	    		//Perform query and return here;
	    	}
	    });
	}
	
	public void run() {
	    try{
	    	DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	    	//Change the below line to match your oracle username/password
	    	con = DriverManager.getConnection("jdbc:oracle:thin@localhost:1522:ug", "ora_i2m8", "a92859115");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
		
		frame = new GuiProgram();
		panel = new JPanel();

		final JLabel loginName = new JLabel("Login NAME: ");
		loginNameBox = new JTextField(10);
		final JLabel loginPassword = new JLabel("Login PASSWORD: ");
		loginPasswordBox = new JPasswordField(10);
		loginButton = new JButton("Login");
		signUpButton = new JButton("Sign-up");
		searchButton = new JButton("Search");
		
		panel.add(loginName);
		panel.add(loginNameBox);
		panel.add(loginPassword);
		panel.add(loginPasswordBox);
		panel.add(loginButton);
		panel.add(signUpButton);

		frame.setContentPane(panel);
		frame.setLayout(new FlowLayout());
		frame.setResizable(true);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
		init();
	    
		
	}
	
	public static void main (String [ ] args) {
	    GuiProgram g = new GuiProgram();

	    g.run();
	}
}
