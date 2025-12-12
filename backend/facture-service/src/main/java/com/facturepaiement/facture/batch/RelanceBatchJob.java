package com.facturepaiement.facture.batch;

import com.facturepaiement.facture.model.Facture;
import com.facturepaiement.facture.model.StatutFacture;
import com.facturepaiement.facture.repository.FactureRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RelanceBatchJob {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public ItemReader<Facture> factureReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        
        return new RepositoryItemReaderBuilder<Facture>()
                .name("factureReader")
                .repository(factureRepository)
                .methodName("findByStatutAndDateEcheanceBefore")
                .arguments(StatutFacture.EN_ATTENTE, LocalDate.now())
                .sorts(sorts)
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<Facture, Facture> factureProcessor() {
        return facture -> {
            // Mettre à jour le statut en EN_RETARD
            facture.setStatut(StatutFacture.EN_RETARD);
            
            // Envoyer un message Kafka pour notification
            Map<String, Object> message = new HashMap<>();
            message.put("factureId", facture.getId());
            message.put("clientId", facture.getClientId());
            message.put("montant", facture.getMontant());
            message.put("dateEcheance", facture.getDateEcheance().toString());
            message.put("type", "RELANCE");
            
            kafkaTemplate.send("relance-topic", message);
            
            return facture;
        };
    }

    @Bean
    public ItemWriter<Facture> factureWriter() {
        return factureRepository::saveAll;
    }

    @Bean
    public Step relanceStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("relanceStep", jobRepository)
                .<Facture, Facture>chunk(10, transactionManager)
                .reader(factureReader())
                .processor(factureProcessor())
                .writer(factureWriter())
                .build();
    }

    @Bean
    public Job relanceJob(JobRepository jobRepository, Step relanceStep) {
        return new JobBuilder("relanceJob", jobRepository)
                .start(relanceStep)
                .build();
    }
}

