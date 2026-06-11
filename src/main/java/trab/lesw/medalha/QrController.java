package trab.lesw.medalha;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/qr")
public class QrController {

    @GetMapping("/baixar")
    public ResponseEntity<InputStreamResource> baixar(@RequestParam String url) throws Exception {
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data="
                + URLEncoder.encode(url, StandardCharsets.UTF_8);
        InputStream in = new URL(qrUrl).openStream();
        InputStreamResource resource = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
