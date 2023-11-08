package HospitalHub.demo.service;

import HospitalHub.demo.dto.UserDTO;
import HospitalHub.demo.model.User;
import HospitalHub.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }
    public boolean checkData(UserDTO userDto){

        if(userDto.getDateOfBirth().isAfter(LocalDate.now())){
            return false;
        }

        return true;
    }
}
