package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(indexName = "frequency_dist")
public class FrequencyDistribution implements Serializable {
    @Id
    private String id;
    private String document;
    private List<Frequency> mostCommons = new ArrayList<>();
}
