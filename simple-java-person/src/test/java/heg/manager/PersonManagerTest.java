package heg.manager;

import heg.entity.Address;
import heg.entity.AddressType;
import heg.entity.Genre;
import heg.entity.Person;
import heg.exception.PersonAlreadyExistException;
import heg.exception.UnknownPersonException;
import jakarta.persistence.PersistenceException;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PersonManagerTest {

    private static final Logger LOG = Logger.getLogger(PersonManagerTest.class);

    @Autowired
    PersonManager pm;

    @BeforeEach
    public void setUp() throws PersonAlreadyExistException {
        pm.findAllPerson().forEach(person -> {
            pm.remove(person);
        });
    }

    @Test
    public void shouldInsertPersonWithAddress() throws PersonAlreadyExistException, UnknownPersonException {
        Person p2 = new Person("michel@hesge.ch", "michel", 22, Genre.MASCULIN, LocalDate.now());
        Address a1 = new Address("12", "rue beauregard", "Geneve", 1210, "Suisse", AddressType.PRINCIPALE);
        a1.setPerson(p2);
        Address a2 = new Address("22", "rue beauregard", "Lausanne", 1210, "Suisse", AddressType.SECONDAIRE);
        a2.setPerson(p2);
        p2.setAddresses(List.of(a1, a2));
        pm.insert(p2);

        //verification
        Person personInserted = pm.findPersonByMail("michel@hesge.ch");
        Assertions.assertNotNull(personInserted);
        Assertions.assertEquals(p2.getName(), personInserted.getName());
        Assertions.assertEquals(p2.getAge(), personInserted.getAge());
        Assertions.assertEquals(p2.getEmail(), personInserted.getEmail());
        Assertions.assertEquals(p2.getGenre(), personInserted.getGenre());
        Assertions.assertEquals(p2.getBirthday(), personInserted.getBirthday());

        List<Address> addresses = personInserted.getAddresses();
        Assertions.assertNotNull(addresses);
        Assertions.assertEquals(2, addresses.size());

        for (Address ad : addresses) {
            Person person = ad.getPerson();
            Assertions.assertNotNull(person);
            Assertions.assertEquals(p2.getEmail(), person.getEmail());
            Assertions.assertEquals(p2.getName(), person.getName());
        }

        //clean
        pm.remove(personInserted);
    }

    @Test
    public void shouldDeletePerson() throws PersonAlreadyExistException, UnknownPersonException {
        //initialisation
        Person personTest = new Person("test@hesge.ch", "test", 50, Genre.AUTRE, LocalDate.now());
        pm.insert(personTest);

        //Test
        pm.remove(personTest);

        //verification
        assertThrows(UnknownPersonException.class, () -> {
            pm.findPersonByMail("test@hesge.ch");
        });
    }

    @Test
    public void shouldDeletePersonWithId() throws PersonAlreadyExistException, UnknownPersonException {
        //initialisation
        Person personTest = new Person("test@hesge.ch", "test", 50, Genre.AUTRE, LocalDate.now());
        pm.insert(personTest);

        //Test
        pm.remove("test@hesge.ch");

        //verification
        assertThrows(UnknownPersonException.class, () -> {
            pm.findPersonByMail("test@hesge.ch");
        });
    }

    @Test
    public void shouldUpdatePerson() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        p1.setAge(50);
        p1.setGenre(Genre.FEMININ);
        Person personUpdated = pm.update(p1);

        //verification
        Assertions.assertEquals(50, personUpdated.getAge());
        Assertions.assertEquals(Genre.FEMININ, personUpdated.getGenre());
    }

    @Test
    public void shouldUpdateGenrePerson() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        pm.updateGenre(p1.getEmail(), Genre.AUTRE);

        //verification
        Person byEmail = pm.findPersonByMail(p1.getEmail());
        Assertions.assertEquals(Genre.AUTRE, byEmail.getGenre());
    }

    @Test
    public void shouldInsertPersonAndAddress() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jean@hesge.ch", "jean", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        Address a1 = new Address("12", "rue beauregard", "Geneve", 1210, "Suisse", AddressType.PRINCIPALE);
        p1.getAddresses().add(a1);
        a1.setPerson(p1);
        pm.insert(p1);

        //test
        Person personByMail = pm.findPersonByMail("jean@hesge.ch");

        //verification
        Assertions.assertEquals(p1.getEmail(), personByMail.getEmail());
        Assertions.assertNotNull(personByMail.getAddresses());
        Assertions.assertEquals(1, personByMail.getAddresses().size());
        Assertions.assertEquals(p1.getAddresses().get(0).getCountry(), personByMail.getAddresses().get(0).getCountry());
        Address address = personByMail.getAddresses().get(0);
        Assertions.assertNotNull(address.getPerson());
    }

    @Test
    public void shouldFindAllPerson() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        Address a1 = new Address("6", "rue des morgines", "Petit-Lancy", 1210, "Suisse", AddressType.PRINCIPALE);
        a1.setPerson(p1);
        p1.getAddresses().add(a1);
        pm.insert(p1);

        //test
        List<Person> personList = pm.findAllPerson();

        //verification
        Assertions.assertNotNull(personList);
        Assertions.assertEquals(1, personList.size());
        Person person = personList.get(0);
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertNotNull(person.getAddresses());
        Assertions.assertEquals(1, person.getAddresses().size());
        Assertions.assertEquals(p1.getAddresses().get(0).getCountry(), person.getAddresses().get(0).getCountry());
    }

    @Test
    public void shouldFindPersonByMail() throws UnknownPersonException {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        Address a1 = new Address("6", "rue des morgines", "Petit-Lancy", 1210, "Suisse", AddressType.PRINCIPALE);
        a1.setPerson(p1);
        p1.getAddresses().add(a1);
        pm.insert(p1);

        //test
        Person person = pm.findPersonByMail(p1.getEmail());

        //verification
        Assertions.assertNotNull(person);
        LOG.info(person.toString());
        Assertions.assertEquals(p1.getName(), person.getName());
        Assertions.assertEquals(p1.getEmail(), person.getEmail());
        Assertions.assertEquals(p1.getAge(), person.getAge());
        Assertions.assertEquals(p1.getGenre(), person.getGenre());
        Assertions.assertEquals(p1.getBirthday(), person.getBirthday());
        Assertions.assertNotNull(person.getAddresses());
        Assertions.assertEquals(1, person.getAddresses().size());
        Assertions.assertEquals(p1.getAddresses().get(0).getCountry(), person.getAddresses().get(0).getCountry());
    }

    @Test
    public void shouldGetException() {
        Person p1 = new Person("paul@hesge.ch", "paul", 33, Genre.MASCULIN, LocalDate.of(2000, 4, 20));
        Address a1 = new Address("12", "boulevard du theatre", null, 1210, "Suisse", AddressType.PRINCIPALE);
        assertThrows(DataIntegrityViolationException.class, () -> {
            p1.getAddresses().add(a1);
            a1.setPerson(p1);
            pm.insert(p1);
        });
    }

    @Test
    public void shouldFindPersonByName() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        List<Person> persons = pm.findByName("jacques");

        //verification
        Assertions.assertNotNull(persons);
        Assertions.assertEquals(1, persons.size());
    }

    @Test
    public void shouldNotFindPersonByName() {
        //test
        List<Person> persons = pm.findByName("thierry");

        //verification
        Assertions.assertNotNull(persons);
        Assertions.assertEquals(0, persons.size());
    }

    @Test
    public void shouldFindPersonByAge() {
        //initialisation
        Person p1 = new Person("jacques@hesge.ch", "jacques", 22, Genre.MASCULIN, LocalDate.of(1990, 4, 20));
        pm.insert(p1);

        //test
        List<Person> persons = pm.findByAge(p1.getAge());

        //verification
        Assertions.assertNotNull(persons);
        persons.forEach(person -> {
            Assertions.assertEquals(p1.getAge(), person.getAge());
        });
    }
}
