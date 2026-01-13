<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Catalog student</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .container{max-width:1150px;margin:0 auto;padding:24px; min-height:auto; display:block}

    .topbar{display:flex;align-items:flex-start;justify-content:space-between;gap:12px}
    .title{font-size:28px;font-weight:900;letter-spacing:-.02em}
    .subtitle{color:var(--muted);font-size:13px;margin-top:6px; line-height:1.4}

    .card{
      background:rgba(255,255,255,.86);
      border:1px solid rgba(229,231,235,.95);
      border-radius:var(--radius);
      box-shadow: var(--shadow);
      padding:16px;
    }

    .grid{display:grid;gap:16px}
    .grid.kpi{grid-template-columns:repeat(4,minmax(0,1fr)); gap:12px}
    @media (max-width: 980px){ .grid.kpi{grid-template-columns:repeat(2,minmax(0,1fr))} }

    .k{
      padding:18px;
      border-radius:var(--radius);
      background:rgba(255,255,255,.86);
      border:1px solid rgba(229,231,235,.95);
      box-shadow: 0 14px 34px rgba(15,23,42,.08);
    }
    .k .label{color:var(--muted);font-size:12px;font-weight:700}
    .k .value{font-size:26px;font-weight:900;margin-top:6px}

    /* actions */
    .actions{display:flex;gap:12px;flex-wrap:wrap;margin-left:auto; align-items:center}
    .btn{
      cursor:pointer;border:0;
      padding:12px 16px;border-radius:12px;
      font-weight:900;
      background: linear-gradient(135deg, var(--primary), var(--primary2));
      color:#fff;
      box-shadow: 0 12px 26px rgba(79,70,229,.25);
      text-decoration:none;
      display:inline-flex;align-items:center;justify-content:center;gap:8px;
      white-space:nowrap;
    }
    .btn.secondary{
      background:rgba(255,255,255,.85);
      border:1px solid rgba(229,231,235,.95);
      color:var(--primary);
      box-shadow: 0 14px 34px rgba(15,23,42,.08);
    }

    /* dropdown download */
    .dl{position:relative}
    .dl-menu{
      position:absolute; right:0; top:calc(100% + 10px);
      min-width:210px;
      background:rgba(255,255,255,.92);
      border:1px solid rgba(229,231,235,.95);
      border-radius:14px;
      box-shadow: 0 18px 50px rgba(15,23,42,.18);
      padding:8px;
      display:none;
      z-index:50;
    }
    .dl-menu.show{display:block}
    .dl-item{
      display:block;
      padding:10px 12px;
      border-radius:12px;
      font-weight:900;
      color:var(--text);
      text-decoration:none;
    }
    .dl-item:hover{background:rgba(79,70,229,.10)}

    /* FILTRE – fara spatiu mort */
    .filterCard{padding:14px 16px}
    .filterRow{
      display:grid;
      grid-template-columns: 1fr 1fr 1fr 1fr 1fr auto; /* 5 campuri + butoane */
      gap:12px;
      align-items:end;
    }
    @media (max-width: 1100px){
      .filterRow{grid-template-columns: repeat(2,minmax(0,1fr));}
      .filterActions{grid-column: 1 / -1; display:flex; justify-content:flex-end; gap:12px}
      .filterActions .btn{min-width:160px}
    }

    .f{display:flex;flex-direction:column;gap:6px}
    .f label{font-size:12px;color:var(--muted);font-weight:700}
    .f input,.f select{
      background:#fff;
      border:1px solid rgba(229,231,235,.95);
      color:var(--text);
      padding:10px 10px;
      border-radius:12px;
      outline:none;
      width:100%;
      min-width:0;
    }
    .f input:focus,.f select:focus{
      border-color:rgba(79,70,229,.6);
      box-shadow:0 0 0 4px rgba(79,70,229,.12);
    }

    .filterActions{
      display:flex;
      gap:12px;
      align-items:center;
      justify-content:flex-end;
      padding-bottom:2px; /* evita spatiu jos */
    }
    .filterActions .btn{min-width:170px}

    /* layout jos */
    .split{display:grid;grid-template-columns: 2fr 1fr; gap:16px}
    @media (max-width: 980px){ .split{grid-template-columns:1fr} }

    table{width:100%;border-collapse:separate;border-spacing:0 10px}
    th{color:var(--muted);font-size:12px;text-align:left;padding:0 10px}
    td{
      padding:12px 10px;
      background:rgba(255,255,255,.85);
      border:1px solid rgba(229,231,235,.95);
      vertical-align:middle;
    }
    tr td:first-child{border-top-left-radius:var(--radius);border-bottom-left-radius:var(--radius)}
    tr td:last-child{border-top-right-radius:var(--radius);border-bottom-right-radius:var(--radius)}

    .chip{
      display:inline-flex;gap:8px;align-items:center;
      padding:6px 10px;border-radius:999px;
      background:rgba(79,70,229,.10);
      border:1px solid rgba(79,70,229,.18);
      margin:4px 6px 4px 0;
      font-size:12px;
    }
    .chip b{font-size:13px}
    .chip small{color:rgba(15,23,42,.65)}

    .pill{
      display:inline-flex;
      padding:6px 10px;
      border-radius:999px;
      background:rgba(79,70,229,.10);
      color:rgba(79,70,229,1);
      font-weight:900;
      font-size:12px;
      border:1px solid rgba(79,70,229,.18);
      white-space:nowrap;
    }
    .pill.good{background:rgba(34,197,94,.12); border-color:rgba(34,197,94,.22); color:#166534}
    .pill.bad{background:rgba(239,68,68,.10); border-color:rgba(239,68,68,.20); color:#991b1b}

    .sectionTitle{font-weight:900;margin:0 0 10px 0}

    /* "Medii pe materii" - lista clara + progress */
    .avgList{display:grid; gap:10px; margin-top:8px}
    .avgItem{
      display:grid;
      grid-template-columns: 1fr auto;
      gap:10px;
      align-items:center;
      padding:12px;
      border-radius:14px;
      border:1px solid rgba(229,231,235,.95);
      background:rgba(255,255,255,.65);
    }
    .avgName{font-weight:900}
    .avgMeta{color:var(--muted); font-size:12px; margin-top:2px}
    .avgRight{display:flex; gap:10px; align-items:center}
    .avgScore{
      font-weight:1000;
      padding:6px 10px;
      border-radius:999px;
      background:rgba(79,70,229,.10);
      border:1px solid rgba(79,70,229,.18);
      color:rgba(79,70,229,1);
      min-width:56px;
      text-align:center;
    }
    .bar{
      width:140px;
      height:10px;
      border-radius:999px;
      background:rgba(15,23,42,.08);
      overflow:hidden;
    }
    .bar > i{
      display:block;
      height:100%;
      width:0%;
      background: linear-gradient(135deg, var(--primary), var(--primary2));
      border-radius:999px;
    }
    @media (max-width: 980px){
      .bar{width:160px}
    }
  </style>
</head>

<body>
<div class="container">

  <div class="topbar">
    <div>
      <div class="title">📘 Catalogul meu</div>
      <div class="subtitle">Situația școlară pe perioada selectată: note, absențe și medii pe materii.</div>
    </div>

    <div class="actions">
      <div class="dl">
        <button type="button" class="btn" onclick="toggleDL()">Descarcă situația ⬇</button>
        <div id="dlMenu" class="dl-menu">
          <a class="dl-item"
             href="${pageContext.request.contextPath}/app/student/catalog?from=${from}&to=${to}&materie=${materieSelected}&minNota=${minNota}&maxNota=${maxNota}&export=pdf">PDF</a>
          <a class="dl-item"
             href="${pageContext.request.contextPath}/app/student/catalog?from=${from}&to=${to}&materie=${materieSelected}&minNota=${minNota}&maxNota=${maxNota}&export=xls">Excel (XLS)</a>
        </div>
      </div>
    </div>
  </div>

  <div style="height:14px"></div>

  <div class="grid kpi">
    <div class="k">
      <div class="label">Total note</div>
      <div class="value">${kpi_total_note}</div>
    </div>
    <div class="k">
      <div class="label">Media generală</div>
      <div class="value"><fmt:formatNumber value="${kpi_medie}" minFractionDigits="2" maxFractionDigits="2"/></div>
    </div>
    <div class="k">
      <div class="label">Absențe nemotivate</div>
      <div class="value">${kpi_nemotivate}</div>
    </div>
    <div class="k">
      <div class="label">Top / Slabă materie</div>
      <div class="value" style="font-size:14px;line-height:1.2">
        <c:if test="${not empty kpi_best}">
          <span class="pill good">↑ ${kpi_best.materie} (<fmt:formatNumber value="${kpi_best.medie}" minFractionDigits="2" maxFractionDigits="2"/>)</span>
        </c:if>
        <c:if test="${not empty kpi_worst}">
          <span class="pill bad" style="margin-left:8px">↓ ${kpi_worst.materie} (<fmt:formatNumber value="${kpi_worst.medie}" minFractionDigits="2" maxFractionDigits="2"/>)</span>
        </c:if>
      </div>
    </div>
  </div>

  <div style="height:16px"></div>

  <div class="card filterCard">
    <form method="get" action="${pageContext.request.contextPath}/app/student/catalog">
      <div class="filterRow">
        <div class="f">
          <label>De la</label>
          <input type="date" name="from" value="${from}">
        </div>
        <div class="f">
          <label>Până la</label>
          <input type="date" name="to" value="${to}">
        </div>
        <div class="f">
          <label>Materie</label>
          <select name="materie">
            <option value="">(toate)</option>
            <c:forEach var="m" items="${materii}">
              <option value="${m}" <c:if test="${m == materieSelected}">selected</c:if>>${m}</option>
            </c:forEach>
          </select>
        </div>
        <div class="f">
          <label>Nota min</label>
          <input type="number" name="minNota" min="1" max="10" value="${minNota}">
        </div>
        <div class="f">
          <label>Nota max</label>
          <input type="number" name="maxNota" min="1" max="10" value="${maxNota}">
        </div>

        <div class="filterActions">
          <button class="btn" type="submit">Aplică filtre</button>
          <a class="btn secondary" href="${pageContext.request.contextPath}/app/student/catalog">Reset</a>
        </div>
      </div>
    </form>
  </div>

  <div style="height:16px"></div>

  <div class="split">
    <div class="card">
      <h3 class="sectionTitle">📌 Note pe materie</h3>
      <table>
        <thead>
        <tr>
          <th style="width:22%">Materie</th>
          <th style="width:10%">Medie</th>
          <th style="width:12%">Nr. note</th>
          <th style="width:56%">Note (cu dată)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${rows}">
          <tr>
            <td><b>${r.materie}</b></td>
            <td><span class="pill"><fmt:formatNumber value="${r.medie}" minFractionDigits="2" maxFractionDigits="2"/></span></td>
            <td>${r.nrNote}</td>
            <td>
              <c:forEach var="n" items="${r.note}">
                <span class="chip">
                  <b>${n.valoare}</b>
                  <small>
                    <c:choose>
                      <c:when test="${not empty n.data}">${n.data}</c:when>
                      <c:otherwise>fără dată</c:otherwise>
                    </c:choose>
                  </small>
                </span>
              </c:forEach>
            </td>
          </tr>
        </c:forEach>

        <c:if test="${empty rows}">
          <tr>
            <td colspan="4" style="text-align:center;color:var(--muted);padding:18px">
              Nu există note pentru filtrele selectate.
            </td>
          </tr>
        </c:if>
        </tbody>
      </table>
    </div>

    <div class="card">
      <h3 class="sectionTitle">📊 Medii pe materii</h3>

      <div class="avgList">
        <c:forEach var="r" items="${rows}">
          <div class="avgItem">
            <div>
              <div class="avgName">${r.materie}</div>
              <div class="avgMeta">${r.nrNote} note</div>
            </div>
            <div class="avgRight">
              <div class="avgScore">
                <fmt:formatNumber value="${r.medie}" minFractionDigits="2" maxFractionDigits="2"/>
              </div>
              <div class="bar" title="Medie / 10">
                <i style="width: ${r.medie * 10}%"></i>
              </div>
            </div>
          </div>
        </c:forEach>

        <c:if test="${empty rows}">
          <div style="color:var(--muted);padding:10px">Nu există medii de afișat.</div>
        </c:if>
      </div>

      <div style="height:16px"></div>

      <h3 class="sectionTitle">🧾 Absențe</h3>
      <div style="max-height:240px; overflow:auto">
        <c:forEach var="a" items="${abs}">
          <div style="display:flex;justify-content:space-between;gap:10px;padding:10px;border:1px solid rgba(229,231,235,.95);border-radius:14px;background:rgba(255,255,255,.65);margin-bottom:10px">
            <div>
              <div style="font-weight:800">${a.materie}</div>
              <div style="color:var(--muted);font-size:12px">${a.data}</div>
            </div>
            <div>
              <c:choose>
                <c:when test="${a.motivata}">
                  <span class="pill good">Motivată</span>
                </c:when>
                <c:otherwise>
                  <span class="pill bad">Nemotivată</span>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </c:forEach>

        <c:if test="${empty abs}">
          <div style="color:var(--muted);padding:12px">Nu există absențe în perioada selectată.</div>
        </c:if>
      </div>
    </div>
  </div>

</div>

<script>
  function toggleDL(){
    document.getElementById("dlMenu").classList.toggle("show");
  }
  document.addEventListener("click", function(e){
    const menu = document.getElementById("dlMenu");
    const box = document.querySelector(".dl");
    if(box && !box.contains(e.target)) menu.classList.remove("show");
  });
</script>

</body>
</html>
