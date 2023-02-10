package com.korit.library.security;


import com.korit.library.entity.UserMst;
import com.korit.library.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2DetailsService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
//        loadUser(userRequest) => userRequest > 구글에서 정보를 가져옴
        PrincipalDetails principalDetails = null;
//        principalDetails에서 oauth2User를 implements를 받고 있기 때문에 return을 업케스트팅가능!
        log.info("ClientRegistration: {}", userRequest.getClientRegistration()); // 클라이언트 id, pw 출력
        log.info("Attributes: {}", oAuth2User.getAttributes()); // 유저 정보 출력

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String)attributes.get("email"); // value가 Object이므로 String으로 다운캐스팅
        String username = email.substring(0, email.indexOf("@")); // 처음부터 @이전까지 출력

        //회원가입 할때만 필요
//        String name = (String)attributes.get("name");
//        String password = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()); // UUID를 이용해서 임시 비밀번호 생성
        
        String provider = userRequest.getClientRegistration().getClientName();

//      중복된 username(id)를 가지고 있는 것을 방지하기 위하여 제한 걸기
        UserMst userMst = accountRepository.findUserByUsername(username);
        
        if(userMst == null) {
            String name = (String)attributes.get("name");
            String password = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()); // UUID를 이용해서 임시 비밀번호 생성

            userMst = UserMst.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .build();
            
            accountRepository.saveUser(userMst);
            accountRepository.saveRole(userMst);
            
            userMst = accountRepository.findUserByUsername(username); //roleDtl를 가져올수 없어서 select를 해야 가져올 수 있는 값이기때문에 findByName을 한번 실행시켜준다
        }else if(userMst.getProvider() == null ) { //회원가입은 되었고 provider이 비었을때(sns로그인을 하지 않을떄)
            userMst.setProvider(provider);
            accountRepository.setUserProvider(userMst);
        }

        principalDetails = new PrincipalDetails(userMst, attributes); //저장된 userMst의 값을

        return principalDetails;
    }
}
