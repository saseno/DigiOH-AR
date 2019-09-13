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

import javax.swing.JFrame;

import com.codeforwin.projects.onscreenkeyboard.KeyboardUI;

@SuppressWarnings("serial")
public class CaptureDialog extends Dialog {
			
	private int width = 483;
	private int heigh = 350;
	
	private TextField addressField = null;
	private TextArea messageText = null;
	private String attachment = null;
	
	KeyboardUI keyboard; //.setVisible(true);
	
	public CaptureDialog(Frame frame) {
		super(frame);
		
		//setModalityType(ModalityType.TOOLKIT_MODAL);		
		setTitle("Email Sender");		
		setBackground(new Color(234, 234, 234));

		Label addressLabel = new Label("Alamat Email: (pisah dengan koma ',')");
		addressField = new TextField(60);		

		Label messageLabel = new Label("Pesan Email:");
		messageText = new TextArea();
		messageText.setColumns(20);
		
		Button sendButton = new Button("Kirim Email");
		sendButton.addActionListener(e -> {
			try {

				this.setVisible(false);
				
				new Thread(new SendEmailAttachment(addressField.getText(), 
								messageText.getText(), attachment)).start();

				//new Thread(new PostInFacebook(attachment)).start();

				//this.dispose();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}		
		});
		
		Button cancelButton = new Button("Batal");
		cancelButton.addActionListener(e -> {
			try {
				this.setVisible(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		Button showKeyBoardButton = new Button("Keyboard");
		showKeyBoardButton.addActionListener(e -> {
			try {
				if (keyboard != null) {
					keyboard.setVisible(!keyboard.isVisible());
					keyboard.setState(JFrame.NORMAL);
					keyboard.toFront();
					
					addressField.transferFocus();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		add(addressLabel);
		add(addressField);

		add(messageLabel);
		add(messageText);
		
		add(sendButton);
		add(cancelButton);
		add(showKeyBoardButton);

		pack();
		setLayout(new FlowLayout(10, 10, 5));		
		setSize(width, heigh);	
		
		setVisible(false);
		
		try {
			keyboard = new KeyboardUI();
			keyboard.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		
		if (keyboard != null) {
			keyboard.setVisible(b);
		}
	}
	
	public void sendEmail(Dimension dimension, String attachment) {
		try {
			
			addressField.setText("");
			messageText.setText("");

			this.attachment = attachment;
			setLocation((dimension.width / 5) * 2, (dimension.height / 6) * 2);
			setVisible(true);
			toFront();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
