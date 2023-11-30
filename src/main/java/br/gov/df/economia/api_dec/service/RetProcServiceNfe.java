package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFe;
import br.gov.df.economia.api_dec.Entity.TbNFe;

public class RetProcServiceNfe {

    public static String montarRetProc(TbNFe nfe) {
        StringBuilder retProc = new StringBuilder();
        retProc.append("<retProc NSUAN=\"" + nfe.getNsudf() + "\" NSU=\"" + nfe.getNsuan() + "\">");
        retProc.append("<proc IpTransmissor=\"" + nfe.getIpTransmissor() + "\">");
        String infDFe = nfe.getInfDFe();
        infDFe = infDFe.replaceAll(">\\s+<", "><");
        retProc.append("<nfeProc>" + infDFe + "</nfeProc></proc></retProc>");
        return retProc.toString();
    }

    public static String montarRetProcDto(DadosNFe dadosNFe) {
        String infDFe = dadosNFe.infDFe().replaceAll(">\\s+<", "><");

        return String.format("    <retProc NSUAN=\"\" NSUDF=\"%s\">\n", dadosNFe.Nsudf()) +
                String.format("      <proc IpTransmissor=\"%s\">\n        <nfeProc>\n", dadosNFe.IpTransmissor()) +
                String.format("          %s\n        </nfeProc>\n      </proc>\n    </retProc>\n", infDFe);
    }

}
