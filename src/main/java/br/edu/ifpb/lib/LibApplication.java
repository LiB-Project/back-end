package br.edu.ifpb.lib;

import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibApplication.class, args);
    }
}
