package dev.saseno.jakarta.digioh.email;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CaptureDialog extends WindowAdapter implements ActionListener {
	private Frame frame;
	private Label label1;
	private TextField field1;
	private Button button1;
	private Dialog d1;

	public CaptureDialog() {
		frame = new Frame("Frame");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});
		
		button1 = new Button("Open Modal Dialog");
		label1 = new Label("Click on the button to open a Modal Dialog");

		frame.add(label1);
		frame.add(button1);

		button1.addActionListener(this);
		frame.pack();

		frame.setLayout(new FlowLayout());
		frame.setSize(330, 250);
		frame.setVisible(false);
	}
	
	public void showWindow() {
		try {
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().equals("Open Modal Dialog")) {
			//Creating a non-modeless blocking Dialog
			d1 = new Dialog(frame, "Modal Dialog", true);
			Label label = new Label("You must close this dialog window to use Frame window", Label.CENTER);
			d1.add(label);

			d1.addWindowListener(this);
			d1.pack();
			d1.setLocationRelativeTo(frame);
			d1.setLocation(new Point(100, 100));
			d1.setSize(400, 200);
			d1.setVisible(true);
		}
	}

	public void windowClosing(WindowEvent we) {
		d1.setVisible(false);
	}
	
	public static void main(String args[]) {
		CaptureDialog f = new CaptureDialog();
	}
}
