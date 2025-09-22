package heg.controller;

import heg.entity.Person;
import heg.exception.UnknownPersonException;
import heg.manager.PersonManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class PersonController {

    private static final Logger LOG = Logger.getLogger(PersonController.class);

    private final PersonManager personManager;

    public PersonController(PersonManager personManager) {
        this.personManager = personManager;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/persons", produces = "application/json")
    public List<Person> getPersons() {
        return personManager.findAllPerson();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/person")
    public void addPerson(@RequestBody Person person) {
        personManager.insert(person);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/person")
    public Person updatePeson(@RequestBody Person person) throws UnknownPersonException {
        return personManager.update(person);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/person/{personMail}")
    public void removePerson(@PathVariable String personMail) {
        personManager.remove(personMail);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/person/{personMail}")
    public Person retrivePerson(@RequestHeader(value="User-Agent") String userAgent, @PathVariable String personMail) throws UnknownPersonException {
        LOG.info("userAgent : " + userAgent);
        return personManager.findPersonByMail(personMail);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/persons/search")
    public List<Person> getPerson(@RequestParam String email, @RequestParam String name) throws UnknownPersonException {
        if (email != null && !email.isEmpty()) {
            return List.of(personManager.findPersonByMail(email));
        } else if (name != null && !name.isEmpty()) {
            return personManager.findByName(name);
        } else {
            return Collections.emptyList();
        }
    }
}
