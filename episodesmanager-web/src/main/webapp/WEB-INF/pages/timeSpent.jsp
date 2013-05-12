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

    <div>

        <h1>Temps passé</h1>

        <table class="table table-bordered table-striped table-hover table-condensed">
            <thead>
                <tr>
                    <th>Nom</th>
                    <th>Terminé</th>
                    <th>Durée épisode</th>
                    <th>Nombre d'épisodes</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${table}" var="show">
                    <tr>
                        <td>${show[0]}</td>
                        <td>${show[1]}</td>
                        <td>${show[2]}</td>
                        <td>${show[3]}</td>
                        <td>${show[4]}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    </body>
</html>