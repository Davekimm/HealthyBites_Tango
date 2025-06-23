package healthyBites;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
	public LoginPanel (JPanel mainPanel, CardLayout cardLayout) {
		setLayout(new GridLayout(4,2, 5, 5));
		JTextField emailField = new JTextField();
		JButton loginBtn = new JButton("Login");
		JButton registerBtn = new JButton("Register");
		add(new JLabel("Email"));
		
//		Controller c = Controller.getInstance();
//		loginBtn.addActionListener(e -> c.login());
		registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "RegisterPage"));
		
		add(emailField);
		add(loginBtn);
		add(registerBtn);
		
		
	}

}
