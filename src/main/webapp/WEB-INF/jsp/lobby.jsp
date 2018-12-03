<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:default>
    <jsp:attribute name="title">
      Play game
    </jsp:attribute>

    <jsp:attribute name="script">
      <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js"></script>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
      <script src="/js/ejs.min.js"></script>
      <script src="/js/game.js"></script>
    </jsp:attribute>

    <jsp:body>

        <div id="game-main" class="jumbotron">
            <div id="lobby">
                <div class="row mb-4">
                    <div class="col-sm-12 col-md-6">
                        <h3 class="d-inline"><i class="fas fa-khanda"></i>&nbsp; <span id="count-rooms">0</span> rooms</h3>
                        &nbsp;
                        <h3 class="d-inline"><i class="fas fa-users"></i>&nbsp; <span id="count-users">0</span> online users</h3>
                    </div>
                    <div class="col-sm-12 col-md-6">
                        <button type="button" class="btn btn-primary float-right"
                                data-toggle="modal" data-target="#createRoomModal">
                            <i class="fas fa-plus-circle"></i>&nbsp;Create new room
                        </button>
                    </div>
                </div>

                <div id="rooms"></div>
            </div>

            <div id="game-board-container" style="display: none;">
                <h3 class="mb-2 text-center">
                    <i class="far fa-flag"></i>
                    <span id="room-title"></span>
                    <i class="far fa-flag"></i>
                </h3>

                <div class="row justify-content-center">
                    <div class="col-sm-12 col-md-4">
                        <div class="media">
                            <img class="align-self-center mr-3" src="/img/red_ball.png">

                            <div class="media-body text-center">
                                <h4 class="mt-0 text-success">You</h4>

                                <div class="row">
                                    <div class="col-4">
                                        Win
                                        <h4 id="me-win-count"></h4>
                                    </div>
                                    <div class="col-4">
                                        Drawn
                                        <h4 id="me-drawn-count"></h4>
                                    </div>
                                    <div class="col-4">
                                        Lose
                                        <h4 id="me-lose-count"></h4>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-1 text-center">
                        <span style="font-size: 4rem;">
                            <i class="fas fa-khanda"></i>
                        </span>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="media">
                            <div class="media-body text-center">
                                <h4 id="competitor-full-name" class="user-status mt-0">Your friend</h4>

                                <div class="row text-left">
                                    <div class="col-4">
                                        Win
                                        <h4 id="competitor-win-count"></h4>
                                    </div>
                                    <div class="col-4">
                                        Drawn
                                        <h4 id="competitor-drawn-count"></h4>
                                    </div>
                                    <div class="col-4">
                                        Lose
                                        <h4 id="competitor-lose-count"></h4>
                                    </div>
                                </div>
                            </div>

                            <img class="align-self-center ml-3 text-sm-left" src="/img/blue_ball.png">
                        </div>
                    </div>
                </div>

                <div class="row justify-content-center">
                    <div id="game-board" class="game-board">
                        <div class="game-row-header row justify-content-center">
                            <c:forEach var = "i" begin = "0" end = "6">
                                <div class="game-cell" col-index="<c:out value = "${i}"/>"></div>
                            </c:forEach>
                        </div>

                        <c:forEach var = "i" begin = "0" end = "5">
                            <div class="game-row row justify-content-center" row-index="<c:out value = "${i}"/>">
                                <c:forEach var = "j" begin = "0" end = "6">
                                    <div class="game-cell" col-index="<c:out value = "${j}"/>"></div>
                                </c:forEach>
                            </div>
                        </c:forEach>

                        <div class="game-notify mt-2 d-flex flex-row justify-content-center"></div>

                        <div class="d-flex flex-row mt-3 justify-content-center">
                            <button type="button" class="leave-room btn btn-outline-danger">Leave room</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <audio id="sound-win" src="/img/win.mp3" preload="auto" crossOrigin="anonymous"></audio>
        <audio id="sound-lose" src="/img/lose.mp3" preload="auto" crossOrigin="anonymous"></audio>
        <audio id="sound-click" src="/img/click.mp3" preload="auto" crossOrigin="anonymous"></audio>

        <div class="modal fade" id="createRoomModal" tabindex="-1" role="dialog"
             aria-labelledby="createRoomModalTitle" aria-hidden="true" data-focus="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createRoomModalTitle">Create new room</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="new-room-title">Room title</label>
                            <input type="text" class="form-control form-control-lg" id="new-room-title">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button id="submit-new-room" type="button" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </div>
        </div>

        <script id="rooms-template" type="text/x-handlebars-template">
            <div class="row">
                <c:forTokens items = "leftRooms,rightRooms" delims = "," var = "name">
                    <div class="col-md-6 col-sm-12">
                        <ul class="list-group">
                            <? <c:out value = "${name}"/>.forEach(function(roomEvent){ ?>
                                <li class="list-group-item">

                                    <div class="media">
                                        <span class="room-icon room-icon-<?- roomEvent.firstUser.username + '-' + roomEvent.roomId -?> room-icon-<?- roomEvent.secondUser.username + '-' + roomEvent.roomId -?> mr-3" style="font-size: 2rem;">
                                            <? if (roomEvent.secondUser.id) { ?>
                                                <i class="fas fa-users"></i>
                                            <? } else { ?>
                                                <i class="fas fa-user"></i>
                                            <? } ?>
                                        </span>

                                        <div class="media-body">
                                            <h5 class="mt-0">
                                                <?- roomEvent.roomTitle -?>
                                            </h5>

                                            <? if (roomEvent.secondUser.id) { ?>
                                            <b class="user-status <?- roomEvent.firstUser.username -?>"><?- roomEvent.firstUser.fullName -?></b> <i class="fas fa-khanda"></i> <b class="user-status <?- roomEvent.secondUser.username -?>"><?- roomEvent.secondUser.fullName -?></b>
                                            <? } else { ?>
                                                Hosted by
                                                <? if (currentUserId === roomEvent.firstUser.id) {?>
                                                    you
                                                <? } else { ?>
                                                    <b class="user-status <?- roomEvent.firstUser.username -?>"><?- roomEvent.firstUser.fullName -?></b>
                                                <? } ?>
                                            <? } ?>
                                        </div>

                                        <? if (!roomEvent.secondUser.id || currentUserId === roomEvent.firstUser.id || currentUserId === roomEvent.secondUser.id) { ?>
                                            <button class="btn btn-outline-primary align-middle join-room" type="button" room-id="<?- roomEvent.roomId -?>">
                                                <i class="fas fa-gamepad"></i>
                                            </button>
                                        <? } ?>
                                    </div>
                                </li>

                            <? }); ?>
                        </ul>
                    </div>
                </c:forTokens>
            </div>
        </script>

    </jsp:body>
</t:default>