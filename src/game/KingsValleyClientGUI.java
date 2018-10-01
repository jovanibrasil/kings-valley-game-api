package game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Interface gráfica para o cliente do KingsValley. 
 * 
 * 
 * Créditos:
 * A base da interface gráfica foi o jogo da velha implementado em https://www.paulocollares.com.br/2012/08/jogo-da-velha-em-java/.
 *
 */
public class KingsValleyClientGUI extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton[] button;
    private JButton btnEncerrarPartida;
    private JLabel lblTxtVirorias;
    private JLabel lblTxtDerrotas;
    private JLabel lblValueVitorias;
    private JLabel lblValueDerrotas;
    private int quemJoga = 0;
    private int pnt1 = 0;
    private int pnt2 = 0;
    private JLabel lblStatusPartida;

    public void acao(int bnt) {
        String XO;
        if (quemJoga == 0) {
            XO = "X";
            quemJoga = 1;
            lblStatusPartida.setText("Vez do Jogador 2");
        } else {
            XO = "O";
            quemJoga = 0;
            lblStatusPartida.setText("Vez do Jogador 1");

        }
        button[bnt].setText(XO);
        button[bnt].setEnabled(false);
        verifica(XO);
    }
    
    public boolean verifica(String XO) {

        //verificações horizontais
        if ((button[0].getText().equals(XO)) && (button[1].getText().equals(XO)) && (button[2].getText().equals(XO))) {
            button[0].setBackground(Color.green);
            button[1].setBackground(Color.green);
            button[2].setBackground(Color.green);
            ganhou(XO);
            return true;
        }
        if ((button[3].getText().equals(XO)) && (button[4].getText().equals(XO)) && (button[5].getText().equals(XO))) {
            button[3].setBackground(Color.green);
            button[4].setBackground(Color.green);
            button[5].setBackground(Color.green);
            ganhou(XO);
            return true;
        }
        if ((button[6].getText().equals(XO)) && (button[7].getText().equals(XO)) && (button[8].getText().equals(XO))) {
            button[6].setBackground(Color.green);
            button[7].setBackground(Color.green);
            button[8].setBackground(Color.green);
            ganhou(XO);
            return true;
        }

        //verificaçoes verticais
        if ((button[0].getText().equals(XO)) && (button[3].getText().equals(XO)) && (button[6].getText().equals(XO))) {
            button[0].setBackground(Color.green);
            button[3].setBackground(Color.green);
            button[6].setBackground(Color.green);
            ganhou(XO);
            return true;
        }
        if ((button[1].getText().equals(XO)) && (button[4].getText().equals(XO)) && (button[7].getText().equals(XO))) {
            button[1].setBackground(Color.green);
            button[4].setBackground(Color.green);
            button[7].setBackground(Color.green);
            ganhou(XO);
            return true;
        }
        if ((button[2].getText().equals(XO)) && (button[5].getText().equals(XO)) && (button[8].getText().equals(XO))) {
            button[2].setBackground(Color.green);
            button[5].setBackground(Color.green);
            button[8].setBackground(Color.green);
            ganhou(XO);
            return true;
        }

        //verificações diagonais
        if ((button[0].getText().equals(XO)) && (button[4].getText().equals(XO)) && (button[8].getText().equals(XO))) {
            button[0].setBackground(Color.green);
            button[4].setBackground(Color.green);
            button[8].setBackground(Color.green);
            ganhou(XO);
            return true;
        }
        if ((button[2].getText().equals(XO)) && (button[4].getText().equals(XO)) && (button[6].getText().equals(XO))) {
            button[2].setBackground(Color.green);
            button[4].setBackground(Color.green);
            button[6].setBackground(Color.green);
            ganhou(XO);
            return true;
        }

        //verifica se deu velha
        if ((button[0].getText() != "   ") && (button[1].getText() != "   ") && (button[2].getText() != "   ") && (button[3].getText() != "   ") && (button[4].getText() != "   ") && (button[5].getText() != "   ") && (button[6].getText() != "   ") && (button[7].getText() != "   ") && (button[8].getText() != "   ")) {
            for (int i = 0; i < 9; i++) {
                button[i].setBackground(Color.red);
            }
            velha();
            return true;
        }
        return true;
    }

    public void velha() {
        for (int i = 0; i < 9; i++) {
            button[i].setEnabled(false);
        }
        btnEncerrarPartida.setVisible(true);
        lblStatusPartida.setText("Deu Velha");
    }

    public void ganhou(String XO) {
        for (int i = 0; i < 9; i++) {
            button[i].setEnabled(false);
        }
        String texto;
        if (XO == "X") {
            texto = "Jogador 1 Venceu";
            pnt1++;
        } else {
            texto = "Jogador 2 Venceu";
            pnt2++;
        }
        lblValueVitorias.setText(Integer.toString(pnt1));
        lblValueDerrotas.setText(Integer.toString(pnt2));
        btnEncerrarPartida.setVisible(true);
        lblStatusPartida.setText(texto);
    }

    public void newGame() {
        btnEncerrarPartida.setVisible(false);
        lblStatusPartida.setText("Vez do Jogador 1");
        quemJoga = 0;
        for (int i = 0; i < 9; i++) {
            button[i].setText("   ");
            button[i].setBackground(btnEncerrarPartida.getBackground());
            button[i].setEnabled(true);
        }
    }

    public KingsValleyClientGUI() {
        super();
        setResizable(false);
        setTitle("KingsValleyClientGUI");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        Box editBox = Box.createVerticalBox();
        Box[] box = new Box[9];
        button = new JButton[25];
        
        lblStatusPartida = new JLabel("[Msg] Status da partida ...");
        btnEncerrarPartida = new JButton("EncerrarPartida");
        btnEncerrarPartida.setVisible(true);
        lblTxtVirorias = new JLabel("Vitorias: ");
        lblTxtDerrotas = new JLabel("Derrotas: ");
        lblValueVitorias = new JLabel("0");
        lblValueDerrotas = new JLabel("0");
        
        for (int i = 0; i < 9; i++) {
            box[i] = Box.createHorizontalBox();
        }
        for (int i = 0; i < 25; i++) {
            button[i] = new JButton("   ");
            //button[i].setPreferredSize(new Dimension(100,300) );
            button[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    acao(0);
                }
            });
        }
        
        box[0].add(lblTxtVirorias);
        box[0].add(lblValueVitorias);
        box[0].add(Box.createRigidArea(new Dimension(20, 0)));
        box[0].add(lblTxtDerrotas);
        box[0].add(lblValueDerrotas);
        box[2].add(lblStatusPartida);
        
        for(int i=3, j=0; i<8; i++, j+=5) {
        	box[i].add(button[j]);
            box[i].add(button[j+1]);
            box[i].add(button[j+2]);
            box[i].add(button[j+3]);
            box[i].add(button[j+4]);
            //box[i].setSize(40, 80);
        }
        
        box[8].add(btnEncerrarPartida);

        editBox.add(box[0]);
        editBox.add(box[1]);
        editBox.add(Box.createVerticalStrut(30));
        editBox.add(box[2]);
        editBox.add(box[3]);
        editBox.add(box[4]);
        editBox.add(box[5]);
        editBox.add(box[6]);
        editBox.add(box[7]);
        editBox.add(box[8]);

        Container container = getContentPane();
        container.setLayout(new GridBagLayout());
        
//        GridBagConstraints c = new GridBagConstraints();
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.gridx = 0;
//        c.gridy = 0;
//        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
//        
        container.add(editBox);
        setSize(300, 300);
        setLocation((d.width - 460) / 2, (d.height - 500) / 2);
        setVisible(true);

        
    }
    
    public static void main(String[] args) {
        KingsValleyClientGUI vf = new KingsValleyClientGUI();
        vf.setVisible(true);
    }
}