import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import java.awt.Image;
import javax.swing.Timer;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.Icon;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;
import java.awt.SystemColor;
import javax.swing.JInternalFrame;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.UIManager;
import javax.swing.JTextPane;
import javax.swing.JLayeredPane;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class GUI  {

	public static JFrame frmOur;
	
	//This text area is the logbox the user will receive notifications from the server on
	public static JTextArea log;
	
	public static JTextArea textArea;
	
	//This comboBox stores all the documents accessible by the user at this point in time
	public static JComboBox<String> docs;
	
	//This comboBox stores the users who have entries in the table to give them access to other documents
	public static JComboBox<String>users;

	
	public static void main(String[] args) throws Exception {
		

		//initialize client for this GUI
		new TCPClient();
		
		//start client
		TCPClient.main(args);
		
		//Initialize Login window and start it
		new Login();
		
		Login.main(args);
		
		
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				try {
					
					//initialize window
					GUI window = new GUI();
					
					//make it invisible first until login window ends
					window.frmOur.setVisible(false);
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

	/**
	 * Create the application.
	 */
	
	public GUI() throws ClassNotFoundException{
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		
		//Editing the Frame
		frmOur = new JFrame();
		frmOur.getContentPane().setBackground(SystemColor.menu);
		frmOur.setBackground(new Color(51, 102, 153));
		frmOur.setTitle("OurEdit (Group 7)");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frmOur.setBounds(100, 100, (int) dim.getWidth(), (int) dim.getHeight());
		frmOur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOur.setLocationRelativeTo(null);
		
		//Formatting menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		menuBar.setBackground(new Color(51, 102, 153));
		menuBar.setToolTipText("");
		menuBar.setMargin(new Insets(28, 13, 10, 10));
		frmOur.setJMenuBar(menuBar);
		
		//File button
		JMenu mnFile = new JMenu("File");
		mnFile.setForeground(Color.WHITE);
		mnFile.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		mnFile.setHorizontalAlignment(SwingConstants.CENTER);
		menuBar.add(mnFile);
		
		//newdoc function must go here
		JMenuItem file_new = new JMenuItem("New ");
		file_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					TCPClient.writeToServer("newdoc", " ", " ");
					System.out.println("yey");

				}
			
			}
		);
		
		mnFile.add(file_new);
		
		
		JMenuItem file_give_access = new JMenuItem("Give Access to this Document");
		file_give_access.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String str= (String) docs.getSelectedItem();
				
				TCPClient.writeToServer("checkallusers", str, " ");
				
				users = new JComboBox<String> ();
				
				docs.setBackground(UIManager.getColor("Button.background"));
				
				Object[] fields = { "Which users do you want to give acces to this document ? ", users};
				
				int input = JOptionPane.showConfirmDialog(frmOur, fields, "Share Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				
				if(input== JOptionPane.OK_OPTION) {
					TCPClient.writeToServer("user",(String) users.getSelectedItem(), str);
				}
			}
		});
		mnFile.add(file_give_access);
		
		JMenuItem file_save = new JMenuItem("Save");
		file_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str1 = (String)docs.getSelectedItem();
				String str2 = textArea.getText();
				if (str1==null){
					TCPClient.writeToServer("savenewdoc",str2," ");
				}
				else {
					
					TCPClient.writeToServer("savedoc", str1, str2);
					
				}
				
			}
		});
		mnFile.add(file_save);
		
		JMenuItem file_exit = new JMenuItem("Exit");
		file_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenuItem mntDelete = new JMenuItem("Delete");
		mntDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TCPClient.writeToServer("deldoc", (String)docs.getSelectedItem(), TCPClient.username);
				
			}
		});
		mnFile.add(mntDelete);
		mnFile.add(file_exit);
		frmOur.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(451, 152, 640, 509);
		frmOur.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setText(" ");
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Arial", Font.PLAIN, 18));
		textArea.setMargin(new Insets(100,100,100,100));
		
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(51, 102, 153));
		panel.setBounds(0, 0, frmOur.getWidth(), 102);
		frmOur.getContentPane().add(panel);
		panel.setLayout(null);
		
		JSpinner fontSize = new JSpinner();
		fontSize.setBounds(67, 26, 57, 50);
		panel.add(fontSize);
		fontSize.setValue(18);
		fontSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(int)fontSize.getValue()));
			}
		});
		
		JComboBox<Object> fontSelector = new JComboBox<Object> (fonts);
		fontSelector.setBounds(254, 26, 161, 50);
		panel.add(fontSelector);
		fontSelector.setBackground(UIManager.getColor("Button.background"));
		fontSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				textArea.setFont(new Font((String)fontSelector.getSelectedItem(),Font.PLAIN,textArea.getFont().getSize()));
				
			}
		});
		fontSelector.setSelectedItem("Arial");
		
		docs = new JComboBox<String> ();
		docs.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String str= (String) docs.getSelectedItem();
				TCPClient.writeToServer("text",str," ");
			}
		});
		docs.setBounds(902, 26, 161, 50);
		panel.add(docs);
		docs.setBackground(UIManager.getColor("Button.background"));
		
		
		JButton colorButton = new JButton();
		colorButton.setBounds(150, 26, 62, 50);
		ImageIcon originalIcon = new ImageIcon(getClass().getResource("Font color.png"));
		Image scaled = originalIcon.getImage().getScaledInstance(colorButton.getWidth(), colorButton.getHeight(), Image.SCALE_SMOOTH);
		colorButton.setIcon(new ImageIcon(scaled));
	
		panel.add(colorButton);
		
		
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Color color= JColorChooser.showDialog(null, "Choose a color", Color.black);
				
				textArea.setForeground(color);
			}
		});
		colorButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JButton refreshbtn = new JButton("");
		ImageIcon originalIcon2 = new ImageIcon(getClass().getResource("refresh.png"));
		Image scaled2 = originalIcon2.getImage().getScaledInstance(colorButton.getWidth(), colorButton.getHeight(), Image.SCALE_SMOOTH);
		refreshbtn.setIcon(new ImageIcon(scaled2));
		refreshbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TCPClient.writeToServer("checkdocs",TCPClient.username," ");
			}
		});
		refreshbtn.setBounds(1095, 26, 50, 50);
		panel.add(refreshbtn);
		
		JLabel background_editor = new JLabel("");
		background_editor.setBounds(0, 99, 1266, 562);
		ImageIcon originalIcon3 = new ImageIcon(getClass().getResource("background gui.jpg"));
		Image scaled3 = originalIcon3.getImage().getScaledInstance(frmOur.getWidth(), background_editor.getHeight(), Image.SCALE_SMOOTH);
		background_editor.setIcon(new ImageIcon(scaled3));
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 1266, 647);
		frmOur.getContentPane().add(layeredPane);
		
		log = new JTextArea();
		log.setBounds(44, 152, 266, 329);
		layeredPane.add(log);
		log.setEditable(false);
		log.setLineWrap(true);
		layeredPane.add(log,0);
		layeredPane.add(background_editor,1);

	}
	public static void updateLog(String str) {
		log.append(str+"\n");
	}
	
	public static void sendUser(String str) {
		TCPClient.writeUsername(str);
	}
	
	public static void updateText(String str) {
		if (str==null) {
			textArea.setText(null);
		}
		else {
			textArea.setText(str+" ");
		}
		
	}
	
	public static void markupDeletion(String str1, String str2) {
		int input = JOptionPane.showConfirmDialog(frmOur, str1+" was submitted for deletion by "+str2, "New Document", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(input == JOptionPane.YES_OPTION)
		{
		   TCPClient.writeToServer("option", str1, "yes");
		}
		else if (input ==JOptionPane.NO_OPTION) {
			TCPClient.writeToServer("option", str1, "no");
		}
	}
	}

