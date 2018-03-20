package aaplication;

import java.io.IOException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import aaplication.FenetreGuide.OpenImageOnClick;
import util.Debug;
import util.Fichier;

/**
 * Fenetre presentant des experiences et des tests a effectuer avec l'application
 * @author Leo Jetzer
 */
public class FenetreTests extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabs;

	/**
	 * Creer la fenetre
	 */
	public FenetreTests(){
		super();
		setBounds(100, 100, 800, 500);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		setTitle("Tests recommend\u00E9s");
		
		tabs = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabs);
		
		generateLayout();
		
		ajouterTest("D\u00E9capitation", "test1decapite");
		ajouterTest("Sauvetage", "test2sauver");
		ajouterTest("Zizanie", "test3Zizanie");
		
	}
	
	/**
	 * Ajoute un onglet a la fenetre de test
	 * @param nom - {@code String} : le nom de l'onglet
	 * @param fichier - {@code String} : le nom des fichiers contenant l'information, sans l'extension
	 */
	public void ajouterTest(String nom, String fichier){
		JPanel panel = new JPanel();
		JLabel titre = new JLabel(nom);
		JTextPane texte = new JTextPane();

		texte.setEditable(false);
		String fileName = "test/" + fichier;
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
	 * Genere le layout principal de l'application
	 */
	private void generateLayout(){
		JPanel contentPane = (JPanel) getContentPane();
		GroupLayout gp = new GroupLayout(contentPane);
		
		gp.setHorizontalGroup(gp.createSequentialGroup().addContainerGap().addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap());
		gp.setVerticalGroup(gp.createSequentialGroup().addContainerGap().addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap());
		
		contentPane.setLayout(gp);
	}
	
}
