package com.sangmin.jwttutorial.repository;

import com.sangmin.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * username을 기준으로 권한 정보와 함께 User를 가져온다
     * @param username 사용자 이름
     * @return User 객체
     */
    @EntityGraph(attributePaths = "authorities") // Lazy 조회가 아닌 Eager 조회로 authorities 한번에 조회
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
