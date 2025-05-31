package FormsData;


import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Drivers extends JPanel {

    public Drivers(){init();}

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("wrap,fillx", "[fill]", "[top]"));
        drivericons();
    }

    private void drivericons() {
        setSize(400, 100);
        setLayout(new FlowLayout(FlowLayout.CENTER, 40, 40));

        add(criarCartao("Hamilton", "Reino Unido", 250, 7, "Mercedes", 103));
        add(criarCartao("Verstappen", "Holanda", 280, 3, "Red Bull", 60));

    }

    private JPanel criarCartao(String nome, String pais, int pontos, int titulos, String equipe, int gps) {
        JPanel cartao = new JPanel();
        cartao.setPreferredSize(new Dimension(450, 120));
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Quadrado à esquerda
        JPanel quadrado = new JPanel();
        quadrado.setPreferredSize(new Dimension(120, 50));
        quadrado.setBackground(Color.DARK_GRAY);
        cartao.add(quadrado, BorderLayout.WEST);

        // Painel de informações (empilhadas)
        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));

        infos.add(new JLabel("Nome: " + nome));
        infos.add(new JLabel("País: " + pais));
        infos.add(new JLabel("Pontos: " + pontos));
        infos.add(new JLabel("Títulos: " + titulos));
        infos.add(new JLabel("Equipe: " + equipe));
        infos.add(new JLabel("GPs vencidos: " + gps));

        cartao.add(infos, BorderLayout.CENTER);

        return cartao;
    }

}