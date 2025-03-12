package kr.co.solpick.order.service;

import java.util.List;

import kr.co.solpick.external.recipick.client.RecipickOrderApiClient;
import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final RecipickOrderApiClient recipickOrderApiClient;

    public List<OrderHistoryResponseDTO> getOrderHistory(int memberId) {
        log.info("주문 내역 조회 서비스 호출: memberId={}", memberId);
        return recipickOrderApiClient.getOrderHistory(memberId);
    }
}