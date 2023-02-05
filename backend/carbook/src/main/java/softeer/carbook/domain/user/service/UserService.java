package softeer.carbook.domain.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import softeer.carbook.domain.user.dto.Message;
import softeer.carbook.domain.user.dto.LoginForm;
import softeer.carbook.domain.user.dto.SignupForm;
import softeer.carbook.domain.user.exception.LoginEmailNotExistException;
import softeer.carbook.domain.user.exception.SignupEmailDuplicateException;
import softeer.carbook.domain.user.exception.SignupNicknameDuplicateException;
import softeer.carbook.domain.user.model.User;
import softeer.carbook.domain.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ResponseEntity<Message> signup(SignupForm signupForm){
        try {
            // 중복 체크
            checkDuplicated(signupForm);
        } catch (SignupEmailDuplicateException emailDE){
            // 이메일 중복 처리
            logger.debug(emailDE.getMessage());
            return Message.make400Response(emailDE.getMessage());
        } catch (SignupNicknameDuplicateException nicknameDE){
            // 닉네임 중복 처리
            logger.debug(nicknameDE.getMessage());
            return Message.make400Response(nicknameDE.getMessage());
        }
        // 데이터베이스에 유저 추가
        userRepository.addUser(signupForm);
        return Message.make200Response("SignUp Success");
    }

    private void checkDuplicated(SignupForm signupForm){
        if(userRepository.isEmailDuplicated(signupForm.getEmail()))
            throw new SignupEmailDuplicateException("중복된 이메일입니다.");
        if(userRepository.isNicknameDuplicated(signupForm.getNickname()))
            throw new SignupNicknameDuplicateException("중복된 닉네임입니다.");
    }

    public ResponseEntity<Message> isLoginSuccess(LoginForm loginForm, HttpSession session) {
        try{
            User user = userRepository.findUserByEmail(loginForm.getEmail());
            if(Objects.equals(user.getPassword(), loginForm.getPassword())){
                // 성공했을 경우 세션에 추가
                session.setAttribute("user", user);
                return Message.make200Response("Login Success");
            }
            return Message.make400Response("ERROR: Password not match");
        } catch (LoginEmailNotExistException emailNE){
            // 등록된 이메일 없는 경우 예외 처리
            logger.debug(emailNE.getMessage());
            return Message.make400Response("ERROR: Email not exist");
        }
    }

    public static boolean isLogin(HttpServletRequest httpServletRequest){
        return httpServletRequest.getSession(false) != null;
    }



}
