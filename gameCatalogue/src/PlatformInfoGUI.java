import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class PlatformInfoGUI extends JFrame{
	private Integer loggedInUserId;
	private Connection con;
	private JFrame frame;
	private JLabel ratingLabel;
	
	private Integer platformId;
	private String platformName;
	private String platformPrice;
	private String platformGameInfo;
	
	private boolean isOwned;
	private boolean isWishlist;
	private Integer ownedRating;
	private Integer wishlistRating;
	
	private JPanel panel;
	
	public PlatformInfoGUI (Integer pId, Integer userId, Connection sqlConnection){
		loggedInUserId = userId;
		con = sqlConnection;
		platformId = pId;
		
		//Get the platform details
		try {
			Statement s = con.createStatement();
			String query = "SELECT * from platform WHERE pId = " + pId;
			ResultSet rs = s.executeQuery(query);
			System.out.println("Hi");
			if (rs.next()){
				platformName = rs.getString("pName");
				Double tempPrice = rs.getDouble("cost");
				if (tempPrice == 0){
					platformPrice = "N/A";
				}
				else{
					platformPrice = tempPrice.toString();
				}
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
		platformGameInfo = getGamesForPlatform();
		
		//TODO: Check if the user is a moderator
		
		//TODO: Get the average rating
	}
	

	private String getGamesForPlatform(){
		try {
			Statement s = con.createStatement();
			
			String query = "SELECT gameName, gameGenre, avg_rating "
					+ "FROM "
					+ "(SELECT g.gameId, g.gameName, g.gameGenre, AVG(r.rating) AS avg_rating "
					+ "FROM game g INNER JOIN available a ON g.gameId = a.gameId "
					+ "INNER JOIN platform p ON a.pId = p.pId "
					+ "INNER JOIN review r ON g.gameId = r.gameId"
					+ " WHERE p.pId = " +platformId  
					+ " GROUP BY g.gameId, g.gameName, g.gameGenre"
					+ " ORDER BY avg_rating)"
					+ " WHERE ROWNUM <= 10";
			
			ResultSet rs = s.executeQuery(query);
			
			StringBuilder sb = new StringBuilder();

			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columnsNumber = rsmd.getColumnCount();

			while (rs.next()){
				sb.append(rs.getString("gameName"));
				sb.append(" - ");
				sb.append(rs.getString("gameGenre"));
				sb.append(" - ");
				sb.append(rs.getString("avg_rating"));
				sb.append("\n");
			}
			if (columnsNumber < 10){
				System.out.println("OK");
				String number = new Integer(10-columnsNumber).toString();
				//On a side note, this is quite possibly
				//One of the ugliest queries I've ever written
				query = "SELECT gameName, gameGenre "
						+ "FROM "
						+ "(SELECT g.gameId, g.gameName, g.gameGenre "
						+ "FROM game g INNER JOIN available a ON g.gameId = a.gameId "
						+ "INNER JOIN platform p ON a.pId = p.pId "
						+ " WHERE p.pId = " +platformId  
						+ " AND g.gameId NOT IN ("
						+ "SELECT gameId "
						+ "FROM "
						+ "(SELECT g.gameId, g.gameName, g.gameGenre, AVG(r.rating) AS avg_rating "
						+ "FROM game g INNER JOIN available a ON g.gameId = a.gameId "
						+ "INNER JOIN platform p ON a.pId = p.pId "
						+ "INNER JOIN review r ON g.gameId = r.gameId"
						+ " WHERE p.pId = " +platformId  
						+ " GROUP BY g.gameId, g.gameName, g.gameGenre"
						+ " ORDER BY avg_rating)"
						+ " WHERE ROWNUM <= 10"
						+ "))"
						+ " WHERE ROWNUM <= " + number;
				
				rs = s.executeQuery(query);
				while (rs.next()){
					sb.append(rs.getString("gameName"));
					sb.append(" - ");
					sb.append(rs.getString("gameGenre"));
					sb.append("\n");
				}
			}
			if (sb.toString().isEmpty()){
				return "No games are available for this platform.";
			}
			return sb.toString();
			
			
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
		return "No games for this platform.";
		
	}



	
//	private void addOwnButtonListener(JButton ownButton){
//		ownButton.addActionListener(new);
//	}
	
	public void setPanel(JFrame frame){
		panel = new JPanel();
		JTextArea consoleInfo = new JTextArea("Platform: " + platformName + "\nPrice: "+ platformPrice);
		JTextArea consoleGameInfo = new JTextArea(platformGameInfo);
		System.out.println(platformGameInfo);
		consoleInfo.setEditable(false);
		consoleGameInfo.setEditable(false);

		JButton mainPage = new JButton("Main Page");
		panel.add(mainPage);
		//layout.putConstraint(SpringLayout.WEST, mainPage, 200, SpringLayout.EAST, userInfo);
		mainPage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				//TODO call main page function
			}
		});
		
		panel.add(consoleInfo);
		panel.add(consoleGameInfo);
		frame.setContentPane(panel);
		frame.revalidate();
		frame.repaint();
		System.out.println("Gaba");
	}
}
