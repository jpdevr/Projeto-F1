package login_register;

import com.formdev.flatlaf.FlatClientProperties;
import login_register.component.ButtonLink;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import login_register.Usuario;

public class ForgotPassword extends JPanel {

    public ForgotPassword() {
        Usuario forgotPassword = new Usuario();
        setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

        JTextArea text = new JTextArea("Por favor insira o e-mail que você usou para criar\nsua conta, vamos te explicar os próximos passos para\nrecriar sua senha.");
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
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite seu email");
        add(txtEmail);

        JLabel lbUser = new JLabel("Usuário");
        lbUser.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;");
        add(lbUser);

        JTextField txtUser = new JTextField();
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite seu usuário");
        add(txtUser);

        JButton cmdSubmit = new JButton("Enviar") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };

        cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:#FFFFFF;");

        add(cmdSubmit, "gapy 15 15");

        ButtonLink cmdBackLogin = new ButtonLink("Voltar para o login");
        add(cmdBackLogin, "grow 0,al center");

        // event
        cmdBackLogin.addActionListener(actionEvent -> {
            ModalDialog.popModel(Login.ID);
        });

        cmdSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String user = txtUser.getText();

                if(forgotPassword.userPassword(email, user).equals("sucesso")){
                JOptionPane.showMessageDialog(null, "Verifique seu email para ver sua senha!");
                }else{
                    JOptionPane.showMessageDialog(null,"Email não enviado, tente novamente ou\nverifique se os dados foram digitados corretamente!");
                }
            }
        });
    }
}
