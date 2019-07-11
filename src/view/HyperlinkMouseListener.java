package view;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

public class HyperlinkMouseListener extends MouseAdapter {
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			myMouseClickedListener(MouseEvent.BUTTON1, e);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			myMouseClickedListener(MouseEvent.BUTTON3, e);
		}

	}

	private void myMouseClickedListener(int mouseButton, MouseEvent e) {
		JEditorPane editor = (JEditorPane) e.getSource();
		Element hyperlink = getHyperlinkElement(e);

		if (hyperlink != null) {
			Object attribute = hyperlink.getAttributes().getAttribute(HTML.Tag.A);

			if (attribute instanceof AttributeSet) {
				AttributeSet attributeSet = (AttributeSet) attribute;
				String href = (String) attributeSet.getAttribute(HTML.Attribute.HREF);

				if (href != null) {

					if (mouseButton == MouseEvent.BUTTON1) {
						try {
							Desktop.getDesktop().browse(new URI(href));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Cannot open the link.", "Error 011", JOptionPane.ERROR_MESSAGE);
						} catch (URISyntaxException e1) {
							JOptionPane.showMessageDialog(null, "Cannot open the link.", "Error 012", JOptionPane.ERROR_MESSAGE);
						}
					} else if (mouseButton == MouseEvent.BUTTON3) {
						popupMenuSetup(href, editor, e);
					}
				}
			}
		}
	}
	
	private Element getHyperlinkElement(MouseEvent event) {
		JEditorPane editor = (JEditorPane) event.getSource();
		int position = editor.getUI().viewToModel(editor, event.getPoint());

		if (position >= 0 && editor.getDocument() instanceof HTMLDocument) {
			HTMLDocument htmlDocument = (HTMLDocument) editor.getDocument();
			Element element = htmlDocument.getCharacterElement(position);

			if (element.getAttributes().getAttribute(HTML.Tag.A) != null) {
				return element;
			}
		}
		return null;
	}
	
	private void popupMenuSetup(String href, JEditorPane editor, MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem("Open Link");
		JMenuItem copyItem = new JMenuItem("Copy Link");

		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(href));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Cannot open the link.", "Error 013", JOptionPane.ERROR_MESSAGE);
				} catch (URISyntaxException e1) {
					JOptionPane.showMessageDialog(null, "Cannot open the link.", "Error 014", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyLink(href);
			}
		});

		popupMenu.add(openItem);
		popupMenu.addSeparator();
		popupMenu.add(copyItem);

		popupMenu.show(editor, e.getX(), e.getY());
	}
	
	protected void copyLink(String href) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection selectionLink = new StringSelection(href);
		clipboard.setContents(selectionLink, null);
	}
}
