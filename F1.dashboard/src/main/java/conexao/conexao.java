package conexao;

import java.sql.*;

public class conexao {

    public static Statement statement = null;

    String URL = "jdbc:mysql://localhost:3306/F1.dashboard";
    String usuario = "root";

    String senha = "";

    private Statement stm = null;

    private Connection conexao = null;

    public void conectar() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        conexao = DriverManager.getConnection(URL, usuario, senha);
        statement = (Statement) conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }
    public Connection getConnection() {
        return conexao;
    }


    public void desconectar() throws SQLException{

        conexao.close();
    }
    public static int runSQL(String sql)
    {
        int registros = 0;

        try
        {
            registros = statement.executeUpdate(sql);
        }
        catch(SQLException sqlex)
        {
            System.out.println("Erro acesso ao BD" + sqlex);
        }

        return registros;
    }
}
