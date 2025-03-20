package kr.co.solpick.refrigerator.service;

import kr.co.solpick.refrigerator.entity.RefrigeratorIng;
import kr.co.solpick.refrigerator.repository.RefrigeratorIngRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefrigeratorIngService {

    @Autowired
    private RefrigeratorIngRepository refrigeratorIngRepository;

    // 전체 식재료 조회
    public List<RefrigeratorIng> getAllIngredients() {
        return refrigeratorIngRepository.findAll();
    }

    // 특정 사용자의 식재료 조회
    public List<RefrigeratorIng> getIngredientsByUserId(Integer userId) {
        return refrigeratorIngRepository.findByUserId(userId);
    }
}
