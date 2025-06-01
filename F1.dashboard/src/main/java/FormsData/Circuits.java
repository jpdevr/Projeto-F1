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

public class Circuits extends BlurChild {

    private JPanel painelCartoes;

    public Circuits() {
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
        dadosCircuitos(); // carrega os cartões do banco
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
        // Usando layout com apenas uma coluna e centralização
        painelCartoes.setLayout(new MigLayout("wrap 1, gap 30 30, align center", "[920!]", "[]"));
        painelCartoes.setOpaque(false);
    }

    private void dadosCircuitos() {
        conexao comb = new conexao();
        String sql = "SELECT id, data, nome, lastWin, foto, autodromo, voltas FROM circuito ORDER BY id";

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String dataHora = rs.getString("data");
                String nomeGP = rs.getString("nome");
                String ultimoVencedor = rs.getString("lastWin");
                String autodromo = rs.getString("autodromo");
                int volta  = rs.getInt("voltas");

                BufferedImage imagem = null;
                Blob blob = rs.getBlob("foto");
                if (blob != null) {
                    try (InputStream in = blob.getBinaryStream()) {
                        imagem = ImageIO.read(in);
                    }
                }

                BlurChild cartao = criarCartao(id, nomeGP, autodromo, dataHora, ultimoVencedor, imagem, volta);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar circuitos: " + e.getMessage());
        }
    }

    private BlurChild criarCartao(int id, String nomeGP, String circuito, String dataHora, String ultimoVencedor, BufferedImage imagem, int volta) {
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

        cartao.setPreferredSize(new Dimension(920, 250));
        cartao.setLayout(new BorderLayout());

        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        painelCentral.setOpaque(false);

        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.setPreferredSize(new Dimension(300, 200));
        fotoPanel.setOpaque(false);

        if (imagem != null) {
            ImageIcon fotoArredondada = criarImagemArredondada(imagem, 300, 200, 40);
            JLabel labelFoto = new JLabel(fotoArredondada);
            labelFoto.setHorizontalAlignment(SwingConstants.CENTER);
            fotoPanel.add(labelFoto, BorderLayout.CENTER);
        } else {
            JLabel placeholder = new JLabel("Sem imagem");
            placeholder.setHorizontalAlignment(SwingConstants.CENTER);
            placeholder.setVerticalAlignment(SwingConstants.CENTER);
            placeholder.setForeground(Color.GRAY);
            placeholder.setFont(new Font("SansSerif", Font.ITALIC, 16));
            fotoPanel.add(placeholder, BorderLayout.CENTER);
        }

        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infos.setOpaque(false);

        Font fonteMaior = new Font("SansSerif", Font.BOLD, 18);
        Font fonteNormal = new Font("SansSerif", Font.PLAIN, 16);

        JLabel labelNomeGP = new JLabel(nomeGP);
        JLabel labelCircuito = new JLabel(circuito);
        JLabel labelDataHora = new JLabel("Data e hora: " + dataHora);
        JLabel labelVencedor = new JLabel("Último vencedor: " + ultimoVencedor);
        JLabel Voltas = new JLabel("Voltas: " + volta);

        for (JLabel label : new JLabel[]{labelNomeGP, labelCircuito, labelDataHora, labelVencedor, Voltas}) {
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setFont(label == labelNomeGP || label == labelCircuito ? fonteMaior : fonteNormal);
            infos.add(label);
            infos.add(Box.createVerticalStrut(10));
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
