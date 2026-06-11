package trab.lesw.medalha;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class RelatorioService {

    private static final String RELATORIOS_DIR = "relatorios";

    @Autowired
    private MedalhaRepository medalhaRepository;

    public String gerarRelatorioDasMedalhas(List<Long> medalhaIds) throws Exception {
        if (medalhaIds == null || medalhaIds.isEmpty()) return null;

        Map<String, List<String>> porUsuario = new LinkedHashMap<>();
        Map<String, Long> resumo = new LinkedHashMap<>();

        for (Long id : medalhaIds) {
            medalhaRepository.findById(id).ifPresent(m -> {
                String cpf = m.getUsuario().getCpf();
                String nome = m.getUsuario().getNome();
                String chave = nome + "|" + cpf;
                String medalhaNome = m.getNome();
                porUsuario.computeIfAbsent(chave, k -> new ArrayList<>()).add(medalhaNome);
                resumo.merge(medalhaNome, 1L, Long::sum);
            });
        }

        if (porUsuario.isEmpty()) return null;

        String agora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String dataStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String filename = "relatorio-entrega-" + agora + ".pdf";

        File dir = new File(RELATORIOS_DIR);
        if (!dir.exists()) dir.mkdirs();

        Document document = new Document();
        FileOutputStream fos = new FileOutputStream(new File(dir, filename));
        PdfWriter.getInstance(document, fos);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

        document.add(new Paragraph("Relatório de Entrega de Medalhas", titleFont));
        document.add(new Paragraph("Gerado em: " + dataStr, normal));
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Resumo", sectionFont));
        document.add(Chunk.NEWLINE);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.addCell(new PdfPCell(new Phrase("Medalha", bold)));
        summaryTable.addCell(new PdfPCell(new Phrase("Total", bold)));
        for (Map.Entry<String, Long> entry : resumo.entrySet()) {
            summaryTable.addCell(new Phrase(entry.getKey(), normal));
            summaryTable.addCell(new Phrase(String.valueOf(entry.getValue()), normal));
        }
        document.add(summaryTable);
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Por Usuário", sectionFont));
        document.add(Chunk.NEWLINE);

        for (Map.Entry<String, List<String>> entry : porUsuario.entrySet()) {
            String chave = entry.getKey();
            String nome = chave.split("\\|")[0];
            String cpf = chave.split("\\|")[1];
            document.add(new Paragraph("Usu\u00e1rio: " + nome + " (CPF: " + cpf + ")", bold));
            for (String medalhaNome : entry.getValue()) {
                document.add(new Paragraph("  \u2022 " + medalhaNome, normal));
            }
            document.add(Chunk.NEWLINE);
        }

        document.close();
        fos.close();

        return filename;
    }
}
