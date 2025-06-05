package FormsData;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import conexao.conexao;
import login_register.Usuario;
import net.miginfocom.swing.MigLayout;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static conexao.conexao.statement;

public class Cars extends BlurChild {

    private JPanel painelLista;    // Lista vertical para admin
    private JScrollPane scroll;
    private String nivel = Usuario.SessaoUsuario.nivel;


    // Lista para armazenar painéis de linha (carro) para operações em massa (exclusão)
    private List<CarRowPanel> carRows = new ArrayList<>();

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

        if ("administrador".equalsIgnoreCase(nivel)) {
            criarPainelListaAdmin();
            carregarDadosCarrosAdmin();
        } else {
            criarPainelCartoesUsuario();
            carregarDadosCarrosUsuario();
        }
    }

    /*
     * --- Modo Usuário padrão (exibição em cartões) ---
     */

    private JPanel painelCartoes;

    private void criarPainelCartoesUsuario() {
        painelCartoes = new JPanel(new MigLayout("insets 20, gap 30", "[]", "[]"));
        painelCartoes.setOpaque(false);

        scroll = new JScrollPane(painelCartoes, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getHorizontalScrollBar().setOpaque(false);
        scroll.getHorizontalScrollBar().setUnitIncrement(15);
        scroll.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:999;" +
                "width:5;" +
                "thumbInsets:0,0,0,0");

        add(scroll, BorderLayout.CENTER);
    }

    private void carregarDadosCarrosUsuario() {
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

                BufferedImage icon = carregarImagem(rs.getBlob("fotoIcon"));
                BufferedImage fotoCarro = carregarImagem(rs.getBlob("fotoCarro"));
                BufferedImage logoEquipe = carregarImagem(rs.getBlob("logoEquipe"));

                BlurChild cartao = criarCartaoCarro(nomeTime, chassi, motor, icon, fotoCarro, logoEquipe);
                painelCartoes.add(cartao);
            }

            comb.desconectar();
        } catch (Exception e) {
            System.out.println("Erro ao carregar carros: " + e.getMessage());
        }
    }

    private BlurChild criarCartaoCarro(String nomeTime, String chassi, String motor,
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
        JPanel header = new JPanel(new MigLayout("insets 0, gap 20", "[grow]10[grow]10[grow]", "[]"));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Logo da equipe
        if (logoEquipe != null) {
            JLabel labelLogo = new JLabel(criarImagemArredondada(logoEquipe, 130, 80, 20));
            header.add(labelLogo, "grow");
        }

        // Informações centrais
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        Font fonteTitulo = new Font("SansSerif", Font.BOLD, 22);
        Font fonteTexto = new Font("SansSerif", Font.PLAIN, 18);

        JLabel nomeLabel = new JLabel(nomeTime);
        JLabel chassiLabel = new JLabel("Chassi: " + chassi);
        JLabel motorLabel = new JLabel("Motor: " + motor);

        for (JLabel lbl : new JLabel[]{nomeLabel, chassiLabel, motorLabel}) {
            lbl.setFont(lbl == nomeLabel ? fonteTitulo : fonteTexto);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lbl);
            infoPanel.add(Box.createVerticalStrut(8));
        }

        header.add(infoPanel, "grow");

        // Ícone do carro
        if (icon != null) {
            JLabel labelIcon = new JLabel(criarImagemArredondada(icon, 270, 100, 20));
            header.add(labelIcon, "grow");
        }

        // Parte inferior: imagem principal do carro
        JLabel imagemCarroLabel = new JLabel();
        if (fotoCarro != null) {
            imagemCarroLabel.setIcon(criarImagemArredondada(fotoCarro, 800, 450, 15));
        }

        JPanel imagemPanel = new JPanel();
        imagemPanel.setOpaque(false);
        imagemPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        imagemPanel.add(imagemCarroLabel);

        cartao.add(header, BorderLayout.NORTH);
        cartao.add(imagemPanel, BorderLayout.CENTER);

        return cartao;
    }

    /*
     * --- Modo ADMIN ---
     */

    private void criarPainelListaAdmin() {
        // Usa BorderLayout no container principal para melhor organização
        setLayout(new BorderLayout());

        // Painel que vai conter as linhas dos carros (admin), com MigLayout configurado para 5 colunas
        painelLista = new JPanel(new MigLayout("insets 15, gap 10, wrap 1",
                "[30!][80!][grow][120!][80!]", ""));
        painelLista.setOpaque(false);

        String estiloBotao = "" +
                "background:#FF0000;" +
                "foreground:#FFFFFF;" +
                "font:bold;" +
                "arc:999;";

        // Painel superior com botão "Adicionar Carro"
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JButton btnAdd = new JButton("+ Adicionar Carro");
        btnAdd.setFocusable(false);
        btnAdd.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
        btnAdd.addActionListener(e -> mostrarFormularioAdicionar());
        topPanel.add(btnAdd);

        // JScrollPane para conter o painelLista e permitir scroll vertical
        JScrollPane scrollPane = new JScrollPane(painelLista,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:999;" +
                        "width:5;" +
                        "thumbInsets:0,0,0,0");

        // Adiciona os componentes no layout BorderLayout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Ajusta o tamanho do painelLista para acompanhar a largura do viewport do scrollPane,
        // evitando que o conteúdo seja cortado horizontalmente
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension preferred = painelLista.getPreferredSize();
                painelLista.setPreferredSize(new Dimension(scrollPane.getViewport().getWidth(), preferred.height));
                painelLista.revalidate();
            }
        });
    }


    private void carregarDadosCarrosAdmin() {
        carRows.clear();
        painelLista.removeAll();

        conexao comb = new conexao();
        String sql = """
                SELECT c.idCarro, c.nomeTime, c.chassi, c.fotoIcon, c.fotoCarro, c.motor
                FROM carros c
                ORDER BY c.idCarro
                """;

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                int idCarro = rs.getInt("idCarro");
                String nomeTime = rs.getString("nomeTime");
                String chassi = rs.getString("chassi");
                String motor = rs.getString("motor");
                BufferedImage fotoIcon = carregarImagem(rs.getBlob("fotoIcon"));
                BufferedImage fotoCarro = carregarImagem(rs.getBlob("fotoCarro"));

                CarRowPanel row = new CarRowPanel(idCarro, nomeTime, chassi, motor, fotoIcon, fotoCarro);
                painelLista.add(row, "growx, wrap");

                carRows.add(row);
            }
            comb.desconectar();

            painelLista.revalidate();
            painelLista.repaint();
        } catch (Exception e) {
            System.out.println("Erro ao carregar carros admin: " + e.getMessage());
        }
    }

    // Classe interna para representar uma linha da lista de carros (modo admin)
    private class CarRowPanel extends JPanel {
        private int idCarro;
        private JCheckBox checkSelecionar;
        private JTextField txtNomeTime, txtChassi, txtMotor;
        private JLabel lblIconCarro, lblFotoCarro;
        private JButton btnSalvar, btnExcluir;
        private BufferedImage fotoIcon, fotoCarro;

        public CarRowPanel(int idCarro, String nomeTime, String chassi, String motor,
                           BufferedImage fotoIcon, BufferedImage fotoCarro) {
            this.idCarro = idCarro;
            this.fotoIcon = fotoIcon;
            this.fotoCarro = fotoCarro;

            setOpaque(false);
            setLayout(new MigLayout(
                    "insets 5",
                    "[30!] [140!] [500:500:,grow] [120!] [80!]",
                    "[]"
            ));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            // Checkbox selecionar
            checkSelecionar = new JCheckBox();
            checkSelecionar.setOpaque(false);
            add(checkSelecionar, "cell 0 0, aligny center");

            // Foto ícone do carro
            lblIconCarro = new JLabel();
            if (fotoIcon != null) {
                lblIconCarro.setIcon(criarImagemArredondada(fotoIcon, 120, 40, 10));
            }
            add(lblIconCarro, "cell 1 0, aligny center");

            // Informações editáveis
            JPanel infoPanel = new JPanel(new MigLayout("insets 0, gap 10",
                    "[250!][150!][80!]", "[]"));
            infoPanel.setOpaque(false);

            txtNomeTime = new JTextField(nomeTime);
            txtMotor = new JTextField(motor);
            txtChassi = new JTextField(chassi);

            infoPanel.add(new JLabel("Time:"), "cell 0 0");
            infoPanel.add(txtNomeTime, "cell 0 1, growx");

            infoPanel.add(new JLabel("Motor:"), "cell 1 0");
            infoPanel.add(txtMotor, "cell 1 1, growx");

            infoPanel.add(new JLabel("Chassi:"), "cell 2 0");
            infoPanel.add(txtChassi, "cell 2 1, growx");

            add(infoPanel, "cell 2 0, aligny center");

            // Foto grande do carro (miniatura)
            lblFotoCarro = new JLabel();
            if (fotoCarro != null) {
                lblFotoCarro.setIcon(criarImagemArredondada(fotoCarro, 100, 60, 10));
            }
            add(lblFotoCarro, "cell 3 0, aligny center");

            // Botões
            JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            botoesPanel.setOpaque(false);

            String estiloBotao = "" +
                    "background:#FF0000;" +
                    "foreground:#FFFFFF;" +
                    "font:bold;" +
                    "arc:999;";

            btnSalvar = new JButton("");
            btnSalvar.setToolTipText("Salvar alterações");
            btnSalvar.setIcon(new FlatSVGIcon("local/menu/save.svg", 18, 18));
            btnSalvar.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
            btnSalvar.setFocusable(false);
            btnSalvar.addActionListener(e -> salvarAlteracoes());

            btnExcluir = new JButton("");
            btnExcluir.setToolTipText("Excluir carro");
            btnExcluir.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
            btnExcluir.setIcon(new FlatSVGIcon("local/menu/trash.svg", 18, 18));
            btnExcluir.setFocusable(false);
            btnExcluir.addActionListener(e -> excluirCarro());

            botoesPanel.add(btnSalvar);
            botoesPanel.add(btnExcluir);

            add(botoesPanel, "cell 4 0, aligny center");
        }

        private void salvarAlteracoes() {
            String novoNome = txtNomeTime.getText().trim();
            String novoChassi = txtChassi.getText().trim();
            String novoMotor = txtMotor.getText().trim();

            if (novoNome.isEmpty() || novoChassi.isEmpty() || novoMotor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
                return;
            }

            // Aqui você deve fazer UPDATE no banco de dados
            try {
                conexao comb = new conexao();
                comb.conectar();

                String sqlUpdate = "UPDATE carros SET nomeTime = ?, chassi = ?, motor = ? WHERE idCarro = ?";
                Connection conn = comb.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlUpdate);
                ps.setString(1, novoNome);
                ps.setString(2, novoChassi);
                ps.setString(3, novoMotor);
                ps.setInt(4, idCarro);

                int atualizado = ps.executeUpdate();
                comb.desconectar();

                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao salvar alterações.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + ex.getMessage());
            }
        }

        private void excluirCarro() {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este carro?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                conexao comb = new conexao();
                comb.conectar();

                String sqlDelete = "DELETE FROM carros WHERE idCarro = ?";
                Connection conn = comb.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlDelete);
                ps.setInt(1, idCarro);

                int excluido = ps.executeUpdate();
                comb.desconectar();

                if (excluido > 0) {
                    // Remove da lista visual e da lista interna
                    painelLista.remove(this);
                    carRows.remove(this);
                    painelLista.revalidate();
                    painelLista.repaint();
                    JOptionPane.showMessageDialog(this, "Carro excluído com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao excluir carro.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir carro: " + ex.getMessage());
            }
        }

        public boolean isSelecionado() {
            return checkSelecionar.isSelected();
        }
    }

    // Método para mostrar o formulário para adicionar novo carro
    private void mostrarFormularioAdicionar() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Adicionar Carro", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new MigLayout("insets 15, gap 10", "[][grow]", "[][][][][]"));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField txtNomeTime = new JTextField();
        JTextField txtChassi = new JTextField();
        JTextField txtMotor = new JTextField();

        JLabel lblFotoIcon = new JLabel("Clique para selecionar foto ícone");
        lblFotoIcon.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblFotoIcon.setPreferredSize(new Dimension(120, 80));
        lblFotoIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblFotoCarro = new JLabel("Clique para selecionar foto carro");
        lblFotoCarro.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblFotoCarro.setPreferredSize(new Dimension(160, 100));
        lblFotoCarro.setHorizontalAlignment(SwingConstants.CENTER);

        // Armazenar imagens carregadas
        final BufferedImage[] fotoIconSelecionada = new BufferedImage[1];
        final BufferedImage[] fotoCarroSelecionada = new BufferedImage[1];

        // Abrir seletor de arquivo ao clicar nas labels
        lblFotoIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(dialog);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                        fotoIconSelecionada[0] = img;
                        lblFotoIcon.setIcon(criarImagemArredondada(img, 120, 80, 15));
                        lblFotoIcon.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Erro ao carregar imagem: " + ex.getMessage());
                    }
                }
            }
        });

        lblFotoCarro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(dialog);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                        fotoCarroSelecionada[0] = img;
                        lblFotoCarro.setIcon(criarImagemArredondada(img, 160, 100, 15));
                        lblFotoCarro.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Erro ao carregar imagem: " + ex.getMessage());
                    }
                }
            }
        });

        formPanel.add(new JLabel("Nome Time:"), "cell 0 0");
        formPanel.add(txtNomeTime, "cell 1 0, growx");
        formPanel.add(new JLabel("Chassi:"), "cell 0 1");
        formPanel.add(txtChassi, "cell 1 1, growx");
        formPanel.add(new JLabel("Motor:"), "cell 0 2");
        formPanel.add(txtMotor, "cell 1 2, growx");
        formPanel.add(new JLabel("Foto Ícone:"), "cell 0 3");
        formPanel.add(lblFotoIcon, "cell 1 3");
        formPanel.add(new JLabel("Foto Carro:"), "cell 0 4");
        formPanel.add(lblFotoCarro, "cell 1 4");

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            String nomeTime = txtNomeTime.getText().trim();
            String chassi = txtChassi.getText().trim();
            String motor = txtMotor.getText().trim();

            if (nomeTime.isEmpty() || chassi.isEmpty() || motor.isEmpty() || fotoIconSelecionada[0] == null || fotoCarroSelecionada[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Por favor, preencha todos os campos e selecione as imagens.");
                return;
            }

            // Aqui insira o código para inserir o novo carro no banco, convertendo BufferedImage em Blob
            try {
                conexao comb = new conexao();
                comb.conectar();

                // Converter imagens em blobs
                Connection conn = comb.getConnection(); // método que você deve criar para expor o connection

                java.sql.Blob blobIcon = conn.createBlob();
                java.sql.Blob blobCarro = conn.createBlob();

                // Converte BufferedImage para byte[]
                byte[] bytesIcon = bufferedImageToByteArray(fotoIconSelecionada[0], "png");
                byte[] bytesCarro = bufferedImageToByteArray(fotoCarroSelecionada[0], "png");

                blobIcon.setBytes(1, bytesIcon);
                blobCarro.setBytes(1, bytesCarro);

                String sqlInsert = "INSERT INTO carros (nomeTime, chassi, motor, fotoIcon, fotoCarro) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, nomeTime);
                ps.setString(2, chassi);
                ps.setString(3, motor);
                ps.setBlob(4, blobIcon);
                ps.setBlob(5, blobCarro);

                int inserido = ps.executeUpdate();
                comb.desconectar();

                if (inserido > 0) {
                    JOptionPane.showMessageDialog(dialog, "Carro adicionado com sucesso!");
                    carregarDadosCarrosAdmin();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Falha ao adicionar carro.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao adicionar carro: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        botoesPanel.add(btnSalvar);
        botoesPanel.add(btnCancelar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(botoesPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Utilitário para converter BufferedImage em byte[] para salvar no Blob
    private byte[] bufferedImageToByteArray(BufferedImage image, String format) throws Exception {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        }
    }

    // Utilitário para carregar imagem de Blob do banco
    private BufferedImage carregarImagem(Blob blob) {
        if (blob == null) return null;
        try (InputStream is = blob.getBinaryStream()) {
            return ImageIO.read(is);
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
            return null;
        }
    }

    // Cria uma imagem arredondada a partir de BufferedImage
    private ImageIcon criarImagemArredondada(BufferedImage img, int largura, int altura, int radius) {
        if (img == null) return null;
        BufferedImage resized = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, largura, altura, null);

        // Criar máscara arredondada
        BufferedImage mask = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2m = mask.createGraphics();
        g2m.setComposite(AlphaComposite.Clear);
        g2m.fillRect(0, 0, largura, altura);
        g2m.setComposite(AlphaComposite.Src);
        g2m.setColor(Color.WHITE);
        g2m.fill(new RoundRectangle2D.Float(0, 0, largura, altura, radius, radius));
        g2m.dispose();

        // Aplicar máscara
        g2.setComposite(AlphaComposite.DstIn);
        g2.drawImage(mask, 0, 0, null);

        g2.dispose();

        return new ImageIcon(resized);
    }
}
