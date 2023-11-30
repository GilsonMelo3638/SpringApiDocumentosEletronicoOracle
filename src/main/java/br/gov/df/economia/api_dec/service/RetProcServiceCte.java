package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Dto.Dec.DadosCTe;
import br.gov.df.economia.api_dec.Entity.TbCTe;

public class RetProcServiceCte {

    public static String montarRetProc(TbCTe cte) {
        StringBuilder retProc = new StringBuilder();
        retProc.append("<retProc NSUAUT=\"" + cte.getNsuaut() + "\" NSUSVD=\"" + cte.getNsusvd() + "\">");
        retProc.append("<proc IpTransmissor=\"" + cte.getIpTransmissor() + "\">");
        String infDFe = cte.getInfDFe();
        infDFe = infDFe.replaceAll(">\\s+<", "><");
        retProc.append("<cteProc>" + infDFe + "</cteProc></proc></retProc>");
        return retProc.toString();
    }

    public static String montarRetProcDto(DadosCTe dadosCTe) {
        String infDFe = dadosCTe.infDFe().replaceAll(">\\s+<", "><");

        return String.format("    <retProc NSUAUT=\"" + dadosCTe.nsuaut() + "\"  NSUSVD=\"%s\">\n", dadosCTe.Nsusvd()) +
                String.format("      <proc IpTransmissor=\"%s\">\n        <cteProc>\n", dadosCTe.IpTransmissor()) +
                String.format("          %s\n        </cteProc>\n      </proc>\n    </retProc>\n", infDFe);
    }

}
