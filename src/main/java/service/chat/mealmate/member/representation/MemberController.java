package service.chat.mealmate.member.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.chat.mealmate.member.service.MemberService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

//    @GetMapping("/signUp")
//    public void signUp(String userName) {
//        this.memberService.signUp(userName);
//    }
}
