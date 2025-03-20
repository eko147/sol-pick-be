package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.entity.UserAllergy;
import kr.co.solpick.refrigerator.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-allergy")
@RequiredArgsConstructor
public class UserAllergyController {

    private final UserAllergyService userAllergyService;

    /**
     * 특정 사용자의 알러지 목록 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserAllergy>> getUserAllergies(@PathVariable Integer userId) {
        return ResponseEntity.ok(userAllergyService.getAllergiesByUserId(userId));
    }

    /**
     * 사용자의 알러지 정보 추가
     */
    @PostMapping("/{userId}")
    public ResponseEntity<UserAllergy> addUserAllergy(
            @PathVariable Integer userId, @RequestParam String ingredientName) {
        return ResponseEntity.ok(userAllergyService.addAllergy(userId, ingredientName));
    }

    /**
     * 사용자의 특정 알러지 정보 삭제
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserAllergy(
            @PathVariable Integer userId, @RequestParam String ingredientName) {
        userAllergyService.removeAllergy(userId, ingredientName);
        return ResponseEntity.noContent().build();
    }
}
