package service.chat.mealmate.mealMate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealMate.domain.*;
import service.chat.mealmate.mealMate.domain.vote.*;
import service.chat.mealmate.mealMate.domain.vote.validate.VoteValidateDto;
import service.chat.mealmate.mealMate.dto.*;
import service.chat.mealmate.mealMate.repository.*;
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
    private final ChatRoomStatusChangePolicy chatRoomStatusChangePolicy;
    private final MealMateRepository mealMateRepository;
    private final MemberRepository memberRepository;
    private final FeedbackHistoryRepository feedbackHistoryRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    private final ChatPeriodRepository chatPeriodRepository;
    private final VoteRepository voteRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final VotePaperRepository votePaperRepository;
    private final CreateVoteStrategy createVoteStrategy;
    private final ChatRoomJoinPolicy chatRoomJoinPolicy;
    private final ChatRoomEnterPolicy chatRoomEnterPolicy;
    protected void voteComplete(String chatRoomId, Long voteId, VoteValidateDto dto) {
        Vote vote = voteRepository.findOneWithChatRoom(voteId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("적절한 투표 대상을 찾을 수 없습니다"));
        Long totalMember = mealMateRepository.countAllActivatedBy(chatRoomId);
        vote.complete(totalMember, votingMethodStrategy, dto);
    }

    public MealMate join(Member member, ChatRoom chatRoom) {
        return join(member.getMemberId(), chatRoom.getChatRoomId());
    }
    public MealMate join(Long memberId, String chatRoomId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버가 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
        return chatRoomJoinPolicy.canJoinImmediately(member, chatRoom);
    }

    public MealMate createAndJoin(Long memberId, String chatRoomId, String chatRoomTitle) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버가 없습니다."));
        ChatRoom chatRoom = new ChatRoom(chatRoomId, chatRoomTitle, LocalDateTime.now(), (short) 3);
        chatRoomRepository.save(chatRoom);
        MealMate mealMate = join(member, chatRoom);
        return mealMate;
    }
    public MealMate enter(Long memberId, String chatRoomId) {
        return chatRoomEnterPolicy.canEnterImmediately(memberId, chatRoomId);
    }
    public void feedback(Long senderId, String chatRoomId, Long chatPeriodId, FeedbackDto feedbackDto) {
        LocalDateTime now = LocalDateTime.now();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
        MealMate giver = mealMateRepository.findOneActivatedCompositeBy(senderId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다"));
        // 아래 두 쿼리는 "연관관계 검증" 과정인데, 각각 다른 방식을 사용
        MealMate receiver = mealMateRepository.findOneActivatedWithChatRoomByName(feedbackDto.receiverName, chatRoomId)
                .orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다."));
        ChatPeriod chatPeriod = chatRoom.findMatchedChatPeriod(chatPeriodId);

        FeedbackHistory feedbackHistory = new FeedbackHistory(feedbackDto.feedbackMention, giver, receiver, chatPeriod, now, feedbackDto.mileage);
        feedbackHistoryRepository.save(feedbackHistory);

        MileageHistory latestMileageHistory = mileageHistoryRepository.findLatestBy(feedbackDto.receiverName)
                .orElseThrow(() -> new RuntimeException("적절한 마일리지 히스토리를 찾을 수 없습니다."));
        MileageHistory newMileageHistory = latestMileageHistory.createHistory(feedbackHistory.getFeedbackMileage(), MileageChangeReason.FEEDBACK, feedbackHistory, now);
        mileageHistoryRepository.save(newMileageHistory);
    }

    public void addChatPeriod(String chatRoomId, Long voteId, ChatPeriodDto dto) {
        voteComplete(chatRoomId, voteId, VoteValidateDto.of(dto));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        boolean immediately = chatRoomStatusChangePolicy.canAddImmediately(LocalTime.now(), dto);
        chatRoom.addChatPeriod(dto.getStartHour(), dto.getStartMinute(), dto.getEndHour(), dto.getEndMinute(), immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }

    public void deleteChatPeriod(String chatRoomId, Long voteId, Long chatPeriodId) {
        voteComplete(chatRoomId, voteId, null);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        ChatPeriod chatPeriod = chatRoom.findMatchedChatPeriod(chatPeriodId);
        boolean immediately = chatRoomStatusChangePolicy.canDeleteImmediately(LocalTime.now(), chatPeriod);
        chatRoom.deleteChatPeriod(chatPeriodId, immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }

    public void updateChatPeriod(String chatRoomId, Long voteId, ChatPeriodDto dto) {
        voteComplete(chatRoomId, voteId, VoteValidateDto.of(dto));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        Long chatPeriodId = dto.getChatPeriodId();
        ChatPeriod chatPeriod = chatRoom.findMatchedChatPeriod(chatPeriodId);
        boolean immediately = chatRoomStatusChangePolicy.canUpdateImmediately(LocalTime.now(), chatPeriod);
        chatRoom.updateChatPeriod(chatPeriodId, dto.getStartHour(), dto.getStartMinute(), dto.getStartHour(), dto.getEndMinute(), immediately);
        // 즉시/예약 여부를 프론트에서 알 수 있게끔 처리
    }
    public void updateChatRoomTitle(String chatRoomId, Long voteId, String newTitle) {
        voteComplete(chatRoomId, voteId, VoteValidateDto.of(newTitle));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 업습니다"));
        chatRoom.updateChatRoomTitle(newTitle);
    }

    public void lockChatRoom(String chatRoomId, Long voteId) {
        voteComplete(chatRoomId, voteId, VoteValidateDto.of(true)); // null을 넣는 건 좀 아닌거 같은데;;
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        chatRoom.lock(LocalDateTime.now());
    }
    public void voteList(Long memberId, String chatRoomId) {
        MealMate mealMate = mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("적절한 사용자가 아닙니다."));
        ChatRoom chatRoom = mealMate.getChatRoom();

    }
    public void createVoteAndVoting(Long creatorId, String chatRoomId, CreateVoteAndVotingDto dto) {
        // 동등성 가지는 투표를 또 생성하려고 하면 예외를 발생시키자
        MealMate mealMate = this.mealMateRepository.findOneActivatedCompositeBy(creatorId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("밀 메이트를 찾을 수 없습니다"));
        ChatRoom chatRoom = this.chatRoomRepository.findOneWithMealMate(mealMate.getMealMateId(), chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        Vote vote = createVoteStrategy.createVote(dto, chatRoom);
        // VotePaper votePaper = mealMate.createVoteAndVoting(dto.getVoteTitle(), dto.getContent(), dto.getVoteMethodType(), dto.getVotingDto().getVoterStatus(), chatRoom);
        boolean isCreator = true;
        VotePaper votePaper = mealMate.voting(vote, dto.getVoting().getVoterStatus(), isCreator);
        // cascade 하면 될텐데
        this.voteRepository.save(vote);
        this.votePaperRepository.save(votePaper);
    }

    public void voting(Long memberId, String chatRoomId, VotingDto dto) {
        Vote vote = voteRepository.findOneWithChatRoom(dto.getVoteId(), chatRoomId)
                .orElseThrow(() -> new RuntimeException("적절한 투표 대상을 찾을 수 없습니다"));
        MealMate mealMate = this.mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("밀 메이트를 찾을 수 없습니다"));
        boolean isCreator = false;
        VotePaper votePaper = mealMate.voting(vote, dto.getVoterStatus(), isCreator);
        // 만약 기존에 했던 투표를 취소하고 싶다면? 아직 구현X
        // 만약 기존에 했던 투표를 바꾸고 싶다면? 아직 구현X
        // cascade 하면 될텐데
        this.votePaperRepository.save(votePaper);
    }

    public VotingStatusDto votingStatus(Long voteId, String chatRoomId) {
        // 유저와 연관관계 검증을 할 필요는 없을 듯
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("적절한 튜표를 찾을 수 없습니다."));
        List<VotePaper> votePaperList = vote.getVotePaperList();
        Long agree = votePaperList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.AGREE).count();
        Long disagree = votePaperList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.DISAGREE).count();
        Long personnel = mealMateRepository.countAllActiveMealMateBy(chatRoomId);
        return new VotingStatusDto(personnel, agree, disagree);
    }
    public void saveChatMessage(String message, Long mealMateId) {
        MealMate mealMate = mealMateRepository.findById(mealMateId).orElse(null);
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
        return mealMateRepository.findAllActiveMealMateBy(chatRoomId);
    }

    public List<FeedbackHistory> getReceivedFeedbackHistories(Long mealmateId) {
        MealMate receiver = mealMateRepository.findById(mealmateId).orElseThrow(() -> new RuntimeException(""));
        return feedbackHistoryRepository.findByReceiverOrderByFeedbackDateDesc(receiver);
    }

    public List<ChatPeriod> getChatPeriods(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        // 자동으로 deleted=false 조건 붙이겠지?
        return chatRoom.getChatPeriodList();
    }
}
