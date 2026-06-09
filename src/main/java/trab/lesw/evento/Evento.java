package trab.lesw.evento;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trab.lesw.disciplina.Disciplina;
import trab.lesw.tag.Tag;
import trab.lesw.usuario.Usuario;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    private String tipo;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private Integer pontos = 1;

    private String imagemUrl;

    @Column(columnDefinition = "TEXT")
    private String mensagemPublicacao;

    private Boolean publicado = false;

    private LocalDateTime inicioInscricao;
    private LocalDateTime fimInscricao;
    private LocalDateTime inicioConfirmacao;
    private LocalDateTime fimConfirmacao;

    public boolean isInscricaoAberta() {
        LocalDateTime now = LocalDateTime.now();
        if (inicioInscricao != null && now.isBefore(inicioInscricao)) return false;
        if (fimInscricao != null && now.isAfter(fimInscricao)) return false;
        return true;
    }

    public boolean isConfirmacaoAberta() {
        LocalDateTime now = LocalDateTime.now();
        if (inicioConfirmacao != null && now.isBefore(inicioConfirmacao)) return false;
        if (fimConfirmacao != null && now.isAfter(fimConfirmacao)) return false;
        return true;
    }

    @ManyToMany
    @JoinTable(
        name = "evento_tag",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToMany
    @JoinTable(
        name = "evento_organizador",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> organizadores = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "evento_palestrante",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> palestrantes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "evento_professor",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> professores = new ArrayList<>();
}