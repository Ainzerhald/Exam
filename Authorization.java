import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.JLabel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Point;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import java.awt.Font;

public class Authorization extends JFrame{
	private JTextField login;
	private JPasswordField password;
	public Authorization() {
		setResizable(false);
		setLocation(new Point(450, 200));
		setTitle("\u0410\u0432\u0442\u043E\u0440\u0438\u0437\u0430\u0446\u0438\u044F");
		setMinimumSize(new Dimension(330, 200));
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("");
		label.setBounds(30, 94, 275, 23);
		getContentPane().add(label);
		
		login = new JTextField();
		login.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(KeyEvent.getKeyText(arg0.getKeyCode()).equals("Enter")) {
					password.grabFocus();
				}
			}
		});
		login.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				label.setText("");
			}
		});
		login.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				label.setText("");
			}
		});
		login.setBounds(89, 32, 154, 20);
		getContentPane().add(login);
		login.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u041B\u043E\u0433\u0438\u043D");
		lblNewLabel.setBounds(30, 32, 49, 20);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\u041F\u0430\u0440\u043E\u043B\u044C");
		lblNewLabel_1.setBounds(30, 63, 49, 20);
		getContentPane().add(lblNewLabel_1);
		
		JButton Enter = new JButton("\u0412\u0445\u043E\u0434");
		
		Enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!login.getText().equals("") && !password.getText().equals("")) {
					try {
						Connection connection = DriverManager.getConnection(Helper.url, Helper.username, Helper.password);
						Statement statement = connection.createStatement();
						ResultSet result = statement.executeQuery("SELECT u_password, u_login FROM user where u_login like '" + login.getText() + "'");
						result.next();
						String user = result.getString("u_login").toString();
						if(password.getText().equals(result.getString("u_password"))) {
							connection.close();
							Date date = new Date();
							connection = DriverManager.getConnection(Helper.url, Helper.username, Helper.password);
							statement = connection.createStatement();
							statement.execute("INSERT INTO log (l_user, l_status, l_date) VALUES ('" + user + "', 'Выполнен вход', '" + date + "')");
							connection.close();
							Main asd = new Main(user);
							asd.setVisible(true);
							dispose();
						}
						else {
							Enter.setEnabled(false);
							new Timer().schedule(new TimerTask() {
								public void run() {
									label.setText("Блокировка снята");
									label.setForeground(Color.green);
									Enter.setEnabled(true);
								}
							}, Helper.auth_block * 1000);
							connection.close();
							Date date = new Date();
							connection = DriverManager.getConnection(Helper.url, Helper.username, Helper.password);
							statement = connection.createStatement();
							statement.execute("INSERT INTO log (l_user, l_status, l_date) VALUES ('" + user + "', 'Попытка входа', '" + date + "')");
							connection.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
						label.setText("Неправильный логин или пароль");
						label.setForeground(Color.red);
					}
				}
				else {
					label.setText("Заполните все поля");
					label.setForeground(Color.red);
				}
			}
		});
		Enter.setBounds(154, 128, 89, 23);
		getContentPane().add(Enter);
		
		password = new JPasswordField();
		password.setEchoChar('*');
		password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(KeyEvent.getKeyText(arg0.getKeyCode()).equals("Enter")) {
					Enter.doClick();
				}
			}
		});
		password.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				label.setText("");
			}
		});
		password.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				label.setText("");
			}
		});
		password.setBounds(89, 63, 154, 20);
		getContentPane().add(password);
		
		JButton check = new JButton("-_-");
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(check.getText().equals("-_-")) {
					check.setText("о_о");
					password.setEchoChar((char) 0);
				}
				else {
					check.setText("-_-");
					password.setEchoChar('*');
				}
			}
		});
		check.setBounds(250, 62, 55, 22);
		getContentPane().add(check);
	}
	
	public static String getHash(String plaintext) {
		try {
		MessageDigest m;
		m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update(plaintext.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		while (hashtext.length() < 32) {
		hashtext = "0" + hashtext;
		}
		return hashtext;
		} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
		}
		return null;
	}
}