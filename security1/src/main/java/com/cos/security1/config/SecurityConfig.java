package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;

//1.코드받기(인증) 2.엑세스토큰(권한) 
//3.사용자프로필 정보를 가져오고 
//4-1.그 정보를 토대로 회원가입을 자동으로 진행시키도 함
//4-2.(이메일,전화번호,이름,아이디) 쇼핑몰 ->(집주소),백화점몰 ->(vip등급,일반등급) 추가적으로 구성해야할것


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화,preAuthorize,postAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	//해당 메서드의 리턴되는 오브젝트를 loC로 등록해준다.
	/* Spring Boot 2.6.x 버전부터, 순환 참조를 default로 금지함으로서 발생되는 에러메시지
	 * 로 인하여 여기서 주석처리하고 CustomBCryptPasswordEncoder로 사용
	 * @Bean public BCryptPasswordEncoder encodePwd() { return new
	 * BCryptPasswordEncoder(); }
	 */
	 
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //로그인한사람만 들어올수있고
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")//로그인도 하고 어드민이나 매니저라는 권한이 있어야한다
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")// 로그인도 하고 어드민이라는 권한도 있어야한다.
			.anyRequest().permitAll() //그외의 요청은 다 됨 
			.and()
			.formLogin()
			.loginPage("/loginForm") //무조건 로그인페이지를 거처서 가게 함
			.loginProcessingUrl("/login")// /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다. 
			.defaultSuccessUrl("/")	//loginProcessingUrl의 장점은 Controller에서 login을 따로 만들필요가없음
			.and()
			.oauth2Login() //oauth를 쓰려면 해줘야함
			.loginPage("/loginForm") //구글 로그인이 완료된 뒤에 후처리가 필요함 Tip.코드X(엑세스토큰+사용자프로필정보 O)
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	
	}

	
}
