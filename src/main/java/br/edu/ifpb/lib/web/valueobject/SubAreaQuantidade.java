package br.edu.ifpb.lib.web.valueobject;

import br.edu.ifpb.lib.domain.SubArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAreaQuantidade {
    public SubArea subArea;
    public int quantidade;
}
