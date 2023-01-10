package com.korit.library.security;

import com.korit.library.aop.annotation.ParamsAspect;
import com.korit.library.repository.AccountRepository;
import com.korit.library.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @ParamsAspect // 로그 찍는 aop
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDto user = accountRepository.findUserByUsername(username);
        //비밀번호 암호화 체크
        if(user == null) {
            throw  new UsernameNotFoundException("회원정보를 확인 할 수 없습니다"); //예외처리
        }


        return new PrincipalDetails(user);
        // loadUserByUsername의 리턴 자료형이 UserDetails이기때문에 업캐스팅이 되어서 리턴이 가능함
    }
}
