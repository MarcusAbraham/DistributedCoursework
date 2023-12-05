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

        .container {
            display: flex;
            justify-content: space-around;
            align-items: flex-start;
        }

        table {
            margin: 10px;
            border-collapse: separate;
            border-spacing: 10px;
        }

        form {
            margin-top: 10px;
        }
    </style>

    <title>Borrow Books</title>
</head>
<body>

    <h1>Borrow Books</h1>
    <br/>

    <div class="container">
        <div>
        <%
            List<bookModel> books = (List<bookModel>) request.getAttribute("books");
            if (books != null) {
                if (!books.isEmpty()) {
        %>
                <table>
                    <tr>
                        <th>Book Id</th>
                        <th>Book Name</th>
                        <th>Course Title</th>
                    </tr>

                    <% for (bookModel book : books) { %>
                    <tr>
                        <td><%= book.getBook_id() %></td>
                        <td><%= book.getBook_name() %></td>
                        <td><%= book.getCourse_title() %></td>
                    </tr>
                    <% } %>
                </table>
        <%
                }
            }
        %>
        </div>

        <div>
        <form action="BorrowServlet" method="post">
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

            <label class="ml-1" for="bookId">Select Book ID:</label>
            <select name="bookId" id="bookId">
                <%
                    if (books != null) {
                        if (!books.isEmpty()) {
                        for (bookModel book : books) {
                %>
                <option value="<%= book.getBook_id() %>"><%= book.getBook_id() %></option>
                <%
                        }
                        }
                    }
                %>
            </select>

            <input class="btn btn-secondary ml-2" type="submit" name="action" value="Borrow">
        </form>
        </div>
    </div>

    <%
        Boolean loaned = (Boolean) request.getAttribute("loaned");
        if (loaned != null && loaned) {
    %>
    <p><b>Book loaned successfully! This loan will last a duration of 30 days.</b></p>
    <%
        }
    %>

    <a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>

</body>
</html>
