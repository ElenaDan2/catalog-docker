<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Profesor - Raport & Grafice</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .wrap{max-width:1150px;margin:0 auto;padding:18px}
    .head{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;flex-wrap:wrap}
    .title{font-size:28px;font-weight:900;letter-spacing:-.02em}
    .subtitle{color:var(--muted);font-size:13px;margin-top:6px;line-height:1.4}
    .cardX{background:rgba(255,255,255,.86);border:1px solid rgba(229,231,235,.95);border-radius:var(--radius);box-shadow: var(--shadow);padding:16px;}
    .filters{display:grid;grid-template-columns: 1fr 1fr 1.4fr 1fr 1fr auto;gap:12px;align-items:end;}
    @media (max-width: 1100px){ .filters{grid-template-columns: repeat(2,minmax(0,1fr));} .filters .actions{grid-column:1/-1; display:flex; justify-content:flex-end; gap:12px}}
    .f{display:flex;flex-direction:column;gap:6px}
    .f label{font-size:12px;color:var(--muted);font-weight:800}
    .f input,.f select{background:#fff;border:1px solid rgba(229,231,235,.95);padding:10px 10px;border-radius:12px;outline:none}
    .f input:focus,.f select:focus{border-color:rgba(79,70,229,.6);box-shadow:0 0 0 4px rgba(79,70,229,.12)}
    .btnS{cursor:pointer;border:0;padding:12px 16px;border-radius:12px;font-weight:900;background: linear-gradient(135deg, var(--primary), var(--primary2));color:#fff;box-shadow: 0 12px 26px rgba(79,70,229,.25);text-decoration:none;display:inline-flex;align-items:center;justify-content:center;gap:8px;white-space:nowrap;}
    .btnS.secondary{background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);color:var(--primary);box-shadow: 0 14px 34px rgba(15,23,42,.08);}
    .pill2{display:inline-flex;padding:6px 10px;border-radius:999px;background:rgba(79,70,229,.10);color:rgba(79,70,229,1);font-weight:900;font-size:12px;border:1px solid rgba(79,70,229,.18)}
    table{width:100%;border-collapse:separate;border-spacing:0 10px}
    th{color:var(--muted);font-size:12px;text-align:left;padding:0 10px}
    td{padding:12px 10px;background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);vertical-align:middle}
    tr td:first-child{border-top-left-radius:var(--radius);border-bottom-left-radius:var(--radius)}
    tr td:last-child{border-top-right-radius:var(--radius);border-bottom-right-radius:var(--radius)}
    .kpis{display:grid;grid-template-columns:repeat(5,minmax(0,1fr));gap:12px}
    @media(max-width: 1050px){.kpis{grid-template-columns:repeat(2,minmax(0,1fr));}}
    .kpi{background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);border-radius:var(--radius);padding:14px;}
    .kpi .l{color:var(--muted);font-size:12px;font-weight:800}
    .kpi .v{font-size:22px;font-weight:900;margin-top:6px}
    .chartWrap{background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);border-radius:var(--radius);padding:14px;}
    canvas{width:100%;height:260px;display:block;}
  </style>
</head>

<body>
<div class="topbar">
  <div style="display:flex; gap:10px; align-items:center;">
    <b>Catalog Școlar</b>
    <span class="pill">PROFESOR</span>
    <a href="${pageContext.request.contextPath}/app/dashboard">↩ Dashboard</a>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <span style="color:#475569;">${user.nume} (${user.email})</span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
  </div>
</div>

<div class="wrap">
  <div class="head">
    <div>
      <div class="title">📊 Raport & Grafice</div>
      <div class="subtitle">Raport pe perioadă + diagramă distribuție note (1–10). Filtre: materie / clasă / nume.</div>
    </div>
  </div>

  <div style="height:14px"></div>

  <c:if test="${not empty error}">
    <div class="error">${error}</div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="success">${success}</div>
  </c:if>

  <!-- FILTRE -->
  <div class="cardX">
    <form method="get" action="${pageContext.request.contextPath}/app/profesor/raport">
      <div class="filters">
        <div class="f">
          <label>Materie</label>
          <select name="materieId">
            <c:forEach var="m" items="${materii}">
              <option value="${m.id}" <c:if test="${m.id == materieIdSel}">selected</c:if>>${m.nume}</option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Clasă</label>
          <select name="clasaId">
            <option value="">Toate</option>
            <c:forEach var="c" items="${clase}">
              <option value="${c.id}" <c:if test="${clasaIdSel == ('' + c.id)}">selected</c:if>>${c.an}${c.litera}</option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Caută după nume</label>
          <input name="q" value="${q}" placeholder="ex: Popescu"/>
        </div>

        <div class="f">
          <label>De la</label>
          <input type="date" name="from" value="${from}"/>
        </div>

        <div class="f">
          <label>Până la</label>
          <input type="date" name="to" value="${to}"/>
        </div>

        <div class="actions" style="display:flex; gap:12px; justify-content:flex-end; flex-wrap:wrap;">
          <button class="btnS" type="submit">Aplică</button>
          <a class="btnS secondary" href="${pageContext.request.contextPath}/app/profesor/raport">Reset</a>

          <!-- Export raport -->
          <form method="get" action="${pageContext.request.contextPath}/app/profesor/export" style="margin:0">
            <input type="hidden" name="entity" value="raport"/>
            <input type="hidden" name="fmt" value="pdf"/>
            <input type="hidden" name="materieId" value="${materieIdSel}"/>
            <input type="hidden" name="clasaId" value="${clasaIdSel}"/>
            <input type="hidden" name="q" value="${q}"/>
            <input type="hidden" name="from" value="${from}"/>
            <input type="hidden" name="to" value="${to}"/>
            <button class="btnS secondary" type="submit">Export PDF</button>
          </form>

          <form method="get" action="${pageContext.request.contextPath}/app/profesor/export" style="margin:0">
            <input type="hidden" name="entity" value="raport"/>
            <input type="hidden" name="fmt" value="xls"/>
            <input type="hidden" name="materieId" value="${materieIdSel}"/>
            <input type="hidden" name="clasaId" value="${clasaIdSel}"/>
            <input type="hidden" name="q" value="${q}"/>
            <input type="hidden" name="from" value="${from}"/>
            <input type="hidden" name="to" value="${to}"/>
            <button class="btnS secondary" type="submit">Export Excel</button>
          </form>
        </div>
      </div>
    </form>
  </div>

  <div style="height:12px"></div>

  <!-- KPI -->
  <c:if test="${not empty summary}">
    <div class="kpis">
      <div class="kpi">
        <div class="l">Total note</div>
        <div class="v">${summary.totalNote}</div>
      </div>
      <div class="kpi">
        <div class="l">Media generală</div>
        <div class="v">${summary.medieGenerala}</div>
      </div>
      <div class="kpi">
        <div class="l">Total absențe</div>
        <div class="v">${summary.totalAbsente}</div>
      </div>
      <div class="kpi">
        <div class="l">Absențe motivate</div>
        <div class="v">${summary.absMotivate}</div>
      </div>
      <div class="kpi">
        <div class="l">Absențe nemotivate</div>
        <div class="v">${summary.absNemotivate}</div>
      </div>
    </div>
  </c:if>

  <div style="height:12px"></div>

  <!-- CHART -->
  <div class="chartWrap">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;">
      <div style="font-weight:900;">📈 Distribuție note (1–10)</div>
      <div class="pill2">bazat pe filtre</div>
    </div>
    <div style="height:8px"></div>
    <canvas id="distChart" width="1000" height="260"></canvas>
    <div style="margin-top:8px;color:var(--muted);font-size:12px;">
      Note pe valori: 1..10 (bar chart). Dacă e 0 peste tot => nu există note în perioada/filtrul ales.
    </div>
  </div>

  <div style="height:12px"></div>

  <!-- TOP / BOTTOM -->
  <div class="cardX">
    <div style="display:grid; grid-template-columns: 1fr 1fr; gap:12px;">
      <div>
        <div style="font-weight:900; margin-bottom:8px;">🏆 Top 5 medii</div>
        <c:if test="${empty top5}">
          <div style="color:var(--muted); font-size:13px;">Nu există medii.</div>
        </c:if>
        <c:if test="${not empty top5}">
          <table>
            <thead><tr><th>Student</th><th>Clasă</th><th>Medie</th></tr></thead>
            <tbody>
              <c:forEach var="r" items="${top5}">
                <tr>
                  <td><b>${r.studentNume}</b></td>
                  <td>${r.clasaNume}</td>
                  <td>${r.medie}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:if>
      </div>

      <div>
        <div style="font-weight:900; margin-bottom:8px;">📉 Bottom 5 medii</div>
        <c:if test="${empty bottom5}">
          <div style="color:var(--muted); font-size:13px;">Nu există medii.</div>
        </c:if>
        <c:if test="${not empty bottom5}">
          <table>
            <thead><tr><th>Student</th><th>Clasă</th><th>Medie</th></tr></thead>
            <tbody>
              <c:forEach var="r" items="${bottom5}">
                <tr>
                  <td><b>${r.studentNume}</b></td>
                  <td>${r.clasaNume}</td>
                  <td>${r.medie}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:if>
      </div>
    </div>
  </div>

  <div style="height:12px"></div>

  <!-- TABEL COMPLET -->
  <div class="cardX">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;">
      <div style="font-weight:900;">📋 Detaliu pe elev</div>
      <div class="pill2">${fn:length(rows)} elevi</div>
    </div>

    <div style="height:10px"></div>

    <c:if test="${empty rows}">
      <div style="color:var(--muted); font-size:13px;">Nu există elevi (filtru prea restrictiv?)</div>
    </c:if>

    <c:if test="${not empty rows}">
      <table>
        <thead>
          <tr>
            <th>Student</th>
            <th>Clasă</th>
            <th># Note</th>
            <th>Medie</th>
            <th>Abs Motivate</th>
            <th>Abs Nemotivate</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="r" items="${rows}">
            <tr>
              <td><b>${r.studentNume}</b></td>
              <td>${r.clasaNume}</td>
              <td>${r.nrNote}</td>
              <td>
                <c:if test="${r.medie == null}">-</c:if>
                <c:if test="${r.medie != null}">${r.medie}</c:if>
              </td>
              <td>${r.absMotivate}</td>
              <td>${r.absNemotivate}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:if>
  </div>

</div>

<script>
(function(){
  const canvas = document.getElementById('distChart');
  if(!canvas) return;
  const ctx = canvas.getContext('2d');

  const counts = [
    0,
    ${dist[1]},${dist[2]},${dist[3]},${dist[4]},${dist[5]},
    ${dist[6]},${dist[7]},${dist[8]},${dist[9]},${dist[10]}
  ];

  const labels = [1,2,3,4,5,6,7,8,9,10];

  const w = canvas.width, h = canvas.height;
  const padL = 42, padR = 12, padT = 16, padB = 34;

  ctx.clearRect(0,0,w,h);

  // max
  let max = 1;
  for(let i=1;i<=10;i++) if(counts[i] > max) max = counts[i];

  // axes
  ctx.strokeStyle = "rgba(15,23,42,.25)";
  ctx.lineWidth = 1;
  ctx.beginPath();
  ctx.moveTo(padL, padT);
  ctx.lineTo(padL, h - padB);
  ctx.lineTo(w - padR, h - padB);
  ctx.stroke();

  const chartW = (w - padL - padR);
  const chartH = (h - padT - padB);
  const barW = chartW / 10;

  // bars
  for(let i=1;i<=10;i++){
    const x = padL + (i-1)*barW + 8;
    const bw = Math.max(10, barW - 16);
    const v = counts[i];
    const bh = (v / max) * (chartH - 10);
    const y = (h - padB) - bh;

    ctx.fillStyle = "rgba(79,70,229,.65)";
    ctx.fillRect(x, y, bw, bh);

    // value
    ctx.fillStyle = "rgba(15,23,42,.75)";
    ctx.font = "12px ui-sans-serif, system-ui, Segoe UI, Arial";
    ctx.textAlign = "center";
    ctx.fillText(String(v), x + bw/2, y - 6);

    // label
    ctx.fillStyle = "rgba(100,116,139,1)";
    ctx.fillText(String(labels[i-1]), x + bw/2, h - 12);
  }
})();
</script>

</body>
</html>
