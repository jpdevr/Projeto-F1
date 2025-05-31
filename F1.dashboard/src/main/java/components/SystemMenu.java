package components;

import FormsData.Drivers;
import com.formdev.flatlaf.FlatClientProperties;
import conexao.conexao;
import information.Ergast;
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
import login_register.Usuario;
import conexao.conexao;
import java.sql.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;

import static conexao.conexao.statement;

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
        String nome = Usuario.SessaoUsuario.nomeUsuario;
        int ID = Usuario.SessaoUsuario.userLogged;
        InputStream foto;
        conexao comb = new conexao();

        String sql = "SELECT * FROM user where id='" + ID + "';";

        System.out.println(sql);
        try{
            comb.conectar();

            ResultSet rs = statement.executeQuery(sql);

            System.out.println(rs);

            if(rs.next()){
                    foto = rs.getBinaryStream("icon");

                    String caminhoProjeto = System.getProperty("user.dir");
                    File arquivoImagem = new File("/src/main/resources/local/imgs/foto"+ID+".jpg");

                    try (OutputStream outputStream = new FileOutputStream(caminhoProjeto+arquivoImagem)) {
                        byte[] buffer = new byte[128000000];
                        int bytesRead;

                        while ((bytesRead = foto.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        System.out.println("Imagem salva em: " + arquivoImagem.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
            comb.desconectar();

        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("erro");
        }


        return new SimpleHeaderData()
                .setTitle(nome)
                .setDescription("Java Student")
                .setIcon(new AvatarIcon(getClass().getResource("/local/imgs/foto"+ID+".jpg"),60,60,999));

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
                    public void selected(MenuAction menuAction, int[] into) {
                        System.out.println("menu select");
                        if(into.length == 1){
                            int index = into[0];
                            if(index == 0){
                                formManager.getInstance().showForm("Dashboard", new JLabel("dashboard"));
                            }        else if (index == 2) {
                                formManager.getInstance().showForm("Notícias", new JLabel("Notícias", SwingConstants.CENTER));
                            }
                            else if (index == 3) {
                                formManager.getInstance().showForm("Calendário", new JLabel("Calendário", SwingConstants.CENTER));
                            }

                        } else if (into.length == 2) {
                            int index = into[0];
                            int subIndex = into[1];
                            if (index == 1) {
                                if (subIndex == 0) {
                                    formManager.getInstance().showForm("Equipes", new JLabel("Equipes", SwingConstants.CENTER));
                                } else if (subIndex == 1) {
                                    formManager.getInstance().showForm("Pilotos", new Drivers());
                                } else if (subIndex == 2) {
                                    formManager.getInstance().showForm("Carros", new JLabel("Carros", SwingConstants.CENTER));
                                }
                            }

                            else if (index == 4) {
                                if (subIndex == 0) {
                                    formManager.getInstance().showForm("Resultados", new JLabel("Resultados", SwingConstants.CENTER));
                                } else if (subIndex == 1) {
                                    formManager.getInstance().showForm("Circuitos", new JLabel("Circuitos", SwingConstants.CENTER));
                                } else if (subIndex == 2) {
                                    formManager.getInstance().showForm("Dados", new JLabel("Dados", SwingConstants.CENTER));
                                }
                            }

                            else if (index == 5) {
                                if (subIndex == 0) {
                                    formManager.getInstance().showForm("Vencedores", new JLabel("Resultados", SwingConstants.CENTER));
                                } else if (subIndex == 1) {
                                    formManager.getInstance().showForm("Construtores", new JLabel("Circuitos", SwingConstants.CENTER));
                                } else if (subIndex == 2) {
                                    formManager.getInstance().showForm("Recordes", new JLabel("Dados", SwingConstants.CENTER));
                                } else if (subIndex == 3) {
                                    formManager.getInstance().showForm("Histórias", new JLabel("Dados", SwingConstants.CENTER));
                                }

                            }

                            else if (index == 6) {
                                if (subIndex == 0) {
                                    formManager.getInstance().showForm("Alterar Foto de Perfil", new JLabel("Resultados", SwingConstants.CENTER));
                                } else if (subIndex == 1) {
                                    formManager.getInstance().showForm("Alterar Apelido", new JLabel("Circuitos", SwingConstants.CENTER));
                                } else if (subIndex == 2) {
                                    formManager.getInstance().showForm("Configurações de conta & Acesso", new JLabel("Dados", SwingConstants.CENTER));
                                }
                            }
                        }

                    }
                })
                ;
    }

    private SimpleMenu simpleMenu;
}