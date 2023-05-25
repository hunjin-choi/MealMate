<!doctype html>
<html lang="en">
<head>
    <title>Websocket ChatRoom</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="container" id="app" v-cloak>
    <div class="row">
        <div class="col-md-6">
            <h3>{{roomName}}</h3>
        </div>
        <div class="col-md-6 text-right">
            <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>
        </div>
    </div>
    <div class="input-group">
        <div class="input-group-prepend">
            <label class="input-group-text">내용</label>
        </div>
        <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage('TALK')">
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">채팅 보내기</button>
            <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">피드백 보내기</button>
            <!-- 모달창 -->
            <div v-if="showModal">
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
            <!-- 버튼 -->
            <button @click="showModal = true">Open Modal</button>

        </div>
    </div>
    <ul class="list-group">
        <li class="list-group-item" v-for="message in messages">
            {{message.sender}} - {{message.message}}</a>
        </li>
    </ul>
</div>
<!-- JavaScript -->

<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
<script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
<script>
    // websocket & stomp initialize
    var sock = new SockJS("/ws-stomp");
    var ws = Stomp.over(sock);
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
            readOnlyToken: '',
            readWriteToken: '',
            showModal: false,
            startHour: null,
            startMinute: null,
            endHour: null,
            endMinute: null
        },
        // 아래가 vm 생성자인가보다;
        created() {
            this.roomId = localStorage.getItem('wschat.roomId');
            this.roomName = localStorage.getItem('wschat.roomName');
            var _this = this;
            window.addEventListener('beforeunload', function(e) {
                // WebSocket 연결을 닫는다.
                this.roomId = null;
                this.roomName = null;
                _this.token = null;
                _this.readOnlyToken = null;
                _this.readWriteToken = null;
                vm.$destroy();
                sock.close();
            });
            axios.get('/chat/user/' + this.roomId).then(response => {
                _this.token = response.data.token;
                _this.readOnlyToken = response.data.readOnlyToken;
                _this.readWriteToken = response.data.readWriteToken;
                ws.connect({"token":_this.token, "readOnlyToken" : _this.readOnlyToken, "readWriteToken": _this.readWriteToken}, function(frame) {
                    alert("구독 신청");
                    ws.subscribe("/sub/chat/room/"+_this.roomId, function(message) {
                        var recv = JSON.parse(message.body);
                        _this.recvMessage(recv);
                    });
                    _this.sendMessage('ENTER');
                    alert("서버 접속 성공!");
                }, function(error) {
                    alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
                    location.href="/chat/room";
                });
            });
        },
        methods: {
            sendMessage: function(type) {
                ws.send("/pub/chat/message", {"token":this.token, "readOnlyToken" : this.readOnlyToken, "readWriteToken": this.readWriteToken},
                    JSON.stringify({type:type, roomId:this.roomId, message:this.message}));
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

                axios.post('http://localhost:8080/chat/period/' + this.roomId, data)
                    .then(response => {
                        console.log(response);
                        this.showModal = false;
                    })
                    .catch(error => {
                        console.error(error);
                    });
            }
        }
    });
</script>


</body>
</html>