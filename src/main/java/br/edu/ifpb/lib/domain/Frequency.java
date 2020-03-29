package br.edu.ifpb.lib.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Frequency implements Serializable {
    private String word;
    private int quantity;
}
