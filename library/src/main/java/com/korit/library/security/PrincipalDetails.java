package com.korit.library.security;

import com.korit.library.entity.RoleDtl;
import com.korit.library.entity.RoleMst;
import com.korit.library.entity.UserMst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
public class PrincipalDetails implements UserDetails {

    //생성자 생성
    @Getter
    private final UserMst user;
    private Map<String, Object> response;

    // 권한을 리스트로 관리하는 부분
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //lamda
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        List<RoleDtl> roleDtlList = user.getRoleDtl();
        for(int i = 0; i < roleDtlList.size(); i++) {
            RoleDtl dtl =  roleDtlList.get(i);
            RoleMst roleMst = dtl.getRoleMst();
            String roleName = roleMst.getRoleName();

            GrantedAuthority role = new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return roleName;
                }
            };
            authorities.add(role);
        }

//        user.getRoleDtlDto().forEach(dtl -> {
//            authorities.add(() -> dtl.getRoleMstDto().getRoleName());
//            //                   람다식으로 GrantedAuthority를 생성
//            //                     위쪽구문 roleName 생성과 같음
//        });

        return authorities;
        // role_user
        // role_admin 등 권한이 들어가 있음
    }

    //로그인을 했을때 Repository에 있는 저장된 비밀번호를 Security가 비밀번호를 복호화
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

//   아래의 4개중 하나라도 false라면 로그인이 안됨
    /*
      계정 만료 여부
      일정 기간이 지났을때 못쓰게 하는거(은행 보안 프로그램)
    */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /*
      계정 잠김 여부(계정이 부정한 행위를 했을때 (blacklist에 추가할때)
      휴먼계정을 의미함
    */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /*
      비밀번호 만료 여부
      비밀번호 5번이상 틀렸을때 false를 주면 로그인이 되지 않음
    */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /*
      사용자 활성화 여부
      휴먼계정을 의미함 or 회원가입, email, phone인증을 하지 않으면 로그인이 되지 않게 하는 방법
    */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
