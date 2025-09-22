package heg.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "person")
@NamedQuery(name=Person.PERSON_ENTITY_FIND_ALL, query="SELECT p FROM Person p")
@NamedQuery(name=Person.PERSON_ENTITY_FIND_BY_MAIL, query="SELECT p FROM Person p where p.email = ?1")
public class Person {

    public static final String PERSON_ENTITY_FIND_ALL = "Person.findAll";

    public static final String PERSON_ENTITY_FIND_BY_MAIL = "Person.findByMail";

    @Id
    @Column(name="email")
    private String email;

    @Column(name="name")
    private String name;

    @Column(name="age")
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name="genre")
    private Genre genre;

    private LocalDate birthday;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Address> addresses = new ArrayList<>();

    public Person(){}

    public Person(String email, String name, int age, Genre genre, LocalDate birthday) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.genre = genre;
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(email, person.email) && Objects.equals(name, person.name) && genre == person.genre && Objects.equals(birthday, person.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, age, genre, birthday);
    }

    @Override
    public String toString() {
        return "Person{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", genre=" + genre +
                ", birthday=" + birthday +
                '}';
    }
}
