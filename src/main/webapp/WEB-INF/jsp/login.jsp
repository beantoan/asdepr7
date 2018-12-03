<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<t:login>
    <jsp:attribute name="title">
      Login
    </jsp:attribute>

    <jsp:body>
        <form:form method="POST" class="col-sm form-signin" action="/login" modelAttribute="user">
            <h1 class="h3 mb-3 font-weight-normal">Login CONNECT FOUR</h1>
            <label for="inputUsername" class="sr-only">Username</label>
            <form:input type="text" path="username" id="inputUsername" class="form-control" placeholder="Username"/>
            <label for="inputPassword" class="sr-only">Password</label>
            <form:input type="password" path="password" id="inputPassword" class="form-control" placeholder="Password"/>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
        </form:form>

        <div class="col-sm text-center">
            <a href="/register" role="button">Register new account to play game</a>
        </div>
    </jsp:body>
</t:login>