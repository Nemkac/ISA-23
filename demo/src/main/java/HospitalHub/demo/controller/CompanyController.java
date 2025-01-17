package HospitalHub.demo.controller;

import HospitalHub.demo.dto.CompanyDTO;
import HospitalHub.demo.dto.UserDTO;
import HospitalHub.demo.model.Company;
import HospitalHub.demo.model.CompanyAdministrator;
import HospitalHub.demo.model.EquipmentPickupSlot;
import HospitalHub.demo.model.User;
import HospitalHub.demo.service.CompanyAdministratorService;
import HospitalHub.demo.service.CompanyService;
import HospitalHub.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyAdministratorService companyAdministratorService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/getAll")
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<Company> companies = companyService.findAll();

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping(value = "/getId")
    public ResponseEntity<Integer> getCompanyId() {
        Integer companyId = this.companyService.calculateCompanyId() + 1;

        return new ResponseEntity<Integer>(companyId, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json", value = "/save")
    public ResponseEntity<CompanyDTO> saveCompany(@RequestBody CompanyDTO companyDTO) {

        Company company = new Company(
                companyDTO.getName(),
                companyDTO.getCity(),
                companyDTO.getCountry(),
                companyDTO.getDescription(),
                companyDTO.getLatitude(),
                companyDTO.getLongitude()
        );

        this.companyService.save(company);
        return new ResponseEntity<>(new CompanyDTO(company), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Integer id) {
        List<Company> companies = companyService.findAll();

        for (Company company : companies) {
            int companyId = company.getId();
            if (companyId == id) {
                return new ResponseEntity<>(company, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping(consumes = "application/json", value = "/update/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@RequestBody CompanyDTO companyDTO, @PathVariable Integer id) {
        User loggedInUser = userService.getById(id);
        CompanyAdministrator companyAdministrator = companyAdministratorService.getByUser(loggedInUser);
        Company company = companyService.getById(companyAdministrator.getCompany().getId());

        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (companyDTO.getName() != null) {
            company.setName(companyDTO.getName());
        }
        if (companyDTO.getCity() != null) {
            company.setCity(companyDTO.getCity());
        }
        if (companyDTO.getCountry() != null) {
            company.setCountry(companyDTO.getCountry());
        }
        if (companyDTO.getAddress() != null) {
            company.setAddress(companyDTO.getAddress());
        }
        if (companyDTO.getLatitude() != null) {
            company.setLatitude(companyDTO.getLatitude());
        }
        if (companyDTO.getLongitude() != null) {
            company.setLongitude(companyDTO.getLongitude());
        }
        if (companyDTO.getDescription() != null) {
            company.setDescription(companyDTO.getDescription());
        }

        company = companyService.save(company);

        return new ResponseEntity<>(new CompanyDTO(company), HttpStatus.OK);

    }

    @GetMapping(value = "/getAdminsCompany/{id}")
    public ResponseEntity<Company> getAdminsCompany(@PathVariable Integer id) {
        User loggedInUser = userService.getById(id);
        CompanyAdministrator companyAdministrator = companyAdministratorService.getByUser(loggedInUser);

        if (companyAdministrator == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Company selectedCompany = companyAdministrator.getCompany();

        selectedCompany.getMedicalEquipmentList().forEach(equipment -> {
            equipment.setCompany(null);
        });

        return new ResponseEntity<>(selectedCompany, HttpStatus.OK);
    }

    @GetMapping(value = "/getAvailableDays/{id}")
    public ResponseEntity<List<LocalDate>> getCompaniesAvailableDaysInFollowingTen(@PathVariable Integer id){
        Company company = companyService.getById(id);
        if(company != null){
            if(companyService.getAvailableDaysInFollowingTen(id) != null) {
                return new ResponseEntity<>(companyService.getAvailableDaysInFollowingTen(id),HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/getAllFreeAppointments/{companyId}")
    public ResponseEntity<List<EquipmentPickupSlot>> getAllFreeAppointments(@PathVariable Integer companyId){
        Company company = this.companyService.getById(companyId);
        List<CompanyAdministrator> companyAdministrators = company.getCompanyAdministrator();
        List<EquipmentPickupSlot> freeAppointments = new ArrayList<>();


        for(CompanyAdministrator companyAdministrator : companyAdministrators){
            List<EquipmentPickupSlot> adminsSlots = companyAdministrator.getEquipmentPickupSlots();

            for(EquipmentPickupSlot slot : adminsSlots){
                if(slot.getReservedBy() == null && slot.getDateTime().isAfter(LocalDateTime.now())){
                    freeAppointments.add(slot);
                }
            }
        }

        return new ResponseEntity<>(freeAppointments, HttpStatus.OK);
    }

    @GetMapping(value = "/getAllAppointments/{companyId}")
    public ResponseEntity<List<EquipmentPickupSlot>> getAllAppointments(@PathVariable Integer companyId){
        Company company = this.companyService.getById(companyId);
        List<CompanyAdministrator> companyAdministrators = company.getCompanyAdministrator();
        List<EquipmentPickupSlot> appointments = new ArrayList<>();

        for(CompanyAdministrator companyAdministrator : companyAdministrators){
            List<EquipmentPickupSlot> adminsSlots = companyAdministrator.getEquipmentPickupSlots();

            appointments.addAll(adminsSlots);
        }

        for(EquipmentPickupSlot appointment : appointments){
            if(appointment.getDateTime().isBefore(LocalDateTime.now())){
                appointment.setStatus(EquipmentPickupSlot.Status.EXPIRED);
                if(appointment.getReservedBy() != null){
                    User user = appointment.getReservedBy();
                    Integer penalties = user.getPenaltyPoints() + 2;
                    user.setPenaltyPoints(penalties);
                    userService.save(user);
                }
            }
        }

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/getAdministrators/{companyId}")
    public ResponseEntity<List<UserDTO>> getAdministrators(@PathVariable Integer companyId){
        Company company = this.companyService.getById(companyId);
        List<CompanyAdministrator> companyAdministrators = company.getCompanyAdministrator();
        List<UserDTO> dtos = new ArrayList<>();

        for(CompanyAdministrator admin : companyAdministrators){
            UserDTO dto = new UserDTO(admin.getUser());
            dtos.add(dto);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
