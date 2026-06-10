package trab.lesw.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void enviarEmail(String destinatario, String assunto, String mensagem) {

		try {
			SimpleMailMessage email = new SimpleMailMessage();

			email.setTo(destinatario);
			email.setSubject(assunto);
			email.setText(mensagem);

			mailSender.send(email);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarEmailComAnexo(String destinatario, String assunto, String mensagem, byte[] arquivo,
			String nomeArquivo) {

		try {

			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(destinatario);
			helper.setSubject(assunto);
			helper.setText(mensagem);

			ByteArrayResource resource = new ByteArrayResource(arquivo);

			helper.addAttachment(nomeArquivo, resource);

			mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}