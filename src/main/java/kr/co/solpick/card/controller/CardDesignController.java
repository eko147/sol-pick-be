package kr.co.solpick.card.controller;

import kr.co.solpick.card.dto.*;
import kr.co.solpick.card.service.CardDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/solpick/api/card-design")
@RequiredArgsConstructor
public class CardDesignController {
    private final CardDesignService cardDesignService;

    @PostMapping("/save-background")
    public ResponseEntity<Map<String, Object>> saveBackground(
            @RequestBody CardBackgroundRequestDTO requestDTO) {
        Integer designId = cardDesignService.saveBackgroundInfo(
                requestDTO.getUserId(),
                requestDTO.getBackgroundId());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "designId", designId
        ));
    }

    @PostMapping("/save-stickers")
    public ResponseEntity<Map<String, Object>> saveStickers(
            @RequestBody CardStickersRequestDTO requestDTO) {
        cardDesignService.saveStickersInfo(
                requestDTO.getDesignId(),
                requestDTO.getStickersData());

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/issue-card")
    public ResponseEntity<CardResponseDTO> issueCard(
            @RequestBody CardIssueRequestDTO requestDTO) {
        CardResponseDTO response = cardDesignService.issueCard(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/card-info/{userId}")
    public ResponseEntity<CardCompleteResponseDTO> getCardInfo(
            @PathVariable Integer userId) {
        CardCompleteResponseDTO response = cardDesignService.getCardInfo(userId);
        return ResponseEntity.ok(response);
    }
}
