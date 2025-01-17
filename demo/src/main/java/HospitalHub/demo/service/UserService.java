package HospitalHub.demo.service;

import HospitalHub.demo.dto.UserProfileDTO;
import HospitalHub.demo.dto.UserRegisterDTO;
import HospitalHub.demo.model.EquipmentPickupSlot;
import HospitalHub.demo.model.User;
import HospitalHub.demo.model.UserInfoDetails;
import HospitalHub.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipmentPickupSlotService equipmentPickupSlotService;
    @Autowired
    private PasswordEncoder encoder;
    public List<User> findAll(){
        return userRepository.findAll();
    }

    //@Cacheable(value = "users", key = "#id")
    public User getById(Integer id){
        //System.out.println("Retriving user from database...");
        return userRepository.getById(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userDetail = userRepository.findByUsername(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    //@CachePut(cacheNames = "users", key = "#result.id")
    public User addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
        return userInfo;
    }
    public boolean checkData(UserRegisterDTO userRegisterDto){

        if(userRegisterDto.getDateOfBirth().isAfter(LocalDate.now())){
            return false;
        }
        if(userRepository.findByEmailIgnoreCase(userRegisterDto.getEmail()) != null){
            return false;
        }
        if(userRepository.findByUsername(userRegisterDto.getUsername()).isPresent()){
            return false;
        }
        if(!userRegisterDto.getPassword().equals(userRegisterDto.getRetypePassword()))
        {
            return false;
        }
        return true;
    }

    @Cacheable(value = "users", key = "#email")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //@Cacheable(value = "users", key = "#username")
    public User getByUsername(String username){
        System.out.println("Retriving user from database...");
        return userRepository.getByUsername(username);
    }

    public List<EquipmentPickupSlot> getAllUsersSlots(Integer userId){
        List<EquipmentPickupSlot> allSlots = equipmentPickupSlotService.getAll();
        List<EquipmentPickupSlot> foundSlots = new ArrayList<>();
        for(EquipmentPickupSlot slot: allSlots) {
            if(slot.getReservedBy()!=null && slot.getReservedBy().getId() == userId && slot.getDateTime().isAfter(LocalDateTime.now())){
                foundSlots.add(slot);
            }
        }
        return foundSlots;
    }

    public void clearOutPenaltyPoints(){
        List<User> users = userRepository.findAll();
        for(User user : users) {
            user.setPenaltyPoints(0);
            userRepository.save(user);
        }
    }


}
