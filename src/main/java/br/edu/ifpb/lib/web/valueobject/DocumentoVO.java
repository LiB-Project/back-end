package br.edu.ifpb.lib.web.valueobject;

import br.edu.ifpb.lib.util.LocalDateDeserializer;
import br.edu.ifpb.lib.util.LocalDateSerializer;
import br.edu.ifpb.lib.util.LocalDateTimeDeserializer;
import br.edu.ifpb.lib.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import br.edu.ifpb.lib.domain.Autor;
import br.edu.ifpb.lib.domain.Orientador;
import br.edu.ifpb.lib.domain.SubArea;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentoVO implements Serializable {

    private String id;
    private String titulo;
    private Autor autor;
    private String resumo;
    private CursoVO curso;
    private Orientador orientador;
    private Orientador coorientador;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dataApresentacao;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataPublicacao;
    private List<SubArea> subAreas;
    private List<String> palavrasChave;
    private String isbn;
    private byte[] arquivo;
    pprivate String arquivoBase64;
    private Double score;
}
