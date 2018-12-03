<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:default>
    <jsp:attribute name="title">
      Play game
    </jsp:attribute>

    <jsp:body>
        <div class="jumbotron">

            <h2><i class="fas fa-medal"></i>&nbsp;Your Achievements</h2>

            <div class="row text-center">
                <div class="col-3 text-success">
                    <h3><i class="far fa-grin-squint"></i>&nbsp;Win</h3>
                    <h4>${user.winCount}</h4>
                </div>
                <div class="col-1">
                    <span style="font-size: 0.5rem;">
                        <i class="fas fa-circle"></i>
                    </span>
                </div>
                <div class="col-4">
                    <h3><i class="far fa-smile"></i>&nbsp;Drawn</h3>
                    <h4>${user.drawnCount}</h4>
                </div>
                <div class="col-1">
                    <span style="font-size: 0.5rem;" class="align-middle">
                        <i class="fas fa-circle"></i>
                    </span>
                </div>
                <div class="col-3 text-danger">
                    <h3><i class="far fa-sad-cry"></i>&nbsp;Lose</h3>
                    <h4>${user.loseCount}</h4>
                </div>
            </div>

            <h2><i class="fas fa-khanda"></i>&nbsp;Your Battles</h2>

            <div class="row">
                <div class="col-12">
                    <ul class="list-group">
                        <c:forEach items="${rooms.iterator()}" var="room">
                            <li class="list-group-item">

                                <div  class="media">
                                    <span class="mr-3 ${room.drawn ? '' : (room.winUser.id.equals(user.id) ? 'text-success' : 'text-danger')}" style="font-size: 2.5rem;">
                                        <i class="far fa-${room.drawn ? 'smile' : (room.winUser.id.equals(user.id) ? 'grin-squint' : 'sad-cry')}"></i>
                                    </span>

                                    <div class="media-body">
                                        <h5 class="mt-0">
                                                ${room.title} - ${room.drawn ? 'You and '.concat(room.firstUser.id.equals(user.id) ? room.firstUser.fullName : room.secondUser.fullName).concat(' are the best') :
                                                (room.winUser.id.equals(user.id) ? 'You knock out '.concat(room.loseUser.fullName) : room.winUser.fullName.concat(' beat you'))}
                                        </h5>

                                        Started from <fmt:formatDate value="${room.startedAt}" pattern="HH:mm:ss dd-MM-yyyy" /> to <fmt:formatDate value="${room.finishedAt}" pattern="HH:mm:ss dd-MM-yyyy" />

                                    </div>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>

                    <t:pagination items="${rooms}"/>

                </div>
            </div>

        </div>
    </jsp:body>
</t:default>