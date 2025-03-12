package kr.co.solpick.auth.controller;

import kr.co.solpick.auth.dto.AuthRequestDTO;
import kr.co.solpick.auth.dto.AuthResponseDTO;
import kr.co.solpick.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000/"})
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO loginRequest) {
//        log.info("로그인 요청 수신: {}", loginRequest.getEmail());
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}