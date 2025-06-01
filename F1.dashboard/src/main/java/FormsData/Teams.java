package FormsData;

import com.formdev.flatlaf.FlatClientProperties;
import conexao.conexao;
import net.miginfocom.swing.MigLayout;
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

public class Teams extends BlurChild {

    private JPanel painelCartoes;

    public Teams() {
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
        dadosEquipes(); // carrega os cartões do banco
        add(painelCartoes);

        JScrollPane scrollPane = new JScrollPane(painelCartoes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,"" +
                "trackArc:999;"+
                "width:5;"+
                "thumbInsets:0,0,0,0");

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, "grow, push");
    }

    private void criarPainelCartoes() {
        painelCartoes = new JPanel();
        painelCartoes.setLayout(new MigLayout("wrap 1, gap 30 30, align center", "[1000!]", "[]"));
        painelCartoes.setOpaque(false);
    }

    private void dadosEquipes() {
        conexao comb = new conexao();
        String sql = "SELECT nome, titulos, pontos, pilotos, icon, idEquipe, carro, fotoCarro, ano FROM equipes ORDER BY idEquipe";

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String nome = rs.getString("nome");
                int titulos = rs.getInt("titulos");
                int pontos = rs.getInt("pontos");
                String pilotos = rs.getString("pilotos");
                String nomeCarro = rs.getString("carro");
                int ano = rs.getInt("ano");

                BufferedImage logo = null;
                Blob blobLogo = rs.getBlob("icon");
                if (blobLogo != null) {
                    try (InputStream in = blobLogo.getBinaryStream()) {
                        logo = ImageIO.read(in);
                    }
                }

                BufferedImage carroImg = null;
                Blob blobCarro = rs.getBlob("fotoCarro");
                if (blobCarro != null) {
                    try (InputStream in = blobCarro.getBinaryStream()) {
                        carroImg = ImageIO.read(in);
                    }
                }

                BlurChild cartao = criarCartao(nome, titulos, pontos, pilotos, nomeCarro, logo, carroImg, ano);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar equipes: " + e.getMessage());
        }
    }

    private BlurChild criarCartao(String nome, int titulos, int pontos, String pilotos, String nomeCarro, BufferedImage logo, BufferedImage carroImg, int ano) {
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
                .setOverlay(new StyleOverlay(new Color(250, 250, 250), 0.10f))
        );

        cartao.setPreferredSize(new Dimension(1000, 300));
        cartao.setLayout(new BorderLayout());

        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        painelCentral.setOpaque(false);

        // Painel da imagem (logo + carro)
        JPanel painelImagem = new JPanel();
        painelImagem.setLayout(new BoxLayout(painelImagem, BoxLayout.Y_AXIS));
        painelImagem.setOpaque(false);

        // Logo
        if (logo != null) {
            ImageIcon logoIcon = criarImagemArredondada(logo, 200, 120, 30);
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelImagem.add(logoLabel);
        }

        painelImagem.add(Box.createVerticalStrut(10));

        // Nome do carro
//        JLabel nomeCarroLabel = new JLabel(nomeCarro);
//        nomeCarroLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
//        nomeCarroLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        painelImagem.add(nomeCarroLabel);

        // Imagem do carro
        if (carroImg != null) {
            ImageIcon carroIcon = criarImagemArredondada(carroImg, 300, 100, 30);
            JLabel carroLabel = new JLabel(carroIcon);
            carroLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelImagem.add(Box.createVerticalStrut(5));
            painelImagem.add(carroLabel);
        }

        // Painel de informações à direita
        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infos.setOpaque(false);

        Font fonteMaior = new Font("SansSerif", Font.BOLD, 20);
        Font fonteNormal = new Font("SansSerif", Font.PLAIN, 18);

        JLabel labelNome = new JLabel(nome);
        JLabel labelTitulos = new JLabel("Títulos: " + titulos);
        JLabel labelPontos = new JLabel("Pontos: " + pontos);
        JLabel labelPilotos = new JLabel("Pilotos: " + pilotos);
        JLabel labelAno = new JLabel("Ano de entrada: " + ano);

        for (JLabel label : new JLabel[]{labelNome, labelTitulos, labelPontos, labelPilotos, labelAno}) {
            label.setFont(label == labelNome ? fonteMaior : fonteNormal);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            infos.add(label);
            infos.add(Box.createVerticalStrut(10));
        }

        painelCentral.add(painelImagem);
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
