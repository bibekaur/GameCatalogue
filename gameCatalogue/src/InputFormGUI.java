import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class InputFormGUI extends JFrame{
	private JPanel panel;
	private JFrame frame;
	private Connection con;
	
	ArrayList<Component> visible = new ArrayList<Component>();

	public InputFormGUI (Connection con){
		this.con = con;
	}
	
	private void addGameButtonListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});	
	}
	
	private void addPlatformButtonListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});	
	}
	
	private void addDeveloperButtonListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
	}
	
	public void setPanel (JFrame frame){
		this.frame = frame;
		this.panel = new JPanel();
		
		//Need three radio buttons: Game, Platform, Developer
		JRadioButton game = new JRadioButton("Game");
		JRadioButton platform = new JRadioButton("Platform");
		JRadioButton developer = new JRadioButton("Developer");
		ButtonGroup options = new ButtonGroup();
		options.add(game);
		options.add(platform);
		options.add(developer);
		
		//Add action listeners to each button, each displays the appropriate form
		//addGameButtonListener(game);
		//addPlatformButtonListener(platform);
		addDeveloperButtonListener(developer);
		
		
		this.panel.add(game);
		this.panel.add(platform);
		this.panel.add(developer);
		this.frame.setContentPane(panel);
		this.frame.revalidate();
		this.frame.repaint();
		
		
	}
	
}
