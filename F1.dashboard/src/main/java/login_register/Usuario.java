package login_register;

import conexao.conexao;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;

import static conexao.conexao.statement;

import java.io.*;
import java.sql.*;

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
        public static String getNomeUsuario() {
            return nomeUsuario;
        }
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
                    SessaoUsuario.userLogged = ID;
                    SessaoUsuario.nomeUsuario = nome;

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


