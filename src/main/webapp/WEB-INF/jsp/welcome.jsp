<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<t:default>
    <jsp:attribute name="title">
      Play game
    </jsp:attribute>

    <jsp:body>
        <div class="jumbotron">

            <div class="row">
                <div class="col-sm-12 col-lg-4 text-center">
                    <img class="welcome-img" src="/img/img02.png">
                </div>

                <div class="col-sm-12 col-lg-8">
                    <h2 class="mt-0">Welcome to CONNECT FOUR !!!</h2>
                    <p>This game is centuries old, Captain James Cook used to play it with his fellow officers on his long voyages, and so it has also been called "Captain's Mistress". Milton Bradley (now owned by Hasbro) published a version of this game called "Connect Four" in 1974.</p>

                    <h2 class="mt-0">How to play</h2>
                    <p>Connect four of your checkers in a row while preventing your opponent from doing the same. But, look out -- your opponent can sneak up on you and win the game!</p>
                </div>
            </div>

            <hr/>

            <div class="col-sm text-center">
                <sec:authorize access="!isAuthenticated()">
                    <a class="btn btn-lg btn-primary" href="/login" role="button">Login to play game</a>
                    &nbsp;Or&nbsp;

                    <a href="/register" role="button">Create new account</a>
                </sec:authorize>

                <sec:authorize access="isAuthenticated()">
                    <a class="btn btn-lg btn-primary" href="/lobby" role="button">
                        <i class="fas fa-gamepad"></i>&nbsp;Play now !!!
                    </a>
                </sec:authorize>
            </div>
        </div>
    </jsp:body>
</t:default>