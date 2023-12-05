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
        .table-container {
            display: flex;
            justify-content: center;
            align-items: flex-start;
        }
        .table-container table {
            margin: 10px;
            border-collapse: separate;
            border-spacing: 10px;
        }
        .header-container {
            display: flex;
            justify-content: center;
            align-items: flex-start;
        }
        .header-container h3 {
            margin-right: 45px;
            margin-right: 300px;
        }
    </style>

    <title>Student Reports</title>
</head>
<body>

<h1>View Student Reports</h1>
<br/>

<form action="ReportServlet" method="post">
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

    <label class="ml-1" for="monthAndYear">Select Month:</label>
    <input type="month" id="monthAndYear" name="monthAndYear"/>


    <input class="btn btn-secondary ml-2" type="submit" value="Search">
</form>

<br/>

<div class="header-container">
    <%
        // Retrieve student data from request attribute
        List<loanModel> loans = (List<loanModel>) request.getAttribute("loans");
        if (loans != null) {
            if (!loans.isEmpty()) {
    %>
            <h3>Loans</h3>
    <%
            }
        }
    %>

    <%
        List<fineModel> fines = (List<fineModel>) request.getAttribute("fines");
        if (fines != null) {
            if (!fines.isEmpty()) {
    %>
        <h3>Fines</h3>
    <%
            }
        }
    %>
</div>

<div class="table-container">
    <%
        if (loans != null) {
            if (!loans.isEmpty()) {
    %>
            <table>
                <tr>
                    <th>Loan Id</th>
                    <th>Student ID</th>
                    <th>Book ID</th>
                    <th>Date Borrowed</th>
                </tr>

                <% for (loanModel loan : loans) { %>
                <tr>
                    <td><%= loan.getLoan_id() %></td>
                    <td><%= loan.getStudent_id() %></td>
                    <td><%= loan.getBook_id() %></td>
                    <td><%= loan.getDate_borrowed() %></td>
                </tr>
                <% } %>
            </table>
    <%
            }
        }
    %>

    <%
        if (fines != null) {
            if (!fines.isEmpty()) {
    %>
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
    <%
             }
        }
    %>
</div>

<%
    // Retrieve total fines paid from request attribute
    Double amount_paid = (Double) request.getAttribute("amount_paid");
    if (amount_paid != null) {
%>

    <h4>Total fines paid:</h4>
    <p><%= amount_paid.doubleValue() %></p>
<%
    }
%>

<a class="btn btn-secondary mt-2" href="index.jsp" role="button">Return To Home</a>


<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>

