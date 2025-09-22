package heg.manager;

import heg.entity.Genre;
import heg.entity.Person;
import heg.exception.UnknownPersonException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

import static heg.entity.Person.PERSON_ENTITY_FIND_ALL;
import static heg.entity.Person.PERSON_ENTITY_FIND_BY_MAIL;

@Service
public class PersonManager {

    private static final Logger LOG = Logger.getLogger(PersonManager.class);

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void insert(Person p) {
        entityManager.persist(p);
    }

    @Transactional
    public void remove(Person p) {
        Person personToRemove = entityManager.find(Person.class, p.getEmail());
        entityManager.remove(personToRemove);
    }

    @Transactional
    public void remove(String email) {
        Person personToRemove = entityManager.find(Person.class, email);
        entityManager.remove(personToRemove);
    }

    public List<Person> findAllPerson() {
        TypedQuery<Person> namedQuery = entityManager.createNamedQuery(PERSON_ENTITY_FIND_ALL, Person.class);
        return namedQuery.getResultList();
    }

    public Person findPersonByMail(final String mail) throws UnknownPersonException {
        try {
            TypedQuery<Person> namedQuery = entityManager.createNamedQuery(PERSON_ENTITY_FIND_BY_MAIL, Person.class);
            namedQuery.setParameter(1, mail);
            return namedQuery.getSingleResult();
        } catch (NoResultException nre) {
            throw new UnknownPersonException("Person with enail " + mail + " not found", nre);
        }
    }

    public List<Person> findByName(String name) {
        return entityManager.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Person> findByAge(int age) {
        return entityManager.createQuery("SELECT p FROM Person p WHERE p.age = :age", Person.class)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * @param person
     * @return
     */
    @Transactional
    public Person update(final Person person) throws UnknownPersonException {
        return entityManager.merge(person);
    }

    /**
     * @param email
     * @param genre
     */
    @Transactional
    public void updateGenre(final String email, final Genre genre) {
        Person person = entityManager.find(Person.class, email);
        person.setGenre(genre);
    }
}
