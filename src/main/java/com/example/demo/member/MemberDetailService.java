package com.example.demo.member;

import com.example.demo.persistence.entity.Member;
import com.example.demo.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // todo - 역할 enum 처리 혹은 DB 로 관리
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        if (email.equals("suyeonjungdev@gmail.com")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        return new User(member.getEmail(), member.getPassword(), grantedAuthorities);
    }


}

