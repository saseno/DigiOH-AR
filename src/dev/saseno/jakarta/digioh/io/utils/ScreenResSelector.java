package dev.saseno.jakarta.digioh.io.utils;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ScreenResSelector {
	static interface DisplayModeFilter {
		public boolean filter(DisplayMode mode);
	}

	public static java.util.List getAvailableDisplayModes() {
		java.util.List modes = getDisplayModes();
		final DisplayMode curMode = getCurrentDisplayMode();
		
		// Filter everything which is higher frequency than the current display mode
		modes = filterDisplayModes(modes, new DisplayModeFilter() {
			public boolean filter(DisplayMode mode) {
				return (mode.getRefreshRate() <= curMode.getRefreshRate());
			}
		});
		
		// Filter everything that is not at least 24-bit
		modes = filterDisplayModes(modes, new DisplayModeFilter() {
			public boolean filter(DisplayMode mode) {
				// Bit depth < 0 means "multi-depth" -- can't reason about it
				return (mode.getBitDepth() < 0 || mode.getBitDepth() >= 24);
			}
		});
		
		// Filter everything less than 640x480
		modes = filterDisplayModes(modes, new DisplayModeFilter() {
			public boolean filter(DisplayMode mode) {
				return (mode.getWidth() >= 640 && mode.getHeight() >= 480);
			}
		});
		
		if (modes.size() == 0) {
			throw new RuntimeException("Couldn't find any valid display modes");
		}
		
		return modes;
	}

	/**
	 * Shows a modal dialog containing the available screen resolutions and requests
	 * selection of one of them. Returns the selected one.
	 */
	public static DisplayMode showSelectionDialog() {
		SelectionDialog dialog = new SelectionDialog();
		dialog.setVisible(true);
		dialog.waitFor();
		return dialog.selected();
	}
	
	public static DisplayMode getHighestMode() {
		SelectionDialog dialog = new SelectionDialog();
		
		//dialog.setVisible(true);
		//dialog.waitFor();
		
		return dialog.getHighestMode();
	}

	public static void main(String[] args) {
		DisplayMode mode = showSelectionDialog();
		if (mode != null) {
			System.err.println("Selected display mode:");
			System.err.println(modeToString(mode));
		} else {
			System.err.println("No display mode selected.");
		}
		System.exit(0);
	}

	// ----------------------------------------------------------------------
	// Internals only below this point
	//

	private static DisplayMode getCurrentDisplayMode() {
		GraphicsDevice dev = getDefaultScreenDevice();
		return dev.getDisplayMode();
	}

	private static java.util.List/* <DisplayMode> */ getDisplayModes() {
		GraphicsDevice dev = getDefaultScreenDevice();
		DisplayMode[] modes = dev.getDisplayModes();
		return toList(modes);
	}

	private static GraphicsDevice getDefaultScreenDevice() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}

	private static java.util.List/* <DisplayMode> */ toList(DisplayMode[] modes) {
		java.util.List res = new ArrayList();
		for (int i = 0; i < modes.length; i++) {
			res.add(modes[i]);
		}
		return res;
	}

	private static String modeToString(DisplayMode mode) {
		return (((mode.getBitDepth() > 0) ? (mode.getBitDepth() + " bits, ") : "") + mode.getWidth() + "x"
				+ mode.getHeight() + ", " + mode.getRefreshRate() + " Hz");
	}

	private static String[] modesToString(java.util.List/* <DisplayMode> */ modes) {
		String[] res = new String[modes.size()];
		int i = 0;
		for (Iterator iter = modes.iterator(); iter.hasNext();) {
			res[i++] = modeToString((DisplayMode) iter.next());
		}
		return res;
	}

	private static java.util.List/* <DisplayMode> */ filterDisplayModes(java.util.List/* <DisplayMode> */ modes,
			DisplayModeFilter filter) {
		java.util.List res = new ArrayList();
		for (Iterator iter = modes.iterator(); iter.hasNext();) {
			DisplayMode mode = (DisplayMode) iter.next();
			if (filter.filter(mode)) {
				res.add(mode);
			}
		}
		return res;
	}

	static class SelectionDialog extends JFrame {
		
		private Object monitor = new Object();
		private java.util.List modes;
		private volatile boolean done = false;
		private volatile int selectedIndex;

		public SelectionDialog() {
			super();

			setTitle("Display Modes");
			modes = getAvailableDisplayModes();
			String[] strings = modesToString(modes);
			
			final JList modeList = new JList(strings);

			modeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			modeList.setSelectedIndex(0);
			
			JScrollPane scroller = new JScrollPane(modeList);
			getContentPane().setLayout(new BorderLayout());
			
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(new JLabel("Select full-screen display mode,"), BorderLayout.NORTH);
			panel.add(new JLabel("or Cancel for windowed mode:"), BorderLayout.CENTER);
			
			getContentPane().add(BorderLayout.NORTH, panel);
			getContentPane().add(BorderLayout.CENTER, scroller);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.add(Box.createGlue());
			
			JButton button = new JButton("OK");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedIndex = modeList.getSelectedIndex();
					done = true;
					synchronized (monitor) {
						monitor.notify();
					}
					setVisible(false);
					dispose();
				}
			});
			
			buttonPanel.add(button);
			buttonPanel.add(Box.createHorizontalStrut(10));
			button = new JButton("Cancel");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {					
					System.exit(0);
				}
			});
			
			buttonPanel.add(button);
			buttonPanel.add(Box.createGlue());
			
			getContentPane().add(BorderLayout.SOUTH, buttonPanel);
			setSize(300, 200);
			center(this, Toolkit.getDefaultToolkit().getScreenSize());
		}

		public void waitFor() {
			synchronized (monitor) {
				while (!done) {
					try {
						monitor.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}

		public DisplayMode selected() {
			if (selectedIndex < 0) {
				return null;
			} else {
				return (DisplayMode) modes.get(selectedIndex);
			}
		}

		public DisplayMode getHighestMode() {
			return (DisplayMode) modes.get(modes.size() - 1);
			
		}
	}

	private static void center(Component component, Dimension containerDimension) {
		Dimension sz = component.getSize();
		int x = ((containerDimension.width - sz.width) / 2);
		int y = ((containerDimension.height - sz.height) / 2);
		component.setLocation(x, y);
	}
}