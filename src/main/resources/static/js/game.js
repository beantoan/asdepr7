
var GAME = {
    stompClient:  null,
    me: {
        id: null,
        username: null,
        fullName: null
    },
    competitor: {
        id: null,
        username: null,
        fullName: null
    }
};

GAME.NAME = {
    DEFAULT_ENDPOINT: '/ws-game',
    EVENT_PREFIX: '/event/',
    CMD_PREFIX: '/cmd/',
    USER_PREFIX: '/user/queue/',
    ME: 'me',
    USER_CONNECTED: 'userConnected',
    LIST_ROOMS: 'rooms',
    LIST_USERS: 'users',
    CREATE_ROOM: 'createRoom',
    ROOM_CHANGED: 'roomChanged',
    JOIN_ROOM: 'joinRoom',
    TURN: 'turn',
    LEAVE_ROOM: 'leaveRoom'
};

GAME.utils = {
    parseJson: function (rawData) {
        return JSON.parse(rawData);
    },
    getEventName: function (endPoint) {
        return GAME.NAME.EVENT_PREFIX + endPoint;
    },
    getEventNameByUser: function (endPoint) {
        return GAME.NAME.USER_PREFIX + endPoint;
    },
    getCommandName: function (endPoint) {
        return GAME.NAME.CMD_PREFIX + endPoint;
    },
    isMe: function (username) {
        Logger.info('GAME.utils', 'isMe', username + ' & ' + GAME.me.username);

        return username === GAME.me.username;
    },
    isMeById: function (userId) {
        Logger.info('GAME.utils', 'isMeById', userId + ' & ' + GAME.me.id);

        return userId === GAME.me.id;
    },
    resetCompetitor: function() {
        GAME.competitor = {
            id: null,
            username: null,
            fullName: null
        };
    }
};

GAME.server = {
    connect: function () {
        var socket = new SockJS(GAME.NAME.DEFAULT_ENDPOINT);

        socket.onclose = function (e) {
            Logger.info('GAME.server', 'connect', 'socket.onclose');
        };

        GAME.stompClient = Stomp.over(socket);

        GAME.ui.showSpinner('main');

        GAME.stompClient.connect({},
            function (frame) {
                Logger.info('GAME.server', 'connect', 'GAME.stompClient.connect.success');

                GAME.events.subscribeMe();
                GAME.cmd.me();
            },
            function (error) {
                Logger.warn('GAME.server', 'connect', 'GAME.stompClient.connect.error', error);
            }
        );
    },
    disconnect() {
        Logger.info('GAME.server', 'disconnect');

        if (GAME.stompClient !== null) {
            GAME.stompClient.disconnect();
        }
    }
};

GAME.events = {
    subscriptions: {
        me: null,
        listRooms: null,
        roomChanged: null,
        createRoom: null,
        joinRoom: null,
        leaveRoom: null,
        turn: null
    },
    subscribeMe: function () {
        var event = GAME.utils.getEventNameByUser(GAME.NAME.ME);

        GAME.events.subscriptions.me = GAME.stompClient.subscribe(event, function (data) {

            GAME.me = GAME.utils.parseJson(data.body);

            Logger.info('GAME.events', 'subscribeMe', GAME.me);

            GAME.ui.hideSpinner('main');

            GAME.ui.displayUserInfo(GAME.me);

            GAME.events.subscribeListRooms();
            GAME.events.subscribeRoomChanged();
            GAME.events.subscribeUserConnected();
            GAME.events.subscribeListUsers();

            GAME.cmd.listRooms();
        });
    },
    subscribeUserConnected: function () {
        var event = GAME.utils.getEventName(GAME.NAME.USER_CONNECTED);

        GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeUserConnected', data.body);

            GAME.cmd.listUsers();
        });
    },
    subscribeListRooms: function () {
        var event = GAME.utils.getEventNameByUser(GAME.NAME.LIST_ROOMS);

        GAME.events.subscriptions.listRooms = GAME.stompClient.subscribe(event, function (data) {
            GAME.ui.displayRooms(data.body);

            GAME.cmd.listUsers();

            GAME.ui.hideSpinner('#game-main');
        });
    },
    unsubscribeListRooms: function () {
        if (GAME.events.subscriptions.listRooms) {
            GAME.events.subscriptions.listRooms.unsubscribe();
        }
    },
    subscribeListUsers: function () {
        var event = GAME.utils.getEventNameByUser(GAME.NAME.LIST_USERS);

        GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeListUsers', data.body);

            GAME.ui.displayUsers(data.body);
        });
    },
    subscribeRoomChanged: function () {
        var event = GAME.utils.getEventName(GAME.NAME.ROOM_CHANGED);

        GAME.events.subscriptions.roomChanged = GAME.stompClient.subscribe(event, function (data) {
            GAME.ui.displayRooms(data.body);

            GAME.cmd.listUsers();
        });
    },
    subscribeCreateRoom: function () {
        var event = GAME.utils.getEventNameByUser(GAME.NAME.CREATE_ROOM);

        GAME.events.subscriptions.createRoom = GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeCreateRoom', data.body);

            var roomEvent = GAME.utils.parseJson(data.body);

            GAME.events.subscribeJoinRoom(roomEvent.roomId);

            GAME.ui.displayGameBoard(roomEvent, true);

            GAME.cmd.roomChanged();

            GAME.ui.hideCreateRoomForm();
        });
    },
    unsubscribeCreateRoom: function () {
        if (GAME.events.subscriptions.createRoom) {
            GAME.events.subscriptions.createRoom.unsubscribe();
        }
    },
    subscribeJoinRoom: function (roomId) {
        var event = GAME.utils.getEventName(GAME.NAME.JOIN_ROOM) + '/' + roomId;

        GAME.events.subscriptions.joinRoom = GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeJoinRoom', data.body);

            var roomEvent = GAME.utils.parseJson(data.body);

            GAME.logic.currentRoomId = roomEvent.roomId;

            GAME.events.subscribeTurn(roomEvent.roomId);
            GAME.events.subscribeLeaveRoom(roomEvent.roomId);

            GAME.cmd.roomChanged();

            GAME.ui.displayGameBoard(roomEvent, false);

            if (GAME.utils.isMe(roomEvent.eventCreator)) {
                var roomState = GAME.utils.parseJson(roomEvent.roomState);

                GAME.logic.fillState(roomState);
                GAME.ui.gameBoard.fillState(roomState);
            }

            if (GAME.logic.isRoomReady(roomEvent)) {
                GAME.logic.isMyTurn = GAME.utils.isMe(roomEvent.firstPlayUser);
                GAME.ui.showTurnNotify(GAME.logic.isMyTurn)
            } else {
                GAME.ui.showWaitingNotify();
            }

            if (GAME.utils.isMe(roomEvent.firstUser.username)) {
                GAME.competitor = roomEvent.secondUser;
            } else {
                GAME.competitor = roomEvent.firstUser;
            }

            GAME.ui.hideSpinner('#game-main');
        });
    },
    unsubscribeJoinRoom: function () {
        if (GAME.events.subscriptions.joinRoom) {
            GAME.events.subscriptions.joinRoom.unsubscribe();
        }
    },
    subscribeLeaveRoom: function (roomId) {
        var event = GAME.utils.getEventName(GAME.NAME.LEAVE_ROOM) + '/' + roomId;

        GAME.events.subscriptions.leaveRoom = GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeLeaveRoom', data.body);

            var roomEvent = GAME.utils.parseJson(data.body);

            GAME.cmd.roomChanged();

            if (GAME.utils.isMe(roomEvent.eventCreator)) {

                GAME.logic.currentRoomId = null;

                GAME.ui.hideGameBoard();
                GAME.ui.reset();

                GAME.events.unsubscribeCreateRoom();
                GAME.events.unsubscribeJoinRoom();
                GAME.events.unsubscribeTurn();

                GAME.utils.resetCompetitor();
            }

            GAME.ui.showWaitingNotify();

            GAME.ui.hideSpinner('#game-main');
        });
    },
    subscribeTurn: function (roomId) {
        var event = GAME.utils.getEventName(GAME.NAME.TURN) + '/' + roomId;

        GAME.events.subscriptions.turn = GAME.stompClient.subscribe(event, function (data) {
            Logger.info('GAME.events', 'subscribeTurn', data.body);

            GAME.ui.hideSpinner('#game-board');

            var turn = GAME.utils.parseJson(data.body);
            var wasMyTurn = GAME.utils.isMe(turn.creator);

            if (turn.error) {
                if (wasMyTurn) {
                    GAME.ui.displayNotify(turn.error, 'danger');
                }

                GAME.ui.sound.click();
            } else {

                GAME.logic.setState(turn.row, turn.col);
                GAME.ui.gameBoard.setBallCell(turn.row, turn.col, wasMyTurn);

                if (turn.drawn) {
                    GAME.ui.displayNotify('You and ' + GAME.competitor.fullName + ' are the best!!!', 'success');
                    GAME.ui.displayUserInfo(turn.firstUser);
                    GAME.ui.displayUserInfo(turn.secondUser);

                    GAME.logic.isMyTurn = false;

                    GAME.ui.sound.win();
                } else if (turn.winUser) {
                    var isWinner = GAME.utils.isMe(turn.winUser.username);
                    var notifyContent = isWinner ? 'You win. Congratulations!!!' : 'You lose. Good luck!!!';

                    GAME.ui.displayNotify(notifyContent, isWinner ? 'success' : 'warning');
                    GAME.ui.displayUserInfo(turn.firstUser);
                    GAME.ui.displayUserInfo(turn.secondUser);

                    GAME.logic.isMyTurn = false;

                    if (isWinner) {
                        GAME.ui.sound.win();
                    } else {
                        GAME.ui.sound.lose();
                    }
                } else {
                    GAME.logic.isMyTurn = !wasMyTurn;

                    GAME.ui.showTurnNotify(GAME.logic.isMyTurn);

                    GAME.ui.sound.click();
                }
            }
        });
    },
    unsubscribeTurn: function () {
        if (GAME.events.subscriptions.turn) {
            GAME.events.subscriptions.turn.unsubscribe();
        }
    }
};

GAME.cmd = {
    me: function () {
        Logger.info('GAME.cmd', 'me');

        GAME.stompClient.send(GAME.utils.getCommandName(GAME.NAME.ME), {});
    },
    listRooms: function () {
        Logger.info('GAME.cmd', 'listRooms');

        GAME.ui.showSpinner('#game-main');

        GAME.stompClient.send(GAME.utils.getCommandName(GAME.NAME.LIST_ROOMS), {});
    },
    listUsers: function () {
        Logger.info('GAME.cmd', 'listUsers');

        GAME.stompClient.send(GAME.utils.getCommandName(GAME.NAME.LIST_USERS), {});
    },
    roomChanged: function () {
        Logger.info('GAME.cmd', 'roomChanged');

        GAME.stompClient.send(GAME.utils.getCommandName(GAME.NAME.ROOM_CHANGED), {});
    },
    createRoom: function (title) {
        Logger.info('GAME.cmd', 'createRoom');

        GAME.stompClient.send(GAME.utils.getCommandName(GAME.NAME.CREATE_ROOM), {}, title);
    },
    joinRoom: function (roomId) {
        Logger.info('GAME.cmd', 'joinRoom', roomId);

        var cmd = GAME.utils.getCommandName(GAME.NAME.JOIN_ROOM) + '/' + roomId;

        GAME.stompClient.send(cmd, {});
    },
    leaveRoom: function (roomId) {
        Logger.info('GAME.cmd', 'leaveRoom', roomId);

        var cmd = GAME.utils.getCommandName(GAME.NAME.LEAVE_ROOM) + '/' + roomId;

        GAME.stompClient.send(cmd, {});
    },
    turn: function (roomId, row, col) {
        Logger.info('GAME.cmd', 'turn', roomId + ' - ' + row + ' - ' + col);

        var params = {
            row: row,
            col: col
        };

        var cmd = GAME.utils.getCommandName(GAME.NAME.TURN) + '/' + roomId;

        GAME.stompClient.send(cmd, {}, JSON.stringify(params));
    }
};

GAME.logic = {
    isMyTurn: false,
    state: [],
    maxRow: 6,
    maxCol: 7,
    currentRoomId: null,
    init: function () {
        for (var i = 0; i < GAME.logic.maxRow; i++) {
            GAME.logic.state[i] = [];

            for (var j = 0; j < GAME.logic.maxCol; j++) {
                GAME.logic.state[i][j] = 0;
            }
        }
    },
    setState: function (rowIndex, colIndex) {
        Logger.info('GAME.logic', 'setState', rowIndex, colIndex);

        if (rowIndex >= 0 && colIndex >= 0 && GAME.logic.state[rowIndex][colIndex] === 0) {
            GAME.logic.state[rowIndex][colIndex] = 1;
            return true;
        }

        return false;
    },
    fillState: function(roomState) {
        for (var i = 0; i < GAME.logic.maxRow; i++) {
            GAME.logic.state[i] = [];

            for (var j = 0; j < GAME.logic.maxCol; j++) {
                GAME.logic.state[i][j] = roomState[i][j];
            }
        }
    },
    getNextRowOfState: function (colIndex) {
        Logger.info('GAME.logic', 'getNextRowOfState', colIndex);

        var col = parseInt(colIndex);

        for (var i = GAME.logic.maxRow - 1; i >= 0; i--) {
            if (GAME.logic.state[i][col] === 0) {
                return i;
            }
        }
    },
    isRoomReady: function (roomEvent) {
        Logger.info('GAME.logic', 'isRoomReady', roomEvent);

        return roomEvent && roomEvent.firstUser && roomEvent.firstUser.currentRoomId &&
            roomEvent.secondUser && roomEvent.secondUser.currentRoomId;
    },
    reset: function() {
        GAME.logic.isMyTurn = false;
        GAME.logic.currentRoomId = null;

        GAME.logic.init();
    }
};

GAME.ui = {
    buildAlertMessage: function (type, content) {
        var source = $('#alert-template').html();

        var templateScript = ejs.compile(source);

        return templateScript({type: type, content: content});
    },
    buildWarningMessage: function (content) {
        return GAME.ui.buildAlertMessage('warning', content);
    },
    buildSuccessMessage: function (content) {
        return GAME.ui.buildAlertMessage('success', content);
    },
    showSpinner: function(elementId) {
        $(elementId).waitMe({
            effect: 'facebook'
        });
    },
    hideSpinner: function(elementId) {
        $(elementId).waitMe('hide');
    },
    displayUsers: function(data) {
        var userEvents = data ? JSON.parse(data) : null;

        $('.user-status').removeClass('text-success');
        $('.room-icon').removeClass('text-success');

        userEvents.forEach(function (user) {
           $('.' + user.username).addClass('text-success');

           if (!GAME.utils.isMe(user.username)) {
               $('.room-icon-' + user.username + '-' + user.currentRoomId).addClass('text-success');
           }
        });

        $('#count-users').text(userEvents.length);
    },
    displayRooms: function (data) {
        var roomEvents = data ? JSON.parse(data) : null;

        Logger.info('GAME.ui', 'displayRooms', data, roomEvents);

        if (roomEvents && roomEvents.length > 0) {
            var middle = Math.ceil(roomEvents.length / 2);

            var params = {
                currentUserId: GAME.me.id,
                leftRooms: roomEvents.slice(0, middle),
                rightRooms: roomEvents.slice(middle, roomEvents.length)
            };

            var source = $('#rooms-template').html();

            var templateScript = ejs.compile(source);

            var roomsHtml = templateScript(params);

            $('#rooms').html(roomsHtml);
        } else {
            $('#rooms').html(GAME.ui.buildWarningMessage('There is no available room. Create a new room to start game. Remember to invite your friends.'));
        }

        $('#count-rooms').text(roomEvents.length);
    },
    hideGameBoard: function () {
        $('#lobby').show();
        $('#game-board-container').hide();
        $('#room-title').text('');

        $('#me-win-count').text('');
        $('#me-drawn-count').text('');
        $('#me-lose-count').text('');

        $('#competitor-full-name').text('Your friend');
        $('#competitor-win-count').text('');
        $('#competitor-drawn-count').text('');
        $('#competitor-lose-count').text('');
    },
    displayUserInfo: function(user) {
        Logger.info('GAME.ui', 'displayUserInfo', user);

        var winCount = user.winCount > 0 ? user.winCount : 0;
        var drawnCount = user.drawnCount > 0 ? user.drawnCount : 0;
        var loseCount = user.loseCount > 0 ? user.loseCount : 0;

        if (GAME.utils.isMe(user.username)) {
            $('#me-win-count').text(winCount);
            $('#me-drawn-count').text(drawnCount);
            $('#me-lose-count').text(loseCount);
        } else {
            var fullName = user.fullName ? user.fullName : (user.username ? user.username : 'Your friend');

            $('#competitor-full-name').text(fullName);
            $('#competitor-full-name').addClass(user.username);
            $('#competitor-win-count').text(winCount);
            $('#competitor-drawn-count').text(drawnCount);
            $('#competitor-lose-count').text(loseCount);
        }
    },
    showGameBoard: function (roomEvent) {
        $('#lobby').hide();
        $('#game-board-container').show();
        $('#room-title').text(roomEvent.roomTitle);

        GAME.ui.displayUserInfo(roomEvent.firstUser);
        GAME.ui.displayUserInfo(roomEvent.secondUser);

        $([document.documentElement, document.body]).animate({
            scrollTop: $("#room-title").offset().top
        }, 2000);
    },
    hideCreateRoomForm: function() {
        GAME.ui.hideSpinner('#createRoomModal .modal-content');
        $('#createRoomModal').modal('hide');
    },
    displayGameBoard: function (roomEvent, isCreateRoomEvent) {
        if (roomEvent) {
            if (isCreateRoomEvent) {
                if (GAME.utils.isMe(roomEvent.eventCreator)) {
                    GAME.ui.showGameBoard(roomEvent);
                }
            } else {
                if (GAME.utils.isMe(roomEvent.eventCreator)) {
                    GAME.ui.showGameBoard(roomEvent);
                } else {
                    GAME.ui.displayUserInfo(roomEvent.firstUser);
                    GAME.ui.displayUserInfo(roomEvent.secondUser);
                }
            }
        } else {
            GAME.ui.hideGameBoard();
        }
    },
    displayNotify: function(content, type) {
        var notifyEle = $('#game-board .game-notify');

        if (content) {
            notifyEle.show();
            notifyEle.html(GAME.ui.buildAlertMessage(type, content));
        } else {
            notifyEle.hide();
            notifyEle.html('');
        }
    },
    showWaitingNotify: function() {
        GAME.ui.displayNotify('Waiting for your friend...', 'warning');
    },
    showTurnNotify: function(isMyTurn) {
        var content = isMyTurn ? 'Your turn' : 'Your friend\'s turn';
        var type = isMyTurn ? 'success' : 'warning';

        GAME.ui.displayNotify(content, type);
    },
    hideNotify: function() {
        GAME.ui.displayNotify(false);
    },
    reset: function() {
        GAME.logic.reset();
        GAME.ui.gameBoard.clearBallCells();
        GAME.ui.displayNotify(false);
    },
    sound: {
        play: function (eleId) {
            var playPromise = document.querySelector(eleId).play();

            if (playPromise !== undefined) {
                playPromise.then(function() {
                }).catch(function(error) {
                    Logger.error('GAME.ui.sound', 'play', error);
                });
            }
        },
        click: function () {
            GAME.ui.sound.play('#sound-click');
        },
        win: function () {
            GAME.ui.sound.play('#sound-win');
        },
        lose: function () {
            GAME.ui.sound.play('#sound-lose');
        }
    },
    gameBoard: {
        myBall: 'red-ball',
        competitorBall: 'blue-ball',
        getColIndex: function (ele) {
            return parseInt($(ele).attr('col-index'));
        },
        setBallCell: function (rowIndex, colIndex, wasMyTurn) {
            Logger.info('GAME.ui.gameBoard', 'setBallCell', rowIndex + ' ' + colIndex + ' ' + wasMyTurn);

            var ballClass = wasMyTurn ? GAME.ui.gameBoard.myBall : GAME.ui.gameBoard.competitorBall;

            $('#game-board .game-row[row-index=' + rowIndex + '] .game-cell[col-index=' + colIndex + ']').addClass(ballClass);
        },
        clearBallCells: function() {
            for (var i = 0; i < GAME.logic.maxRow; i++) {
                for (var j = 0; j < GAME.logic.maxCol; j++) {
                    var cellEle = $('#game-board .game-row[row-index=' + i + '] .game-cell[col-index=' + j + ']');

                    cellEle.removeClass(GAME.ui.gameBoard.myBall);
                    cellEle.removeClass(GAME.ui.gameBoard.competitorBall);
                }
            }
        },
        doMyTurn: function (cellEle) {
            if (GAME.logic.isMyTurn && GAME.logic.currentRoomId) {
                var colIndex = GAME.ui.gameBoard.getColIndex(cellEle);
                var rowIndex = GAME.logic.getNextRowOfState(colIndex);

                Logger.info('GAME.ui.gameBoard', 'doMyTurn', rowIndex, colIndex);

                if (colIndex >= 0 && rowIndex >= 0) {
                    GAME.cmd.turn(GAME.logic.currentRoomId, rowIndex, colIndex);

                    GAME.logic.isMyTurn = false;
                    GAME.ui.showSpinner('#game-board');
                }
            }
        },
        fillState: function(roomState) {
            for (var i = 0; i < GAME.logic.maxRow; i++) {
                for (var j = 0; j < GAME.logic.maxCol; j++) {
                    if (roomState[i][j] > 0) {
                        GAME.ui.gameBoard.setBallCell(i, j, GAME.utils.isMeById(roomState[i][j]));
                    }
                }
            }
        },
        events: function () {
            $('#game-board .game-row .game-cell').hover(
                function () {
                    var colIndex = GAME.ui.gameBoard.getColIndex(this);

                    $('#game-board .game-row-header .game-cell[col-index=' + colIndex + ']').addClass(GAME.ui.gameBoard.myBall);
                },
                function () {
                    var colIndex = GAME.ui.gameBoard.getColIndex(this);

                    $('#game-board .game-row-header .game-cell[col-index=' + colIndex + ']').removeClass(GAME.ui.gameBoard.myBall);
                }
            );

            $('#game-board .game-row .game-cell').click(function (event) {
                event.preventDefault();

                GAME.ui.gameBoard.doMyTurn(this);
            });
        }
    },
    callbacks: {
        createRoom: function () {
            var roomTitleEle = $('#new-room-title');
            var title = roomTitleEle.val();

            if (title) {
                GAME.events.subscribeCreateRoom();

                GAME.cmd.createRoom(title);

                GAME.ui.showWaitingNotify();

                GAME.ui.showSpinner('#createRoomModal .modal-content');

                GAME.ui.sound.click();

                roomTitleEle.val('');
            }
        },
        joinRoom: function(roomId) {
            if (roomId) {
                GAME.events.subscribeJoinRoom(roomId);

                GAME.cmd.joinRoom(roomId);

                GAME.ui.showWaitingNotify();

                GAME.ui.showSpinner('#game-main');

                GAME.ui.sound.click();
            }
        },
        leaveRoom: function() {
            GAME.cmd.leaveRoom(GAME.logic.currentRoomId);

            GAME.ui.showSpinner('#game-main');
        }
    },
    events: function () {
        $('#createRoomModal').on('shown.bs.modal', function () {
            $('#new-room-title').trigger('focus');
        });

        $('#submit-new-room').click(function (event) {
            event.preventDefault();

            GAME.ui.callbacks.createRoom();
        });

        $('#new-room-title').on('keydown', function(event) {
            if (event.which === 13) {
                GAME.ui.callbacks.createRoom();
            }
        });

        $('#rooms').on('click', '.join-room', function (event) {
            event.preventDefault();

            var roomId = $(this).attr('room-id');

            GAME.ui.callbacks.joinRoom(roomId);
        });

        $('#game-board .leave-room').click(function (event) {
            event.preventDefault();

            GAME.ui.callbacks.leaveRoom();
        });
    }
};

GAME.start = function() {
    ejs.delimiter = '?';

    GAME.server.connect();

    GAME.ui.events();
    GAME.ui.gameBoard.events();

    GAME.ui.reset();
};

$(function () {
    GAME.start();
});