package healthyBites;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class App {
	public static void main(String[] args) {
		JFrame frame = new JFrame("healthapp");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(400,300);
		CardLayout cardLayout = new CardLayout();
		
		JPanel mainPanel = new JPanel(cardLayout);
		
		LoginPanel loginPanel = new LoginPanel(mainPanel, cardLayout);
		RegisterPanel registerPanel = new RegisterPanel(mainPanel, cardLayout);

		
		
		mainPanel.add(loginPanel, "LoginPage");
		mainPanel.add(registerPanel, "RegisterPage");
		
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}

}
