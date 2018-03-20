package ui;

import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

/**
 * JFrame non-decore qui disparait sitot qu'elle pert le focus
 * @author Leo Jetzer
 *
 */
public class TemporaryFrame extends JFrame implements MouseMotionListener, MouseListener, FocusListener{

	private static final long serialVersionUID = 760404296203412644L;
	private Point point = null;

	/**
	 * Creer une nouvelle fenetre temporaire
	 */
	public TemporaryFrame(){
		super();
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setUndecorated(true);
		this.addFocusListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void focusGained(FocusEvent e) {}
	@Override
	public void focusLost(FocusEvent e) {
		dispose();
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		Point p2 = arg0.getPoint();
		int dx = p2.x - point.x;
		int dy = p2.y - point.y;
		Point p = this.getLocationOnScreen();
		this.setLocation(p.x + dx, p.y + dy);
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		point = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
}
