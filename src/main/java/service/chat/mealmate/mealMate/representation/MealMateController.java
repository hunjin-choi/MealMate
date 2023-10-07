package service.chat.mealmate.mealMate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.mealMate.dto.*;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.mealMate.repository.VoteRepository;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.domain.FeedbackHistory;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mealmate")
@RequiredArgsConstructor
@ControllerAdvice
public class MealMateController {
    private final MealMateService mealmateService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MealMateRepository mealMateRepository;
    private final VoteRepository voteRepository;

    @GetMapping("/vote/chatPeriod/list/{chatRoomId}")
    @ResponseBody
    public List<VoteChatPeriodChangeDto> voteChatPeriodList(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        return voteRepository.findActivatedChatPeriodChangeVote(mealMate.getChatRoom());
    }
    @GetMapping("/vote/chatPeriod/list/all/{chatRoomId}")
    @ResponseBody
    public List<VoteChatPeriodChangeDto> voteChatPeriodListAll(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        return voteRepository.findAllChatPeriodChangeVote(mealMate.getChatRoom());
    }

    @GetMapping("/vote/title/list/{chatRoomId}")
    @ResponseBody
    public List<VoteTitleChangeDto> voteTitleList(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        return voteRepository.findActivatedTitleChangeVote(mealMate.getChatRoom());
    }
    @GetMapping("/vote/title/list/all/{chatRoomId}")
    @ResponseBody
    public List<VoteTitleChangeDto> voteTitleListAll(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        return voteRepository.findAllTitleChangeVote(mealMate.getChatRoom());
    }
    @PostMapping("/create/vote/voting/{chatRoomId}")
    @ResponseBody
    public void createVoteAndVoting(@RequestBody() CreateVoteAndVotingDto dto, @PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        mealmateService.createVoteAndVoting(memberId, chatRoomId, dto);
        // /chat/vote/list API가 있어서 굳이 voteId 반환 안해도 될 거 같음
        // int voteId = 0;
        // return voteId;
    }
    @PostMapping("/voting/{chatRoomId}")
    @ResponseBody
    public VotingStatusDto voting(@RequestBody() VotingDto dto, @PathVariable String chatRoomId) {
        int agreeCount = 0;
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        mealmateService.voting(memberId, chatRoomId, dto);
        return mealmateService.votingStatus(dto.getVoteId(), chatRoomId);
    }
    @GetMapping("/voting/status/{chatRoomId}/{voteId}")
    @ResponseBody
    public VotingStatusDto votingStatus(@PathVariable String chatRoomId, @PathVariable Long voteId) {
        return mealmateService.votingStatus(voteId, chatRoomId);
    }
    @PostMapping("/addPeriod/{roomId}")
    @ResponseBody
    public void addChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @RequestBody ChatPeriodDto chatPeriodDto) throws IOException {
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.addChatPeriod(roomId, voteId, chatPeriodDto);
    }

    @PostMapping ("/deletePeriod/{roomId}/{voteId}/{chatPeriodId}")
    public void deleteChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @PathVariable("chatPeriodId") Long chatPeriodId) throws IOException {
        //
        mealmateService.deleteChatPeriod(roomId, voteId, chatPeriodId);
    }

    @PostMapping("/updatePeriod/{rooId}/{voteId}")
    public void updateChatPeriod(@PathVariable String rooId, @PathVariable Long voteId, @RequestBody ChatPeriodDto chatPeriodDto) {
        mealmateService.updateChatPeriod(rooId, voteId, chatPeriodDto);
    }

    @PostMapping("/lock")
    public void lockChatRoom() {

    }
    @PostMapping("/feedback/one/{roomId}")
    @ResponseBody
    public void addFeedbackOne(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody FeedbackDto feedbackDto) throws IOException {
        // roomId를 pathVaraiable로 넣지 말고, jwt에서 payload로 넣어 둔 값을 가져오는 식으로 하자
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderId = auth.getName();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        String roomIdFromJwt = jwtTokenProvider.getChatRoomIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        if (!roomIdFromJwt.equals(roomId)) throw new RuntimeException("");
        Long chatPeriodId = jwtTokenProvider.getChatPeriodIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.feedback(principal.getMemberId(), roomId, chatPeriodId, feedbackDto);
    }

    @PostMapping("/feedback/many/{roomId}")
    @ResponseBody
    public void addFeedbackMany(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody FeedbackDto feedbackDto) throws IOException {
        // roomId를 pathVaraiable로 넣지 말고, jwt에서 payload로 넣어 둔 값을 가져오는 식으로 하자
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderId = auth.getName();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        String roomIdFromJwt = jwtTokenProvider.getChatRoomIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        if (!roomIdFromJwt.equals(roomId)) throw new RuntimeException("");
        Long chatPeriodId = jwtTokenProvider.getChatPeriodIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.feedback(principal.getMemberId(), roomId, chatPeriodId, feedbackDto);
    }

    @GetMapping("/list")
    public String findMealMates(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MealMate> mealMateList = mealmateService.getMealMates(name);
        model.addAttribute("mealmateList", mealMateList);
        return "mealmate/mealmateList";
    }
    @GetMapping("/feedback/history/{mealmateId}")
    public String findReceivedFeedbackHistories(Model model, @PathVariable("mealmateId") Long mealmateId) {
        List<FeedbackHistory> feedbackList = mealmateService.getReceivedFeedbackHistories(mealmateId);
        model.addAttribute("feedbackList", feedbackList);
        return "mealmate/feedbackList";
    }

    @GetMapping("/chatPeriod/list")
    @ResponseBody
    public List<ChatPeriodDto> findChatPeriod(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<ChatPeriod> chatPeriodList = mealmateService.getChatPeriods(name);
        return ChatPeriodDto.entityToDtoList(chatPeriodList);
//        model.addAttribute("chatPeriodList", chatPeriodList);
//        return "mealmate/chatPeriodList";
    }
}
