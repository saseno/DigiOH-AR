package dev.saseno.jakarta.digioh.email;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.codeforwin.projects.onscreenkeyboard.KeyboardUI;

import dev.saseno.jakarta.digioh.img.ScaledImageLabel;

@SuppressWarnings("serial")
public class CaptureDialog extends Dialog {
			
	private int width = 490;
	private int heigh = 550;
	
	private TextField addressField = null;
	private TextArea messageText = null;
	private String attachment = null;
	
	private KeyboardUI keyboard; //.setVisible(true);

	private Dimension initDimension = new Dimension(300, 280);
	private JLabel labelImage = new ScaledImageLabel();
	private File imageNotFound = new File("data/no-image.png");
	private ImageIcon imageIcon;
	
	private Label addressLabel;
	
	public CaptureDialog(Frame frame) {
		super(frame);
		setIconImage((new ImageIcon(getClass().getResource("/photos-icon.png"))).getImage());
		
		//setModalityType(ModalityType.TOOLKIT_MODAL);		
		setTitle("Email Sender");		
		setBackground(new Color(234, 234, 234));
		setLayout(new GridBagLayout());		

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(1, 1, 1, 2);

		/////////////////////////////////////// buttons - start ///////////////////////////////////////
		int buttonGridY = 5;
		
		Button sendButton = new Button("Kirim Email");
		sendButton.addActionListener(e -> {
			try {

				setVisible(false);				
				new Thread(new SendEmailAttachment(addressField.getText(), 
								messageText.getText(), attachment)).start();

				//new Thread(new PostInFacebook(attachment)).start();
				//dispose();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}		
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = (1/3);
		c.gridx = 0;
		c.gridy = buttonGridY;
		add(sendButton, c);
				
		Button showKeyBoardButton = new Button("Keyboard");
		showKeyBoardButton.addActionListener(e -> {
			try {
				if (keyboard != null) {
					keyboard.setVisible(!keyboard.isVisible());
					keyboard.setState(JFrame.NORMAL);
					keyboard.toFront();
					
					addressLabel.transferFocus();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = (1/3);
		c.gridx = 1;
		c.gridy = buttonGridY;
		add(showKeyBoardButton, c);	
		
		Button cancelButton = new Button("Batal");
		cancelButton.addActionListener(e -> {
			try {
				this.setVisible(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		c.fill = GridBagConstraints.LINE_END;
		c.weightx = (1/3);
		c.gridx = 2;
		c.gridy = buttonGridY;
		add(cancelButton, c);	

		/////////////////////////////////////// buttons - end ///////////////////////////////////////
		
		addressLabel = new Label("Alamat Email: (pisah dengan koma ',')");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		add(addressLabel, c);
		
		addressField = new TextField(60);
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		add(addressField, c);

		Label messageLabel = new Label("Pesan Email:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;		
		add(messageLabel, c);
				
		messageText = new TextArea();
		messageText.setColumns(20);
		messageText.setRows(5);
		//c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		add(messageText, c);
		
		try {
			
			Image image = ImageIO.read(imageNotFound);
			imageIcon = new ImageIcon(image);

			labelImage.setIcon(imageIcon);
			labelImage.setPreferredSize(initDimension);

			c.insets = new Insets(5, 1, 5, 1);
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = 3;
			c.gridx = 0;
			c.gridy = 4;
			
			add(labelImage, c);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pack();
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
		
		if (keyboard != null && !b) {
			keyboard.setVisible(b);
		}
	}
	
	public void sendEmail(Dimension dimension, String attachment) {
		try {

			setSize(width, heigh);
			addressField.setText("");
			messageText.setText("");

			if (keyboard != null) {
				keyboard.setVisible(false);
			}

			try {
				File imageFile = new File(attachment); 
				imageIcon.setImage(ImageIO.read(imageNotFound));				
				if (imageFile != null && imageFile.exists()) {
					imageIcon.setImage(ImageIO.read(imageFile));
				}
				labelImage.setIcon(imageIcon);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.attachment = attachment;
			setLocation((dimension.width / 5) * 2, (dimension.height / 6));
			setVisible(true);
			toFront();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] input) {
		Frame frame = new Frame();
		CaptureDialog cd = new CaptureDialog(frame);
		cd.sendEmail(Toolkit.getDefaultToolkit().getScreenSize(), "photos/DigiOH-AR_20190917_100928.png");		
	}
	
}
