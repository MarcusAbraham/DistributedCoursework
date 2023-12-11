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
            // Retrieve student id's from the servlet, and checks if the list is empty
            List<String> studentIds = (List<String>) request.getAttribute("studentIds");
            if (studentIds != null) {
                for (String studentId : studentIds) {
        %>
                    <!-- Creates a dropdown of student id's -->
                    <option value="<%= studentId %>"><%= studentId %></option>
        <%
                }
            }
        %>
    </select>

    <input class="btn btn-secondary ml-2" type="submit" name="action" value="Search">
</form>

<%
    // Retrieve list of loaned books for the student from the servlet, and checks if the list is empty
    List<bookModel> loanedBooks = (List<bookModel>) request.getAttribute("loanedBooks");
    if (loanedBooks != null) {
        if (!loanedBooks.isEmpty()) {
%>
    <!-- Creates a table of books that are currently being loaned by the student -->
    <h3>Loaned Books</h3>
    <form action="ReturnServlet" method="post">
        <table>
            <tr>
                <th>Book Id</th>
                <th>Book Name</th>
                <th>Course Title</th>
            </tr>

            <!-- Iterate over all books and create an entry in the table for them -->
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
            <!-- Create a dropdown of book id's -->
            <% for (bookModel book : loanedBooks) { %>
            <option value="<%= book.getBook_id() %>"><%= book.getBook_id() %></option>
            <% } %>
        </select>
        <input class="btn btn-secondary ml-2" type="submit" name="action" value="Return">
    </form>
<%
        }
    }

    // Retrieve the "returned" boolean and fine amount from the servlet, and checks if the boolean is null
    Boolean returned = (Boolean) request.getAttribute("returned");
    Long fineAmount = (Long) request.getAttribute("fineAmount");
    //Check if "returned" is null, if true then a book has been returned, so it should display a message
    if (returned != null && returned) {
%>
    <p>Book returned!</p>
    <%
            // Check if the fine amount is null, if not then it should display a message
            if (fineAmount != null && fineAmount > 0) {
    %>
            <p>Late return: a fine amount of <%=fineAmount%> has been issued</p>
<%
            }
        }
%>

<a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>

<!-- Script tags for bootstrap -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>
