package kingsvalley.client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * TODO diminuir as dimensões do tesouro
 * TODO inicialização do tabuleiro
 * TODO cores diferentes no tabuleiro
 * TODO set do tabuleiro de acordo com o retorno do servidor em formato string
 * TODO set do tabuleiro unitariamente
 * 
 * 
 * Créditos da imagens utilizadas no tabuleiro:
 * 	Tesouro do centro: https://opengameart.org/content/golden-treasures
 *  Demais peças: https://opengameart.org/content/chess-pieces
 * 
 */

public class KingsValleyClientGUI extends JFrame {
	
	Color lightgreen = new Color(150, 223, 193);
	
	public class BoardSpace extends JButton implements ActionListener {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		ImageIcon image;
		private BufferedImage originalImage, modifiedImage;
		
		public BoardSpace(){
			this.setFocusPainted(false);
			this.setContentAreaFilled(false);
			this.setOpaque(true);
			//this.setForeground(Color.WHITE);
			//image = new ImageIcon(this.getClass().getResource("white_pawn.png"));
			
			getModifiedImage();
	        image = new ImageIcon(modifiedImage);
			
			setIcon(image);
			
			this.setBackground(lightgreen);
			
			this.addActionListener(this);
		}

		private void getModifiedImage()
	    {
	        try
	        {
	            originalImage = ImageIO.read(
	                new File("/home/jovani/Projects/kings-valley-game/src/kingsvalley/client/black_king.png"));
	            modifiedImage = new BufferedImage(
	                originalImage.getWidth(),
	                originalImage.getHeight(),
	                BufferedImage.TYPE_INT_ARGB);       
	        }
	        catch(IOException ioe)
	        {
	            System.out.println("Unable to read the Content of the Image.");
	            ioe.printStackTrace();
	        }

	        Graphics2D g2 = modifiedImage.createGraphics();
	        AlphaComposite newComposite = 
	            AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER, 0.5f);
	        g2.setComposite(newComposite);      
	        g2.drawImage(originalImage, 0, 0, null);
	        g2.dispose();
	    }
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setIcon(image);
		}
		
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	BoardSpace[]board;
	
	//	JLabel lblTurn = new JLabel("Player turn label");
	//	JButton btnPlay = new JButton("Play");
	//	Color blue = new Color(0, 0, 255);	
	
	public KingsValleyClientGUI() {
		super("KingsValleyGame");
		
		this.board = new BoardSpace[25];
		setSize(400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		//setBackground(blue);
		panel.setLayout(new GridLayout(5, 5));
		
		for (int i = 0; i < board.length; i++) {
			board[i] = new BoardSpace();
			panel.add(board[i]);
		}
		add(panel);
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		KingsValleyClientGUI gui = new KingsValleyClientGUI();
	}
	
	
}