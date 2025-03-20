package kr.co.solpick.refrigerator.service;

import kr.co.solpick.refrigerator.entity.UserAllergy;
import kr.co.solpick.refrigerator.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAllergyService {
    private final UserAllergyRepository userAllergyRepository;

    public List<String> getUserAllergies(Integer userId) {
        return userAllergyRepository.findByUserId(userId)
                .stream()
                .map(UserAllergy::getIngredientName)
                .collect(Collectors.toList());
    }
    public List<UserAllergy> getAllergiesByUserId(Integer userId) {
        return userAllergyRepository.findByUserId(userId);
    }
    @Transactional
    public UserAllergy addAllergy(Integer userId, String ingredientName) {
        UserAllergy allergy = new UserAllergy();
        allergy.setUserId(userId);
        allergy.setIngredientName(ingredientName);
        return userAllergyRepository.save(allergy);
    }

    @Transactional
    public void removeAllergy(Integer userId, String ingredientName) {
        userAllergyRepository.deleteByUserIdAndIngredientName(userId, ingredientName);
    }
}

