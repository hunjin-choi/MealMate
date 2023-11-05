<!doctype html>
<html lang="en">
<head>
    <title>Websocket ChatRoom</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.2/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="container" id="app" v-cloak>
    <div class="row justify-content-between align-items-center">
        <div class="col-3">
            <button class="btn btn-warning" type="button" @click="redirectToChatRoom">대기방으로 이동</button>
        </div>
        <div class="col-6 text-center">
            <h3>{{roomName}}</h3>
        </div>
        <div class="col-3 text-right">
            <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>
        </div>
    </div>

    <div class="input-group mb-3">
        <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage('TALK')" placeholder="Enter chat message here...">
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">채팅 보내기</button>
            <button class="btn btn-info" type="button" @click="fetchFeedbackMealMateList(); feedbackModal = feedbackModal ^ true">피드백 보내기</button>
            <button class="btn btn-third" type="button" @click="fetchVoteList(); voteListFlag = voteListFlag ^ true;" >투표 리스트</button>

        </div>
    </div>

    <div class="list-group" v-if="voteListFlag">
        <button type="button" @click="fetchVoteList(); chatPeriodVoteFlag=chatPeriodVoteFlag ^ true;">채팅 시간대 변경 투표 리스트 보기</button>
        <div v-if="chatPeriodVoteFlag">
            <li class="" v-for="(chatPeriodVote, index) in allChatPeriodVoteList">
                {{chatPeriodVote.voteId}}
                {{chatPeriodVote.voteTitle}}
                {{chatPeriodVote.content}}
                {{chatPeriodVote.voteMethodType}}
                {{chatPeriodVote.voteSubject}}
                {{chatPeriodVote.createdAt}}
                {{chatPeriodVote.startTime}}
                {{chatPeriodVote.endTime}}
                찬성: {{chatPeriodVote.agree}}
                반대: {{chatPeriodVote.disagree}}
                <span>찬성: ~~~~~</span><span>반대: ~~~~~</span>
                <button @click="alert('test')">press me</button>
                <button type="button" @click="votingMethod(chatPeriodVote, 'agree')">agree</button>
                <button type="button" @click="votingMethod(chatPeriodVote, 'disAgree')">disAgree</button>
                <button @click="chatPeriodVoteComplete(chatPeriodVote)">투표 결과 실제로 반영하기</button>
            </li>
        </div>
        <button type="button" @click="fetchVoteList(); titleChangeVoteFlag=titleChangeVoteFlag ^ true;">채팅방 제목 투표 리스트 보기</button>
        <div v-if="titleChangeVoteFlag">
            <li class="" v-for="(titleChangeVote, index) in allTitleChangeVoteList">
                <span>
                    {{titleChangeVote.voteId}}
                    {{titleChangeVote.voteTitle}}
                    {{titleChangeVote.content}}
                    {{titleChangeVote.voteMethodType}}
                    {{titleChangeVote.voteSubject}}
                    {{titleChangeVote.createdAt}}
                    {{titleChangeVote.completedDate}}
                    {{titleChangeVote.chatRoomTitle}}
                    <span>찬성: ~~~~~</span><span>반대: ~~~~~</span>
                    <button @click="alert('test')">press me</button>
                    <button type="button" @click="votingMethod(titleChangeVote, 'agree')">agree</button>
                    <button type="button" @click="votingMethod(titleChangeVote, 'disAgree')">disAgree</button>
                    <button @click="chatTitleVoteComplete(titleChangeVote.voteId, titleChangeVote.chatRoomTitle)">투표 결과 실제로 반영하기</button>
                </span>
            </li>
<#--        <button type="button" @click="createTitleVoteFlag = createTitleVoteFlag ^ true">create title vote</button>-->
<#--        <button type="button" @click="lockVoteFlag = lockVoteFlag ^ true">lock chatRoom vote</button>-->
        </div>
        <button type="button" @click="fetchVoteList(); lockVoteFlag=lockVoteFlag ^ true;">채팅방 잠금 투표 리스트 보기</button>
        <div v-if="lockVoteFlag">
            <li class="" v-for="(lockChangeVote, index) in allLockChangeVoteList">
                <span>
                    {{lockChangeVote.voteId}}
                    {{lockChangeVote.voteTitle}}
                    {{lockChangeVote.content}}
                    {{lockChangeVote.voteMethodType}}
                    {{lockChangeVote.voteSubject}}
                    {{lockChangeVote.createdAt}}
                    {{lockChangeVote.completedDate}}
                    <span>찬성: ~~~~~</span><span>반대: ~~~~~</span>
                    <button @click="alert('test')">press me</button>
                    <button type="button" @click="votingMethod(titleChangeVote, 'agree')">agree</button>
                    <button type="button" @click="votingMethod(titleChangeVote, 'disAgree')">disAgree</button>
                    <button>투표 결과 실제로 반영하기</button>
                    <button @click="chatRoomLockVoteComplete(lockChangeVote.voteId, lockChangeVote.chatRoomTitle)">투표 결과 실제로 반영하기</button>
                </span>
            </li>
            <#--        <button type="button" @click="createTitleVoteFlag = createTitleVoteFlag ^ true">create title vote</button>-->
            <#--        <button type="button" @click="lockVoteFlag = lockVoteFlag ^ true">lock chatRoom vote</button>-->
        </div>
        <div>
            --------------------------------------------
        </div>
        <button type="button" @click="createChatPeriodVoteFlag = createChatPeriodVoteFlag ^ true">create vote</button>
        <div v-if="createChatPeriodVoteFlag">
            <div>
                <div>투표 주제: {{ voteSubject }}
                    <select v-model="voteSubject">
                        <option>ADD_CHAT_PERIOD</option>
                        <option>DELETE_CHAT_PERIOD</option>
                        <option>UPDATE_CHAT_PERIOD</option>
                        <option>UPDATE_CHAT_ROOM_TITLE</option>
                        <option>LOCK</option>
                    </select>
                </div>
                <label>투표 제목: </label>
                <input type="text" v-model="voteTitle" minlength="1" maxlength="30"><br/>
                <label>제안 내용 or 이유: </label>
                <input type="text" v-model="contents" minlength="0" maxlength="50"><br/>
                <div>투표 방식: {{ voteMethodType }}
                    <select v-model="voteMethodType">
                        <option>MAJORITY</option>
                        <option>UNANIMOUS</option>
                        <option>NONE</option>
                    </select>
                </div>
                <div>찬성 혹은 반대를 선택:
                    <select v-model="voterStatus">
                        <option disabled value="">다음 중 하나를 선택하세요</option>
                        <option>agree</option>
                        <option>disAgree</option>
                    </select>
                </div>
                <div v-if="voteSubject != null && voteSubject == 'ADD_CHAT_PERIOD'">
                <label>시작 시각(0 ~ 23): </label>
                <input type="number" v-model="startHour" min="0" max="23"><br/>
                <label>시작 분(0 ~ 59): </label>
                <input type="number" v-model="startMinute" min="0" max="59"><br/>
                <label>종료 시각(0 ~ 23): </label>
                <input type="number" v-model="endHour" min="0" max="23"><br/>
                <label>종료 분(0 ~ 59): </label>
                <input type="number" v-model="endMinute" min="0" max="59"><br/>
            </div>
            <div v-if="voteSubject != null && voteSubject == 'UPDATE_CHAT_PERIOD'">
                <button @click="fetchChatPeriodList()">refresh</button>
                <table>
                        <tr>
                            <th>ChatPeriodId</th>
                            <th>startHour</th>
                            <th>startMinute</th>
                            <th>endHour</th>
                            <th>endMinute</th>
                            <th>latestFeedbackDate</th>
                        </tr>
                        <tr v-for="chatPeriod in chatPeriodList">
                            <td>{{chatPeriod.chatPeriodId}}</td>
                            <td>{{chatPeriod.startHour}}</td>
                            <td>{{chatPeriod.startMinute}}</td>
                            <td>{{chatPeriod.endHour}}</td>
                            <td>{{chatPeriod.endMinute}}</td>
                            <td>{{chatPeriod.latestFeedbackDate}}</td>
                            <td><button @click="chatPeriodId = chatPeriod.chatPeriodId; updateChatPeriodForm = true;">Update Form</button></td>
                        </tr>
                    </table>
                <div v-if="updateChatPeriodForm">
                    <label>시작 시각(0 ~ 23): </label>
                    <input type="number" v-model="startHour" min="0" max="23"><br/>
                    <label>시작 분(0 ~ 59): </label>
                    <input type="number" v-model="startMinute" min="0" max="59"><br/>
                    <label>종료 시각(0 ~ 23): </label>
                    <input type="number" v-model="endHour" min="0" max="23"><br/>
                    <label>종료 분(0 ~ 59): </label>
                    <input type="number" v-model="endMinute" min="0" max="59"><br/>
                    <button @click="updateChatPeriodForm = false;">OK</button>
                </div>
                </div>
                <div v-if="voteSubject != null && voteSubject == 'DELETE_CHAT_PERIOD'">
                    <button @click="fetchChatPeriodList()">refresh</button>
                    <table>
                        <tr>
                            <th>ChatPeriodId</th>
                            <th>startHour</th>
                            <th>startMinute</th>
                            <th>endHour</th>
                            <th>endMinute</th>
                            <th>latestFeedbackDate</th>
                        </tr>
                        <tr v-for="chatPeriod in chatPeriodList">
                            <td>{{chatPeriod.chatPeriodId}}</td>
                            <td>{{chatPeriod.startHour}}</td>
                            <td>{{chatPeriod.startMinute}}</td>
                            <td>{{chatPeriod.endHour}}</td>
                            <td>{{chatPeriod.endMinute}}</td>
                            <td>{{chatPeriod.latestFeedbackDate}}</td>
                            <td><button @click="chatPeriodId = chatPeriod.chatPeriodId">Delete</button></td>
                        </tr>
                    </table>
                </div>
                <div v-if="voteSubject != null && voteSubject == 'UPDATE_CHAT_ROOM_TITLE'">
                    <label>새로운 채팅방 제목을 입력하세요: </label>
                    <input type="text" v-model="chatRoomTitle" minlength="1" maxlength="30"><br/>
                </div>

                <button @click="createVoteAndVoting()">제출</button>
            </div>
            <button @click="voteId = 0; createChatPeriodVoteFlag = false; fetchChatPeriodList()">Close</button>
        </div>
    </div>

    <!-- Chat Time Period Modal -->
    <div v-if="addChatPeriodFlag" class="modal-container">
        <h4>Chat Time Period</h4>
        <div>
            <label>시작 시각: </label>
            <input type="number" v-model="startHour" min="0" max="23"><br/>
            <label>시작 분: </label>
            <input type="number" v-model="startMinute" min="0" max="59"><br/>
            <label>종료 시각: </label>
            <input type="number" v-model="endHour" min="0" max="23"><br/>
            <label>종료 분: </label>
            <input type="number" v-model="endMinute" min="0" max="59"><br/>
            <button @click="addChatPeriod">제출</button>
        </div>
        <button @click="addChatPeriodFlag = false; fetchChatPeriodList()">Close</button>
    </div>

    <!-- Chat Time Period Delete Modal -->
    <div v-if="deleteChatPeriodModal" class="modal-container">
        <h4>Delete Chat Time Period</h4>
        <table>
            <tr>
                <th>ChatPeriodId</th>
                <th>startHour</th>
                <th>startMinute</th>
                <th>endHour</th>
                <th>endMinute</th>
                <th>latestFeedbackDate</th>
            </tr>
            <tr v-for="chatPeriod in chatPeriodList">
                <td>{{chatPeriod.chatPeriodId}}</td>
                <td>{{chatPeriod.startHour}}</td>
                <td>{{chatPeriod.startMinute}}</td>
                <td>{{chatPeriod.endHour}}</td>
                <td>{{chatPeriod.endMinute}}</td>
                <td>{{chatPeriod.latestFeedbackDate}}</td>
                <td><button @click="deleteChatPeriod(chatPeriod.chatPeriodId); deleteChatPeriodModal = false;">Delete</button></td>
            </tr>
        </table>
        <button @click="deleteChatPeriodModal = false;">Close</button>
    </div>

    <!-- Feedback Modal -->
    <div v-if="feedbackModal" class="modal-container">
        <div v-for="feedback in feedbackMealMateList">
            <span>{{feedback.receiverName}}</span>
            <span>{{feedback.feedbackMention}}</span>
            <span>{{feedback.feedbackMileage}}</span>
            <span>{{feedback.feedbackTime}}</span>
            <span>{{feedback.mealMateId}}</span>
            <span><button @click="feedbackForm=true;">Add Feedback</button></span>
            <div v-if="feedbackForm">
                <label>피드백: </label>
                <input type="text" v-model="feedbackMention" required minlength="5" placeholder="5글자 이상 입력하세요."><br/>
                <label>마일리지: </label>
                <input type="number" v-model="mileage" required min="0" placeholder="0 이상의 정수를 입력하세요."><br/>
                <button @click="submitFeedback(feedback.mealMateId, feedback.receiverName)">제출</button>
                <button @click="cancelFeedback">취소</button>
            </div>
        </div>
    </div>
    <ul class="list-group" v-if="messageFetchFlag">
        <li class="list-group-item d-flex justify-content-between" v-for="message in messages">
        <span>
          {{message.sender}} - {{message.message}}
        </span>
            <span>
          {{message.date}}
        </span>
        </li>
    </ul>

</div>
<!-- JavaScript -->

<script src="/webjars/vue/2.6.14/dist/vue.min.js"></script>
<script src="/webjars/axios/0.21.1/dist/axios.min.js"></script>
<script src="/webjars/sockjs-client/1.5.1/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.4/stomp.min.js"></script>
<script>
    // websocket & stomp initialize
    var sock;// = new SockJS("/ws-stomp");
    var ws;// = Stomp.over(sock);
    var reconnect = 0;

    // vue.js
    var vm = new Vue({
        el: '#app',
        data: {
            roomId: '',
            roomName: '',
            message: '',
            messages: [],
            token: '',
            deleteChatPeriodModal: false,
            chatPeriodList: [],
            readOnlyToken: '',
            readWriteToken: '',
            addChatPeriodFlag: false,
            accessToken : null,
            refreshToken : null,
            feedbackModal: false,
            startHour: null,
            startMinute: null,
            endHour: null,
            endMinute: null,
            feedbackMention: '',
            mileage: null,
            tempValue:null,
            messageFetchFlag: false,

            allChatPeriodVoteList: null,
            allTitleChangeVoteList: null,
            voteListFlag: false,
            chatPeriodVoteFlag: true,
            titleChangeVoteFlag: false,
            createChatPeriodVoteFlag: false,
            createTitleVoteFlag: false,
            lockVoteFlag:  false,

            contents: null,
            voteSubject: null,
            voteTitle: null,
            voteMethodType: null,
            chatRoomTitle : null,
            locking: null,

            voteId: null,
            voterStatus: null,
            chatPeriodId: null,
            updateChatPeriodForm: null,
            allLockChangeVoteList: null,
            lockVoteFlag: null,
            feedbackMealMateList: null,
            feedbackInfo: {},
            feedbackForm: false,
        },
        // 아래가 vm 생성자인가보다;
        async created() {
            console.log("created!")
            this.roomId = localStorage.getItem('wschat.roomId');
            this.roomName = localStorage.getItem('wschat.roomName');
            console.log(this.roomId);
            var _this = this;
            window.addEventListener('beforeunload', function (e) {
                // WebSocket 연결을 닫는다.
                this.roomId = null;
                this.roomName = null;
                _this.token = null;
                _this.readOnlyToken = null;
                _this.readWriteToken = null;
                _this.accessToken = null;
                _this.refreshToken = null;
                vm.$destroy();
                sock.close();
            });

            const response = await axios.get('/chat/user/' + this.roomId);
            console.log("axios complete");
            _this.token = response.data.token;
            _this.readOnlyToken = response.data.readOnlyToken;
            _this.readWriteToken = response.data.readWriteToken;
            ///////////
            sock = new SockJS("/ws-stomp");
            ws = Stomp.over(sock);

            console.log("before connect");
            this.roomId = localStorage.getItem('wschat.roomId');
            this.roomName = localStorage.getItem('wschat.roomName');
            var _this = this;
            axios.get('/chat/user/' + this.roomId).then(response => {
                _this.token = response.data.token;
                ws.connect({"token":_this.token}, function(frame) {
                    ws.subscribe("/sub/chat/room/"+_this.roomId, function(message) {
                        var recv = JSON.parse(message.body);
                        _this.recvMessage(recv);
                    });
                    _this.sendMessage('ENTER');
                }, function(error) {
                    alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
                    location.href="/chat/room";
                });
            });
            const resp = await axios.get('/chat/room/messages/' + _this.roomId);
            console.log(resp);
            _this.messages = resp.data;
            _this.messages.sort(function(a, b) {
                return a.date < b.date ? -1 : a.date > b.date ? 1 : 0;
            });
            console.log(resp.data);
            console.log(_this.messages);
            _this.messageFetchFlag = true;
        },
        beforeDestroy() {
            this.roomId = null;
            this.roomName = null;
            this.token = null;
            this.readOnlyToken = null;
            this.readWriteToken = null;
            // this.message = "퇴장하겠습니다";
            // this.sendMessage('QUIT');
            this.message = '';
            sock.close();
            console.log("beforeDestroy")
        },
        methods: {
            sendMessage: function(type) {
                ws.send("/pub/chat/message/" + this.roomId,
                    {"token":this.token, "readOnlyToken" : this.readOnlyToken, "readWriteToken": this.readWriteToken,
                        "accessToken": this.accessToken, "refreshToken" : this.refreshToken},
                    JSON.stringify({type:type, roomId:this.roomId, message:this.message}), function(response) {
                        // 응답을 처리하는 로직을 작성합니다.
                        console.log("서버로부터의 응답:", response);
                    });

                this.message = '';
            },
            recvMessage: function(recv) {
                // this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message, "date": recv.date})
                this.messages.push({"type":recv.type,"sender":recv.sender,"message":recv.message, "date": recv.date});
            },
            submitFeedback: function(mealMateId, mealMateNickname) {
                if(this.feedbackMention.length < 5 || this.mileage < 0) {
                    alert("피드백은 5글자 이상, 마일리지는 0 이상의 정수를 입력해야 합니다.");
                    return;
                }

                const data = {
                    feedbackMention: this.feedbackMention,
                    mileage: this.mileage,
                    receiverMealMateId: mealMateId,
                    receiverNickname: mealMateNickname,
                };

                axios.post('http://localhost:8080/mealmate/feedback/one/' + this.roomId, data, {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                })
                    .then(response => {
                        this.feedback = '';
                        this.mileage = null;
                        this.feedbackModal = false;
                        alert("Feedback complete");
                    })
                    .catch(error => {
                        console.error(error);
                        alert("피드백 제출에 문제가 발생하였습니다. 다시 시도해주세요.");
                    });
            },
            cancelFeedback: function() {
                this.feedback = '';
                this.mileage = null;
                this.feedbackModal = false;
            },
            addChatPeriod: function () {
                axios.post('http://localhost:8080/mealmate/addPeriod/' + this.roomId, {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                })
            },
            updateChatPeriod: function () {
                axios.post('http://localhost:8080/mealmate/updatePeriod/' + this.roomId + '/' + chatPeriodId, {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                })
            },
            deleteChatPeriod: function(chatPeriodId) {
                axios.post('http://localhost:8080/mealmate/deletePeriod/' + this.roomId + '/' + chatPeriodId, {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                })
                    .then(response => {
                        console.log(response);
                    })
                    .catch(error => {
                        console.error(error);
                    });
            },
            fetchChatPeriodList: function() {
                axios.get('http://localhost:8080/mealmate/chatPeriod/list', {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                }).then(response => {
                        console.log(response);
                        this.chatPeriodList = response.data;
                        this.deleteChatPeriodModal = true;
                }).catch(error => {
                    console.error("chatPeriod 리스트 받아오기 실패");
                    console.error(error);
                });
            },
            redirectToChatRoom: function() {
                window.location.href = "/chat/room";
            },
            fetchVoteList: function () {
                axios.get('http://localhost:8080/mealmate/vote/chatPeriod/list/all/' + this.roomId)
                    .then(response => {
                        this.allChatPeriodVoteList = response.data;
                    }).catch(error => {
                        alert("fetchVoteList fail");
                        console.error(error);
                    });
                axios.get('http://localhost:8080/mealmate/vote/title/list/all/' + this.roomId)
                    .then(response => {
                        this.allTitleChangeVoteList = response.data;
                    }).catch(error => {
                        alert("fetchVoteList fail");
                        console.error(error);
                    });
                // /vote/lock/list/all/{chatRoomId}
                axios.get('http://localhost:8080/mealmate/vote/lock/list/all/' + this.roomId)
                    .then(response => {
                        this.allLockChangeVoteList = response.data;
                    }).catch(error => {
                    alert("fetchVoteList fail");
                    console.error(error);
                });
            },
            createVoteAndVoting : function () {
                axios.post('http://localhost:8080/mealmate/create/vote/voting/' + this.roomId,
                    {
                        contents : this.contents,
                        voteSubject: this.voteSubject,
                        voteTitle: this.voteTitle,
                        voteMethodType: this.voteMethodType,
                        voting: {
                          voteId: 0,
                          voterStatus: this.voterStatus
                        },
                        chatPeriod: {
                            chatPeriodId: this.chatPeriodId,
                            startHour: this.startHour,
                            startMinute: this.startMinute,
                            endHour: this.endHour,
                            endMinute: this.endMinute,
                        },
                        chatRoomTitle: this.chatRoomTitle,

                        locking: this.locking,

                    })
            },
            votingMethod : function (chatPeriodVote, voterStatus) {
                axios.post('http://localhost:8080/mealmate/voting/' + this.roomId,
                    {
                        voteId: chatPeriodVote.voteId,
                        voterStatus,
                    })
            },
    // <option>ADD_CHAT_PERIOD</option>
    // <option>DELETE_CHAT_PERIOD</option>
    // <option>UPDATE_CHAT_PERIOD</option>
    // <option>UPDATE_CHAT_ROOM_TITLE</option>
    // <option>LOCK</option>
            chatPeriodVoteComplete: function (chatPeriodVote) {
                for (const temp in chatPeriodVote) {
                    console.log(temp + "|" + chatPeriodVote[temp]);
                }
                const voteSubject = chatPeriodVote.voteSubject;
                switch (voteSubject){
                    case 'ADD_CHAT_PERIOD':
                        axios.post('http://localhost:8080/mealmate/addPeriod/' + this.roomId + "/" + chatPeriodVote.voteId, {
                            voteId: chatPeriodVote.voteId,
                            startTime: chatPeriodVote.startTime,
                            endTime: chatPeriodVote.endTime,
                        });
                        break;
                    case 'DELETE_CHAT_PERIOD':
                        axios.post('http://localhost:8080/mealmate/deletePeriod/' + this.roomId + "/" + chatPeriodVote.voteId + "/" + chatPeriodVote.chatPeriodId);
                        break;
                    case 'UPDATE_CHAT_PERIOD':
                        axios.post('http://localhost:8080/mealmate/updatePeriod/' + this.roomId + "/" + chatPeriodVote.voteId, {
                            chatPeriodId: chatPeriodVote.chatPeriodId,
                            startHour: chatPeriodVote.startHour,
                            startMinute: chatPeriodVote.startMinute,
                            endHour: chatPeriodVote.endHour,
                            endMinute: chatPeriodVote.endMinute,
                        });
                        break;
                    default:
                        alert("chatPeriodVoteComplete fail");
                }
            },
            chatTitleVoteComplete: function (voteId, newTitle) {
                axios.post('http://localhost:8080/mealmate/updateTitle/' + this.roomId + "/" + voteId + "/" + newTitle)
                    .then(response => {
                        // 이러면 클라이언트 하나의 채팅방 제목은 즉각 바뀌겠지만, 다른 클라이언트가 보기엔 바뀌지 않을 것...
                        this.roomName = response.data;
                    }).catch(error => {
                        alert("chatTitleVoteComplete fail");
                        console.error(error);
                    });
            },
            chatRoomLockVoteComplete: function (voteId) {
                axios.post('http://localhost:8080/mealmate/lock/' + this.roomId + "/" + voteId);
            },
            fetchFeedbackMealMateList: function () {
                axios.get('http://localhost:8080/mealmate/chatroom/feedback/current/list')
                    .then(response => {
                        this.feedbackMealMateList = response.data;
                        console.log(response);
                    }).catch(error => {
                    alert("fetchMemberList fail");
                    console.error(error);
                });
            },
        }
    });
</script>


</body>
</html>