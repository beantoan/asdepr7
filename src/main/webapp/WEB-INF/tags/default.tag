<%@tag description="Login page template" pageEncoding="UTF-8"%>
<%@attribute name="title" fragment="true" %>
<%@attribute name="script" fragment="true" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>
        <jsp:invoke fragment="title"/>
    </title>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.5.0/css/all.css" integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU" crossorigin="anonymous">

    <link rel="stylesheet" type="text/css" href="/css/waitMe.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/style.css"/>
</head>
<body>

    <nav class="navbar navbar-expand-md navbar-dark bg-primary mb-4">

        <a class="navbar-brand" href="/">
            <img class="logo" src="/img/connect4_logo.png"/>
        </a>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarCollapse">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="/lobby">Play Game</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/achievement">Achievement</a>
                </li>
            </ul>

            <sec:authorize access="isAuthenticated()">
                <ul class="navbar-nav navbar-right">
                    <li class="nav-item">
                        <a class="nav-link">Hello <sec:authentication property="principal.username" /></a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/logout">
                            <i class="fas fa-sign-out-alt"></i>&nbsp;Exit
                        </a>
                    </li>
                </ul>
            </sec:authorize>
        </div>
    </nav>

    <main role="main" class="container">
        <jsp:doBody/>
    </main>

    <script id="alert-template" type="text/x-handlebars-template">
        <div class="alert alert-<?- type -?>" role="alert">
            <?- content -?>
        </div>
    </script>

    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

    <script src="/js/logger.js"></script>
    <script src="/js/waitMe.min.js"></script>

    <jsp:invoke fragment="script"/>

</body>
</html>