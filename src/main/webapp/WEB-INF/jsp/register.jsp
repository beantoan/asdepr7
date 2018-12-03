<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<t:login>
    <jsp:attribute name="title">
      Register
    </jsp:attribute>

    <jsp:body>
        <form:form method="POST" class="form-register" action="/register" modelAttribute="user">
            <h1 class="h3 mb-3 font-weight-normal">Create new account</h1>

            <div class="form-group">
                <label for="inputFullName">Full name</label>
                <form:input type="text" path="fullName" id="inputFullName" class="form-control"/>
            </div>

            <div class="form-group">
                <label for="inputUsername">Username</label>
                <form:input type="text" path="username" id="inputUsername" class="form-control"/>
            </div>

            <div class="form-group">
                <label for="inputPassword">Password</label>
                <form:input type="password" path="password" id="inputPassword" class="form-control"/>
            </div>

            <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
        </form:form>

        <div class="col-sx-12 text-center">
            <a href="/login" role="button">Login to play game</a>
        </div>
    </jsp:body>
</t:login>