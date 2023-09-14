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
            <button class="btn btn-warning" type="button" @click="redirectToChatRoom">채팅룸으로 이동</button>
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
            <button class="btn btn-secondary" type="button" @click="chatPeriodModal = true">채팅 시간 설정</button>
            <button class="btn btn-third" type="button" @click="fetchChatPeriodList()">채팅 시간 삭제</button>
            <button class="btn btn-info" type="button" @click="feedbackModal = true">피드백 보내기</button>

        </div>
    </div>

    <ul class="list-group" v-if="isDataFetched">
        <li class="list-group-item d-flex justify-content-between" v-for="message in messages">
    <span>
      {{message.sender}} - {{message.message}}
    </span>
            <span>
      {{message.date}}
    </span>
        </li>
    </ul>

    <!-- Chat Time Period Modal -->
    <div v-if="chatPeriodModal" class="modal-container">
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
            <button @click="submit">제출</button>
        </div>
        <button @click="chatPeriodModal = false; fetchChatPeriodList()">Close</button>
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
        <h4>Feedback</h4>
        <div>
            <label>피드백: </label>
            <input type="text" v-model="feedback" required minlength="5" placeholder="5글자 이상 입력하세요."><br/>
            <label>마일리지: </label>
            <input type="number" v-model="mileage" required min="0" placeholder="0 이상의 정수를 입력하세요."><br/>
            <button @click="submitFeedback">제출</button>
            <button @click="cancelFeedback">취소</button>
        </div>
    </div>

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
            chatPeriodModal: false,
            accessToken : null,
            refreshToken : null,
            feedbackModal: false,
            startHour: null,
            startMinute: null,
            endHour: null,
            endMinute: null,
            feedback: '',
            mileage: null,
            tempValue:null,
            isDataFetched: false,
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
            console.log(resp.data);
            console.log(_this.messages);
            _this.isDataFetched = true;
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
                this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message})
            },
            submit: function() {
                const data = {
                    startHour: this.startHour,
                    startMinute: this.startMinute,
                    endHour: this.endHour,
                    endMinute: this.endMinute
                };

                axios.post('http://localhost:8080/mealmate/addPeriod/' + this.roomId, data, {
                    headers: {
                        "token":this.token,
                        "readOnlyToken" : this.readOnlyToken,
                        "readWriteToken": this.readWriteToken
                    }
                })
                    .then(response => {
                        console.log(response);
                        this.chatPeriodModal = false;
                    })
                    .catch(error => {
                        console.error(error);
                    });
            },
            submitFeedback: function() {
                if(this.feedback.length < 5 || this.mileage < 0) {
                    alert("피드백은 5글자 이상, 마일리지는 0 이상의 정수를 입력해야 합니다.");
                    return;
                }

                const data = {
                    feedbackMention: this.feedback,
                    mileage: this.mileage
                };

                axios.post('http://localhost:8080/mealmate/feedback/' + this.roomId, data, {
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
            deleteChatPeriod: function(chatPeriodId) {
                axios.get('http://localhost:8080/mealmate/deletePeriod/' + this.roomId + '/' + chatPeriodId, {
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
                })
                    .then(response => {
                        console.log(response);
                        this.chatPeriodList = response.data;
                        this.deleteChatPeriodModal = true;
                    })
                    .catch(error => {
                        console.error("chatPeriod 리스트 받아오기 실패");
                        console.error(error);
                    });
            },
            redirectToChatRoom: function() {
                window.location.href = "/chat/room";
            }
        }
    });
</script>


</body>
</html>