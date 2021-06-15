import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JFrame{
	static int input = Helper.work_hour * 3600 + Helper.work_minute * 60 + Helper.work_second;
	static int Hour = 1;
	static int Minute = 1;
	static int Second = 1;

	public Main(String user) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						if(Hour == 0 && Minute == 0 && Second == 0) {
							Date date = new Date();
							try {
								Connection connection = DriverManager.getConnection(Helper.url, Helper.username, Helper.password);
								Statement statement = connection.createStatement();
								statement.execute("INSERT INTO log (l_user, l_status, l_date) VALUES ('" + user + "', 'Истекло время работы', '" + date + "')");
								connection.close();
								timer.cancel();
								dispose();
								Authorization auth = new Authorization();
								auth.setVisible(true);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						Hour = (input % 86400 ) / 3600 ;
						Minute = ((input % 86400 ) % 3600 ) / 60; 
						Second = ((input % 86400 ) % 3600 ) % 60;
						setTitle("Оставшееся время работы " + Hour + ":" + Minute + ":" + Second);
						input--;
					}
				}, 0, 1000);
			}
		});
		
	}
	
	public static void main(String[] args) {
		Authorization asd = new Authorization();
		asd.setVisible(true);
	}
}