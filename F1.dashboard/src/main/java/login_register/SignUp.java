package login_register;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import login_register.component.ButtonLink;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SignUp extends JPanel {

    public File imagemSelecionada;

    public SignUp() {
        Usuario Sign = new Usuario();
        setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

        JTextArea text = new JTextArea("Faça login para ter acesso a informações sobre a Fórmula 1,\n pilotos, equipes e calendário de corridas");
        text.setEditable(false);
        text.setFocusable(false);
        text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;background:null;");
        add(text);
        add(new JSeparator(), "gapy 15 15");

        JLabel lbEmail = new JLabel("E-mail");
        lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        add(lbEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "exemplo@mail.com");
        add(txtEmail);

        JLabel lbEmailconfirm = new JLabel("Confirme seu e-mail");
        lbEmailconfirm.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        add(lbEmailconfirm, "gapy 0 n");

        JTextField txtEmailconfirm = new JTextField();
        txtEmailconfirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "exemplo@mail.com");
        add(txtEmailconfirm);

        JLabel lbUser = new JLabel("Nome de usuário");
        lbUser.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        add(lbUser, "gapy 10 n");

        JTextField txtUser = new JTextField();
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Rammeta");
        add(txtUser);

        JLabel lbPassword = new JLabel("Crie uma senha");
        lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        add(lbPassword, "gapy 10 n");

        JPasswordField txtPassword = new JPasswordField();
        installRevealButton(txtPassword);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Escreva sua senha");
        add(txtPassword);

        JLabel lbDateOfBirth = new JLabel("Data de nascimento");
        lbDateOfBirth.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        add(lbDateOfBirth, "gapy 10 n");

        JTextField txtDateOfBirth = new JTextField();
        txtDateOfBirth.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        add(txtDateOfBirth);

        JLabel lbNote = new JLabel("Queremos te dar algo especial no seu aniversário!");
        lbNote.putClientProperty(FlatClientProperties.STYLE, "font:-1;foreground:$Label.disabledForeground;");
        add(lbNote);

        JFileChooser fileChooser = new JFileChooser();
        JButton cmdImg = new JButton("Foto de perfil");
        cmdImg.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
        add(cmdImg);

        cmdImg.addActionListener(e -> {
            fileChooser.setDialogTitle("Escolha uma imagem");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                imagemSelecionada = fileChooser.getSelectedFile();
                JOptionPane.showMessageDialog(null, "Imagem selecionada: " + imagemSelecionada.getName());
            }
        });

        JButton cmdSignUp = new JButton("Cadastre-se") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
        add(cmdSignUp);

        add(new JSeparator(), "gapy 15 15");
        add(new JLabel("Já possui uma conta ?"), "split 2, gapx push n");
        ButtonLink cmdBackLogin = new ButtonLink("Login");
        add(cmdBackLogin, "gapx n push");

        cmdBackLogin.addActionListener(actionEvent -> ModalDialog.popModel(Login.ID));

        cmdSignUp.addActionListener(e -> {
            String email = txtEmail.getText();
            String emailconfirm = txtEmailconfirm.getText();
            String senha = new String(txtPassword.getPassword());
            String dt = txtDateOfBirth.getText();
            String user = txtUser.getText();

            if (!Validador.camposPreenchidos(email, emailconfirm, senha, dt, user)) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                return;
            }

            if (!validarEmails(email, emailconfirm)) {
                JOptionPane.showMessageDialog(null, "Os emails não conferem!");
                return;
            }

            if (!Validador.validarIdadeMinima(dt, 12)) {
                JOptionPane.showMessageDialog(null, "Você deve ter pelo menos 12 anos e inserir uma data válida.");
                return;
            }

            InputStream imagemStream;
            try {
                imagemStream = new FileInputStream(imagemSelecionada);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Selecione uma imagem de perfil.");
                return;
            }

            DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatoSaida = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate data = LocalDate.parse(dt, formatoEntrada);
            String dt_nasc = data.format(formatoSaida);

            if (Sign.userSignup(email, senha, dt_nasc, user, imagemStream).equals("Sucesso")) {
                JOptionPane.showMessageDialog(null, "Cadastrado com sucesso! Faça login para acessar o sistema");
            } else {
                JOptionPane.showMessageDialog(null, "Erro no seu cadastro, verifique as informações");
            }
        });
    }

    public static boolean validarEmails(String email, String emailConfirm) {
        return email != null && email.equals(emailConfirm);
    }

    private void installRevealButton(JPasswordField txt) {
        FlatSVGIcon iconEye = new FlatSVGIcon("login_register/icon/eye.svg", 0.3f);
        FlatSVGIcon iconHide = new FlatSVGIcon("login_register/icon/hide.svg", 0.3f);

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");
        JButton button = new JButton(iconEye);

        button.addActionListener(new ActionListener() {
            private char defaultEchoChar = txt.getEchoChar();
            private boolean show;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                show = !show;
                if (show) {
                    button.setIcon(iconHide);
                    txt.setEchoChar((char) 0);
                } else {
                    button.setIcon(iconEye);
                    txt.setEchoChar(defaultEchoChar);
                }
            }
        });
        toolBar.add(button);
        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
    }
}
