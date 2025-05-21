package components;

import net.miginfocom.swing.MigLayout;
import raven.swing.blur.BlurBackground;
import raven.swing.blur.style.StyleOverlay;

import javax.swing.*;
import java.awt.*;

public class MainForm extends BlurBackground {



    public MainForm(){
        ImageIcon icon = new ImageIcon(getClass().getResource("/local/imgs/background.jpg"));
        setImage(icon.getImage());

        setOverlay(new StyleOverlay(new Color(20,20,20),0.1f));
        init();
    }

    private void init(){

        setLayout(new MigLayout("fill, insets 0 0 6 6","[fill]","[fill]"));

        systemMenu = new SystemMenu();
        title = new Title();
        desktop.setLayout(null);

        desktop.setOpaque(false);
        formManager.getInstance().setDesktop(desktop);

        add(systemMenu, "dock west, gap 6 6 6 6, width 280!");
        add(title,"dock north, gap 0 6 6 6, height 50!");
        add(desktop);
    }

    private SystemMenu systemMenu = new SystemMenu();
    private Title title = new Title();
    private JDesktopPane desktop = new JDesktopPane();


}