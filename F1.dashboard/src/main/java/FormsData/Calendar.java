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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import static conexao.conexao.statement;

public class Calendar extends BlurChild {

    private JPanel painelCartoes;
    private boolean proximaCorridaMarcada = false;

    public Calendar() {
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
        setLayout(new MigLayout("fill", "[fill]", "[fill]"));

        criarPainelCartoes();
        carregarCalendario();

        // Scroll horizontal configurado para painelCartoes
        JScrollPane scrollPane = new JScrollPane(painelCartoes,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(15);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,"" +
                "trackArc:999;" +
                "width:5;" +
                "thumbInsets:0,0,0,0");

        add(scrollPane, "grow, push");
    }

    private void criarPainelCartoes() {
        painelCartoes = new JPanel();

        // Layout horizontal: MigLayout com wrap 1 para linha única horizontal
        painelCartoes.setLayout(new MigLayout("insets 10, gap 20 20, align center",
                "[]",
                "[]"));
        painelCartoes.setOpaque(false);
    }

    private void carregarCalendario() {
        conexao comb = new conexao();
        String sql = "SELECT id, data, nome, voltas, foto FROM circuito ORDER BY data";

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date agora = new Date();

            while (rs.next()) {
                String nomeGP = rs.getString("nome");
                String dataHora = rs.getString("data");
                int voltas = rs.getInt("voltas");

                Date dataGP = inputFormat.parse(dataHora);
                String dataFormatada = outputFormat.format(dataGP);

                if (!proximaCorridaMarcada && dataGP.after(agora)) {
                    painelCartoes.add(criarDivisoriaProximaCorrida(), "gapleft 20, gapright 20");
                    proximaCorridaMarcada = true;
                }

                BufferedImage imagem = null;
                Blob blob = rs.getBlob("foto");
                if (blob != null) {
                    try (InputStream in = blob.getBinaryStream()) {
                        imagem = ImageIO.read(in);
                    }
                }

                BlurChild cartao = criarCartao(nomeGP, dataFormatada, voltas, imagem);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar calendário: " + e.getMessage());
        }
    }

    private JComponent criarDivisoriaProximaCorrida() {
        JPanel divisor = new JPanel(new BorderLayout());
        divisor.setOpaque(false);
        divisor.setPreferredSize(new Dimension(180, 160));  // ajuste para barra menor e mais vertical

        JLabel label = new JLabel("<html><center>||||||||<br><br>PRÓXIMA<br>CORRIDA<br><br>||||||||</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(255, 255, 255));

        divisor.add(label, BorderLayout.CENTER);
        return divisor;

    }

    private BlurChild criarCartao(String nomeGP, String dataHora, int voltas,
                                          BufferedImage imagem) {

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

        // Linha superior com 2 colunas: logo equipe à esquerda e infos circuito à direita
        JPanel linhaSuperior = new JPanel(new MigLayout("insets 0, gap 50", "[190px][grow]", "[]"));
        linhaSuperior.setOpaque(false);
        linhaSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        try {
            BufferedImage logoEquipeImg = ImageIO.read(getClass().getResource("/local/imgs/F1logo.png")); // caminho absoluto
            if (logoEquipeImg != null) {
                ImageIcon logoIcon = criarImagemArredondada(logoEquipeImg, 190, 70, 0);
                JLabel labelLogo = new JLabel(logoIcon);
                linhaSuperior.add(labelLogo);
            } else {
                linhaSuperior.add(Box.createHorizontalStrut(130));
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar logo da equipe: " + e.getMessage());
            linhaSuperior.add(Box.createHorizontalStrut(130));
        }

        // Informações do circuito (direita)
        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setOpaque(false);

        Font fonteMaior = new Font("SansSerif", Font.BOLD, 22);
        Font fonteNormal = new Font("SansSerif", Font.PLAIN, 18);

        JLabel labelNomeGP = new JLabel(nomeGP);
        JLabel labelDataHora = new JLabel("Data e hora: " + dataHora);
        JLabel labelVoltas = new JLabel("Voltas: " + voltas);

        for (JLabel lbl : new JLabel[]{labelNomeGP, labelDataHora, labelVoltas}) {
            lbl.setFont(lbl == labelNomeGP ? fonteMaior : fonteNormal);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelInfo.add(lbl);
            painelInfo.add(Box.createVerticalStrut(8));
        }

        linhaSuperior.add(painelInfo, "grow");

        // Parte inferior: imagem do circuito (menor que a foto do carro original)
        JLabel labelImagem = new JLabel();
        if (imagem != null) {
            ImageIcon imagemCircuito = criarImagemArredondada(imagem, 800, 450, 15);
            labelImagem.setIcon(imagemCircuito);
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
