package com.cos.security1.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder{
//	순환참조를 위하여 만듬
//	순환 참조가 나는 이유:
//
//		1. SpringContainer에서 처음 빈으로 등록하기 위해 객체를 생성 하여 줍니다(싱클톤)
//
//		2. 그래서 SecurityConfig 객체를 생성하던 중 
//
//		PrincipalOauth2UserService객체를 의존하고있네요?
//		그래서 PrincipalOauth2UserService를 만들어 주는데..
//
//
//		3. 어라? PrincipalOauth2UserService에서도 SecurityConfig에서
//		빈으로 등록한 BCryptPasswordEncoder를 참조하고있네??
//
//
//		4. 오잉? 스프링:참조가 순환되넹?아아아아아악! => 오류
//
//		즉,
//
//		SecurityConfig -> PrincipalOauth2UserService,
//
//		다시 PrincipalOauth2UserService->SecurityConfig
//
//		 
//
//		그래서 SecuritiConfig -> PrincipalOauth2UserService->
}
