<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <style>
        h1{
            text-align: center;
            margin-top: 2%;
        }

        .container {
            display: flex;
            justify-content: space-between;
        }
    </style>

    <title>UoG Library Home</title>
</head>
<body>
<h1><%= "University of Gloucestershire Library Manager" %></h1>
<br/>
<div class="container">
    <a class="btn btn-secondary" href="borrow.jsp" role="button">Borrow Books</a>
    <br/>
    <a class="btn btn-secondary" href="return.jsp" role="button">Return Books</a>
    <br/>
    <a class="btn btn-secondary" href="payment.jsp" role="button">Pay Fine</a>
    <br/>
    <a class="btn btn-secondary" href="ReportServlet" role="button">Views Reports</a>
</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>