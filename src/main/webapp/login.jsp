<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Login - Catalog</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>

<div class="auth-bg">
  <div class="auth-wrap">

    <!-- stânga: ilustrație -->
    <div class="auth-hero">
      <div class="auth-brand">
        <span class="auth-mark">🎓</span>
        <div>
          <div class="auth-title">Catalog Școlar</div>
          <div class="auth-sub">Școala Gimnazială „Dunărea de Jos”</div>
        </div>
      </div>

      <div class="auth-illustration">
        <!-- SVG simplu (fără fișiere externe) -->
        <svg viewBox="0 0 860 520" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="g1" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0" stop-color="rgba(79,70,229,.22)"/>
              <stop offset="1" stop-color="rgba(124,58,237,.16)"/>
            </linearGradient>
            <linearGradient id="g2" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0" stop-color="rgba(79,70,229,.85)"/>
              <stop offset="1" stop-color="rgba(124,58,237,.85)"/>
            </linearGradient>
          </defs>

          <!-- fundal -->
          <rect x="0" y="0" width="860" height="520" rx="26" fill="url(#g1)"/>
          <circle cx="720" cy="120" r="140" fill="rgba(255,255,255,.45)"/>
          <circle cx="130" cy="420" r="170" fill="rgba(255,255,255,.35)"/>

          <!-- “desktop” -->
          <rect x="120" y="185" width="260" height="175" rx="18" fill="rgba(15,23,42,.14)"/>
          <rect x="140" y="205" width="220" height="120" rx="12" fill="rgba(255,255,255,.65)"/>
          <rect x="210" y="360" width="80" height="18" rx="9" fill="rgba(15,23,42,.14)"/>

          <!-- “books” -->
          <rect x="120" y="365" width="170" height="34" rx="10" fill="rgba(255,255,255,.75)"/>
          <rect x="130" y="405" width="190" height="36" rx="10" fill="rgba(255,255,255,.70)"/>
          <rect x="150" y="448" width="210" height="38" rx="10" fill="rgba(255,255,255,.65)"/>

          <!-- “backpack” -->
          <rect x="420" y="235" width="175" height="210" rx="30" fill="url(#g2)"/>
          <rect x="452" y="265" width="112" height="150" rx="22" fill="rgba(255,255,255,.16)"/>
          <rect x="470" y="205" width="78" height="46" rx="18" fill="rgba(255,255,255,.18)"/>

          <!-- “clock” -->
          <circle cx="650" cy="408" r="62" fill="rgba(255,255,255,.62)"/>
          <circle cx="650" cy="408" r="50" fill="rgba(15,23,42,.10)"/>
          <line x1="650" y1="408" x2="650" y2="378" stroke="rgba(15,23,42,.55)" stroke-width="6" stroke-linecap="round"/>
          <line x1="650" y1="408" x2="675" y2="420" stroke="rgba(15,23,42,.55)" stroke-width="6" stroke-linecap="round"/>

          <!-- puncte decorative -->
          <circle cx="70" cy="120" r="6" fill="rgba(79,70,229,.55)"/>
          <circle cx="92" cy="105" r="4" fill="rgba(124,58,237,.55)"/>
          <circle cx="760" cy="470" r="6" fill="rgba(124,58,237,.45)"/>
          <circle cx="784" cy="452" r="4" fill="rgba(79,70,229,.45)"/>
        </svg>
      </div>

      <div class="auth-foot">
        <span>2025–2026</span>
      </div>
    </div>

    <!-- dreapta: card login -->
    <div class="auth-card">
      <div class="auth-card-head">
        <div class="auth-h1">Autentificare</div>
        <div class="auth-h2">Introdu email-ul și parola.</div>
      </div>

      <%
        String err = (String) request.getAttribute("error");
        if (err != null) {
      %>
        <div class="error"><%= err %></div>
      <% } %>

      <form method="post" action="<%= request.getContextPath() %>/login" class="auth-form">
        <div class="row">
          <label>Email</label>
          <input class="input" type="email" name="email" placeholder="ex: prof@test.com" required />
        </div>

        <div class="row">
          <label>Parola</label>
          <input class="input" type="password" name="parola" placeholder="••••••••" required />
        </div>

        <button class="btn" type="submit">Intră</button>
<a class="auth-link" href="<%= request.getContextPath() %>/forgot-password">Mi-am uitat parola</a>

      </form>
    </div>

  </div>
</div>

</body>
</html>
