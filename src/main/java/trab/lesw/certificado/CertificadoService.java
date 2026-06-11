package trab.lesw.certificado;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfStamper;

import trab.lesw.evento.Evento;
import trab.lesw.usuario.Usuario;

@Service
public class CertificadoService {
    private static final String SECRET = "ValorTeste";
    
    public byte[] gerarCertificado(Usuario usuario, Evento evento) {
        try {
            InputStream template = getClass().getClassLoader().getResourceAsStream("templates/certificado.pdf");
            if (template == null) {
                return gerarCertificadoSimples(usuario, evento);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(template);
            PdfStamper stamper = new PdfStamper(reader, out);

            AcroFields form = stamper.getAcroFields();

//Carregar fonte
            BaseFont calibriRegular = null;
            try (InputStream regularStream = getClass().getClassLoader().getResourceAsStream("calibri-font-family/calibri-regular.ttf")) {
                if (regularStream != null) {
                    byte[] regularBytes = regularStream.readAllBytes();
                    calibriRegular = BaseFont.createFont("calibri-regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, BaseFont.CACHED, regularBytes, null);
                }
            } catch (Exception e) {
//Esta dentro de um try catch para caso nao encontre a fonte nao quebre o programa
            }
            if (calibriRegular != null) {
                form.setFieldProperty("usuarioNome", "textfont", calibriRegular, null);
                form.setFieldProperty("usuarioNome", "textsize", 16.5f, null);
                form.setFieldProperty("eventoTitulo", "textfont", calibriRegular, null);
                form.setFieldProperty("eventoTitulo", "textsize", 16.5f, null);
                form.setFieldProperty("eventoData", "textfont", calibriRegular, null);
                form.setFieldProperty("eventoData", "textsize", 16.5f, null);
                form.setFieldProperty("eventoCargaHoraria", "textfont", calibriRegular, null);
                form.setFieldProperty("eventoCargaHoraria", "textsize", 17.5f, null);
                form.setFieldProperty("dataAtual", "textfont", calibriRegular, null);
                form.setFieldProperty("dataAtual", "textsize", 17.5f, null);
                form.setFieldProperty("tipo", "textfont", calibriRegular, null);
                form.setFieldProperty("tipo", "textsize", 16.5f, null);
                form.setFieldProperty("hashValidation","textfont", calibriRegular, null);
                form.setFieldProperty("hashValidation","textsize", 11f, null);
            }

            
            
            String nome = usuario.getNome() != null ? usuario.getNome() : "Aluno";
            String tituloEvento = evento.getTitulo() != null ? evento.getTitulo() : "Evento";
            String dataEvento = evento.getData() != null
                    ? evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";

            String duracao = "";
            if (evento.getHoraInicio() != null && evento.getHoraFim() != null) {
                long minutos = Duration.between(evento.getHoraInicio(), evento.getHoraFim()).toMinutes();
                long horas = minutos / 60;
                long minRestantes = minutos % 60;
                if (horas > 0 && minRestantes > 0) {
                    duracao = horas + " hora(s) " + minRestantes + "minutos.";
                } else if (horas > 0) {
                    duracao = horas + " hora(s).";
                } else {
                    duracao = minRestantes + "minutos.";
                }
            }

            String dataEmissao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            String hash = generateValidationHash(usuario, evento, dataEmissao); 
            
            String tipoUsuario = usuario.getTipo() != null ? usuario.getTipo() : "";
            
            form.setField("usuarioNome", nome);
            form.setField("eventoTitulo", tituloEvento);
            form.setField("eventoData", dataEvento);
            form.setField("eventoCargaHoraria", duracao);
            form.setField("dataAtual", dataEmissao);
            form.setField("hashvalidation", hash);
            form.setField("tipo", tipoUsuario);
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (Exception e) {
            return gerarCertificadoSimples(usuario, evento);
        }
    }

    private String generateValidationHash(Usuario usuario, Evento evento, String dataEmissao) {
    	String input = usuario.getId() + "-" + evento.getId() + "-" + dataEmissao + "-" + SECRET;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
	}
    
    private byte[] gerarCertificadoSimples(Usuario usuario, Evento evento) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            com.lowagie.text.Document doc = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(doc, out);
            doc.open();

            String nome = usuario.getNome() != null ? usuario.getNome() : "Aluno";
            String tituloEvento = evento.getTitulo() != null ? evento.getTitulo() : "Evento";

            com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                "CERTIFICADO DE PARTICIPAÇÃO\n\n" +
                nome + "\n\n" +
                "participou do evento " + tituloEvento + "\n\n" +
                "Emitido em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FontFactory.getFont(FontFactory.HELVETICA, 14));
            p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            doc.add(p);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
