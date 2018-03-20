package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * JPanel qui dessine une image le plus grand possible, mais sans deformation
 * @author Leo Jetzer
 *
 */
public class ImagePane extends JPanel {

	private static final long serialVersionUID = 5436314380767474237L;
	private BufferedImage img;
	
	/**
	 * Creer un nouvel ImagePane qui dessine l'image
	 * @param image - {@code BufferedImage} : l'image a dessiner
	 */
	public ImagePane(BufferedImage image){
		super();
		img = image;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (img == null)
			return;
		Graphics2D g2 = (Graphics2D)g;
		double wi = img.getWidth(), hi = img.getHeight(), wc = getWidth(), hc = getHeight();
		double wp = wc/wi, hp = hc/hi;
		if (wp < hp){
			g2.drawImage(img, 0, 0, (int)wc, (int)(hi*wp), null);
		}
		else {
			g2.drawImage(img, 0, 0, (int)(wi*hp), (int)hc, null);
		}
	}
	
	/**
	 * Assigne une nouvelle image a cet ImagePane
	 * @param image - {@code BufferedImage} : la nouvelle image
	 */
	public void setImage(BufferedImage image){ img = image; }
	/**
	 * Retourne l'image presentement affichee par ce composant
	 * @return l'image
	 */
	public BufferedImage getImage(){ return img; }
	
	@Override
	public Dimension getPreferredSize(){
		if (img == null)
			return super.getPreferredSize();
		return new Dimension(img.getWidth(), img.getHeight());
	}
	
}
