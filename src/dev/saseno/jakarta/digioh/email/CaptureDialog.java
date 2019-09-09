package dev.saseno.jakarta.digioh.email;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class CaptureDialog extends Dialog implements ActionListener {
		
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private int width = 483;
	private int heigh = 350;
	
	private TextField addressField = null;
	private TextArea messageText = null;
	private String attachment = null;
	
	public CaptureDialog(Frame frame) {
		super(frame);
		this.setTitle("Email Sender");		
		this.setBackground(new Color(234, 234, 234));

		Label addressLabel = new Label("Alamat Email: (pisah dengan koma ',')");
		addressField = new TextField(60);		

		Label messageLabel = new Label("Pesan Email:");
		messageText = new TextArea();
		messageText.setColumns(20);
		
		Button sendButton = new Button("Kirim Email");
		sendButton.addActionListener(this);
		
		Button cancelButton = new Button("Batal");
		cancelButton.addActionListener(e -> {
			try {
				this.setVisible(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		this.add(addressLabel);
		this.add(addressField);

		this.add(messageLabel);
		this.add(messageText);
		
		this.add(sendButton);
		this.add(cancelButton);

		this.pack();
		this.setLayout(new FlowLayout(10, 10, 5));		
		this.setSize(width, heigh);
		this.setLocation((screenSize.width / 5) * 2, (screenSize.height / 6) * 2);		
		this.setVisible(false);
		this.toFront();
	}
	
	public void sendEmail(String attachment) {
		try {
			
			addressField.setText("");
			messageText.setText("");
			
			this.setVisible(true);
			this.attachment = attachment;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {						

			this.setVisible(false);
						
			new Thread(new SendEmailAttachment(addressField.getText(), 
							messageText.getText(), attachment)).start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
