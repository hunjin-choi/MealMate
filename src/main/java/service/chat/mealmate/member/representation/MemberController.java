package service.chat.mealmate.member.representation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import service.chat.mealmate.member.dto.MileageDto;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.member.service.MemberService;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
//    @GetMapping("/signUp")
//    public void signUp(String userName) {
//        this.memberService.signUp(userName);
//    }

    @GetMapping("/mileage/history/json")
    @ResponseBody
    public List<MileageDto> findMileageHistoryJson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        return memberService.dynamicTest(name);

    }

    @GetMapping("/mileage/history")
    public String findMileageHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MileageHistory> mileageHistoryList = memberService.findAllMileageHistory(name);
        model.addAttribute("mileageHistoryList", mileageHistoryList);
        return "member/mileageHistoryList";
    }
}
