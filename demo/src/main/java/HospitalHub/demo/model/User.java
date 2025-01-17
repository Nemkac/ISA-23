package HospitalHub.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "Users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ovo je bilo na auto mozda nastane problem
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "password")
    private String password;

    //'YYYY-MM-DD'
    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String phoneNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "profession")
    private String profession;

    @Column(name = "companyInfo")
    private String companyInfo;

    @Column(name = "enabled")
    private boolean Enabled = false;

    @Column(name = "penaltyPoints")
    private Integer penaltyPoints;

    private String roles;

    @OneToMany(mappedBy = "reservedBy")
    private List<EquipmentPickupSlot> equipmentPickupSlots;

    public User(){

    }

    //Registering new user constructor
    public User(String username,String name, String lastName, String password, LocalDate dateOfBirth, String email, String phoneNumber, String country, String city, String profession, String companyInfo,String role) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.profession = profession;
        this.companyInfo = companyInfo;
        this.roles = role;
        this.penaltyPoints = 0;
    }

/*    public User(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.dateOfBirth = user.getDateOfBirth();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.profession = user.getProfession();
        this.companyInfo = user.getCompanyInfo();
        this.roles = user.getRoles();
        this.penaltyPoints = user.getPenaltyPoints();
    }*/

    public User(String username,String name, String lastName, String password, LocalDate dateOfBirth, String email, String phoneNumber, String country, String city, String profession, String companyInfo,String role,Integer penaltyPoints) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.profession = profession;
        this.companyInfo = companyInfo;
        this.roles = role;
        this.penaltyPoints = penaltyPoints;
    }


    //Initial data insertion constructor
    public User(String username,String name, String lastName, String password, LocalDate dateOfBirth, String email, String phoneNumber, String country, String city, String profession, String companyInfo,String role, boolean enabled) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.profession = profession;
        this.companyInfo = companyInfo;
        this.roles = role;
        this.Enabled = enabled;
        this.penaltyPoints = 0;
    }

    public User(String username,String name, String lastName, String password, LocalDate dateOfBirth, String email, String phoneNumber, String country, String city, String profession, String companyInfo) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.profession = profession;
        this.companyInfo = companyInfo;
        this.penaltyPoints = 0;
    }

    public User(Integer id, String name, String lastName, String password, LocalDate dateOfBirth, String email, String phoneNumber, String country, String city, String profession, String companyInfo) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.profession = profession;
        this.companyInfo = companyInfo;
        this.penaltyPoints = 0;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(String companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public List<EquipmentPickupSlot> getEquipmentPickupSlots() {
        return equipmentPickupSlots;
    }

    public void setEquipmentPickupSlots(List<EquipmentPickupSlot> equipmentPickupSlots) {
        this.equipmentPickupSlots = equipmentPickupSlots;
    }

    public Integer getPenaltyPoints() {
        return penaltyPoints;
    }

    public void setPenaltyPoints(Integer penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

}
