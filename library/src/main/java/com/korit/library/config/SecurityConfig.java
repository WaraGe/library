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
        //BCryptPasswordEncoder.matches를 사용해서 비밀번호 변경기능 가능
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
                .antMatchers("/admin/**")
                .hasRole("ADMIN")// DB에서는 ROLE_ADMIN으로 저장했는데 ADMIN으로만 적는 이유는 시큐리티가 접두사로 ROLE_이 붙혀 있는것만 인식해서 그 뒷부분을 읽기 때문(포함)
                .anyRequest() // 모든 경로를
                .permitAll() // 인증을 줘라
                .and()
                .formLogin() // 폼tag로 로그인하라
                .loginPage("/account/login") // 로그인 페이지 GET요청
                .loginProcessingUrl("/account/login") // 로그인 인증 POST요청
//                .successForwardUrl("") // 로그인 성공하면 경로에 있는 페이지로 이동해라
//                .failureForwardUrl() // 위와 반대로 로그인 실패했을때 위의 페이지로 이동해라
                .failureForwardUrl("/account/login/error") // 로그인 실패시 error페이지
                // get요청을 한다면 Forward와 request가 연결됨 위의 login.html이 post요청이므로 postmapping으로 연결을 해줘야함
                //.failureHandler() 위의 ForwardUrl의 방법은 편법임
                .defaultSuccessUrl("/index"); // 성공한다면 index.html로 돌아옴 갈때가 없을때
                // security에 권한에 걸려서 못들어갈때는 로그인할때는 그쪽 경로로 들어가짐

    }
}
