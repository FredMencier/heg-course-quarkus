package heg.timer;

import heg.manager.PersonManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PersonTimer {

    private static final Logger LOG = Logger.getLogger(PersonTimer.class);

    final PersonManager personManager;

    public PersonTimer(PersonManager personManager) {
        this.personManager = personManager;
    }

    @Scheduled(fixedRate = 10000)
    public void printNbPerson() {
        int nbPerson = personManager.findAllPerson().size();
        LOG.info("Actuellement il y a %s personnes dans la base de données".formatted(nbPerson));
    }
}
