package login_register;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import components.MainForm;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.option.BorderOption;
import raven.modal.option.Option;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import components.MainForm;
import javax.swing.*;
import java.awt.*;

public class TestLoginRegisterForm extends JFrame {

    public TestLoginRegisterForm() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("al center center"));

        // style modal border
        ModalDialog.getDefaultOption()
                .setOpacity(0f)
                .getBorderOption()
                .setShadow(BorderOption.Shadow.MEDIUM);

        JButton button = new JButton("Show");

        button.addActionListener(actionEvent -> {
            showLogin();
        });
        add(button);

        showLogin();
    }

    private void showLogin() {
        Option option = ModalDialog.createOption()
                .setCloseOnPressedEscape(false)
                .setBackgroundClickType(Option.BackgroundClickType.BLOCK)
                .setAnimationEnabled(false)
                .setOpacity(0.2f);
        String icon = "login_register/icon/account.svg";
        ModalDialog.showModal(this, new CustomModalBorder(new Login(), "Login", icon), option, Login.ID);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("login_register.themes");
        FlatMacDarkLaf.setup();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        EventQueue.invokeLater(() -> new TestLoginRegisterForm().setVisible(true));
    }

}
