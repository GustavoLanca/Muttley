package trab.lesw.usuario;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trab.lesw.medalha.Medalha;
import trab.lesw.participacao.Participacao;


@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    
    private String email;

    private String senha;
    
    private String linkedin;

    @Column(length = 2048)
    private String linkedinToken;

    private Long linkedinTokenExpires;

    private String linkedinPersonId;

    private String cpf;
    
    private String tipo;

    @OneToMany(mappedBy = "usuario")
    private List<Medalha> medalhas;

    @OneToMany(mappedBy = "usuario")
    private List<Participacao> participacoes;
}