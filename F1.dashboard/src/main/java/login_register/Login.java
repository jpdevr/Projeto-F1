package login_register;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import login_register.component.ButtonLink;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jpdevr.application.application;

public class Login extends JPanel{

    public static final String ID = "login_id";

    public Login() {
        Usuario LoginU = new Usuario();
        setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));
        JTextArea text = new JTextArea("Faça login para ter acesso a informações sobre a Fórmula 1,\n pilotos, equipes e calendário de corridas");
        text.setEditable(false);
        text.setFocusable(false);
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,0,0,0;" +
                "background:null;");
        add(text);

        add(new JSeparator(), "gapy 15 15");

        JLabel lbEmail = new JLabel("Email");
        lbEmail.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");
        add(lbEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite seu e-mail");
        add(txtEmail);

        JLabel lbPassword = new JLabel("Senha");
        lbPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");
        add(lbPassword, "gapy 10 n");

        JPasswordField txtPassword = new JPasswordField();
        installRevealButton(txtPassword);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite sua senha");

        add(txtPassword);
        ButtonLink cmdForgotPassword = new ButtonLink("Esqueceu sua senha ?");
        add(cmdForgotPassword, "gapx push n");

        JButton cmdLogin = new JButton("Login") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:#FFFFFF;");
        add(cmdLogin);

        add(new JSeparator(), "gapy 15 15");

        add(new JLabel("Não Possui uma conta ?"), "split 2,gapx push n");
        ButtonLink cmdSignUp = new ButtonLink("Cadastre-se");
        add(cmdSignUp, "gapx n push");

        // event
        cmdSignUp.addActionListener(actionEvent -> {
            String icon = "login_register/icon/signup.svg";
            ModalDialog.pushModal(new CustomModalBorder(new SignUp(), "Cadastre-se", icon), ID);
        });

        cmdForgotPassword.addActionListener(actionEvent -> {
            String icon = "login_register/icon/forgot_password.svg";
            ModalDialog.pushModal(new CustomModalBorder(new ForgotPassword(), "Esqueci minha senha", icon), ID);
        });

        cmdLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String senha = new String(txtPassword.getPassword());
                if(LoginU.userLogin(email, senha) == true){

                    JOptionPane.showMessageDialog(null, "Login efetuado com sucesso");

                    JFrame frameAtual = (JFrame) SwingUtilities.getWindowAncestor(cmdLogin);

                    if (frameAtual != null) {
                        application novaTela = new application(); // Exemplo
                        novaTela.setVisible(true);
                        frameAtual.dispose();
                    }

                }else{
                    JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos");
                }
            }
        });

    }

    private void installRevealButton(JPasswordField txt) {
        FlatSVGIcon iconEye = new FlatSVGIcon("login_register/icon/eye.svg", 0.3f);
        FlatSVGIcon iconHide = new FlatSVGIcon("login_register/icon/hide.svg", 0.3f);

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:0,0,0,5;");
        JButton button = new JButton(iconEye);

        button.addActionListener(new ActionListener() {

            private char defaultEchoChart = txt.getEchoChar();
            private boolean show;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                show = !show;
                if (show) {
                    button.setIcon(iconHide);
                    txt.setEchoChar((char) 0);
                } else {
                    button.setIcon(iconEye);
                    txt.setEchoChar(defaultEchoChart);
                }
            }
        });
        toolBar.add(button);
        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
    }

}
