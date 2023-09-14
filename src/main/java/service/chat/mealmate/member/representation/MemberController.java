package service.chat.mealmate.member.representation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.member.dto.MileageDto;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.member.service.MemberService;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.security.domain.SecurityMember;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@ControllerAdvice
public class MemberController {
    private final MemberService memberService;
//    @GetMapping("/signUp")
//    public void signUp(String userName) {
//        this.memberService.signUp(userName);
//    }

    @GetMapping("/change/nickname/{nickname}")
    public void changeNickname(@PathVariable("nickname") String nickname) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        memberService.changeNickname(principal.getMemberId(), nickname);
    }
    @GetMapping("/mileage/history/json")
    @ResponseBody
    public List<MileageDto> findMileageHistoryJson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        return memberService.dynamicTest(principal.getMemberId());
    }

    @GetMapping("/mileage/history")
    public String findMileageHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        List<MileageHistory> mileageHistoryList = memberService.findAllMileageHistory(principal.getMemberId());
        model.addAttribute("mileageHistoryList", mileageHistoryList);
        return "member/mileageHistoryList";
    }

}
