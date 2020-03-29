package br.edu.ifpb.lib.config;

import br.edu.ifpb.lib.DocumentServiceGrpc;
import br.edu.ifpb.lib.LdaGrpc;
import br.edu.ifpb.lib.RecomendacaoGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "analyzer.server")
@Getter
@Setter
public class PythonServerConfig {

    private String host;
    private int port;

    @Bean
    public ManagedChannel provideChanelAnalyzerService(){
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public DocumentServiceGrpc.DocumentServiceStub provideAnalyzerService(ManagedChannel channel){
        return DocumentServiceGrpc.newStub(channel);
    }

    @Bean
    public LdaGrpc.LdaStub provideLdaService(ManagedChannel channel){
        return LdaGrpc.newStub(channel);
    }

    @Bean
    public RecomendacaoGrpc.RecomendacaoBlockingStub provideRecomendacaoService(ManagedChannel channel){
        return RecomendacaoGrpc.newBlockingStub(channel);
    }
}
