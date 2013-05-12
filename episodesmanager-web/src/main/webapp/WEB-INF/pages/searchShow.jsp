<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EpisodesManager - Recherche série</title>
    </head>
    <body id="container">

    <div class="hero-unit">

        <h1>Rechercher une série</h1>

        <p/>

        <form action="searchShow">
            <input type="text" name="q"></input>
            <input type="submit" value="Rechercher" class="btn btn-primary"/>
        </form>
    </div>
    </body>
</html>
