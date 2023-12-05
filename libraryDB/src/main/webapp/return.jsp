<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.librarydb.models.*" %>
<%@ page import="java.util.List" %>

<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <style>
        body {
            text-align: center;
        }
        h1{
            margin-top: 2%;
        }

        table{
            display: flex;
            justify-content: center;
            align-items: flex-start;
            margin: 10px;
            border-collapse: separate;
            border-spacing: 10px;
        }
    </style>

    <title>Return Books</title>
</head>
<body>

<h1>Return Books</h1>
<br/>

<form action="ReturnServlet" method="post">
    <label for="studentId">Select Student ID:</label>
    <select name="studentId" id="studentId">
        <%
            // Retrieve student IDs from request attribute
            List<Integer> studentIds = (List<Integer>) request.getAttribute("studentIds");
            if (studentIds != null) {
                for (Integer studentId : studentIds) {
        %>
        <option value="<%= studentId %>"><%= studentId %></option>
        <%
                }
            }
        %>
    </select>

    <input class="btn btn-secondary ml-2" type="submit" name="action" value="Search">
</form>

<%
    // Retrieve student data from request attribute
    List<bookModel> loanedBooks = (List<bookModel>) request.getAttribute("loanedBooks");
    if (loanedBooks != null) {
        if (!loanedBooks.isEmpty()) {
%>
    <h3>Loaned Books</h3>
    <form action="ReturnServlet" method="post">
        <table>
            <tr>
                <th>Book Id</th>
                <th>Book Name</th>
                <th>Course Title</th>
            </tr>

            <% for (bookModel book : loanedBooks) { %>
            <tr>
                <td><%= book.getBook_id() %></td>
                <td><%= book.getBook_name() %></td>
                <td><%= book.getCourse_title() %></td>
            </tr>
            <% } %>
        </table>

        <label for="bookId">Select Book ID:</label>
        <select name="bookId" id="bookId">
            <% for (bookModel book : loanedBooks) { %>
            <option value="<%= book.getBook_id() %>"><%= book.getBook_id() %></option>
            <% } %>
        </select>
        <input class="btn btn-secondary ml-2" type="submit" name="action" value="Return">
    </form>
<%
        }
    }

    Boolean returned = (Boolean) request.getAttribute("returned");
    Long fineAmount = (Long) request.getAttribute("fineAmount");
    if (returned != null && returned) {
%>
    <p>Book returned!</p>
    <%
            if (fineAmount > 0) {
    %>
            <p>Late return: a fine amount of <%=fineAmount%> has been issued</p>
<%
            }
        }
%>

<a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>

</body>
</html>
