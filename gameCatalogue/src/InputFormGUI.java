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
	
	private JTextField developerName;
	private JTextField developerFoundedDate;
	
	ArrayList<Component> visible = new ArrayList<Component>();

	public InputFormGUI (Connection con){
		this.con = con;
	}
	
	private void addGameButtonListener(JButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});	
	}
	
	private void addPlatformButtonListener(JButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});	
	}
	
	private void addDeveloperListener(JButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String dName = developerName.getText();
				String founded = developerFoundedDate.getText();
				
				if (dName == null){
					JOptionPane.showMessageDialog(frame, "Please enter a developer name");
					return;
				}
				else if (dName.length() > 100){
					JOptionPane.showMessageDialog(frame, "Developer name is too long (limit 100 chars)");
					return;
				}
				
				if (founded == null) {
					founded = "n/a";
				}
				
				
				
				try{
					Statement s = con.createStatement();
					String query = "INSERT into developer VALUES (DEFAULT,'" + dName + "',to_date('" + founded + "', 'YYYY-MM-DD'))";
					System.out.println(query);
					ResultSet rs = s.executeQuery(query);				
					
				} catch (SQLException e1){
					JOptionPane.showMessageDialog(frame, "Please enter a valid date with the format YYYY-MM-DD");
					return;
				}
				
				JOptionPane.showMessageDialog(frame, "Successfully added " + dName + ", founded on " + founded);
			}
		});	
	}
	
	private void drawGameFormListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
	}
	
	private void drawPlatformFormListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
	}
	
	private void drawDeveloperFormListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JLabel name = new JLabel("Developer Name (limit 100 chars)");
				JLabel date = new JLabel("Founded (YYYY-MM-DD)");
				
				//Add a developer name text field
				developerName = new JTextField(20);
				
				//Add a founded date text field
				developerFoundedDate = new JTextField(20);
				
				//Add an add button
				JButton addDeveloper = new JButton("Add");
				addDeveloperListener(addDeveloper);
				
				panel.add(name);
				panel.add(date);
				panel.add(developerName);
				panel.add(developerFoundedDate);
				panel.add(addDeveloper);
				frame.revalidate();
				frame.repaint();
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
		//drawGameFormListener(game);
		//drawPlatformFormListener(platform);
		drawDeveloperFormListener(developer);
		
		
		this.panel.add(game);
		this.panel.add(platform);
		this.panel.add(developer);
		this.frame.setContentPane(panel);
		this.frame.revalidate();
		this.frame.repaint();
		
		
	}
	
}
