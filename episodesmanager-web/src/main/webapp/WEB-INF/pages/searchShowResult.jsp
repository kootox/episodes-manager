<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EpisodesManager - Résultats de recherche</title>
    </head>
    <body id="container">

    <div class="hero-uni">

        <h1>Résultats de recherche pour :"${q}"</h1>


        <table class="table table-bordered table-striped table-hover table-condensed">
            <thead>
                <tr>
                    <th>Show</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>

            <c:forEach items="${results}" var="entry">
                <tr>
                    <td>${entry.value}</td>
                    <td><a href="<c:url value='/add/show?id=${entry.key}'/>">Ajouter</a></td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <p/>
    </div>
    </body>
</html>
