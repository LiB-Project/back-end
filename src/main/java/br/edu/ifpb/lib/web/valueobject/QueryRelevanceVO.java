package br.edu.ifpb.lib.web.valueobject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QueryRelevanceVO {
    private String field;
    private Double value;
}
