package login_register;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//teste
public class Validador {

    public static boolean validarIdadeMinima(String dataNascimento, int idadeMinima) {
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate nascimento = LocalDate.parse(dataNascimento, formato);
            LocalDate hoje = LocalDate.now();
            int idade = hoje.getYear() - nascimento.getYear();
            if (nascimento.plusYears(idade).isAfter(hoje)) {
                idade--;
            }
            return idade >= idadeMinima && idade < 120;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean camposPreenchidos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
