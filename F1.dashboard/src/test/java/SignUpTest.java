import login_register.SignUp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import login_register.Usuario;

public class SignUpTest {

    @Test
    public void testValidarEmailsIguais() {
        SignUp signUp = new SignUp();
        assertTrue(signUp.validarEmails("teste@email.com", "teste@email.com"));
    }

    @Test
    public void testValidarEmailsDiferentes() {
        SignUp signUp = new SignUp();
        assertFalse(signUp.validarEmails("teste@email.com", "outro@email.com"));
    }

    @Test
    public void testLoginUsuarioRammeta() {
        Usuario usuario = new Usuario();

        boolean loginSucesso = usuario.userLogin("joaogapires@gmail.com", "carswels05");

        assertTrue(loginSucesso, "O login deve ser bem-sucedido para o usuário Rammeta");
    }

    @Test
    public void testLoginUsuarioRammetaincorreto() {
        Usuario usuario = new Usuario();

        boolean loginErrado = usuario.userLogin("joaogapires@gmail.com", "carswels05546");

        assertFalse(loginErrado, "O login para o usuário Rammeta está incorreto");
    }

}
