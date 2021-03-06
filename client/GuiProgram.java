import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiProgram extends JFrame{
	private JFrame frame;
	private JPanel panel;
	private JTextField loginNameBox;
	private JTextField loginPasswordBox;
	private JButton loginButton;
	private JButton SignUpButton;
	
	public GuiProgram() {
		setTitle("Game Catalogue");
		setSize(500,400); // default size is 0,0
		setLocation(400,200); // default is 0,0 (top left corner)
	}
	
	public void init() {
		frame = new GuiProgram();
		panel = new JPanel();
	    
		final JLabel loginName = new JLabel("Login NAME: ");
		loginNameBox = new JTextField(10);
		final JLabel loginPassword = new JLabel("Login PASSWORD: ");
		loginPasswordBox = new JTextField(10);
		loginButton = new JButton("Login");
		SignUpButton = new JButton("Sign-up");
		
		panel.add(loginName);
		panel.add(loginNameBox);
		panel.add(loginPassword);
		panel.add(loginPasswordBox);
		panel.add(loginButton);
		panel.add(SignUpButton);

		frame.setContentPane(panel);
		frame.setLayout(new FlowLayout());
		frame.setResizable(true);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    loginButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
			    String loginName = loginNameBox.getText();
			    String loginPassword = loginPasswordBox.getText();
			    System.out.println("logged in as "+loginName+" with password: "+loginPassword);
			    if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
			    	//TODO See if the user exists from the database
					System.out.println("Login Got here!");
					
					frame.remove(panel);
					JButton box = new JButton("hello");
					panel = new JPanel();
					panel.add(box);
	                frame.setContentPane(panel);
	                
					frame.revalidate();	
					frame.repaint();
				}
			}
	    });
	    SignUpButton.addActionListener(new ActionListener() {
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
	}
	
	public static void main (String [ ] args) {
	    GuiProgram g = new GuiProgram();
	    g.init();
	}
}
