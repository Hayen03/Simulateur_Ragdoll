package aaplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import corps.Aimant;
import corps.Balle;
import corps.Boite;
import corps.Corps;
import corps.Outil;
import inspecteur.PropMateriaux;
import inspecteur.Propriete;
import interaction.BoutonOutil;
import interaction.BoutonSwitch;
import interaction.EditeurMateriau;
import interaction.Inspecteur;
import interaction.ZoneInteraction;
import physique.Materiaux;
import physique.SVector2d;
import ui.BoutonAnime;
import ui.BoutonSemiAnime;
import ui.MessageBoard;
import util.Debug;
import util.Fichier;
import util.Images;

/**
 * Classe repr�sentant la fen�tre de l'application
 * @author aucun (pour l'instant, tous les �l�ments pr�sents sur la fen�tre sont pr�sentement pour des fins de tests)
 *
 */
public class App21SimulateurRagdoll extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JSpinner forceX;
	private JSpinner forceY;
	private ZoneInteraction zoneInteraction;
	private Inspecteur specteur;
	private MessageBoard messageBoard;
	private JTabbedPane tabbedPane;
	private JButton btnQuitter;
	private BoutonSwitch btnPlay;
	private JPanel pnlBoiteOutils;
	private EditeurMateriau editeurMateriau;
	private JButton btnGraphique, btnPoubelle;
	private JMenuItem itemGraphique;

	private JFrame fenetreGuide, fenetreConcepts, fenetreTests;
	private FenetreGraphique fenetreGraphique;
	private Timer timerUpdate, timerGraphique;
	static final Font FONT_TITRE = new Font("Noteworthy", Font.BOLD, 30);
	private BufferedImage[] fonds;

	/**
	 * Lancer l'application.
	 */
	public static void main(String[] args) {
		Debug.DEBUG = false;
		Debug.STACK = false;
		initLookAndFeel();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					App21SimulateurRagdoll frame = new App21SimulateurRagdoll();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creer le frame.
	 */
	public App21SimulateurRagdoll() {
		fonds = new BufferedImage[]{
				Images.safeCharger("paysage2.png"),
				Images.safeCharger("Ernesto.jpg"), 
				Images.safeCharger("windows.jpg"),
				Images.safeCharger("assemble_nationale.jpg")
				};
		
		setTitle("Simulateur de poupee de chiffon");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 990, 750);

		generateMenu();
		generateMainLayout();
		generateInspecteur();
		generateBoiteAOutils();
		if (Debug.DEBUG){
			generateDebug();
			Debug.board = messageBoard;

		}
		generateListener();

	}

	/**
	 * Genere le layout principale de l'application
	 */
	private void generateMainLayout(){
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		zoneInteraction = new ZoneInteraction();
		zoneInteraction.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		zoneInteraction.demarrer();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		btnQuitter = new BoutonSemiAnime(new ImageIcon(Images.safeCharger("porte_fermee.png")), new ImageIcon(Images.safeCharger("porte_ouverte.png")));
		btnQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnQuitter.setToolTipText("Quitter");
		
		btnPlay = new BoutonSwitch(new Icon[]{new ImageIcon(Images.safeCharger("stop.png")), new ImageIcon(Images.safeCharger("play.png"))}){
			private static final long serialVersionUID = -8602332294030610608L;
			@Override
			public String getToolTipText(){
				if (this.getState() == 1)
					return "D\u00E9marrer l'animation";
				else
					return "Arr\u00EAter l'animation";
			}
		};
		btnPlay.addActionListener(x -> {
			if (btnPlay.getState() == 1)
				zoneInteraction.demarrer();
			else
				zoneInteraction.arreter();
		});

		messageBoard = new MessageBoard();

		// Layout
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(6)
										.addComponent(messageBoard, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
								.addComponent(zoneInteraction, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(btnPlay)
//										.addPreferredGap(ComponentPlacement.RELATED)
//										.addComponent(btnStop)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(btnQuitter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										)
								)
						.addContainerGap()
						)
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(zoneInteraction, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
								.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE))
						.addGap(5)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnQuitter)
								.addComponent(btnPlay)
//								.addComponent(btnStop)
								.addComponent(messageBoard))
						.addContainerGap())
				);
		contentPane.setLayout(gl_contentPane);
	}
	/**
	 * Genere la barre de menu de l'application
	 */
	private void generateMenu(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menuAide = new JMenu("Aide");
		JMenuItem itemGuide = new JMenuItem("Guide d'utilisation");
		JMenuItem itemConcepts = new JMenuItem("Concepts scientifiques");
		JMenuItem itemTests = new JMenuItem("Tests suggeres");
		JMenuItem itemAPropos = new JMenuItem("A propos");
		
		itemGuide.addActionListener(x -> {
			if (fenetreGuide == null)
				fenetreGuide = new FenetreGuide();
			fenetreGuide.setVisible(true);
		});
		itemConcepts.addActionListener(x -> {
			if (fenetreConcepts == null)
				fenetreConcepts = new FenetreConcepts();
			fenetreConcepts.setVisible(true);
		});
		itemTests.addActionListener(x -> {
			if (fenetreTests == null)
				fenetreTests = new FenetreTests();
			fenetreTests.setVisible(true);
		});
		itemAPropos.addActionListener(x -> JOptionPane.showMessageDialog(this, "Creer par Marcus Phan et Leo Jetzer", "A propos", JOptionPane.PLAIN_MESSAGE));

		menuAide.add(itemGuide);
		menuAide.add(itemConcepts);
		menuAide.add(itemTests);
		menuAide.add(itemAPropos);
		menuBar.add(menuAide);
		setJMenuBar(menuBar);

		JMenu menuOptions = new JMenu("Options");
		menuBar.add(menuOptions);
		
		JMenu menuBackground = new JMenu("Fond");
		JRadioButtonMenuItem[] itemFonds = {
				new JRadioButtonMenuItem("Dank"), 
				new JRadioButtonMenuItem("Ernesto"), 
				new JRadioButtonMenuItem("Windows"),
				new JRadioButtonMenuItem("Assembl\u00E9")
				};
		ButtonGroup btnsFond = new ButtonGroup();
		for (int i = 0; i < itemFonds.length; i++){
			JRadioButtonMenuItem item = itemFonds[i];
			btnsFond.add(item);
			menuBackground.add(item);
			if (i < fonds.length){
				final int n = i;
				item.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						zoneInteraction.setImageFond(fonds[n]);
						zoneInteraction.repaint();
					}
				});
			}
		}
		menuOptions.add(menuBackground);
		

		JMenuItem itemQuitter = new JMenuItem("Quitter");
		itemQuitter.addActionListener(x -> System.exit(0));
		menuOptions.add(itemQuitter);

		JMenu menuFenetre = new JMenu("Fenetre");
		menuBar.add(menuFenetre);

		itemGraphique = new JMenuItem("Ouvrir la fenetre des graphiques");
		menuFenetre.add(itemGraphique);

	}
	/**
	 * Genere le panneau Debug de l'application
	 */
	private void generateDebug(){
		JPanel pnlDebugControle = new JPanel();
		pnlDebugControle.setBackground(tabbedPane.getBackground());
		tabbedPane.addTab("debug", null, pnlDebugControle, null);
		pnlDebugControle.setLayout(null);

		JButton btnPush = new JButton("PUSH");
		btnPush.setBounds(13, 5, 77, 29);
		pnlDebugControle.add(btnPush);

		JButton btnUnpush = new JButton("UNPUSH");
		btnUnpush.setBounds(13, 35, 96, 29);
		pnlDebugControle.add(btnUnpush);

		forceX = new JSpinner();
		forceX.setBounds(134, 4, 96, 28);
		pnlDebugControle.add(forceX);
		forceX.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));

		forceY = new JSpinner();
		forceY.setBounds(134, 34, 96, 28);
		pnlDebugControle.add(forceY);
		forceY.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));

		JButton btnDemarrer = new JButton("demarrer");
		btnDemarrer.setBounds(13, 141, 100, 29);
		pnlDebugControle.add(btnDemarrer);

		JButton btnArreter = new JButton("arreter");
		btnArreter.setBounds(13, 195, 85, 29);
		pnlDebugControle.add(btnArreter);

		JButton btnRedemarrer = new JButton("redemarrer");
		btnRedemarrer.setBounds(13, 168, 112, 29);
		pnlDebugControle.add(btnRedemarrer);
		btnRedemarrer.setEnabled(false);

		JToggleButton tglbtnMettreInspecteurA = new JToggleButton("Mettre inspecteur a jour");
		tglbtnMettreInspecteurA.setBounds(46, 299, 184, 29);
		tglbtnMettreInspecteurA.addActionListener(x -> {
			if (tglbtnMettreInspecteurA.isSelected()){
				if (timerUpdate == null)
					timerUpdate = new Timer(100, y -> {
						specteur.update(Corps.PROPRIETE_POSITION);
						specteur.update(Corps.PROPRIETE_VITESSE);
						specteur.update(Corps.PROPRIETE_ACCELERATION);
						specteur.update(Corps.PROPRIETE_STATIQUE);
					});
				timerUpdate.start();
			}
			else
				timerUpdate.stop();

		});

		pnlDebugControle.add(tglbtnMettreInspecteurA);

		JLabel lblCoeffresti = new JLabel("CoeffResti");
		lblCoeffresti.setBounds(13, 67, 96, 14);
		pnlDebugControle.add(lblCoeffresti);

		JSpinner spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				for (Corps courant : zoneInteraction.getADessiner().values()) {
					courant.setcRestitution((Double) spinner.getValue());
				}
			}
		});
		spinner.setModel(new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));
		spinner.setBounds(134, 64, 96, 20);
		pnlDebugControle.add(spinner);

		JButton btnNewButton = new JButton("debug stop");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Corps courant : zoneInteraction.getADessiner().values()) {
					courant.setVitesse(new SVector2d(0,0));
				};
				zoneInteraction.repaint();
			}
		});
		btnNewButton.setBounds(13, 224, 89, 23);
		pnlDebugControle.add(btnNewButton);

		btnRedemarrer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneInteraction.redemarrer();
			}
		});
		btnArreter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneInteraction.arreter();
			}
		});
		btnDemarrer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneInteraction.demarrer();
			}
		});


	}
	/**
	 * Genere le panneau Inspecteur de l'application
	 */
	private void generateInspecteur(){
		JPanel pnlInspecteur = new JPanel();
		specteur = new Inspecteur();
		JScrollPane scrollPaneInspecteur = new JScrollPane(specteur);
		scrollPaneInspecteur.setMaximumSize(new Dimension(300, 255));
		scrollPaneInspecteur.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Objet", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPaneInspecteur.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		btnPoubelle = new BoutonSemiAnime(new ImageIcon(Images.safeCharger("poubelle_fermee.png")), new ImageIcon(Images.safeCharger("poubelle_ouverte.png")));
		btnPoubelle.addActionListener(x -> {
			zoneInteraction.supprimerSelection();
		});
		btnPoubelle.setToolTipText("Supprimer l'objet selectionne");
		btnPoubelle.setEnabled(false);

		Icon[] icons = new Icon[51];
		for (int n = 0; n < 51; n++){
			icons[n] = new ImageIcon(Images.safeCharger("graphique/graphique" + (n+1) + ".png"));
		}
		btnGraphique = new BoutonAnime(80, icons);
		btnGraphique.setToolTipText("Afficher les graphiques");
		
		fenetreGraphique = new FenetreGraphique(40);
		fenetreGraphique.setRefreshRate(0.1d);
		fenetreGraphique.getGraphiqueVitesse().setMaxY(Corps.VITESSE_MAX_VALUE + 1);
		fenetreGraphique.getGraphiquePosition().setMaxY(zoneInteraction.getHauteurMonde()/2 + 1);
		fenetreGraphique.getGraphiquePosition().setMinY(-(zoneInteraction.getHauteurMonde()/2 + 1));
		timerGraphique = new Timer(100, y -> {
			Corps sel = zoneInteraction.getSelection();
			if (sel != null)
				((FenetreGraphique)fenetreGraphique).push(sel.getPosition(), sel.getVitesse(), sel.getAcceleration());
		});

		editeurMateriau = new EditeurMateriau();
		JScrollPane scrollPaneMateriau = new JScrollPane(editeurMateriau);
		scrollPaneMateriau.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Mat\u00E9riau", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPaneMateriau.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		tabbedPane.addTab("Inspecteur", null, pnlInspecteur, null);

		JCheckBox chckbxAfficherLesVecteurs = new JCheckBox("Afficher les vecteurs");
		chckbxAfficherLesVecteurs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Corps.setDrawVectors(chckbxAfficherLesVecteurs.isSelected());
			}
		});
		GroupLayout gl_pnlInspecteur = new GroupLayout(pnlInspecteur);
		gl_pnlInspecteur.setHorizontalGroup(
				gl_pnlInspecteur.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlInspecteur.createSequentialGroup()
						.addGroup(gl_pnlInspecteur.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnlInspecteur.createSequentialGroup()
										.addContainerGap()
										.addComponent(btnPoubelle, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnGraphique, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(chckbxAfficherLesVecteurs))
								.addGroup(gl_pnlInspecteur.createSequentialGroup()
										.addGap(5)
										.addComponent(scrollPaneInspecteur, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_pnlInspecteur.createSequentialGroup()
										.addContainerGap()
										.addComponent(scrollPaneMateriau, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(17, Short.MAX_VALUE))
				);
		gl_pnlInspecteur.setVerticalGroup(
				gl_pnlInspecteur.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pnlInspecteur.createSequentialGroup()
						.addGap(5)
						.addComponent(scrollPaneInspecteur, GroupLayout.DEFAULT_SIZE, 255, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPaneMateriau, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_pnlInspecteur.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnlInspecteur.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(btnPoubelle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnGraphique, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
								.addComponent(chckbxAfficherLesVecteurs))
						.addContainerGap())
				);

		pnlInspecteur.setLayout(gl_pnlInspecteur);
	}
	/**
	 * Genere le panneau Outils de l'application
	 */
	private void generateBoiteAOutils(){
		pnlBoiteOutils = new JPanel();
		pnlBoiteOutils.setBackground(tabbedPane.getBackground());
		tabbedPane.addTab("Objets", null, pnlBoiteOutils, null);
		pnlBoiteOutils.setLayout(new GridLayout(0, 2));

		ActionListener ajoutObjet = new ActionListener(){
			int nb_outil = 0;
			@Override
			public void actionPerformed(ActionEvent x){
				nb_outil++;
				Corps objet = ((BoutonOutil)x.getSource()).getOutil().instance();
				objet.setPosition(new SVector2d(5, 2));
				objet.setOuSuisJe(zoneInteraction);
				objet.setNom("Outil" + nb_outil);
				zoneInteraction.ajouter(objet.getNom(), objet);
			}
		};

		BoutonOutil boutonOutil, boutonOutil2, boutonOutil3;
		try {
			boutonOutil = new BoutonOutil(new Outil(new Balle("balle", new SVector2d(), 0.5, null)).setIcone(Images.charger("balle.png")));
			boutonOutil.addActionListener(ajoutObjet);
			pnlBoiteOutils.add(boutonOutil);
		} catch (IOException e1) { e1.printStackTrace(); }
		try {
			boutonOutil2 = new BoutonOutil(new Outil(new Boite("boite", new SVector2d(), 2, 2, null, 0)).setIcone(Images.charger("boite.png")));
			boutonOutil2.addActionListener(ajoutObjet);
			pnlBoiteOutils.add(boutonOutil2);
		} catch (IOException e1) { e1.printStackTrace(); }
		boutonOutil3 = new BoutonOutil(new Outil(new Aimant("Aimant", new SVector2d(), 1, null)).setIcone(Images.safeCharger("aimant.png")));
		boutonOutil3.addActionListener(ajoutObjet);
		pnlBoiteOutils.add(boutonOutil3);

	}
	/**
	 * Genere les listeners de l'application
	 */
	private void generateListener(){

		// ouverture des graphiques \\
		ActionListener ouvertureGraphique = x -> {
			timerGraphique.start();
			fenetreGraphique.setVisible(true);
		};
		btnGraphique.addActionListener(ouvertureGraphique);
		itemGraphique.addActionListener(ouvertureGraphique);


		// trucs de selection \\ (laid, TODO: cleanup)
		zoneInteraction.addObjetSelectionneListener(x -> {
			specteur.setCible(x.selection);
			if (x.selection != null)
				btnPoubelle.setEnabled(true);
			else {
				btnPoubelle.setEnabled(false);
				specteur.setCible(zoneInteraction);
				editeurMateriau.charger(Materiaux.MAT_DEFAUT);
			}

			if (x.selection != null)
				editeurMateriau.charger(((Corps)x.selection).getMateriau());
			if (x.selection != null)
				Debug.message("selection: " + ((Corps)x.selection).getNom());
			if (fenetreGraphique != null){
				((FenetreGraphique)fenetreGraphique).clear();
				if (x.selection == null)
					timerGraphique.stop();
				else
					timerGraphique.start();
				fenetreGraphique.repaint();
			}
		});
		
		zoneInteraction.addPropertyChangeListener("size", new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Dimension size = (Dimension)evt.getNewValue();
				int ns = size.width < size.height ? size.height : size.width;
				fenetreGraphique.getGraphiquePosition().setMaxY(ns*0.5);
				fenetreGraphique.getGraphiquePosition().setMinY(ns*0.5);
			}	
		});

		// *************** MNEMONICS *************** \\
		// TODO : va demander un peu de travail
		KeyListener list = new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == 'I' || c == 'i')
					tabbedPane.setSelectedIndex(0);
				else if (c == 'O' || c == 'o')
					tabbedPane.setSelectedIndex(1);
				else if (Debug.DEBUG && (c == 'D' || c == 'd'))
					tabbedPane.setSelectedIndex(2);
			}
		};
		zoneInteraction.setFocusable(true);
		zoneInteraction.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
				zoneInteraction.requestFocus();
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		zoneInteraction.addKeyListener(list);

		// changement de materiau
		specteur.addProprieteChangeListener(x -> {
			if (x.getPropertyName().equals(Corps.PROPRIETE_MATERIAU)){
				editeurMateriau.charger((Materiaux)x.getNewValue());
				editeurMateriau.repaint();
			}
		});

		editeurMateriau.addProprieteChangeListener(x -> {
			Propriete prop = (PropMateriaux)specteur.getPropriete(Corps.PROPRIETE_MATERIAU);
			if (prop != null && prop instanceof PropMateriaux){
				((PropMateriaux)prop).getComboBox().repaint();
				specteur.update(Corps.PROPRIETE_MASSE);
			}
		});

	}

	/**
	 * Initialise les donnees pour le look&feel de l'application </br>
	 * (police, etc.)
	 */
	private static void initLookAndFeel(){
		try {
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, Fichier.ouvrir("font/Noteworthy-Light.ttf")));
		}
		catch (FontFormatException | IOException | NullPointerException e) {e.printStackTrace();}
		try {
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, Fichier.ouvrir("font/Noteworthy-Bold.ttf")));
		}
		catch (FontFormatException | IOException | NullPointerException e) {e.printStackTrace();}

		Font font = new Font("Noteworthy", Font.PLAIN, 13);

		UIManager.put("Button.font", font);
		UIManager.put("ToggleButton.font", font);
		UIManager.put("RadioButton.font", font);
		UIManager.put("CheckBox.font", font);
		UIManager.put("ColorChooser.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("Label.font", font);
		UIManager.put("List.font", font);
		UIManager.put("MenuBar.font", font);
		UIManager.put("MenuItem.font", font);
		UIManager.put("RadioButtonMenuItem.font", font);
		UIManager.put("CheckBoxMenuItem.font", font);
		UIManager.put("Menu.font", font);
		UIManager.put("PopupMenu.font", font);
		UIManager.put("OptionPane.font", font);
		UIManager.put("Panel.font", font);
		UIManager.put("ProgressBar.font", font);
		UIManager.put("ScrollPane.font", font);
		UIManager.put("Viewport.font", font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("Table.font", font);
		UIManager.put("TableHeader.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("PasswordField.font", font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TextPane.font", font);
		UIManager.put("EditorPane.font", font);
		UIManager.put("TitledBorder.font", font);
		UIManager.put("ToolBar.font", font);
		UIManager.put("ToolTip.font", font);
		UIManager.put("Tree.font", font);
		UIManager.put("Spinner.font", font);
	}
}
