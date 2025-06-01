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

public class Cars extends BlurChild {

    private JPanel painelCartoes;

    public Cars() {
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
        setLayout(new BorderLayout());
        criarPainelCartoes();
        dadosCarros();

        JScrollPane scrollPane = new JScrollPane(painelCartoes, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(15);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,"" +
                "trackArc:999;" +
                "width:5;" +
                "thumbInsets:0,0,0,0");

        add(scrollPane, BorderLayout.CENTER);
    }

    private void criarPainelCartoes() {
        painelCartoes = new JPanel();
        painelCartoes.setLayout(new MigLayout("insets 20, gap 30", "[]", "[]"));
        painelCartoes.setOpaque(false);
    }

    private void dadosCarros() {
        conexao comb = new conexao();
        String sql = """
                SELECT c.nomeTime, c.chassi, c.fotoIcon, c.fotoCarro, c.motor, c.idCarro, e.icon AS logoEquipe
                FROM carros c
                JOIN equipes e ON c.chassi = e.carro
                ORDER BY c.idCarro
                """;

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String nomeTime = rs.getString("nomeTime");
                String chassi = rs.getString("chassi");
                String motor = rs.getString("motor");

                BufferedImage icon = null;
                Blob blobIcon = rs.getBlob("fotoIcon");
                if (blobIcon != null) {
                    try (InputStream in = blobIcon.getBinaryStream()) {
                        icon = ImageIO.read(in);
                    }
                }

                BufferedImage fotoCarro = null;
                Blob blobCarro = rs.getBlob("fotoCarro");
                if (blobCarro != null) {
                    try (InputStream in = blobCarro.getBinaryStream()) {
                        fotoCarro = ImageIO.read(in);
                    }
                }

                BufferedImage logoEquipe = null;
                Blob blobLogo = rs.getBlob("logoEquipe");
                if (blobLogo != null) {
                    try (InputStream in = blobLogo.getBinaryStream()) {
                        logoEquipe = ImageIO.read(in);
                    }
                }

                BlurChild cartao = criarCartao(nomeTime, chassi, motor, icon, fotoCarro, logoEquipe);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar carros: " + e.getMessage());
        }
    }

    private BlurChild criarCartao(String nomeTime, String chassi, String motor,
                                  BufferedImage icon, BufferedImage fotoCarro, BufferedImage logoEquipe) {

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

        cartao.setPreferredSize(new Dimension(900, 590));
        cartao.setLayout(new BorderLayout());

        // Topo (linha 1): logo, informações e ícone
        JPanel linhaSuperior = new JPanel(new MigLayout("insets 0, gap 20", "[grow]10[grow]10[grow]", "[]"));
        linhaSuperior.setOpaque(false);
        linhaSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Logo da equipe (esquerda)
        if (logoEquipe != null) {
            ImageIcon logoIcon = criarImagemArredondada(logoEquipe, 130, 80, 20);
            JLabel labelLogo = new JLabel(logoIcon);
            linhaSuperior.add(labelLogo, "grow");
        }

        // Informações (centro)
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BoxLayout(painelCentro, BoxLayout.Y_AXIS));
        painelCentro.setOpaque(false);

        Font fonteMaior = new Font("SansSerif", Font.BOLD, 22);
        Font fonteNormal = new Font("SansSerif", Font.PLAIN, 18);

        JLabel labelNome = new JLabel(nomeTime);
        JLabel labelChassi = new JLabel("Chassi: " + chassi);
        JLabel labelMotor = new JLabel("Motor: " + motor);

        for (JLabel lbl : new JLabel[]{labelNome, labelChassi, labelMotor}) {
            lbl.setFont(lbl == labelNome ? fonteMaior : fonteNormal);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelCentro.add(lbl);
            painelCentro.add(Box.createVerticalStrut(8));
        }

        linhaSuperior.add(painelCentro, "grow");

        // Ícone do carro (direita)
        if (icon != null) {
            ImageIcon iconCarro = criarImagemArredondada(icon, 270, 100, 20);
            JLabel labelIcone = new JLabel(iconCarro);
            linhaSuperior.add(labelIcone, "grow");
        }

        // Parte inferior (linha 2): imagem grande do carro
        JLabel labelImagem = new JLabel();
        if (fotoCarro != null) {
            ImageIcon imagemCarro = criarImagemArredondada(fotoCarro, 800, 450, 15);
            labelImagem.setIcon(imagemCarro);
        }

        JPanel painelImagem = new JPanel();
        painelImagem.setOpaque(false);
        painelImagem.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        painelImagem.add(labelImagem);

        // Monta o cartão
        cartao.add(linhaSuperior, BorderLayout.NORTH);
        cartao.add(painelImagem, BorderLayout.CENTER);

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
