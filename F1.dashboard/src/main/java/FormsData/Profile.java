package FormsData;

import com.formdev.flatlaf.FlatClientProperties;
import login_register.Usuario;
import login_register.TestLoginRegisterForm;
import net.miginfocom.swing.MigLayout;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import conexao.conexao;

import static conexao.conexao.statement;

public class Profile extends BlurChild {

    private JTextField txtUsuario, txtEmail, txtNivel, txtNascimento;
    private JPasswordField txtSenha;
    private JLabel labelFoto;
    private BufferedImage imagemPerfil;

    public Profile() {
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

        JPanel painel = new JPanel(new MigLayout(
                "wrap 2, insets 20, fillx",
                "[fill]20[fill]",
                ""
        ));
        painel.setOpaque(false);

        txtUsuario = new JTextField();
        txtUsuario.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        txtSenha = new JPasswordField();
        txtSenha.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        txtNascimento = new JTextField();
        txtNascimento.putClientProperty(FlatClientProperties.STYLE, "arc:999");

        labelFoto = new JLabel();
        painel.add(labelFoto, "span 4, center, gapbottom 20");

        painel.add(new JLabel("Email:"));
        painel.add(new JLabel("Usuário:"));

        painel.add(txtEmail);
        painel.add(txtUsuario);

        painel.add(new JLabel("Senha:"));
        painel.add(new JLabel("Data de Nascimento:"));

        painel.add(txtSenha);
        painel.add(txtNascimento);

        JButton btnSalvar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Perfil");

        // Estilo dos botões
        String estiloBotao = "" +
                "background:#FF0000;" +
                "foreground:#FFFFFF;" +
                "font:bold;" +
                "arc:999;";
        btnSalvar.putClientProperty(FlatClientProperties.STYLE, estiloBotao);
        btnExcluir.putClientProperty(FlatClientProperties.STYLE, estiloBotao);

        painel.add(btnSalvar, "gapy 20, growx");
        painel.add(btnExcluir, "growx");

        add(painel, BorderLayout.CENTER);

        carregarDadosUsuario();

        btnSalvar.addActionListener(e -> salvarAlteracoes());
        btnExcluir.addActionListener(e -> excluirPerfil());
    }



    private void carregarDadosUsuario() {
        int userId = Usuario.SessaoUsuario.userLogged;
        String sql = "SELECT usuario, email, senha, dt_nasc, icon FROM user WHERE id = ?";

        try {
            conexao comb = new conexao();
            comb.conectar();
            Connection conn = comb.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtUsuario.setText(rs.getString("usuario"));
                txtEmail.setText(rs.getString("email"));
                txtSenha.setText(rs.getString("senha"));
                txtNascimento.setText(rs.getString("dt_nasc"));

                Blob blobIcon = rs.getBlob("icon");
                if (blobIcon != null) {
                    try (InputStream in = blobIcon.getBinaryStream()) {
                        imagemPerfil = ImageIO.read(in);
                        labelFoto.setIcon(criarImagemRedonda(imagemPerfil, 140));
                    }
                }
            }
            comb.desconectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }


    private void salvarAlteracoes() {
        int userId = Usuario.SessaoUsuario.userLogged;
        String sql = "UPDATE user SET usuario=?, email=?, senha=?, dt_nasc=? WHERE id=?";

        try {
            conexao comb = new conexao();
            comb.conectar();
            Connection conn = comb.getConnection(); // você precisa adicionar esse método `getConnection()` na sua classe de conexão
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtUsuario.getText());
            ps.setString(2, txtEmail.getText());
            ps.setString(3, new String(txtSenha.getPassword()));
            ps.setString(4, txtNascimento.getText());
            ps.setInt(5, userId);
            ps.executeUpdate();
            comb.desconectar();
            JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + e.getMessage());
        }
    }

    private void excluirPerfil() {
        int userId = Usuario.SessaoUsuario.userLogged;
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o perfil?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conexao comb = new conexao();
                comb.conectar();
                Connection conn = comb.getConnection(); // você precisa
                PreparedStatement ps = conn.prepareStatement("DELETE FROM user WHERE id = ?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                comb.desconectar();
                JOptionPane.showMessageDialog(this, "Perfil excluído com sucesso.");

                // Redireciona para tela de login
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.dispose();
                new TestLoginRegisterForm().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir perfil: " + e.getMessage());
            }
        }
    }

    private ImageIcon criarImagemRedonda(BufferedImage imagemOriginal, int tamanho) {
        BufferedImage imagemFinal = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagemFinal.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double circulo = new Ellipse2D.Double(0, 0, tamanho, tamanho);
        g2.setClip(circulo);
        g2.drawImage(imagemOriginal.getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();
        return new ImageIcon(imagemFinal);
    }
}
