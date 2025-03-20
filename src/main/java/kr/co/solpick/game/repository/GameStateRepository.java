package kr.co.solpick.game.repository;

import kr.co.solpick.game.entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameStateRepository extends JpaRepository<GameState, Integer> {

    // 유저의 게임 상태 조회
    Optional<GameState> findByUserId(Integer userId);

    // 유저의 게임 상태 존재 여부 확인
    boolean existsByUserId(Integer userId);
}