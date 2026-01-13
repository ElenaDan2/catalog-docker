<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Adauga Student</title>
</head>
<body>

<h1>Adauga Student</h1>

<form action="studenti" method="post">
    Nume: <input type="text" name="nume" required><br><br>
    Email: <input type="email" name="email" required><br><br>
    Varsta: <input type="number" name="varsta" min="1" required><br><br>

    <button type="submit">Adauga</button>
</form>

<br>
<a href="studenti">Inapoi la lista</a>

</body>
</html>
