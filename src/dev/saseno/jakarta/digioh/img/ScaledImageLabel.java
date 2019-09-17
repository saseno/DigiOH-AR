package dev.saseno.jakarta.digioh.img;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
 
/**
 * This is an extended version of a JLabel which draws its icon image using
 * the ImageDrawer utility.
 *
 * @author www.codejava.net
 *
 */
@SuppressWarnings("serial")
public class ScaledImageLabel extends JLabel {
	protected void paintComponent(Graphics g) {
		ImageIcon icon = (ImageIcon) getIcon();
		if (icon != null) {
			ImageDrawer.drawScaledImage(icon.getImage(), this, g);
		}
	}
}