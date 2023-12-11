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
            <!-- Creates a table of books that are not currently being loaned -->
        <%
            // Retrieve available books from the servlet, check if it is empty
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

                    <!-- Iterate over all books and create an entry in the table for them -->
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

            <label class="ml-1" for="bookId">Select Book ID:</label>
            <select name="bookId" id="bookId">
                <%
                    // If the list of books is not empty, create a dropdown of book id's
                    if (books != null) {
                        if (!books.isEmpty()) {
                        for (bookModel book : books) {
                %>
                <!-- Creates a dropdown of book id's -->
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
        // Check if a book has been loaned, and if so display a message to indicate this
        Boolean loaned = (Boolean) request.getAttribute("loaned");
        if (loaned != null && loaned) {
    %>
    <p><b>Book loaned successfully! This loan will last a duration of 30 days.</b></p>
    <%
        }
    %>

    <a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>

    <!-- Script tags for bootstrap -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>
