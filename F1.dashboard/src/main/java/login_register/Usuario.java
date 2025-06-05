package login_register;

import conexao.conexao;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import javax.swing.*;

import static conexao.conexao.statement;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class Usuario{

    String dt_nasc;
    String nome;
    String Senha;
    String Email;
    String Nivel;
    int ID;
    InputStream foto;

    public class SessaoUsuario{
        public static int userLogged;
        public static String nomeUsuario;
        public static String nivel;
        public static String getNomeUsuario() {
            return nomeUsuario;
        }
    }

    public String userPassword(String email, String user){

        Email = email;
        nome = user;
        conexao comb = new conexao();

        String sql = "SELECT * FROM user where email='" + Email + "' and usuario='" + nome + "';";

        System.out.println(sql);
        try{
            comb.conectar();

            ResultSet rs = statement.executeQuery(sql);

            System.out.println(rs);

            if(rs.next()){
                String emailc = rs.getString("email");
                String userc = rs.getString("usuario");
                if(Email.equals(emailc) & nome.equals(userc)){
                    Senha=rs.getString("senha");
                    final String fromEmail = "joaogapires@gmail.com"; // seu e-mail
                    final String password = "famqxzylphydjiao"; // use senha de app do Gmail

                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com"); // servidor SMTP do Gmail
                    props.put("mail.smtp.port", "587"); // porta TLS
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");

                    // Criação da sessão
                    Session session = Session.getInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail, password);
                        }
                    });

                    try {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(fromEmail));
                        message.setRecipients(
                                Message.RecipientType.TO, InternetAddress.parse(email));
                        message.setText("Sua senha é: " + Senha);

                        Transport.send(message);
                        System.out.println("E-mail enviado com sucesso");
                        return "sucesso";
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        return "erro";
                    }
                }
            }
            comb.desconectar();

        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("erro");
            return "erro";
        }
        return "erro";
    }

    public boolean userLogin(String EmailLogin, String SenhaLogin){

        Email = EmailLogin;
        Senha = SenhaLogin;

        conexao comb = new conexao();

        String sql = "SELECT * FROM user where email='" + Email + "' and senha='" + Senha + "';";

        System.out.println(sql);
        try{
            comb.conectar();

            ResultSet rs = statement.executeQuery(sql);

            System.out.println(rs);

            if(rs.next()){
               String emailc = rs.getString("email");
               String senhac = rs.getString("senha");
                if(Email.equals(emailc) & Senha.equals(senhac)){
                    ID = rs.getInt("id");
                    nome = rs.getString("usuario");
                    Nivel = rs.getString("nivel");
                    SessaoUsuario.userLogged = ID;
                    SessaoUsuario.nomeUsuario = nome;
                    SessaoUsuario.nivel = Nivel;

                    return true;
                }
            }else{
                return false;
            }
            comb.desconectar();
            return false;

        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("erro");
            return false;
        }
    }

    public String userSignup(String EmailS, String SenhaS, String Datas, String UserS, InputStream imagem) {


        Email = EmailS;
        Senha = SenhaS;
        nome = UserS;
        dt_nasc = Datas;
        Nivel = "usuario";
        foto = imagem;

        if(userLogin(Email, Senha)==true){
            JOptionPane.showMessageDialog(null, "email de usuário já cadastrado!");
        }else{
            conexao comb = new conexao();

            String sql = "INSERT INTO user (usuario, email, senha, id, nivel, dt_nasc, icon) VALUES (?, ?, ?, NULL, ?, ?, ?)";

            try {
                comb.conectar();

                Connection conn = comb.getConnection(); // você precisa adicionar esse método `getConnection()` na sua classe de conexão
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nome);
                pst.setString(2, Email);
                pst.setString(3, Senha);
                pst.setString(4, Nivel);
                pst.setString(5, dt_nasc);

                if (foto != null) {
                    pst.setBlob(6, foto); // insere o InputStream corretamente
                } else {
                    pst.setNull(6, java.sql.Types.BLOB);
                }

                int linhasAfetadas = pst.executeUpdate();

                comb.desconectar();

                return "Sucesso";

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Erro";
            }
        }return "Erro;";

    }


    public boolean forgotPassword(){

        return true;
    }

}


