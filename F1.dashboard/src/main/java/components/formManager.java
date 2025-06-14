package components;

import information.Ergast;

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

        JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
        frame.add(component);
        frame.setSize(new Dimension(1000, 600));
        frame.setFrameIcon(null);
        try {
            frame.setMaximum(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        frame.setVisible(true);
        desktop.add(frame, 0);

//        try {
//            // Chama a API e pega os dados com base na URL
//            Ergast api = new Ergast();
//            String apiResponse = api.getData("2024/drivers");  // Passando a URL da requisição
//            System.out.println(apiResponse);
//
//            // Adiciona os dados da API ao JLabel
//            JPanel panel = new JPanel();
//
//            panel.setLayout(new BorderLayout());
//            JLabel label = new JLabel("<html><pre>" + apiResponse + "</pre></html>");
//            panel.add(label, BorderLayout.CENTER); // Exibindo os dados da API no JLabel
//
//            JInternalFrame frame = new JInternalFrame(title, false, true, false, false);
//            frame.setSize(desktop.getSize());
//            frame.setContentPane(panel);
//          //  frame.add(component);
//            frame.setVisible(true);
//            desktop.add(frame,0);
//
//
//
//        } catch (Exception e) {
//            System.err.println("Erro ao buscar dados da API: " + e.getMessage());
//        }

    }
}