package heg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "address")
@NamedQuery(name=Address.ADDRESS_ENTITY_FIND_CITY, query="SELECT a FROM Address a where a.city = :city")
@NamedQuery(name=Address.ADDRESS_ENTITY_FIND_STREET, query="SELECT a FROM Address a where a.streetName like CONCAT('%', :street, '%')")
public class Address {

    public static final String ADDRESS_ENTITY_FIND_CITY = "Address.findByCity";

    public static final String ADDRESS_ENTITY_FIND_STREET = "Address.findByStreetName";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="street_no")
    private String streetNo;

    @Column(name="street_name")
    private String streetName;

    @Column(name="city", nullable = false)
    private String city;

    @Column(name="post_code")
    private int postCode;

    @Column(name="country")
    private String country;

    @Column(name="address_type")
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "email")
    private Person person;

    public Address(String streetNo, String streetName, String city, int postCode, String country, AddressType addressType) {
        this.streetNo = streetNo;
        this.streetName = streetName;
        this.city = city;
        this.postCode = postCode;
        this.country = country;
        this.addressType = addressType;
    }

    public Address(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AddressType getAddressesType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return postCode == address.postCode && country == address.country && Objects.equals(id, address.id) && Objects.equals(streetNo, address.streetNo) && Objects.equals(streetName, address.streetName) && Objects.equals(city, address.city) && addressType == address.addressType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, streetNo, streetName, city, postCode, country, addressType);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", streetNo='" + streetNo + '\'' +
                ", streetName='" + streetName + '\'' +
                ", city='" + city + '\'' +
                ", postCode=" + postCode +
                ", country=" + country +
                ", addressType=" + addressType +
                '}';
    }
}
