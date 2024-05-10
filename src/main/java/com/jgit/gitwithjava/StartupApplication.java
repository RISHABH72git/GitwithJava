package com.jgit.gitwithjava;

import com.jgit.gitwithjava.local.model.Details;
import com.jgit.gitwithjava.local.model.SiteDetail;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class StartupApplication implements ApplicationListener<ContextRefreshedEvent> {

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        File application = new File(DefaultCredentials.getApplicationFolder());
        if (!application.exists()) {
            application.mkdir();
            File applicationFile = new File(DefaultCredentials.getApplicationFile());
            JAXBContext jaxbContext = JAXBContext.newInstance(Details.class);
            Details details = new Details();
            SiteDetail siteDetail = new SiteDetail();
            siteDetail.setUsername("default");
            siteDetail.setSite("default");
            siteDetail.setNotes("default");
            SecretKey secretKey = siteDetail.generateKey();
            String encoded = siteDetail.encryptPassword("default", secretKey);
            siteDetail.setPassword(encoded);
            String key = siteDetail.setEncodeKey(secretKey.getEncoded());
            siteDetail.setKey(key);
            siteDetail.setId(UUID.randomUUID().toString());
            details.setSiteDetailList(List.of(siteDetail));

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(details, new FileOutputStream(applicationFile));
            log.info("creating application file");
        } else {
            log.info("application file already exists");
        }
    }
}
