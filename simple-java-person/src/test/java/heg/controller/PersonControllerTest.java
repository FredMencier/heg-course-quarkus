package heg.controller;

import heg.entity.Genre;
import heg.entity.Person;
import heg.exception.PersonAlreadyExistException;
import heg.exception.UnknownPersonException;
import heg.manager.PersonManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PersonManager pm;

    @BeforeEach
    public void setUp() throws PersonAlreadyExistException {
        pm.findAllPerson().forEach(person -> {
            pm.remove(person);
        });
    }

    @Test
    public void shouldReturnPersons() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        Person[] persons = restTemplate.getForObject("http://localhost:" + port + "/persons", Person[].class);

        //verification
        Assertions.assertNotNull(persons);
        Assertions.assertEquals(1, persons.length);
        Person person = persons[0];
        Assertions.assertNotNull(person);
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertEquals(p1.getName(), person.getName());
        Assertions.assertEquals(p1.getAge(), person.getAge());
        Assertions.assertEquals(p1.getGenre(), person.getGenre());
        Assertions.assertEquals(p1.getBirthday(), person.getBirthday());
    }

    @Test
    public void shouldReturnResponseEntityPersons() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        ResponseEntity<Person[]> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/persons", Person[].class);
        Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());

        //verification
        Assertions.assertNotNull(responseEntity.getBody());
        Person[] persons = responseEntity.getBody();
        Assertions.assertEquals(1, persons.length);
        Person person = persons[0];
        Assertions.assertNotNull(person);
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertEquals(p1.getName(), person.getName());
        Assertions.assertEquals(p1.getAge(), person.getAge());
        Assertions.assertEquals(p1.getGenre(), person.getGenre());
        Assertions.assertEquals(p1.getBirthday(), person.getBirthday());
    }

    @Test
    public void shouldReturnPerson() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        Person personFound = restTemplate.getForObject("http://localhost:" + port + "/person/" + p1.getEmail(), Person.class);

        //verification
        Assertions.assertNotNull(personFound);
        Assertions.assertEquals(p1.getEmail(), personFound.getEmail());
        Assertions.assertEquals(p1.getName(), personFound.getName());
        Assertions.assertEquals(p1.getAge(), personFound.getAge());
        Assertions.assertEquals(p1.getGenre(), personFound.getGenre());
        Assertions.assertEquals(p1.getBirthday(), personFound.getBirthday());
    }

    @Test
    public void shouldReturnPersonWithGivenName() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        Map<String, Object> params = new HashMap<>();
        params.put("name", p1.getName());
        params.put("email", "");
        Person[] personsFound = restTemplate.getForObject("http://localhost:" + port + "/persons/search?name={name}&email={email}", Person[].class, params);

        //verification
        Assertions.assertNotNull(personsFound);
        Assertions.assertEquals(1, personsFound.length);
        Person person = personsFound[0];
        Assertions.assertNotNull(person);
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertEquals(p1.getName(), person.getName());
        Assertions.assertEquals(p1.getAge(), person.getAge());
        Assertions.assertEquals(p1.getGenre(), person.getGenre());
        Assertions.assertEquals(p1.getBirthday(), person.getBirthday());
    }

    @Test
    public void shouldDeletePerson() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        restTemplate.delete("http://localhost:" + port + "/person/" + p1.getEmail());

        //verification
        assertThrows(UnknownPersonException.class, () -> {
            pm.findPersonByMail(p1.getEmail());
        });
    }

    @Test
    public void shouldAddPerson() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));

        //test
        restTemplate.postForObject("http://localhost:" + port + "/person", p1, Person.class);

        //verification
        Person person = pm.findPersonByMail(p1.getEmail());
        Assertions.assertNotNull(person);
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertEquals(p1.getName(), person.getName());
        Assertions.assertEquals(p1.getAge(), person.getAge());
        Assertions.assertEquals(p1.getGenre(), person.getGenre());
        Assertions.assertEquals(p1.getBirthday(), person.getBirthday());
    }

    @Test
    public void shouldUpdatePerson() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        p1.setAge(30);
        restTemplate.put("http://localhost:" + port + "/person", p1);

        //verification
        Person personByMail = pm.findPersonByMail(p1.getEmail());
        Assertions.assertNotNull(personByMail);
        Assertions.assertEquals(p1.getEmail(), personByMail.getEmail());
        Assertions.assertEquals(p1.getName(), personByMail.getName());
        Assertions.assertEquals(30, personByMail.getAge());
        Assertions.assertEquals(p1.getGenre(), personByMail.getGenre());
        Assertions.assertEquals(p1.getBirthday(), personByMail.getBirthday());
    }
}