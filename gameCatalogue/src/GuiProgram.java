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

    public void run() {
        try{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Change the below line to match your oracle username/password
            //con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_i2m8", "a92859115");
            //con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ug", "ora_r9j8", "a15093123");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_o2n8", "a39088125");

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
