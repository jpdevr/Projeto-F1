package components;

import javax.swing.*;
import java.awt.*;

public class formManager {

    private static formManager instance;
    private JDesktopPane desktop;

    public static formManager getInstance() {
        if (instance == null) {
            instance = new formManager();
        }
        return instance;
    }

    private formManager() {

    }
    public void setDesktop(JDesktopPane desktop) {
        this.desktop = desktop;
    }

    public void showForm(String title, Component component){

        JInternalFrame frame = new JInternalFrame(title, false, true, false, false);
        frame.setSize(desktop.getSize());
        frame.add(component);
        frame.setVisible(true);
        desktop.add(frame,0);

    }
}