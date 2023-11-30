package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Valicacoes.Enun.TipoDoc;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Arquivo {
    public String dataHora;
    public String documento;
    @Getter
    @Setter
    private Date moment;
    @Getter
    @Setter
    private TipoDoc tipo;
    @Getter
    @Setter
    private String Chave;
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String perIni;
    @Getter
    @Setter
    private String perFinal;
    @Getter
    @Setter
    private String documentoDigitado;

    public Arquivo(){
    }
    public Arquivo(String perIni, String perFinal, String documentoDigitado) {
        this.perIni = perIni;
        this.perFinal = perFinal;
        this.documentoDigitado = documentoDigitado;
    }
    public Arquivo(int id, String dataHora, String documento, String perIni, String perFinal, String documentoDigitado) {
        this.id = id;
        this.dataHora = dataHora;
        this.documento = documento;
        this.perIni = perIni;
        this.perFinal = perFinal;
        this.documentoDigitado = documentoDigitado;
   }
    public Arquivo(String documento, String Chave) {
        this.documento = documento;
        this.Chave = Chave;
    }
    public Arquivo(String Chave, Date moment, TipoDoc tipo) {
        this.Chave = Chave;
        this.moment = moment;
        this.tipo = tipo;
    }

    public String horaQuereyInicial(){
        int tamanho = dataHora.length();
        String horaInicial = (tamanho == 10) ? dataHora.substring(8, 10) : "00";
        return  horaInicial;
    }
    public String horaQuereyFinal(){
        int tamanho = dataHora.length();
        String horaFinal = (tamanho == 10) ? dataHora.substring(8, 10) : "23";
        return  horaFinal;
    }
    public void  gerarQuereyApi(){
        String parametroApiRestIni = dataHora.substring(0, 4)
                + "-"
                + dataHora.substring(4, 6)
                + "-"
                + dataHora.substring(6, 8)
                + "T"
                + horaQuereyInicial();
        String parametroApiRestFim = dataHora.substring(0, 4)
                + "-"
                + dataHora.substring(4, 6)
                + "-"
                + dataHora.substring(6, 8)
                + "T"
                + horaQuereyFinal();
        var tipoDoc = "\"tipo_doc\": \""
                + documento
                + "\",";
        var parInicio = "\"par_inicio\": \""
                + parametroApiRestIni
                + ":00:00-03:00" + "\",";
        var parFim = "\"par_fim\": \""
                + parametroApiRestFim
                + ":59:59-03:00"
                + "\",";
        var indSituacao = "\"ind_situacao\": \"AGENDADO\"";
        var query = "{"
        + tipoDoc
        + "\n"
        + parInicio
        + "\n"
        + parFim
        + "\n"
        + indSituacao
        + "\n"
        + "}";
        System.out.println(query);
        String[] lines = new String[] {query};
        String path = "C:\\Java\\api.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean hasId(List<Arquivo> list, int id) {
        Arquivo emp = list.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        return emp != null;
    }
    public String arquivo() {
        return documento + dataHora;
    };
}
