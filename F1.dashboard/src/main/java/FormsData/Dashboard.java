package FormsData;

import conexao.conexao;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.chart.bar.HorizontalBarChart;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JPanel {

    private HorizontalBarChart barChartEquipes;
    private PieChart pieChartCircuitos;

    public Dashboard() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setOpaque(false);

        BlurChild blurBackground = new BlurChild(new Style()
                .setBlur(15)
                .setBorder(new StyleBorder(15)
                        .setBorderWidth(1f)
                        .setOpacity(0.15f)
                        .setBorderColor(new GradientColor(
                                new Color(180, 180, 180),
                                new Color(210, 210, 210),
                                new Point2D.Float(0, 0),
                                new Point2D.Float(1f, 0)
                        )))
                .setOverlay(new StyleOverlay(new Color(245, 245, 245, 180), 0.20f))
        );
        blurBackground.setLayout(new MigLayout(
                "wrap 2, insets 30, alignx center",
                "[center, grow 0]50[center, grow 0]",
                "[]30[]"
        ));
        add(blurBackground, BorderLayout.CENTER);

        BlurChild painelPilotos = createBlurPanel(12, 0.10f, new Color(255, 255, 255, 180), true);
        painelPilotos.setLayout(new BorderLayout(30, 0));
        painelPilotos.setOpaque(false);

        JLabel tituloPilotos = new JLabel("Campeonato de Pilotos");
        tituloPilotos.putClientProperty(FlatClientProperties.STYLE, "font:+3");
        tituloPilotos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tituloPilotos.setHorizontalAlignment(SwingConstants.CENTER);
        painelPilotos.add(tituloPilotos, BorderLayout.NORTH);

        List<Piloto> pilotos = new ArrayList<>();
        try {
            conexao comb = new conexao();
            comb.conectar();
            Connection conn = comb.getConnection();

            String sql = "SELECT nome, pontos, foto FROM drivers ORDER BY pontos DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                int pontos = rs.getInt("pontos");
                byte[] fotoBytes = rs.getBytes("foto");
                ImageIcon imagem = null;

                if (fotoBytes != null && fotoBytes.length > 0) {
                    imagem = new ImageIcon(fotoBytes);
                }

                pilotos.add(new Piloto(nome, pontos, imagem));
            }

            comb.desconectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pilotos: " + e.getMessage());
        }

        JPanel listaPilotos = new JPanel();
        listaPilotos.setLayout(new BoxLayout(listaPilotos, BoxLayout.Y_AXIS));
        listaPilotos.setOpaque(false);

        int colocacao = 1;
        for (Piloto p : pilotos) {
            JPanel linha = new JPanel(new BorderLayout());
            linha.setOpaque(false);
            linha.setMaximumSize(new Dimension(300, 40));
            JLabel texto = new JLabel(colocacao + "° - " + p.nome + " (" + p.pontos + " pts)");
            texto.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            linha.add(texto, BorderLayout.CENTER);

            JSeparator separator = new JSeparator();
            separator.setForeground(new Color(200, 200, 200));
            separator.setMaximumSize(new Dimension(300, 1));

            listaPilotos.add(linha);
            listaPilotos.add(separator);
            colocacao++;
        }

        JScrollPane scrollPane = new JScrollPane(listaPilotos);
        scrollPane.setPreferredSize(new Dimension(420, 190));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:999;" +
                "width:5;" +
                "thumbInsets:0,0,0,0");

        JPanel podio = new JPanel();
        podio.setOpaque(false);
        podio.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);

        if (pilotos.size() >= 3) {
            JPanel segundo = criarCardPiloto(pilotos.get(1), 80);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.SOUTH;
            podio.add(segundo, gbc);

            JPanel primeiro = criarCardPiloto(pilotos.get(0), 100, true);
            gbc.gridx = 1;
            podio.add(primeiro, gbc);

            JPanel terceiro = criarCardPiloto(pilotos.get(2), 70);
            gbc.gridx = 2;
            podio.add(terceiro, gbc);
        }

        blurBackground.add(painelPilotos, "span 2, growx, height 200!");
        painelPilotos.add(scrollPane, BorderLayout.WEST);
        painelPilotos.add(podio, BorderLayout.CENTER);

        BlurChild barChartPanel = createBlurPanel(12, 0.10f, new Color(255, 255, 255, 180), true);
        createBarChartEquipes();
        barChartPanel.setLayout(new BorderLayout());
        barChartPanel.add(barChartEquipes, BorderLayout.CENTER);

        BlurChild pieChartPanel = createBlurPanel(12, 0.10f, new Color(255, 255, 255, 180), false);
        createPieChartCircuitos();
        pieChartPanel.setLayout(new BorderLayout());
        pieChartPanel.add(pieChartCircuitos, BorderLayout.CENTER);

        blurBackground.add(barChartPanel, "grow, height 360!");
        blurBackground.add(pieChartPanel, "grow, height 360!");
    }

    private JPanel criarCardPiloto(Piloto p, int tamanho) {
        return criarCardPiloto(p, tamanho, false);
    }

    private JPanel criarCardPiloto(Piloto p, int tamanho, boolean bold) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);

        if (p.foto != null) {
            Image img = p.foto.getImage().getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH);
            card.add(new JLabel(new ImageIcon(img)), BorderLayout.CENTER);
        } else {
            card.add(new JLabel("Sem imagem"), BorderLayout.CENTER);
        }

        JLabel nomeLabel = new JLabel(p.nome, SwingConstants.CENTER);
        if (bold) nomeLabel.setFont(nomeLabel.getFont().deriveFont(Font.BOLD));
        card.add(nomeLabel, BorderLayout.SOUTH);
        return card;
    }

    private BlurChild createBlurPanel(float blurRadius, float opacity, Color overlayColor, boolean withBorder) {
        Style style = new Style()
                .setBlur(blurRadius)
                .setOverlay(new StyleOverlay(overlayColor, opacity));

        if (withBorder) {
            style.setBorder(new StyleBorder(blurRadius)
                    .setBorderWidth(0.7f)
                    .setOpacity(opacity)
                    .setBorderColor(new GradientColor(
                            new Color(200, 200, 200),
                            new Color(220, 220, 220),
                            new Point2D.Float(0, 0),
                            new Point2D.Float(1f, 0)
                    )));
        } else {
            style.setBorder(new StyleBorder(blurRadius)
                    .setBorderWidth(0f)
                    .setOpacity(0f)
                    .setBorderColor(new Color(0, 0, 0, 0)));
        }

        return new BlurChild(style);
    }

    private void createBarChartEquipes() {
        barChartEquipes = new HorizontalBarChart();
        barChartEquipes.setOpaque(false);
        barChartEquipes.setBarColor(Color.decode("#ff0000"));
        barChartEquipes.setDataset(createDatasetEquipes());
        JLabel header = new JLabel("Campeonato de Construtores");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        barChartEquipes.setHeader(header);
    }

    private void createPieChartCircuitos() {
        pieChartCircuitos = new PieChart();
        pieChartCircuitos.setOpaque(false);
        pieChartCircuitos.getChartColor().addColor(
                Color.decode("#808080"),
                Color.decode("#ff0000")
        );
        pieChartCircuitos.setDataset(createDatasetCircuitos());
        JLabel header = new JLabel("GP's Realizados");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        pieChartCircuitos.setHeader(header);
    }

    private DefaultPieDataset<String> createDatasetEquipes() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        try {
            conexao comb = new conexao();
            comb.conectar();
            Connection conn = comb.getConnection();

            String sql = "SELECT nome, pontos FROM equipes ORDER BY pontos DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                int pontos = rs.getInt("pontos");
                dataset.addValue(nome, pontos);
            }

            comb.desconectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar dataset equipes: " + e.getMessage());
        }
        return dataset;
    }

    private DefaultPieDataset<String> createDatasetCircuitos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        try {
            conexao comb = new conexao();
            comb.conectar();
            Connection conn = comb.getConnection();

            String sql = "SELECT data FROM circuito";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int total = 0;
            int realizados = 0;
            LocalDateTime agora = LocalDateTime.now();

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("data");
                if (ts != null) {
                    total++;
                    LocalDateTime dataGP = ts.toLocalDateTime();
                    if (dataGP.isBefore(agora)) {
                        realizados++;
                    }
                }
            }
            int restantes = total - realizados;

            dataset.addValue("Realizados", realizados);
            dataset.addValue("Restantes", restantes);

            comb.desconectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar dataset circuitos: " + e.getMessage());
        }
        return dataset;
    }

    private static class Piloto {
        String nome;
        int pontos;
        ImageIcon foto;

        public Piloto(String nome, int pontos, ImageIcon foto) {
            this.nome = nome;
            this.pontos = pontos;
            this.foto = foto;
        }
    }
}
