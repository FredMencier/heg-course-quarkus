package heg.manager;

import heg.Spring3PersonApplication;
import heg.config.MailServerConfig;
import jakarta.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StartUpManager {

    private static final Logger LOG = Logger.getLogger(Spring3PersonApplication.class);

    @Autowired
    Environment environment;

    @Autowired
    MailServerConfig mailServerConfig;

    @PostConstruct
    public void init() {
        LOG.info("Start Person Springboot application");
        LOG.info(List.of("Use Profile : " + Arrays.stream(environment.getDefaultProfiles()).toList()));
        LOG.info("Mail server hostname : " + mailServerConfig.getHostName());
        LOG.info("Mail server port : " + mailServerConfig.getPort());
        LOG.info("Mail server from : " + mailServerConfig.getFrom());
    }

}
