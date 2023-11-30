package br.gov.df.economia.api_dec;

import br.gov.df.economia.api_dec.Entity.Arquivo;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

//@SpringBootApplication
public class GeradorQuery {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner sx = new Scanner(System.in);
        String arquivo;
        char resposta, entrada;

        Arquivo arquivoDigitado;
        arquivoDigitado = new Arquivo();

        System.out.print("\n"
                + ""
                + "1 - Digitar o documento(s) fiscal(is) e o período(s) inicial(is), especificando a quantidade desejada(Formato json).\n"
                + "Digite o número desejado\n");

        entrada = sc.next().charAt(0);
        if (entrada == '1') {
            do {
                System.out.println("Digite o arquivo: ");
                arquivo = sc.next();
                arquivoDigitado.dataHora = arquivo.replaceAll("\\D", "");
                arquivoDigitado.documento = arquivo.replaceAll("[^a-zA-Z]", "");
                arquivoDigitado.gerarQuereyApi();
                System.out.print("\nHá outro arquivo (s/n)? ");
                resposta = sc.next().charAt(0);
            } while (resposta != 'n');
        } else {

            }
                sc.close();
                sx.close();
        }

   }
