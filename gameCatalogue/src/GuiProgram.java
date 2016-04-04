import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class GuiProgram extends JFrame{
    private Connection con;

    private JFrame frame;
    
    public GuiProgram() {
        setTitle("Game Catalogue");
        setSize(500,400); // default size is 0,0
        setLocation(400,200); // default is 0,0 (top left corner)
    }

//TODO: Some Queries
        //Query to find game by platform
        /*String query = "SELECT gameName, gameGenre "
                + "FROM game g INNER JOIN available a ON g.gameId = a.gameId "
                + "INNER JOIN platform p ON a.pId = p.pId "
                + "WHERE p.pName LIKE '%" + "query" + "%;";*/

        //Query to find game by average rating?
        /*String query = "SELECT gameName, gameGenre, avg_rating"
                + "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
                + "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
                + "GROUP BY g.gameId)"
                + "WHERE avg_rating > " + 5;*/

        //Query to find the top 10 best games
//	    String query = "SELECT gameName, gameGenre, avg_rating"
//	    		+ "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
//	    		+ "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
//	    		+ "GROUP BY g.gameId)"
//	    		+ "WHERE avg_rating > " + 5;*/
//
//	    //Query to find the top 10 best games
////	    String query = "SELECT gameName, gameGenre, avg_rating"
////	    		+ "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
////	    		+ "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
////	    		+ "GROUP BY g.gameId) "
////	    		+ "ORDER BY avg_rating LIMIT 10";
//
//	    //Query to find the top 10 best rated users -- this needs some testing
////	    String query = "SELECT username, avg_rating"
////	    		+ "FROM (SELECT username, AVG(rating) AS avg_rating"
////	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
////	    		+ "GROUP BY u.username)"
////	    		+ "ORDER BY avg_rating LIMIT 10";
//
//	    //Query to find games played by the top 10 reated users
////	    String query = "SELECT gameName, gameGenre "
////	    		+ "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
////	    		+ "WHERE o.userId IN (SELECT userId"
////	    		+ "FROM (SELECT username, userId, AVG(rating) AS avg_rating"
////	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
////	    		+ "GROUP BY u.username)"
////	    		+ "ORDER BY avg_rating LIMIT 10)";
//
//	    //Select games that are owned by everybody
//	    //This SHOULD be division.
//	    String query = "SELECT gameName, gameGenre "
//	    		+ "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
//	    		+ "WHERE o.userId IN (SELECT userId"
//	    		+ "FROM (SELECT username, userId, AVG(rating) AS avg_rating"
//	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
//	    		+ "GROUP BY u.username)"
//	    		+ "ORDER BY avg_rating LIMIT 10)";

        //Select games that are owned by everybody
        //This SHOULD be division.
//        String query = "SELECT gameName, gameGenre "
//                + "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
//                + "WHERE o.userID in (SELECT userID"
//                + "FROM users u)"
//                + "GROUP BY gameName"
//                + "HAVING COUNT(*) = (SELECT COUNT(*) FROM users)";
//    }

    public void run() {
        try{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Change the below line to match your oracle username/password
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_b8k8", "a33858127");
            //con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ug", "ora_r9j8", "a15093123");
            //con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ug", "ora_r9j8", "a15093123");

        }catch(Exception e){
            e.printStackTrace();
        }

        frame = new GuiProgram();

        frame.setLayout(new FlowLayout());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LoginGUI g = new LoginGUI(con);
        g.setPanel(frame);
        frame.setVisible(true);
    }

    public static void main (String [ ] args) {
        GuiProgram g = new GuiProgram();

        g.run();
    }
}
