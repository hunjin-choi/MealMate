<!doctype html>
<!doctype html>
<html lang="en">
<head>
    <title>Websocket Chat</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <!-- CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.2/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="container" id="app" v-cloak>
    <div class="row">
        <div class="col-md-4 text-left">
            <h3>채팅방 리스트</h3>
        </div>
        <div class="col-md-4 text-mid">
            <button class="btn btn-info btn-sm" @click="window.location.href='/mealmate/list'">MealMate List</button>
            <button class="btn btn-info btn-sm" @click="window.location.href='/member/mileage/history'">Mileage History</button>
        </div>
        <div class="col-md-4 text-right">
            <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>
        </div>
    </div>
    <div class="row">
        <div class="col-md-4 text-mid">
            <button class="btn btn-info btn-sm" @click="findAllRoom">전체 채팅방</button>
            <button class="btn btn-info btn-sm" @click="findMyRoom">내 채팅방</button>
        </div>
    </div>
    <div class="input-group">
        <div class="input-group-prepend">
            <label class="input-group-text">방제목</label>
        </div>
        <input type="text" class="form-control" v-model="room_name" v-on:keyup.enter="createRoom">
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="createRoom">채팅방 개설</button>
        </div>
    </div>
    <ul class="list-group" v-if="showChatrooms">
        <li class="list-group-item list-group-item-action" v-for="item in chatrooms" v-bind:key="item.roomId" v-on:click="enterRoom(item.roomId, item.name)">
            {{item.name}}
        </li>
    </ul>
    <!-- 나의 채팅방 -->
    <ul class="list-group" v-if="showMyRoom">
        <li class="list-group-item list-group-item-action" v-bind:key="myRoom.roomId" v-on:click="enterRoom(myRoom.roomId, myRoom.name)">
            {{myRoom.name}}
        </li>
    </ul>
</div>
<!-- JavaScript -->
<script src="/webjars/vue/2.6.14/dist/vue.min.js"></script>
<script src="/webjars/axios/0.21.1/dist/axios.min.js"></script>
<script>
    var vm = new Vue({
        el: '#app',
        data: {
            room_name : '',
            chatrooms: [
            ],
            myRoom: null, // My Room 정보를 저장할 변수 추가
            showChatrooms: true,
            showMyRoom: false
        },
        created() {
            window.addEventListener('beforeunload', function(e) {
                // WebSocket 연결을 닫는다.
                vm.$destroy();
            });
            this.findAllRoom();
            this.findMyRoom();
        },
        methods: {
            findAllRoom: function() {
                axios.get('/chat/rooms').then(response => { this.chatrooms = response.data; });
                this.showChatrooms = true;
                this.showMyRoom = false;
            },
            findMyRoom: function() { // My Room을 찾는 메소드 추가
                axios.get('/chat/myRoom').then(response => { this.myRoom = response.data; });
                this.showChatrooms = false;
                this.showMyRoom = true;
            },
            createRoom: function() {
                if("" === this.room_name) {
                    alert("방 제목을 입력해 주십시요.");
                    return;
                } else {
                    var params = new URLSearchParams();
                    params.append("chatRoomTitle",this.room_name);
                    axios.post('/chat/room', params)
                        .then(
                            response => {
                                alert(response.data.name+"방 개설에 성공하였습니다.")
                                this.room_name = '';
                                this.findAllRoom();
                            }
                        )
                        .catch( response => { alert("채팅방 개설에 실패하였습니다."); } );
                }
            },
            enterRoom: function(roomId, roomName) {
                localStorage.setItem('wschat.roomId',roomId);
                localStorage.setItem('wschat.roomName',roomName);
                location.href="/chat/room/enter/"+roomId;
            }
        }
    });
</script>
</body>
</html>