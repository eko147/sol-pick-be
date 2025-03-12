package kr.co.solpick.member.repository;

import kr.co.solpick.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}