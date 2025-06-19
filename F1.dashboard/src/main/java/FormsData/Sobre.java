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
import java.net.URI;

public class Sobre extends BlurChild {

    public Sobre() {
        super(new Style()
                .setBlur(8)
                .setBorder(new StyleBorder(10)
                        .setBorderWidth(0.8f)
                        .setOpacity(0.1f)
                        .setBorderColor(new GradientColor(
                                new Color(150, 150, 150),
                                new Color(200, 200, 200),
                                new Point2D.Float(0, 0),
                                new Point2D.Float(1f, 0)))
                )
                .setOverlay(new StyleOverlay(new Color(250, 250, 250), 0.04f))
        );
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Painel centralizado na tela
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel painel = new JPanel(new MigLayout("wrap 1, insets 20", "[center]", "[]20[]10[]10[]10[]20[]"));
        painel.setOpaque(false);

        JLabel titulo = new JLabel("Sistema de Gerenciamento F1");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versao = new JLabel("Versão 1.0.0");
        versao.setFont(new Font("Arial", Font.PLAIN, 14));
        versao.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descricao = new JLabel("<html><div style='text-align: center;'>Este sistema foi desenvolvido para gerenciamento de dados<br>relacionados à Fórmula 1, incluindo pilotos, equipes e corridas.</div></html>");
        descricao.setFont(new Font("Arial", Font.PLAIN, 14));
        descricao.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel creditos = new JLabel("<html><div style='text-align: center;'>Desenvolvido por:<br>João Pedro Gonçalves de Aquino</div></html>");
        creditos.setFont(new Font("Arial", Font.ITALIC, 13));
        creditos.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel links = new JLabel("<html><div style='text-align: center;'>GitHub: <a href=''>github.com/jpdevr</a><br>LinkedIn: <a href=''>linkedin.com/in/jpdevr324</a></div></html>");
        links.setFont(new Font("Arial", Font.PLAIN, 13));
        links.setCursor(new Cursor(Cursor.HAND_CURSOR));
        links.setHorizontalAlignment(SwingConstants.CENTER);

        // Ação de clique nos links
        links.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/jpdevr"));
                    Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/jpdevr324/"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Não foi possível abrir os links.");
                }
            }
        });

        JButton fechar = new JButton("Fechar");
        fechar.putClientProperty(FlatClientProperties.STYLE, "background:#FF0000;foreground:#FFFFFF;font:bold;arc:999;");
        fechar.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        painel.add(titulo);
        painel.add(versao);
        painel.add(descricao);
        painel.add(creditos);
        painel.add(links);
        painel.add(fechar, "gaptop 10, center");

        centerPanel.add(painel);
        add(centerPanel, BorderLayout.CENTER);
    }
}
