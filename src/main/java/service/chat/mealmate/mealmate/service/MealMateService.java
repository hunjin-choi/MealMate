package service.chat.mealmate.mealmate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealmate.domain.*;
import service.chat.mealmate.mealmate.domain.vote.Vote;
import service.chat.mealmate.mealmate.domain.vote.VotingMethodStrategy;
import service.chat.mealmate.mealmate.dto.ChatPeriodDto;
import service.chat.mealmate.mealmate.dto.CreateVoteDto;
import service.chat.mealmate.mealmate.dto.FeedbackDto;
import service.chat.mealmate.mealmate.repository.*;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class MealMateService {
    private final VotingMethodStrategy votingMethodStrategy;
    private final ChatPeriodStrategy chatPeriodStrategy;
    private final MealMateRepository mealMateRepository;
    private final MemberRepository memberRepository;
    private final FeedbackHistoryRepository feedbackHistoryRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    private final ChatPeriodRepository chatPeriodRepository;
    private final VoteRepository voteRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    protected void voteComplete(ChatRoom chatRoom, Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new RuntimeException(""));
        Long totalMember = mealMateRepository.countByChatRoomAndAndLeavedAtIsNotNull(chatRoom);
        vote.complete(totalMember, votingMethodStrategy);
    }
    public void feedback(String senderId, String receiverName, FeedbackDto feedbackDto, String chatRoomId, Long chatPeriodId) {
        LocalDateTime now = LocalDateTime.now();
        Integer feedbackMileage = feedbackDto.getMileage();
        String feedbackMention = feedbackDto.getFeedbackMention();
        MealMate giver = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(senderId, chatRoomId).orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다"));
        Member receiveMember = memberRepository.findByName(receiverName).orElseThrow(() -> new RuntimeException("멤버가 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        MealMate receiver = mealMateRepository.findByMemberAndChatRoomAndLeavedAtIsNull(receiveMember, chatRoom).orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다."));
        ChatPeriod chatPeriod = chatPeriodRepository.findById(chatPeriodId).orElseThrow(() -> new RuntimeException(""));

        FeedbackHistory feedbackHistory = FeedbackHistory.of(feedbackMention, giver, receiver, chatPeriod, now, feedbackMileage);
        feedbackHistoryRepository.save(feedbackHistory);

        MileageHistory latestMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiveMember);
        MileageHistory newMileageHistory = latestMileageHistory.createHistory(feedbackHistory.getFeedbackMileage(), MileageChangeReason.FEEDBACK, feedbackHistory, now);
        mileageHistoryRepository.save(newMileageHistory);
    }
    public void addChatPeriod(String chatRoomId, Long voteId, ChatPeriodDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        voteComplete(chatRoom, voteId);

        boolean immediately = chatPeriodStrategy.canAddImmediately(LocalTime.now(), dto);
        chatRoom.addChatPeriod(dto.getStartHour(), dto.getStartMinute(), dto.getEndHour(), dto.getEndMinute(), immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }

    public void deleteChatPeriod(String chatRoomId, Long voteId, Long chatPeriodId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        voteComplete(chatRoom, voteId);

        ChatPeriod chatPeriod = chatRoom.findMatchedChatPeriod(chatPeriodId);
        boolean immediately = chatPeriodStrategy.canDeleteImmediately(LocalTime.now(), chatPeriod);
        chatRoom.deleteChatPeriod(chatPeriodId, immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }

    public void updateChatPeriod(String chatRoomId, Long voteId, ChatPeriodDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        voteComplete(chatRoom, voteId);

        Long chatPeriodId = dto.getChatPeriodId();
        ChatPeriod chatPeriod = chatRoom.findMatchedChatPeriod(chatPeriodId);
        boolean immediately = chatPeriodStrategy.canUpdateImmediately(LocalTime.now(), chatPeriod);
        chatRoom.updateChatPeriod(chatPeriodId, dto.getStartHour(), dto.getStartMinute(), dto.getStartHour(), dto.getEndMinute(), immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }

    public void createVoteAndVoting(CreateVoteDto createVoteDto) {
        MealMate creator = this.mealMateRepository.findById(createVoteDto.getCreatorId()).orElseThrow(() -> new RuntimeException(""));
        creator.createVoteAndVoting(createVoteDto.getTitle(), createVoteDto.getContent(), createVoteDto.getVotingMethodType(), createVoteDto.getVotingDto().voterStatus);
    }

    public void saveChatMessage(String message, String chatRoomId, String giverId) {
        MealMate mealMate = mealMateRepository.findByChatRoomIdAndGiverId(chatRoomId, giverId).orElse(null);
        ChatMessage chatMessage = mealMate.addChatMessage(message);
        // cascade 하면 될텐데
        chatMessageRepository.save(chatMessage);
        // DB상에 찍힌 시간을 반환하여 프론트에서 메시지 송신 시간을 표시할 수 있게 구현
    }

    public List<ChatMessage> getChatMessagesPageable(String chatRoomId, String giverId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<ChatMessage> chatMessagePageable = mealMateRepository.findChatMessagePageable(chatRoomId, giverId, pageRequest);
        List<ChatMessage> content = chatMessagePageable.getContent();
        return content;
    }

    public List<MealMate> getMealMates(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        return mealMateRepository.findByChatRoomAndAndLeavedAtIsNull(chatRoom);
    }
    public List<FeedbackHistory> getReceivedFeedbackHistories(Long mealmateId) {
        MealMate receiver = mealMateRepository.findById(mealmateId).orElseThrow(() -> new RuntimeException(""));
        return feedbackHistoryRepository.findByReceiverAndOrderByFeedbackDate(receiver);
    }

    public List<ChatPeriod> getChatPeriods(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        // 자동으로 deleted=false 조건 붙이겠지?
        return chatRoom.getChatPeriodList();
    }
}
