package trab.lesw.evento;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    private String descricao;
    
    private int cargaHoraria;
    
    private String tipo;
    
    private LocalDateTime data;
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
}