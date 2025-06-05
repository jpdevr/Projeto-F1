package FormsData;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import conexao.conexao;
import login_register.Usuario;
import net.miginfocom.swing.MigLayout;
import raven.drawer.component.header.SimpleHeader;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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

public class Drivers extends BlurChild {

    private JPanel painelConteudo;  // painel que será lista ou grid
    private JButton btnAdicionar;
    private String nivel = Usuario.SessaoUsuario.nivel; // Pega o nível do usuário
    private List<PilotoItem> pilotoItems = new ArrayList<>();

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
        setLayout(new BorderLayout());

        if ("administrador".equalsIgnoreCase(nivel)) {
            // Para admin: painel vertical com botão add no topo e lista de pilotos
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topo.setOpaque(false);
            btnAdicionar = new JButton("+ Adicionar Piloto");
            btnAdicionar.addActionListener(e -> abrirFormularioAdicionar());
            topo.add(btnAdicionar);
            add(topo, BorderLayout.NORTH);

            painelConteudo = new JPanel();
            painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
            painelConteudo.setOpaque(false);

            String estiloBotao = "" +
                    "background:#FF0000;" +
                    "foreground:#FFFFFF;" +
                    "font:bold;" +
                    "arc:999;";

            btnAdicionar.putClientProperty(FlatClientProperties.STYLE, estiloBotao);

            JScrollPane scrollPane = new JScrollPane(painelConteudo);
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

            carregarPilotosAdmin();

        } else {
            // Usuário padrão: mantém sua exibição de 2 colunas
            painelConteudo = new JPanel(new MigLayout("wrap 2, gap 40 40, align center", "[450!] [450!]", "[]"));
            painelConteudo.setOpaque(false);

            JScrollPane scrollPane = new JScrollPane(painelConteudo);
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

            carregarPilotosUsuario();
        }
    }

    // ********** MÉTODOS PARA USUÁRIO PADRÃO **********
    private void carregarPilotosUsuario() {
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
                painelConteudo.add(cartao);
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

        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelCentral.setOpaque(false);

        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.setPreferredSize(new Dimension(120, 120));
        fotoPanel.setOpaque(false);

        if (imagem != null) {
            ImageIcon fotoArredondada = criarImagemArredondada(imagem, 120, 120, 30);
            JLabel labelFoto = new JLabel(fotoArredondada);
            labelFoto.setHorizontalAlignment(SwingConstants.CENTER);
            fotoPanel.add(labelFoto, BorderLayout.CENTER);
        }

        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        infos.setOpaque(false);

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
            infos.add(Box.createVerticalStrut(5));
        }

        painelCentral.add(fotoPanel);
        painelCentral.add(infos);

        cartao.add(painelCentral, BorderLayout.CENTER);

        return cartao;
    }

    // ********** MÉTODOS PARA ADMIN **********
    private void carregarPilotosAdmin() {
        painelConteudo.removeAll();
        pilotoItems.clear();

        conexao comb = new conexao();
        String sql = "SELECT nome, idPiloto, titulos, equipe, pontos, foto, gpWin, pais FROM drivers order by equipe";

        try {
            comb.conectar();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                int idPiloto = rs.getInt("idPiloto");
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

                PilotoItem item = new PilotoItem(idPiloto, nome, pais, pontos, titulos, equipe, gpsVencidos, imagem);
                pilotoItems.add(item);
                painelConteudo.add(item);
            }

            comb.desconectar();
            painelConteudo.revalidate();
            painelConteudo.repaint();

        } catch (Exception e) {
            System.out.println("Erro ao carregar dados admin: " + e.getMessage());
        }
    }

    // Abre formulário para adicionar novo piloto
    private void abrirFormularioAdicionar() {
        PilotoItem novoPiloto = new PilotoItem(-1, "", "", 0, 0, "", 0, null);
        novoPiloto.setModoAdicionar(true);
        pilotoItems.add(0, novoPiloto);
        painelConteudo.add(novoPiloto, 0);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    // Classe interna que representa a linha do piloto para admin
    private class PilotoItem extends JPanel {
        private int idPiloto;
        private JCheckBox selecionar;
        private JTextField txtNome;
        private JTextField txtPais;
        private JTextField txtPontos;
        private JTextField txtTitulos;
        private JTextField txtEquipe;
        private JTextField txtGpsVencidos;
        private JLabel fotoLabel;
        private BufferedImage imagem;
        private JButton btnSalvar;
        private JButton btnExcluir;
        private JButton btnSelecionarFoto;
        private boolean modoAdicionar = false;

        public PilotoItem(int idPiloto, String nome, String pais, int pontos, int titulos, String equipe, int gpsVencidos, BufferedImage imagem) {
            this.idPiloto = idPiloto;
            this.imagem = imagem;

            String estiloBotao = "" +
                    "background:#FF0000;" +
                    "foreground:#FFFFFF;" +
                    "font:bold;" +
                    "arc:999;";

            setOpaque(false);
            setLayout(new MigLayout("gap 20 10, ins 10",
                    // colunas: [margem][rótulo][campo nome][gap][rótulo][campo equipe][gap][rótulo][campo país][margem]
                    "[30!]10[80!]10[30!]10[150:200:250, grow]10[80!]10[60!]10[60!]10[150:200:250, grow]10[60!]10[30!]10[30!]",
                    "[100!]")
            );

            selecionar = new JCheckBox();
            add(selecionar, "cell 0 0, aligny center");

            // Foto pequena
            fotoLabel = new JLabel();
            fotoLabel.setPreferredSize(new Dimension(80, 80));
            atualizarFotoLabel();
            add(fotoLabel, "cell 1 0, aligny center");

            // Botão trocar foto (aparece só no modo adicionar ou editar)
            btnSelecionarFoto = new JButton("");
            btnSelecionarFoto.setIcon(new FlatSVGIcon("local/menu/photo.svg", 18, 18));
            btnSelecionarFoto.setToolTipText("Selecionar foto");
            btnSelecionarFoto.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
            btnSelecionarFoto.addActionListener(e -> selecionarNovaFoto());
            add(btnSelecionarFoto, "cell 2 0, aligny center");

            // Campos editáveis
            txtNome = new JTextField(nome);
            add(txtNome, "cell 3 0, growx");

            txtPais = new JTextField(pais);
            add(txtPais, "cell 4 0, growx");

            txtPontos = new JTextField(String.valueOf(pontos));
            add(txtPontos, "cell 5 0, growx");

            txtTitulos = new JTextField(String.valueOf(titulos));
            add(txtTitulos, "cell 6 0, growx");

            txtEquipe = new JTextField(equipe);
            add(txtEquipe, "cell 7 0, growx");

            txtGpsVencidos = new JTextField(String.valueOf(gpsVencidos));
            add(txtGpsVencidos, "cell 8 0, growx");

            // Botões salvar e excluir
            btnSalvar = new JButton("");
            btnSalvar.setToolTipText("Salvar alterações");
            btnSalvar.setIcon(new FlatSVGIcon("local/menu/save.svg", 18, 18));
            btnSalvar.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
            btnSalvar.addActionListener(e -> salvarPiloto());
            add(btnSalvar, "cell 9 0, aligny center");

            btnExcluir = new JButton("");
            btnExcluir.setToolTipText("Excluir piloto");
            btnExcluir.addActionListener(e -> excluirPiloto());
            btnExcluir.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
            btnExcluir.setIcon(new FlatSVGIcon("local/menu/trash.svg", 18, 18));
            add(btnExcluir, "cell 10 0, aligny center");

            atualizarEstadoBotoes();
            addListenersCampos();
        }

        private void atualizarFotoLabel() {
            if (imagem != null) {
                ImageIcon icon = new ImageIcon(imagem.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                fotoLabel.setIcon(icon);
            } else {
                fotoLabel.setIcon(null);
                fotoLabel.setText("Sem Foto");
            }
        }

        private void selecionarNovaFoto() {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                try {
                    imagem = ImageIO.read(chooser.getSelectedFile());
                    atualizarFotoLabel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage());
                }
            }
        }

        private void salvarPiloto() {
            // Validação simples
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome não pode estar vazio");
                return;
            }
            // AQUI você deve implementar a lógica para salvar no banco, incluindo a foto (que pode precisar de conversão para Blob)
            // Exemplo básico de update ou insert via sua conexão

            conexao comb = new conexao();
            try {
                comb.conectar();

                String sql;
                if (modoAdicionar) {
                    sql = "INSERT INTO drivers (nome, pais, pontos, titulos, equipe, gpWin, foto) VALUES (?, ?, ?, ?, ?, ?, ?)";
                } else {
                    sql = "UPDATE drivers SET nome=?, pais=?, pontos=?, titulos=?, equipe=?, gpWin=?, foto=? WHERE idPiloto=?";
                }

                Connection conn = comb.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtNome.getText());
                ps.setString(2, txtPais.getText());
                ps.setInt(3, Integer.parseInt(txtPontos.getText()));
                ps.setInt(4, Integer.parseInt(txtTitulos.getText()));
                ps.setString(5, txtEquipe.getText());
                ps.setInt(6, Integer.parseInt(txtGpsVencidos.getText()));

                // Converte BufferedImage para InputStream para salvar blob (simplificado)
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                if (imagem != null) {
                    ImageIO.write(imagem, "png", baos);
                    byte[] bytes = baos.toByteArray();
                    ps.setBytes(7, bytes);
                } else {
                    ps.setNull(7, java.sql.Types.BLOB);
                }

                if (!modoAdicionar) {
                    ps.setInt(8, idPiloto);
                }

                ps.executeUpdate();

                if (modoAdicionar) {
                    // Depois de inserir, pode querer recarregar a lista
                    JOptionPane.showMessageDialog(this, "Piloto adicionado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Piloto atualizado com sucesso!");
                }

                comb.desconectar();
                modoAdicionar = false;
                carregarPilotosAdmin();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            }
        }

        private void excluirPiloto() {
            if (modoAdicionar) {
                // Remove da lista e painel sem tocar banco
                pilotoItems.remove(this);
                painelConteudo.remove(this);
                painelConteudo.revalidate();
                painelConteudo.repaint();
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que quer excluir o piloto " + txtNome.getText() + "?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirmar != JOptionPane.YES_OPTION) return;

            conexao comb = new conexao();
            try {
                comb.conectar();
                String sql = "DELETE FROM drivers WHERE idPiloto=?";

                Connection conn = comb.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idPiloto);
                ps.executeUpdate();
                comb.desconectar();

                pilotoItems.remove(this);
                painelConteudo.remove(this);
                painelConteudo.revalidate();
                painelConteudo.repaint();

                JOptionPane.showMessageDialog(this, "Piloto excluído com sucesso!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir piloto: " + e.getMessage());
            }
        }

        public void setModoAdicionar(boolean modoAdicionar) {
            this.modoAdicionar = modoAdicionar;
            atualizarEstadoBotoes();
        }

        private void atualizarEstadoBotoes() {
            btnSalvar.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnSelecionarFoto.setEnabled(true);
        }

        private void addListenersCampos() {
            DocumentListener dl = new DocumentListener() {
                public void changedUpdate(DocumentEvent e) { atualizarEstadoBotoes(); }
                public void removeUpdate(DocumentEvent e) { atualizarEstadoBotoes(); }
                public void insertUpdate(DocumentEvent e) { atualizarEstadoBotoes(); }
            };
            txtNome.getDocument().addDocumentListener(dl);
            txtPais.getDocument().addDocumentListener(dl);
            txtPontos.getDocument().addDocumentListener(dl);
            txtTitulos.getDocument().addDocumentListener(dl);
            txtEquipe.getDocument().addDocumentListener(dl);
            txtGpsVencidos.getDocument().addDocumentListener(dl);
        }

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

