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
	private JTextField platformName;
	private JTextField platformCost;
	private JTextField platformDate;
	
	ArrayList<Component> visible = new ArrayList<Component>();
	ArrayList<String> developers = new ArrayList<String>();
	ArrayList<String> platforms = new ArrayList<String>();
	ArrayList<Integer> dIds = new ArrayList<Integer>();
	ArrayList<Integer> pIds = new ArrayList<Integer>();

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
				String pName = platformName.getText();
				String date = platformDate.getText();
				Float cost = (float) -1.0;
				
				if (!platformCost.getText().isEmpty()){
					try {
						cost = Float.parseFloat(platformCost.getText());
					} catch(NumberFormatException e){
						JOptionPane.showMessageDialog(frame, "Cost must be a number (can be a float)");
						return;
					}
				}
				
				
				
				if (pName == null || pName.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Please enter a platform name");
					return;
				}
				else if (platforms.contains(pName)){
					JOptionPane.showMessageDialog(frame, "This platform already exists");
					return;
				}
				else if (pName.length()> 100){
					JOptionPane.showMessageDialog(frame, "Platform name is too long (limit 100 chars)");
					return;
				}
				
				try {
					Statement s = con.createStatement();
					String query = "INSERT into platform VALUES (DEFAULT,'" + pName + "'," + cost + ","
							+ "to_date('" + date + "', 'YYYY-MM-DD'))";
					System.out.println(query);
					ResultSet rs = s.executeQuery(query);
					
					
				} catch (SQLException e1){
					JOptionPane.showMessageDialog(frame, "Please enter a valid date with the format YYYY-MM-DD");
					return;
				}
				
				if (cost == (float)-1.0 && date.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Succussfully added " + pName + " to platforms");
					
				}
				
				else if (date.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Succussfully added " + pName + " to platforms, priced at $"+cost);
				}
				else if ( cost == (float)-1.0){
					JOptionPane.showMessageDialog(frame, "Succussfully added " + pName + " to platforms, released on "+date);

				}
				else {
					JOptionPane.showMessageDialog(frame, "Succussfully added " + pName + " to platforms, priced at $"+cost + ", "
							+ "released on "+date);

				}
			
				
					
				platforms.add(pName);
			}
		});	
	}
	
	private void addDeveloperListener(JButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String dName = developerName.getText();
				String founded = developerFoundedDate.getText();
				
				if (dName == null || dName.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Please enter a developer name");
					return;
				}
				else if (dName.length() > 100){
					JOptionPane.showMessageDialog(frame, "Developer name is too long (limit 100 chars)");
					return;
				}
				else if (developers.contains(dName)){
					JOptionPane.showMessageDialog(frame, "Developer already exists");
					return;
				}
				
						
				try{
					Statement s = con.createStatement();
					String query = "INSERT into developer VALUES (DEFAULT,'" + dName + "',to_date('" + founded + "', 'YYYY-MM-DD'))";
					ResultSet rs = s.executeQuery(query);				
					
				} catch (SQLException e1){
					JOptionPane.showMessageDialog(frame, "Please enter a valid date with the format YYYY-MM-DD");
					return;
				}
				if (founded == null || founded.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Successfully added " + dName);

				}
				else {
					JOptionPane.showMessageDialog(frame, "Successfully added " + dName + ", founded on " + founded);

				}
				developers.add(dName);
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
				JLabel name = new JLabel("*Platform Name (limit 100 chars)");
				JLabel cost = new JLabel ("Cost  $");
				JLabel date = new JLabel ("Release Date (YYYY-MM-DD)");
				
				removeComponents();
				
				//Update platform list
				updatePlatforms();
				
				platformName = new JTextField(20);
				platformCost = new JTextField(10);
				platformDate = new JTextField(20);
				
				JButton addPlatform = new JButton("Add");
				addPlatformButtonListener(addPlatform);
				
				visible.add(name);
				visible.add(cost);
				visible.add(date);
				visible.add(platformName);
				visible.add(platformCost);
				visible.add(platformDate);
				visible.add(addPlatform);
				panel.add(name);
				panel.add(cost);
				panel.add(date);
				panel.add(platformName);
				panel.add(platformCost);
				panel.add(platformDate);
				panel.add(addPlatform);
				frame.revalidate();
				frame.repaint();
			}
		});
	}
	
	private void drawDeveloperFormListener(JRadioButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JLabel name = new JLabel("*Developer Name (limit 100 chars)");
				JLabel date = new JLabel("Founded (YYYY-MM-DD)");
				
				removeComponents();
				
				//Update the list of developers
				updateDevelopers();
				
				//Add a developer name text field
				developerName = new JTextField(20);
				
				//Add a founded date text field
				developerFoundedDate = new JTextField(20);
				
				//Add an add button
				JButton addDeveloper = new JButton("Add");
				addDeveloperListener(addDeveloper);
				
				visible.add(name);
				visible.add(date);
				visible.add(developerName);
				visible.add(developerFoundedDate);
				visible.add(addDeveloper);
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
	
	private void removeComponents(){
		for (Component c : visible){
			panel.remove(c);
		}
	}
	
	private void updateDevelopers(){
		try {
			Statement s = con.createStatement();
			String query = "SELECT dName,dId from developer";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				this.developers.add(rs.getString(1));
				this.dIds.add(rs.getInt(2));
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
	}
	
	private void updatePlatforms(){

		try{
			Statement s = con.createStatement();
			String query = "SELECT pName, pId from platform";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				this.platforms.add(rs.getString(1));
				this.pIds.add(rs.getInt(2));
			}
		} catch (SQLException e1){
			e1.printStackTrace();
		}
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
		drawPlatformFormListener(platform);
		drawDeveloperFormListener(developer);
		
		
		this.panel.add(game);
		this.panel.add(platform);
		this.panel.add(developer);
		this.frame.setContentPane(panel);
		this.frame.revalidate();
		this.frame.repaint();
		
		
	}
	
}
