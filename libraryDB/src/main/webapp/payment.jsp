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
        form {
            margin-left: 2%;
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

    <title>Pay Fines</title>
</head>
<body>

<h1>Pay Fines</h1>
<br/>

<form action="PaymentServlet" method="post">
    <label for="studentId">Select Student ID:</label>
    <select name="studentId" id="studentId">
        <%
            // Retrieve student IDs from request attribute
            List<String> studentIds = (List<String>) request.getAttribute("studentIds");
            if (studentIds != null) {
                for (String studentId : studentIds) {
        %>
        <option value="<%= studentId %>"><%= studentId %></option>
        <%
                }
            }
        %>
    </select>

    <input class="btn btn-secondary ml-2" type="submit" name="action" value="Search">
</form>

<br/>

<%
    // Retrieve student data from request attribute
    List<fineModel> fines = (List<fineModel>) request.getAttribute("outstandingFines");
    if (fines != null) {
        if (!fines.isEmpty()) {
%>
            <h3>Outstanding Fines</h3>
            <form action="PaymentServlet" method="post">
                <table>
                    <tr>
                        <th>Fine Id</th>
                        <th>Loan ID</th>
                        <th>Amount Owed</th>
                        <th>Date Issued</th>
                    </tr>

                    <% for (fineModel fine : fines) { %>
                    <tr>
                        <td><%= fine.getFine_id() %></td>
                        <td><%= fine.getLoan_id() %></td>
                        <td><%= fine.getAmount_owed() %></td>
                        <td><%= fine.getDate_issued() %></td>
                    </tr>
                    <% } %>
                </table>

                <label for="fineId">Select Fine ID:</label>
                <select name="fineId" id="fineId">
                    <% for (fineModel fine : fines) { %>
                    <option value="<%= fine.getFine_id() %>"><%= fine.getFine_id() %></option>
                    <% } %>
                </select>
                <input class="btn btn-secondary ml-2" type="submit" name="action" value="Pay">
            </form>
<%
        }
    }

    // Retrieve student data from request attribute
    List<loanModelMongo> mongoFines = (List<loanModelMongo>) request.getAttribute("mongoOutstandingFines");
    if (mongoFines != null) {
        if (!mongoFines.isEmpty()) {
%>
            <h3>Outstanding Fines</h3>
            <form action="PaymentServlet" method="post">
                <table>
                    <tr>
                        <th>Book Id</th>
                        <th>Date Borrowed</th>
                        <th>Date Returned</th>
                        <th>Fine</th>
                    </tr>

                    <% for (loanModelMongo fine : mongoFines) { %>
                    <tr>
                        <td><%= fine.getBook_id() %></td>
                        <td><%= fine.getDate_borrowed() %></td>
                        <td><%= fine.getDate_returned() %></td>
                        <td><%= fine.getFine() %></td>
                    </tr>
                    <% } %>
                </table>

                <label for="bookId">Select Fine ID:</label>
                <select name="bookId" id="bookId">
                    <% for (loanModelMongo fine : mongoFines) { %>
                    <option value="<%= fine.getBook_id() %>"><%= fine.getBook_id() %></option>
                    <% } %>
                </select>
                <input class="btn btn-secondary ml-2" type="submit" name="action" value="Pay">
            </form>

<%
        }
    }

    Boolean paidFine = (Boolean) request.getAttribute("paidFine");
    if (paidFine != null && paidFine) {
%>
        <p>Fine paid successfully!</p>
<%
    }
%>

<a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>
