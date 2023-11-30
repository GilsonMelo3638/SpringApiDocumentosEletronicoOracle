package br.gov.df.economia.api_dec.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatadorDataHora {
    public static String formatarDataHora(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(data);
    }
}