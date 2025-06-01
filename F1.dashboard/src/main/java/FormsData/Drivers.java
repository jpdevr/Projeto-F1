package FormsData;

import com.formdev.flatlaf.FlatClientProperties;
import conexao.conexao;
import net.miginfocom.swing.MigLayout;
import raven.drawer.component.header.SimpleHeader;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;

import static conexao.conexao.statement;

public class Drivers extends BlurChild {

    private JPanel painelCartoes;

    public Drivers() {
        super(new Style()
                .setBlur(8)
                .setBorder(new StyleBorder(10)
                        .setBorderWidth(0.8f)
                        .setOpacity(0.1f)
                        .setBorderColor(new GradientColor(
                                new Color(150, 150, 150),
                                new Color(200, 200, 200),
                                new Point2D.Float(0, 0),
                                new Point2D.Float(1f, 0))
                        )
                )
                .setOverlay(new StyleOverlay(new Color(250, 250, 250), 0.04f))
        );
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("wrap, fillx", "[fill]", "[top]"));
        criarPainelCartoes();
        dadosPilotos(); // carrega os cartões do banco
        add(painelCartoes);

        JScrollPane scrollPane = new JScrollPane(painelCartoes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,"" +
                "trackArc:999;"+
                "width:5;"+
                "thumbInsets:0,0,0,0");

        add(scrollPane);
    }

    private void criarPainelCartoes() {
        painelCartoes = new JPanel();
        painelCartoes.setLayout(new MigLayout("wrap 2, gap 40 40, align center", "[450!] [450!]", "[]"));
        painelCartoes.setOpaque(false);
    }

    private void dadosPilotos() {
        conexao comb = new conexao();
        String sql = "SELECT nome, idPiloto, titulos, equipe, pontos, foto, gpWin, pais FROM drivers order by equipe";

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String nome = rs.getString("nome");
                String pais = rs.getString("pais");
                int pontos = rs.getInt("pontos");
                int titulos = rs.getInt("titulos");
                String equipe = rs.getString("equipe");
                int gpsVencidos = rs.getInt("gpWin");

                BufferedImage imagem = null;
                Blob blob = rs.getBlob("foto");
                if (blob != null) {
                    try (InputStream in = blob.getBinaryStream()) {
                        imagem = ImageIO.read(in);
                    }
                }

                BlurChild cartao = criarCartao(nome, pais, pontos, titulos, equipe, gpsVencidos, imagem);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private BlurChild criarCartao(String nome, String pais, int pontos, int titulos, String equipe, int gps, BufferedImage imagem) {
        BlurChild cartao = new BlurChild(new Style()
                .setBlur(10)
                .setBorder(new StyleBorder(10)
                        .setBorderWidth(0.8f)
                        .setOpacity(0.3f)
                        .setBorderColor(new GradientColor(
                                new Color(150, 150, 150),
                                new Color(200, 200, 200),
                                new Point2D.Float(0, 0),
                                new Point2D.Float(1f, 0))
                        )
                )
                .setOverlay(new StyleOverlay(new Color(250, 250, 250), 0.08f))
        );

        cartao.setPreferredSize(new Dimension(450, 120));
        cartao.setLayout(new BorderLayout());

        // Painel intermediário para centralizar os dois painéis horizontalmente
        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelCentral.setOpaque(false);

        // Foto arredondada
        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.setPreferredSize(new Dimension(120, 120));
        fotoPanel.setOpaque(false);

        if (imagem != null) {
            ImageIcon fotoArredondada = criarImagemArredondada(imagem, 120, 120, 30);
            JLabel labelFoto = new JLabel(fotoArredondada);
            labelFoto.setHorizontalAlignment(SwingConstants.CENTER);
            fotoPanel.add(labelFoto, BorderLayout.CENTER);
        }

        // Infos
        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        infos.setOpaque(false);

        // Alinhar os labels ao centro horizontalmente
        for (Component comp : new Component[]{
                new JLabel("Nome: " + nome),
                new JLabel("País: " + pais),
                new JLabel("Pontos: " + pontos),
                new JLabel("Títulos: " + titulos),
                new JLabel("Equipe: " + equipe),
                new JLabel("GPs vencidos: " + gps)
        }) {
            JLabel label = (JLabel) comp;
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            infos.add(label);
            infos.add(Box.createVerticalStrut(5)); // Espaçamento maior entre linhas
        }

        painelCentral.add(fotoPanel);
        painelCentral.add(infos);

        cartao.add(painelCentral, BorderLayout.CENTER);

        return cartao;
    }


    private ImageIcon criarImagemArredondada(BufferedImage imagemOriginal, int largura, int altura, int raio) {
        BufferedImage imagemFinal = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagemFinal.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, largura, altura, raio, raio));
        g2.drawImage(imagemOriginal.getScaledInstance(largura, altura, Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();

        return new ImageIcon(imagemFinal);
    }
}
