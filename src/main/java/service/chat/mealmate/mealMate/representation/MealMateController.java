package service.chat.mealmate.mealMate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.chat.dto.ChatMessageType;
import service.chat.mealmate.chat.service.RedisChatPublisherService;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;
import service.chat.mealmate.mealMate.domain.vote.VoterStatus;
import service.chat.mealmate.mealMate.dto.*;
import service.chat.mealmate.mealMate.repository.FeedbackHistoryRepository;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.mealMate.repository.VoteRepository;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
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
    private final RedisChatPublisherService redisChatPublisherService;

    private final FeedbackHistoryRepository feedbackHistoryRepository;
    @GetMapping
    @ResponseBody
    public void test() {
        System.out.println("dto = ");
    }
    @GetMapping("/vote/chatPeriod/list/{chatRoomId}")
    @ResponseBody
    public List<VoteChatPeriodChangeDto> voteChatPeriodList(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        voteRepository.findActivatedChatPeriodChangeVote(chatRoomId, VoterStatus.AGREE, VoterStatus.DISAGREE);
        return null;
    }
    @GetMapping("/vote/chatPeriod/list/all/{chatRoomId}")
    @ResponseBody
    public List<VoteRepository.VoteChatPeriodChangeDtoInterface> voteChatPeriodListAll(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        List<VoteRepository.VoteChatPeriodChangeDtoInterface> allChatPeriodChangeVote = voteRepository.findAllChatPeriodChangeVote(chatRoomId, VoterStatus.AGREE, VoterStatus.DISAGREE, List.of(VoteSubject.ADD_CHAT_PERIOD, VoteSubject.UPDATE_CHAT_PERIOD, VoteSubject.DELETE_CHAT_PERIOD));
        return allChatPeriodChangeVote;
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

    @GetMapping("/vote/lock/list/all/{chatRoomId}")
    @ResponseBody
    public List<VoteChatLockDto> voteLockListAll(@PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        // 연관관계 검증
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        return voteRepository.findAllLockChangeVote(mealMate.getChatRoom(), VoteSubject.LOCK);
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
    public void voting(@RequestBody() VotingDto dto, @PathVariable String chatRoomId) {
        int agreeCount = 0;
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        mealmateService.voting(memberId, chatRoomId, dto);
    }
    @GetMapping("/voting/status/{chatRoomId}/{voteId}")
    @ResponseBody
    public VotingStatusDto votingStatus(@PathVariable String chatRoomId, @PathVariable Long voteId) {
        return mealmateService.votingStatus(voteId, chatRoomId);
    }
    @GetMapping("/voting/status/activated/{chatRoomId}")
    @ResponseBody
    public List<VotingStatusDto> votingStatusActivated(@PathVariable String chatRoomId) {
        return mealmateService.votingStatusActivated(chatRoomId);
    }
    @PostMapping("/addPeriod/{roomId}/{voteId}")
    @ResponseBody
    public void addChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @RequestBody ChatPeriodDto chatPeriodDto) {
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        System.out.println("chatPeriodDto = " + chatPeriodDto);
        mealmateService.addChatPeriod(roomId, voteId, chatPeriodDto);
    }

    @PostMapping ("/deletePeriod/{roomId}/{voteId}/{chatPeriodId}")
    public void deleteChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @PathVariable("chatPeriodId") Long chatPeriodId) {
        //
        mealmateService.deleteChatPeriod(roomId, voteId, chatPeriodId);
    }

    @PostMapping("/updatePeriod/{rooId}/{voteId}")
    public void updateChatPeriod(@PathVariable String rooId, @PathVariable Long voteId, @RequestBody ChatPeriodDto chatPeriodDto) {
        mealmateService.updateChatPeriod(rooId, voteId, chatPeriodDto);
    }

    @PostMapping("/updateTitle/{chatRoomId}/{voteId}/{newTitle}")
    @ResponseBody
    public String updateChatRoomTitle(@PathVariable String chatRoomId, @PathVariable Long voteId, @PathVariable String newTitle) {
        return mealmateService.updateChatRoomTitle(chatRoomId, voteId, newTitle);
    }

    @PostMapping("/lock/{chatRoomId}/{voteId}")
    public void lockChatRoom(@PathVariable String chatRoomId, @PathVariable Long voteId) {
        mealmateService.lockChatRoom(chatRoomId, voteId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String message = "채팅방이 잠겼음을 알립니다.";
        String loginId = principal.getUsername();
        Long mealMateId = principal.getMealMateId();
        String chatRoomId1 = principal.getChatRoomId();
        redisChatPublisherService.convertAndSend(message, loginId, chatRoomId1, mealMateId, ChatMessageType.LOCK);
    }
    @PostMapping("/feedback/one/{roomId}")
    @ResponseBody
    public void addFeedbackOne(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody FeedbackDto feedbackDto) {
        // roomId를 pathVaraiable로 넣지 말고, jwt에서 payload로 넣어 둔 값을 가져오는 식으로 하자
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        Long senderMealmateId = principal.getMealMateId();
        Long chatPeriodId = principal.getChatPeriodId();
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.feedback(senderMealmateId, feedbackDto.getReceiverMealMateId(), roomId, chatPeriodId, feedbackDto);
    }

    @GetMapping("/chatroom/feedback/current/list")
    @ResponseBody
    public List<FeedbackMealMateDto> feedbackMealMateList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        Long chatPeriodId = principal.getChatPeriodId();
        String chatRoomId = principal.getChatRoomId();
        return feedbackHistoryRepository.findFeedbackAbleMealMateListAtCurrent(principal.getMealMateId(), chatPeriodId, chatRoomId);
    }
    @GetMapping("/list")
    public String findMealMates(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MealMate> mealMateList = mealmateService.getMealMates(name);
        model.addAttribute("mealmateList", mealMateList);
        return "mealmate/mealmateList";
    }
    @GetMapping("/feedback/history")
    public String findReceivedFeedbackHistories(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        List<FeedbackHistoryDto> feedbackList = feedbackHistoryRepository.findFeedbackHistoriesBy(principal.getMemberId());
        model.addAttribute("feedbackList", feedbackList);
        return "mealmate/feedbackList";
    }

    @GetMapping("/chatPeriod/list")
    @ResponseBody
    public List<ChatPeriodDto> findChatPeriod(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String chatRoomId = principal.getChatRoomId();
        List<ChatPeriod> chatPeriodList = mealmateService.getChatPeriods(chatRoomId);
        return ChatPeriodDto.entityToDtoList(chatPeriodList);
    }
}
