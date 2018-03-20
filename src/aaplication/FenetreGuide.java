package aaplication;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ui.ImagePane;
import ui.TemporaryFrame;
import util.Debug;
import util.Fichier;
import util.Images;

import javax.swing.JScrollPane;

/**
 * Fenetre expliquant le fonctionnement de l'application
 * @author Leo Jetzer
 *
 */
public class FenetreGuide extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabs;
	private JButton btnFermer;
	private JLabel lblTitreOutil;
	private JPanel pnlOutils;

	/**
	 * Creer la fenetre
	 */
	public FenetreGuide(){
		super();
		setBounds(100, 100, 800, 500);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		setTitle("Guide d'utilisation");

		tabs = new JTabbedPane(JTabbedPane.TOP);
		lblTitreOutil = new JLabel("Outils");
		lblTitreOutil.setFont(App21SimulateurRagdoll.FONT_TITRE);
		btnFermer = new JButton("J'ai compris");
		btnFermer.addActionListener(x -> this.dispose());
		pnlOutils = new JPanel();
		pnlOutils.setLayout(new BoxLayout(pnlOutils, BoxLayout.Y_AXIS));

		genererLayout();
		ajouterGuide("G\u00E9n\u00E9ral", "General");
//		genererLayoutOutils();
		ajouterGuide("Outils", "objets");
		ajouterGuide("Inspecteur", "Modification");
		ajouterGuide("Mat\u00E9riau", "Materiau");
		ajouterGuide("Mode scientifique", "Graphique");

	}

	/**
	 * Ajoute un onglet au guide d'utilisation
	 * @param nom - {@code String} : le nom de l'onglet
	 * @param fichier - {@code String} : le nom des fichiers contenant l'information, sans l'extension
	 */
	public void ajouterGuide(String nom, String fichier){
		JPanel panel = new JPanel();
		JLabel titre = new JLabel(nom);
		JTextPane texte = new JTextPane();

		texte.setEditable(false);
		String fileName = "texte/" + fichier;
		URL urlHtml = Fichier.getURL(fileName + ".html");
		try {	
			if (urlHtml != null){
				texte.setPage(urlHtml);
				texte.addHyperlinkListener(new OpenImageOnClick());
			}
			else
				texte.setText(Fichier.read(fileName + ".txt"));
		}
		catch (IOException ex) {
			try {	
				texte.setText(Fichier.read("LoremIpsum.txt"));
			}
			catch (IOException exc) {
				Debug.log("Impossible d'ouvrir le fichier \"" + fileName + "\"\n");
			}
		}

		JScrollPane scroll = new JScrollPane(texte);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		GroupLayout gp = new GroupLayout(panel);
		gp.setHorizontalGroup(
				gp.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						gp.createParallelGroup(Alignment.LEADING)
						.addComponent(titre)
						.addGroup(
								gp.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scroll, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								)
						)
				.addContainerGap()
				);
		gp.setVerticalGroup(
				gp.createSequentialGroup()
				.addContainerGap()
				.addComponent(titre)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(
						gp.createParallelGroup(Alignment.CENTER)
						.addComponent(scroll, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						)
				.addContainerGap()
				);
		panel.setLayout(gp);
		tabs.add(nom, panel);
	}

	/**
	 * Genere le layout principale de l'application
	 */
	private void genererLayout(){
		JPanel contentPane = new JPanel();
		GroupLayout gp = new GroupLayout(contentPane);
		gp.setHorizontalGroup(gp.createSequentialGroup()
				.addContainerGap()
				.addGroup(gp.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnFermer)
						)
				.addContainerGap()
				);
		gp.setVerticalGroup(gp.createSequentialGroup()
				.addContainerGap()
				.addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(btnFermer)
				.addContainerGap()
				);
		contentPane.setLayout(gp);
		this.setContentPane(contentPane);
	}

	/**
	 * Ecouteur qui ouvre une fenetre contenant une image lorsque l'on clique sur le lien
	 * @author Leo Jetzer
	 */
	public static class OpenImageOnClick implements HyperlinkListener {
		TemporaryFrame frame;
		URL url_old;
		
		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
				return;
			URL url = e.getURL();
			if (frame == null || (frame != null && url != url_old)){
				url_old = url;
				BufferedImage img = Images.safeCharger(url_old);
				if (img == null)
					return;
				frame = new TemporaryFrame();
				frame.setContentPane(new ImagePane(img));
			}
			Component comp = (Component)e.getSource();
			Point mouse = comp.getMousePosition();
			Point pos = comp.getLocationOnScreen();
			frame.setLocation(new Point(pos.x + mouse.x - 20, pos.y + mouse.y - 20));
			frame.pack();
			frame.setVisible(true);
		}
		
	}
	
}
