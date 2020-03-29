package br.edu.ifpb.lib.domain;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.util.List;

@Getter
@Setter
@Document(indexName = "usuario")
public class Usuario {
    @Id
    private String id;
    private String login;
    private String nome;
    private String senha;
    private List<Role> roles;
}
