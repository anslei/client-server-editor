import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends GUI {

	private JFrame frame;
	private JTextField userIn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() throws ClassNotFoundException{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 350, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);
		ImageIcon originalIcon = new ImageIcon(getClass().getResource("enter.png"));
		ImageIcon originalIcon2 = new ImageIcon(getClass().getResource("wallpaper login.jpg"));
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 336, 263);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton loginBtn = new JButton("");
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			String str=userIn.getText();	
			
			GUI.sendUser(str);
			
			TCPClient.writeToServer("checkdocs",TCPClient.username," ");
			
			GUI.updateLog("Hello "+str+" !");
				
			GUI.frmOur.setVisible(true);
			
			frame.dispose();
			}
		});
		loginBtn.setBackground(new Color(0, 51, 102));
		loginBtn.setBounds(138, 142, 43, 30);
		panel.add(loginBtn);
		Image scaled = originalIcon.getImage().getScaledInstance(loginBtn.getWidth(), loginBtn.getHeight(), Image.SCALE_SMOOTH);
		loginBtn.setIcon(new ImageIcon(scaled));
		
		JLabel User = new JLabel("Username : ");
		User.setForeground(Color.WHITE);
		User.setBackground(Color.WHITE);
		User.setBounds(30, 81, 116, 37);
		panel.add(User);
		User.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		User.setHorizontalAlignment(SwingConstants.CENTER);
		
		userIn = new JTextField();
		userIn.setBounds(148, 83, 148, 37);
		panel.add(userIn);
		userIn.setColumns(10);
		
		JLabel background = new JLabel();
		background.setBounds(0, 0,panel.getWidth(), panel.getHeight());
		panel.add(background);
		Image scaled2 = originalIcon2.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_SMOOTH);
		background.setIcon(new ImageIcon(scaled2));
	
	}
}
