package com.example.demo.member;

import com.example.demo.persistence.entity.Member;
import com.example.demo.persistence.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostMapping("/api/member")
    public ResponseEntity<?> saveMember(@RequestBody MemberDto memberDto) {
        memberRepository.save(Member.createMember(memberDto.getEmail(), passwordEncoder.encode(memberDto.getPassword())));
        return ResponseEntity.ok(memberRepository.findAll());
    }
}

@Getter @Setter
class MemberDto {
    private String email;
    private String password;
}
