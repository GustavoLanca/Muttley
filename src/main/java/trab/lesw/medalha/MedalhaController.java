package trab.lesw.medalha;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medalha")
public class MedalhaController {

    @Autowired
    private MedalhaService medalhaService;

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/ranking")
    public String ranking(Model model, @RequestParam(defaultValue = "false") boolean apenasPendentes) {
        List<Map<String, Object>> ranking = medalhaService.getRanking();
        if (apenasPendentes) {
            ranking = ranking.stream()
                .filter(e -> {
                    @SuppressWarnings("unchecked")
                    List<Medalha> medals = (List<Medalha>) e.get("medalhas");
                    return medals.stream().anyMatch(m -> !Boolean.TRUE.equals(m.getEntregue()));
                })
                .collect(Collectors.toList());
        }
        model.addAttribute("ranking", ranking);
        model.addAttribute("apenasPendentes", apenasPendentes);
        return "medalha/ranking";
    }

    @PostMapping("/entregar")
    public String entregar(@RequestParam(required = false) List<Long> medalhaIds,
                           @RequestParam(defaultValue = "false") boolean apenasPendentes,
                           RedirectAttributes attr) {
        medalhaService.entregarSelecionadas(medalhaIds);
        try {
            String filename = relatorioService.gerarRelatorioDasMedalhas(medalhaIds);
            if (filename != null) {
                attr.addFlashAttribute("message", "Medalhas entregues! Relatório gerado: " + filename);
            } else {
                attr.addFlashAttribute("message", "Nenhuma medalha foi selecionada.");
            }
        } catch (Exception e) {
            attr.addFlashAttribute("message", "Medalhas entregues, mas erro ao gerar relatório.");
        }
        return "redirect:/medalha/ranking?apenasPendentes=" + apenasPendentes;
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model, @RequestParam(required = false) String data) {
        File dir = new File("relatorios");
        List<Map<String, Object>> arquivos = new ArrayList<>();
        if (dir.exists()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("relatorio-entrega-") && name.endsWith(".pdf"));
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                for (File f : files) {
                    if (data != null && !data.isEmpty()) {
                        String fileDate = f.getName().replaceAll("relatorio-entrega-(\\d{4})(\\d{2})(\\d{2})_.*", "$1-$2-$3");
                        if (!data.equals(fileDate)) continue;
                    }
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("nome", f.getName());
                    entry.put("tamanho", f.length());
                    entry.put("data", new java.util.Date(f.lastModified()));
                    arquivos.add(entry);
                }
            }
        }
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("dataSelecionada", data);
        return "medalha/relatorios";
    }

    @GetMapping("/relatorios/{filename:.+}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String filename) {
        try {
            File file = new File("relatorios", filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/reverter/{id}")
    public String reverter(@PathVariable Long id,
                           @RequestParam(defaultValue = "false") boolean apenasPendentes,
                           RedirectAttributes attr) {
        medalhaService.reverterEntrega(id);
        attr.addFlashAttribute("message", "Entrega da medalha revertida!");
        return "redirect:/medalha/ranking?apenasPendentes=" + apenasPendentes;
    }
}
