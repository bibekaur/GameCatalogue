import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;


public class DeveloperInfoGUI extends JFrame{
	private Connection con;
	private JPanel panel;
	
	private String devName;
	private Integer devId;
	private String founded;
	private Integer loggedInUserId;
	
	public DeveloperInfoGUI(Connection sqlConnection, Integer devId){
		con = sqlConnection;
		this.devId = devId;
		
		//get the user details
		try{
			Statement s = con.createStatement();
			String query = "SELECT * FROM developer WHERE userId = '" + this.devId + "'";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				this.devName = rs.getString(2);
				this.founded = rs.getString(3).toString();
			}

			System.out.println("devId is: " + this.devId.toString());
			System.out.println("devName is: " + devName);
			System.out.println("joindate is: " + founded);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	
	public void setPanel(Integer loggedIn, JFrame frame){
		ArrayList<StoreData> top5games = new ArrayList<StoreData>();
		
		panel = new JPanel();
		loggedInUserId = loggedIn;

		JTextArea devInfo = new JTextArea("Developer: " + devName + "\nFounded in: "+founded.substring(0, founded.indexOf('.')));
		JTextArea top5userInfo = new JTextArea("Top 5 games from "+devName+ ":");
		
		panel.add(devInfo);
		panel.add(top5userInfo);
		
		try{
			Statement s = con.createStatement();
			String query = "SELECT g.gameName, g.gameGenre, g.gameId, o.rating, o.since FROM owns o, game g WHERE o.userId = '" + devId + "' AND o.gameId = g.gameId";
			ResultSet rs = s.executeQuery(query);
			while (rs.next()) {    
				 String gameName = rs.getString(1); 
				 String gameGenre = rs.getString(2);
				 Integer gameId = rs.getInt(3);
				 Integer rating = rs.getInt(4);
				 String since = rs.getString(5);
				 top5games.add(new StoreData(gameId, gameName, gameGenre, rating, since, new JButton("Game: "+gameName+" : "+gameGenre),
						                new JTextArea("Rated: "+rating.toString()+" On "+since)));
				 
				 System.out.println(gameName +" "+ gameGenre +" "+ rating.toString() +" "+ since);
			} 
		}
		catch (SQLException e1){
			e1.printStackTrace();
		}
		
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, top5userInfo, 2, SpringLayout.SOUTH, devInfo);
		
		panel.setLayout(layout);
		
		if (!top5games.isEmpty()) {
            panel.add(top5games.get(0).getButton());
            panel.add(top5games.get(0).getText());
            layout.putConstraint(SpringLayout.NORTH, top5games.get(0).getText(), 1, SpringLayout.SOUTH, top5games.get(0).getButton());
            
            layout.putConstraint(SpringLayout.NORTH, top5games.get(0).getButton(), 4, SpringLayout.SOUTH, top5userInfo);
            
            for (int i=1; i< top5games.size(); i++) {
                panel.add(top5games.get(i).getButton());
                panel.add(top5games.get(i).getText());
                layout.putConstraint(SpringLayout.NORTH, top5games.get(i).getText(), 1, SpringLayout.SOUTH, top5games.get(i).getButton());
                
                layout.putConstraint(SpringLayout.NORTH, top5games.get(i).getButton(), 4, SpringLayout.SOUTH, top5games.get(i-1).getText());
            }
        }
		
		frame.setContentPane(panel);
		frame.revalidate();
		frame.repaint();
	}
	

}
