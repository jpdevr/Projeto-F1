package components;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.drawer.component.header.SimpleHeader;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.menu.*;
import raven.drawer.component.menu.data.Item;
import raven.swing.AvatarIcon;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.GradientColor;
import raven.swing.blur.style.Style;
import raven.swing.blur.style.StyleBorder;
import raven.swing.blur.style.StyleOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class SystemMenu extends BlurChild {

    public SystemMenu(){
        super(new Style()
                .setBlur(30)
                .setBorder(new StyleBorder(10)
                        .setOpacity(0.15f)
                        .setBorderWidth(1.2f)
                        .setBorderColor(new GradientColor(new Color(200, 200, 200), new Color(150, 150, 150), new Point2D.Float(0,0), new Point2D.Float(1f,0)))
                )
                .setOverlay(new StyleOverlay(new Color(0,0,0),0.2f))
        );
        init();
    }

    private void init(){

        setLayout(new MigLayout("wrap, fill", "[fill]", "[grow 0][fill]"));
        simpleMenu = new SimpleMenu(getMenuOption());
        simpleMenu.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(simpleMenu);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,"" +
                "trackArc:999;"+
                "width:5;"+
                "thumbInsets:0,0,0,0");

        SimpleHeader header = new SimpleHeader(getHeaderData());
        header.setOpaque(false);
        add(header);
        add(scrollPane);
    }

    private SimpleHeaderData getHeaderData(){
        return new SimpleHeaderData()
                .setTitle("JPdevr")
                .setDescription("Java Student")
                .setIcon(new AvatarIcon(getClass().getResource("/local/imgs/avatar.png"),60,60,999));
    }

    private SimpleMenuOption getMenuOption(){
        raven.drawer.component.menu.data.MenuItem items[] = new raven.drawer.component.menu.data.MenuItem[]{
                new Item.Label("PRINCIPAL"),
                new Item("Dashboard", "dashboard.svg"),
                new Item.Label("BASES"),
                new Item("Info", "f1.svg")
                        .subMenu("Equipes")
                        .subMenu("Pilotos")
                        .subMenu("Carros"),
                new Item("Notícias", "notices.svg"),
                new Item("Calendário", "calendar.svg"),
                new Item.Label("CAMPEONATO"),
                new Item("Corridas", "circuit.svg")
                        .subMenu("Resultados")
                        .subMenu("Circuitos")
                        .subMenu("Dados"),
                new Item("Temporadas", "seasons.svg")
                        .subMenu("Vencedores")
                        .subMenu("Construtores")
                        .subMenu("Recordes")
                        .subMenu("Histórias"),
                new Item.Label("CONFIG"),
                new Item("Perfil", "config.svg")
                        .subMenu("Alterar foto")
                        .subMenu("Alterar apelido")
                        .subMenu("Config. acesso")
        };
        return new SimpleMenuOption()
                .setBaseIconPath("local/menu")
                .setIconScale(0.5f)
                .setMenus(items)
                .setMenuStyle(new SimpleMenuStyle() {
                    @Override
                    public void styleMenuPanel(JPanel panel, int[] index) {
                        panel.setOpaque(false);
                    }

                    @Override
                    public void styleMenuItem(JButton menu, int[] index) {
                        menu.setContentAreaFilled(false);
                    }

                })
                .addMenuEvent(new MenuEvent() {
                    @Override
                    public void selected(MenuAction menuAction, int[] ints) {
                        System.out.println("menu select");
                        if(ints.length == 1){
                            int index = ints[0];
                            if(index == 0){
                                formManager.getInstance().showForm("Dashboard Title",new JLabel("Dashboard",  SwingConstants.CENTER) );
                            }
                        } else if (ints.length == 2) {
                            int index = ints[0];
                            int subIndex = ints[1];
                            if (index == 1) {
                                if (subIndex == 0) {
                                    formManager.getInstance().showForm("Blur Inbox Title", new JLabel("Inbox", SwingConstants.CENTER));
                                } else if (subIndex == 1) {
                                    formManager.getInstance().showForm("Simple Read Title", new JLabel("Read", SwingConstants.CENTER));
                                }
                            }
                        }
                    }
                })
                ;
    }

    private SimpleMenu simpleMenu;
}