<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Summary</title>
        <meta charset="utf-8" />
    </head>
    <body>
        
        <form>
            <input type="checkbox" name="displayed" value="toAcquire" <c:if test="${acquire}">checked="checked"</c:if>>Acquire</input>
            <input type="checkbox" name="displayed" value="toWatch" <c:if test="${watch}">checked="checked"</c:if>>Watch</input>
            <input type="checkbox" name="displayed" value="toBroadcast" <c:if test="${broadcast}">checked="checked"</c:if>>Coming</input>
            <input type="hidden" name="filtered" value="1"/>
            <input type="submit" value="Filtrer" class="btn btn-primary"/>
        </form>
        
        <table class="table table-bordered table-striped table-hover table-condensed">
            <thead>
                <tr>
                    <th>Name</th>
                    <th></th>
                    <th>Next Episode</th>
                    <th>Operations</th>
                </tr>
            </thead>
            <tbody>
            
                <c:if test="${acquire}">
                    <c:forEach items="${episodesToAcquire}" var="toAcquire">

                        <c:url value="ignore" var="ignoreAnchor"><c:param name="id" value="${toAcquire.topiaId}"/></c:url>
                        <c:url value="acquire" var="acquireAnchor"><c:param name="id" value="${toAcquire.topiaId}"/></c:url>
                        <c:url value="watch" var="watchAnchor"><c:param name="id" value="${toAcquire.topiaId}"/></c:url>
                        <c:url value="episode" var="episodeAnchor"><c:param name="id" value="${toAcquire.topiaId}"/></c:url>
                        <c:url value="show" var="showAnchor"><c:param name="id" value="${toAcquire.season.show.topiaId}"/></c:url>

                        <tr>
                            <td class="showsName"><a href="${showAnchor}">${toAcquire.season.show.title}</a></td>
                            <td class="toDo"> <i class="icon-download-alt"></i></td>
                            <td class="relevant">
                                <c:choose>
                                    <c:when test="${toAcquire.season.number==0}">Specials</c:when>
                                    <c:otherwise>${toAcquire.season.number}</c:otherwise>
                                </c:choose>x${toAcquire.number} - <fmt:formatDate pattern="dd/MM/yyyy" value="${toAcquire.airingDate}" /> - <a href="${episodeAnchor}">${toAcquire.title}</a></td>
                            <td class="operations">
                                <div class="btn-group">
                                    <a href="${acquireAnchor}" class="btn" title="Acquire"><i class="icon-download-alt"></i></a>
                                    <a href="${watchAnchor}" class="btn" title="Watch"><i class="icon-eye-open"></i></a>
                                    <a href="${ignoreAnchor}" class="btn" title="Ignore"><i class="icon-ban-circle"></i></a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>


                <c:if test="${watch}">
                    <c:forEach items="${episodesToWatch}" var="toWatch">

                        <c:url value="ignore" var="ignoreAnchor"><c:param name="id" value="${toWatch.topiaId}"/></c:url>
                        <c:url value="watch" var="watchAnchor"><c:param name="id" value="${toWatch.topiaId}"/></c:url>
                        <c:url value="episode" var="episodeAnchor"><c:param name="id" value="${toWatch.topiaId}"/></c:url>
                        <c:url value="show" var="showAnchor"><c:param name="id" value="${toWatch.season.show.topiaId}"/></c:url>

                        <tr>
                            <td class="showsName"><a href="${showAnchor}">${toWatch.season.show.title}</a></td>
                            <td class="toDo"><i class="icon-eye-open"></i></td>
                            <td class="relevant">
                                <c:choose>
                                    <c:when test="${toWatch.season.number==0}">Specials</c:when>
                                    <c:otherwise>${toWatch.season.number}</c:otherwise>
                                </c:choose>x${toWatch.number} - <fmt:formatDate pattern="dd/MM/yyyy" value="${toWatch.airingDate}" /> - <a href="${episodeAnchor}">${toWatch.title}</a></td>
                            <td class="operations">
                                <div class="btn-group">
                                    <a href="${watchAnchor}" class="btn" title="Watch"><i class="icon-eye-open"></i></a>
                                    <a href="${ignoreAnchor}" class="btn" title="Ignore"><i class="icon-ban-circle"></i></a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>

                <c:if test="${broadcast}">         
                    <c:forEach items="${episodesToBroadcast}" var="toBroadcast">

                        <c:url value="ignore" var="ignoreAnchor"><c:param name="id" value="${toBroadcast.topiaId}"/></c:url>
                        <c:url value="episode" var="episodeAnchor"><c:param name="id" value="${toBroadcast.topiaId}"/></c:url>
                        <c:url value="show" var="showAnchor"><c:param name="id" value="${toBroadcast.season.show.topiaId}"/></c:url>

                        <tr>
                            <td class="showsName"><a href="${showAnchor}">${toBroadcast.season.show.title}</a></td>
                            <td class="toDo"><i class="icon-calendar"></i></td>
                            <td class="relevant">
                                <c:choose>
                                    <c:when test="${toBroadcast.season.number==0}">Specials</c:when>
                                    <c:otherwise>${toBroadcast.season.number}</c:otherwise>
                                </c:choose>x${toBroadcast.number} - <fmt:formatDate pattern="dd/MM/yyyy" value="${toBroadcast.airingDate}" /> - <a href="${episodeAnchor}">${toBroadcast.title}</a></td>
                            <td class="operations">
                                <div class="btn-group">
                                    <a href="${ignoreAnchor}" class="btn" title="Ignore"><i class="icon-ban-circle"></i></a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
            </tbody>
 
	
        </table>

    </body>
</html>