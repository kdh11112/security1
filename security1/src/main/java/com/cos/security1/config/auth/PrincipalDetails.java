package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.security1.model.User;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인이 진행이 완료가 되면 session을 만들어준다.(Security ContextHolder)
//오브젝트타입 => Authentication 타입 객체
//Authentication 안에 User정보가 있어야 됨.
//User 오브젝트타입 => UserDetails 타입객체

//Security Session => Authentication => UserDetails(PrincipalDetails)

public class PrincipalDetails implements UserDetails{

	private User user;//콤포지션
	
	public PrincipalDetails(User user) {
		this.user = user;
	}

	//해당 User의 권한을 리턴하는 곳!!!
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; //계졍 만료됬니 물어보는거 true면 만료되지 않음
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; //계정이 잠겨있니? true는 잠겨있지 않음
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; //계정의 패스워드가 만료가 됬니? true면 아니오
	}

	@Override
	public boolean isEnabled() {
		
		//우리 사이트!! 1년동안 회원이 로그인 안하면 휴면계정으로 하기로함
		//현재시간 - 로긴시간 => 1년을 초과하면 return false;
		
		return true; //계정이 활성화 되어있니?
	}

}
