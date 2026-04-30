package trab.lesw.disciplina;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import trab.lesw.matricula.Matricula;

@Entity
@Table(name = "disciplina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Disciplina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private Integer semestre;

    @OneToMany(mappedBy = "disciplina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matricula> matriculas;
}