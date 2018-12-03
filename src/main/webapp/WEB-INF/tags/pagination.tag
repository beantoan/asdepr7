<%@attribute name="items" required="true" type="org.springframework.data.domain.Page" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${items.totalPages > 1}">
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item ${items.first ? 'disabled' : ''}">
                <a class="page-link" href="/achievement?page=0">First</a>
            </li>

            <li class="page-item ${items.hasPrevious() ? '' : 'disabled'}">
                <a class="page-link" href="/achievement?page=${items.hasPrevious() ? items.number - 1 : items.number}">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>

            <c:forEach var = "i" begin = "0" end = "${items.totalPages - 1}">
                <li class="page-item ${i == items.number ? 'disabled' : ''}"><a class="page-link" href="/achievement?page=${i}">${i + 1}</a></li>
            </c:forEach>

            <li class="page-item ${items.hasNext() ? '' : 'disabled'}">
                <a class="page-link" href="/achievement?page=${items.hasNext() ? items.number + 1 : items.number}">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>

            <li class="page-item ${items.last ? 'disabled' : ''}">
                <a class="page-link" href="/achievement?page=${items.totalPages - 1}">Last</a>
            </li>
        </ul>
    </nav>
</c:if>
