package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller //view를 리턴하겠다.
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { //DI(의존성 주입)
		System.out.println("test/login ===========");
		OAuth2User oaduth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication" +oaduth2User.getAttributes());
		System.out.println("oauth2User : "+oauth.getAttributes());
		

		return "OAuth 세선 정보 확인하기";
		
	}
	@GetMapping("/test/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication
			,@AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성 주입)
		System.out.println("test/login ===========");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication" +principalDetails.getUser());
		
		System.out.println("userDetails:"+userDetails.getUser());
		return "세선 정보 확인하기";
		
	}
	
	//localhost:8080/
	//localhost:8080 2개를 사용
	@GetMapping({"","/"})
	public String index() {
		//머스테치 기본폴더 src/main/resources/
		//뷰리졸버 설정시 : templates(prefix), .mustache(suffix) 생략가능
		return "index"; // src/main/resources/templates/index.mustache
	}
	//OAuth 로그인을 해도 PrincipalDetails
	//일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : "+ principalDetails);
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	//스프링시큐리티가 해당주소를 낚아챔 - SecurityConfig 파일 생성후 작동안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);//회원가입 잘됨.허나 비밀번호가 1234로 됨 
								//=>시큐리티로 로그인을 할 수 없음 이유는 패스워드가 암호화가 안되었기 때문에 그래서 61~63 코드를 추가해줘야함
		
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN") //하나만 쓸려고하면 이걸쓰면됨
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	//@PostAuthorize 메소드가 실행된후 실행됨 요즘에는 Secured때문에 안쓴다고함
	@PreAuthorize("hasRole('ROLE_MANAGER')or hasRole('ROLE_ADMIN')") //메소드가 실행되기전에 실행됨 //여러개 쓸려고 하는거임
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}

}
