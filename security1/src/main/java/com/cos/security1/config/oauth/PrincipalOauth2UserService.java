package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.auth.provider.FacebookUserInfo;
import com.cos.security1.config.auth.provider.GoogleUserInfo;
import com.cos.security1.config.auth.provider.NaverUserInfo;
import com.cos.security1.config.auth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	//구글로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
	//함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration:"+userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 햇는지 확인 가능.
		System.out.println("getAccessToken:"+userRequest.getAccessToken().getTokenValue());
		
	
		OAuth2User oauth2User = super.loadUser(userRequest);
		//구글로그인 버튼 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
		//userRequest 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필 받아준다.
		System.out.println("getAttributes:"+oauth2User.getAttributes());
		//super.loadUser(userRequest) 와 oauth2User 는 같은거(=)
		
		//회원가입 진행
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}else {
			System.out.println("우리는 구글과 페이스북만 지원해요");
		}
		
		//위에서 분기 처리하고 인터페이스를 만들어거 이건 쓸필요가없음
		/*
		String provider = userRequest.getClientRegistration().getRegistrationId(); //google
		String providerId = oauth2User.getAttribute("sub"); // 105345926443541998044구글:sub 페이스북:id
		String username = provider+"_"+providerId; // google_105345926443541998044
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		*/
		//위에서 분기처리후 방법
		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider+"_"+providerId;
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		//회원가입이 되어잇는지 확인
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			//회원가입이 안되어있는경우
			System.out.println("구글 로그인이 최초입니다.");
			userEntity = User.builder()
							.username(username)
							.password(password)
							.email(email)
							.role(role)
							.provider(provider)
							.providerId(providerId)
							.build();
			userRepository.save(userEntity);
			
		}else {
			System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어있습니다.");
		}
			//회원가입이 되어있는 경우

		return new PrincipalDetails(userEntity,oauth2User.getAttributes());
		
	}
}
