package aaplication;

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

import java.io.IOException;
import java.net.URL;

/**
 * Fenetre expliquant les concepts scientifiques utilises
 * @author Leo Jetzer
 */
public class FenetreConcepts extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] tabsName = {"M\u00E9canique", "Magn\u00E9tisme", "Poup\u00E9e de chiffon", "R\u00E9flection", "S.A.T"};
	private static final String[] filesName = {"Mecanique", "Magnetisme", "Poupee_de_chiffon", "Reflection", "SAT"};
	
	private JTabbedPane tabs;

	/**
	 * Creer une nouvelle fenetre de concept
	 */
	public FenetreConcepts(){
		super();
		setBounds(100, 100, 800, 500);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		setTitle("Concepts scienifiques");
		
		tabs = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabs);
		
		generateLayout();
		
		for (int i = 0; i < tabsName.length; i++)
			ajouterTab(tabsName[i], filesName[i]);
	}
	
	/**
	 * Ajoute une "page" a la fenetre des concepts
	 * @param nom - {@code String} : le nom de la page
	 * @param fichier - {@code String} : le nom des fichiers .txt et .png, sans l'extension
	 */
	private void ajouterTab(String nom, String fichier){
		JPanel panel = new JPanel();
		JLabel titre = new JLabel(nom);
		JTextPane texte = new JTextPane();

		texte.setEditable(false);
		String fileName = "science/" + fichier;
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
	private void generateLayout(){
		JPanel contentPane = (JPanel) getContentPane();
		GroupLayout gp = new GroupLayout(contentPane);
		
		gp.setHorizontalGroup(gp.createSequentialGroup().addContainerGap().addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap());
		gp.setVerticalGroup(gp.createSequentialGroup().addContainerGap().addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap());
		
		contentPane.setLayout(gp);
	}
	
}
