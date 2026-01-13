<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Resetare parolă</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>

<div class="auth-bg">
  <div class="auth-wrap" style="grid-template-columns:1fr; max-width:560px;">
    <div class="auth-card" style="min-height:auto;">
      <div class="auth-card-head">
        <div class="auth-h1">Resetare parolă</div>
        <div class="auth-h2">Introdu email-ul și setează o parolă nouă.</div>
      </div>

      <%
        String err = (String) request.getAttribute("error");
        String ok = (String) request.getAttribute("success");
        if (err != null) {
      %>
        <div class="error"><%= err %></div>
      <% } %>

      <% if (ok != null) { %>
        <div class="success"><%= ok %></div>
      <% } %>

      <form method="post" action="<%= request.getContextPath() %>/forgot-password" class="auth-form">
        <div class="row">
          <label>Email</label>
          <input class="input" type="email" name="email" required />
        </div>

        <div class="row">
          <label>Parolă nouă</label>
          <input class="input" type="password" name="parolaNoua" required />
        </div>

        <div class="row">
          <label>Confirmă parola nouă</label>
          <input class="input" type="password" name="parolaNoua2" required />
        </div>

        <button class="btn" type="submit">Schimbă parola</button>

        <a class="auth-link" href="<%= request.getContextPath() %>/login">Înapoi la login</a>
      </form>
    </div>
  </div>
</div>

</body>
</html>
