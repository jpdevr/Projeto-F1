package jpdevr.application;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import components.MainForm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class application extends JFrame {

    private MainForm mainForm = new MainForm();

    public application() {
        init();
    }

    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().add(mainForm);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);

        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/local/imgs/logo.png"));
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new application().setVisible(true));
    }

}
