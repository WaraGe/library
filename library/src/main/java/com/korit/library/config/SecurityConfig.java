package com.korit.library.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity // WebSecurityConfigurerAdapter을 상복받은 계체에서 설정한것보다 밑에서 설정한것으로 실행하겠따. 라는 의미
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //static폴더경로 안에 있는 모든 파일은 막혀있지만 설정을 해주고 밑을 세팅해준다면,
        //폴더경로가 막히지 않음
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //csrf는 나중에
        http.httpBasic().disable(); //httpBasic 팝업창으로 로그인하게 뜨는 형식을 비활성화
        http.authorizeRequests() //http가 없는것을 사용해야함
                .antMatchers("/mypage/**") //,로 여러개의 페이지 경로를 설정할 수 있음
                .authenticated() // /mypage에 들어온 경우 인증을 걸처라
                .anyRequest() // 모든 경로를
                .permitAll() // 인증을 줘라
                .and() //
                .formLogin() // 폼tag로 로그인하라
                .loginPage("/account/login") // 로그인 페이지 get요청
                .loginProcessingUrl("/account/login") // 위와 같은 경로
                .defaultSuccessUrl("/index"); // 성공한다면 index.html로 돌아옴

    }
}
