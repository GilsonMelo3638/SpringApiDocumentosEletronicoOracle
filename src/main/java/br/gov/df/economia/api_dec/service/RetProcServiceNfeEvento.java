package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFeEvento;
import br.gov.df.economia.api_dec.Entity.TbNFeEvento;

public class RetProcServiceNfeEvento {

    public static String montarRetProc(TbNFeEvento nFeEvento) {
        StringBuilder retProc = new StringBuilder();
        retProc.append("<retProc NSUAN=\"" + nFeEvento.getNsudf() + "\" NSU=\"" + nFeEvento.getNsuan() + "\">");
        retProc.append("<proc IpTransmissor=\"" + nFeEvento.getIpTransmissor() + "\">");
        String infDFe = nFeEvento.getInfDFe();
        infDFe = infDFe.replaceAll(">\\s+<", "><");
        retProc.append("<nfeProc>" + infDFe + "</nfeProc></proc></retProc>");
        return retProc.toString();
    }

    public static String montarRetProcDto(DadosNFeEvento dadosNFe) {
        String infDFe = dadosNFe.infDFe().replaceAll(">\\s+<", "><");

        return String.format("    <retProc NSUAN=\"\" NSUDF=\"%s\">\n", dadosNFe.Nsudf()) +
                String.format("      <proc IpTransmissor=\"%s\">\n        <nfeProc>\n", dadosNFe.IpTransmissor()) +
                String.format("          %s\n        </nfeProc>\n      </proc>\n    </retProc>\n", infDFe);
    }

}
