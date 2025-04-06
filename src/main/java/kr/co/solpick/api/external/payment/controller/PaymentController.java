package kr.co.solpick.api.external.payment.controller;

import kr.co.solpick.api.external.payment.dto.VerifyCardRequestDTO;
import kr.co.solpick.api.external.payment.dto.VerifyCardResponseDTO;
import kr.co.solpick.api.external.payment.service.CardValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solpick/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final CardValidationService cardValidationService;

    @PostMapping("/verify-card")
    public ResponseEntity<VerifyCardResponseDTO> verifyCard(@RequestBody VerifyCardRequestDTO request) {
        VerifyCardResponseDTO response = cardValidationService.verifyCardAndUsePoints(
                request.getRecipickUserId(),
                request.getCardNumber(),
                request.getCardExpiry()
        );

        return ResponseEntity.ok(response);
    }
}