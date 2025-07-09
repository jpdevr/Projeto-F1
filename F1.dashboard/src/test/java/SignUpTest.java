import login_register.SignUp;
import login_register.Usuario;
import login_register.Validador;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SignUpTest {

    @Test
    public void testValidarEmailsIguais() {
        assertTrue(SignUp.validarEmails("teste@email.com", "teste@email.com"));
    }

    @Test
    public void testValidarEmailsDiferentes() {
        assertFalse(SignUp.validarEmails("teste@email.com", "outro@email.com"));
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

    @Test
    public void testValidarIdadeValida() {
        assertTrue(Validador.validarIdadeMinima("01/01/2010", 12));
    }

    @Test
    public void testValidarIdadeInvalida() {
        assertFalse(Validador.validarIdadeMinima("01/01/2017", 12));
    }

    @Test
    public void testValidarCamposPreenchidos() {
        assertTrue(Validador.camposPreenchidos("abc", "123", "senha"));
    }

    @Test
    public void testValidarCamposVazios() {
        assertFalse(Validador.camposPreenchidos("abc", "", "senha"));
    }
}
