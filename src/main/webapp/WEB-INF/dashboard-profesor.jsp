<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Dashboard Profesor</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .wrap{max-width:1150px;margin:0 auto;padding:18px}
    .head{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;flex-wrap:wrap}
    .title{font-size:28px;font-weight:900;letter-spacing:-.02em}
    .subtitle{color:var(--muted);font-size:13px;margin-top:6px;line-height:1.4}

    .cardX{background:rgba(255,255,255,.86);border:1px solid rgba(229,231,235,.95);border-radius:var(--radius);box-shadow: var(--shadow);padding:16px;}
    .grid3{display:grid;grid-template-columns: repeat(3,minmax(0,1fr));gap:12px;}
    @media (max-width: 1000px){ .grid3{grid-template-columns: 1fr;} }

    .btnS{
      cursor:pointer;border:0;padding:12px 16px;border-radius:12px;font-weight:900;
      background: linear-gradient(135deg, var(--primary), var(--primary2));
      color:#fff;box-shadow: 0 12px 26px rgba(79,70,229,.25);
      text-decoration:none;display:inline-flex;align-items:center;justify-content:center;gap:8px;white-space:nowrap;
    }
    .btnS.secondary{
      background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);
      color:var(--primary);box-shadow: 0 14px 34px rgba(15,23,42,.08);
    }
    .miniTop{display:flex;gap:10px;align-items:center;flex-wrap:wrap}
    .miniTop a{font-weight:900}
    .pill2{display:inline-flex;padding:6px 10px;border-radius:999px;background:rgba(79,70,229,.10);color:rgba(79,70,229,1);font-weight:900;font-size:12px;border:1px solid rgba(79,70,229,.18)}
  </style>

  <!-- Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>

<div class="topbar">
  <div class="miniTop">
    <b>Catalog Școlar</b>
    <span class="pill">PROFESOR</span>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <span style="color:#475569;">${user.nume} (${user.email})</span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
  </div>
</div>

<div class="wrap">

  <div class="head">
    <div>
      <div class="title">📊 Dashboard Profesor</div>
      <div class="subtitle">Grafice: absențe pe ultimele 30 zile + media notelor pe clase (an școlar).</div>
    </div>

    <div style="display:flex; gap:10px; flex-wrap:wrap;">
      <a class="btnS secondary" href="${pageContext.request.contextPath}/studenti">Studenți</a>
      <a class="btnS secondary" href="${pageContext.request.contextPath}/app/profesor/note">Note</a>
      <a class="btnS secondary" href="${pageContext.request.contextPath}/app/profesor/absente">Absențe</a>
    </div>
  </div>

  <div style="height:14px"></div>

  <div class="grid3">
    <div class="cardX">
      <div style="display:flex; align-items:center; justify-content:space-between; gap:12px;">
        <b>🚫 Absențe / zi</b>
        <span class="pill2">ultimele 30 zile</span>
      </div>
      <div style="height:10px"></div>
      <canvas id="chartAbs"></canvas>
    </div>

    <div class="cardX" style="grid-column: span 2;">
      <div style="display:flex; align-items:center; justify-content:space-between; gap:12px;">
        <b>📝 Media notelor pe clasă</b>
        <span class="pill2">an școlar 2025–2026</span>
      </div>
      <div style="height:10px"></div>
      <canvas id="chartMedii"></canvas>
    </div>
  </div>

</div>

<script>
  const absLabels = ${absLabelsJson};
  const absValues = ${absValuesJson};

  const mediiLabels = ${mediiLabelsJson};
  const mediiValues = ${mediiValuesJson};

  const ctxAbs = document.getElementById('chartAbs').getContext('2d');
  new Chart(ctxAbs, {
    type: 'line',
    data: {
      labels: absLabels,
      datasets: [{
        label: 'Absențe',
        data: absValues,
        tension: 0.25,
        fill: true
      }]
    },
    options: {
      responsive: true,
      scales: {
        x: { ticks: { maxRotation: 0, autoSkip: true, maxTicksLimit: 8 } },
        y: { beginAtZero: true, precision: 0 }
      }
    }
  });

  const ctxMed = document.getElementById('chartMedii').getContext('2d');
  new Chart(ctxMed, {
    type: 'bar',
    data: {
      labels: mediiLabels,
      datasets: [{
        label: 'Media',
        data: mediiValues
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: { beginAtZero: true, suggestedMax: 10 }
      }
    }
  });
</script>

</body>
</html>
