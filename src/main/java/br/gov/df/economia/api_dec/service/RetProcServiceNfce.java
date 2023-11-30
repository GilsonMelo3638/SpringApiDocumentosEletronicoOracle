package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFCe;
import br.gov.df.economia.api_dec.Entity.TbNFCe;

public class RetProcServiceNfce {

    public static String montarRetProc(TbNFCe nfce) {
        StringBuilder retProc = new StringBuilder();
        retProc.append("<retProc NSUAN=\"\" NSU=\"" + nfce.getNsu() + "\">");
        retProc.append("<proc IpTransmissor=\"" + nfce.getIpTransmissor() + "\">");
        String infDFe = nfce.getInfDFe();
        infDFe = infDFe.replaceAll(">\\s+<", "><");
        retProc.append("<nfceProc>").append(infDFe).append("</nfceProc></proc></retProc>");
        return retProc.toString();
    }

    public static String montarRetProcDto(DadosNFCe dadosNFCe) {
        String infDFe = dadosNFCe.infDFe().replaceAll(">\\s+<", "><");

        return String.format("    <retProc NSUAN=\"\" NSU=\"%s\">\n", dadosNFCe.nsu()) +
                String.format("      <proc IpTransmissor=\"%s\">\n        <nfceProc>\n", dadosNFCe.IpTransmissor()) +
                String.format("          %s\n        </nfceProc>\n      </proc>\n    </retProc>\n", infDFe);
    }

}
