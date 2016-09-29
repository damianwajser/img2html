
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/*
 * FileChooserDemo2.java requires these files:
 *   ImageFileView.java
 *   ImageFilter.java
 *   ImagePreview.java
 *   Utils.java
 *   images/jpgIcon.gif (required by ImageFileView.java)
 *   images/gifIcon.gif (required by ImageFileView.java)
 *   images/tiffIcon.gif (required by ImageFileView.java)
 *   images/pngIcon.png (required by ImageFileView.java)
 */
@SuppressWarnings("serial")
public class FileChooserDemo extends JPanel implements ActionListener {

	private JFileChooser fc;
	private JTextField url = new JTextField("http://www.google.com");

	String result = "";
	
	public FileChooserDemo() {
		super(new BorderLayout());

		// Create the log first, because the action listener
		// needs to refer to it.
		
		JButton sendButton = new JButton("Attach...");
		sendButton.addActionListener(this);
		setVisible(true);
		add(url, BorderLayout.NORTH);		
		add(sendButton, BorderLayout.SOUTH);
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent event) {
		// Set up the file chooser.
		if (fc == null) {
			fc = new JFileChooser();
			fc.addChoosableFileFilter(new ImageFilter());
			fc.setAcceptAllFileFilterUsed(false);

			// Add custom icons for file types.
			fc.setFileView(new ImageFileView());

			// Add the preview pane.
			fc.setAccessory(new ImagePreview(fc));
		}

		// Show it.
		int returnVal = fc.showDialog(FileChooserDemo.this, "Attach");

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			String encodedBase64 = null;
			try {
				FileInputStream fileInputStreamReader = new FileInputStream(file);
				byte[] bytes = new byte[(int) file.length()];
				fileInputStreamReader.read(bytes);
				encodedBase64 = new String(Base64.encodeBase64(bytes));
				fileInputStreamReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			result = "<html> <body> <a href='" + url.getText() + "'> <img src='data:image/jpeg;base64,"
					+ encodedBase64 + "'/> </a> </body> </html>";
			try {
				FileUtils.writeStringToFile(new File(System.getProperty("user.home") + "/Desktop/imagen.html"), result);
				JOptionPane.showMessageDialog(new JFrame(), "listo ... busca en escritorio imgan.html", "Dialog",
				        JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// log.setCaretPosition(log.getDocument().getLength());

		// Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("img2html");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new FileChooserDemo());

		// Display the window.
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_HORIZ);
		frame.setVisible(true);
		
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
