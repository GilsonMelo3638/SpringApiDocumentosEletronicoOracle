package br.gov.df.economia.api_dec.infra.TratadorDeErros;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class TratadorDeErros {
    /*Erro 404 - Id inexistente.*/
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        System.out.println("Tratamento de Erro 404(500 do Spring): Id inexistente");
        return ResponseEntity.notFound().build();
    }
    /*Erro 400 - Dados Inválidos.
     * MethodArgumentNotValidException: método para pegar a exception do bean validation.*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        System.out.println("Tratamento de Erro 400: Dados Inválidos(Faltando Campos Obrigatórios)" + erros);
        System.out.println("Tratamento de Erro 400: Dados Inválidos(Faltando Campos Obrigatórios)" +
                ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList()));
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity tratarErroRegraDeNegocio(ValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
