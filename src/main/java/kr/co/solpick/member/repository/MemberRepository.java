package kr.co.solpick.member.repository;

import kr.co.solpick.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

//    // 레시픽 사용자 ID로 회원 조회
    Optional<Member> findByRecipickUserId(Integer recipickUserId);

    // 모든 회원 ID만 조회
    @Query("SELECT m.id FROM Member m")
    List<Integer> findAllMemberIds();
}