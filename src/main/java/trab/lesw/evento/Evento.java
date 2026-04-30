package trab.lesw.evento;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import trab.lesw.competencia.Competencia;

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

    private TipoEvento tipoEvento;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "evento_competencia",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "competencia_id")
    )
    private List<Competencia> competencias;
}