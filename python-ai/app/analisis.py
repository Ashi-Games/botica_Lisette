import math
from datetime import date, datetime

from app.modelos import construir_serie, pronosticar, tendencia, confianza


def analizar(hoy: date, productos: list[dict], historial_dias: int = 365) -> dict:
    """productos: [{id, nombre, stock, stockMinimo, ventas:[(fecha, unidades)]}]"""
    fechas = [f for p in productos for f, _ in p["ventas"]]
    inicio = max(min(fechas), hoy.fromordinal(hoy.toordinal() - historial_dias)) if fechas else hoy
    filas = []
    for p in productos:
        y = construir_serie(p["ventas"], inicio, hoy) if fechas else None
        if y is None or len(y) == 0:
            pred, metodo, err, conf, base7 = [0.0] * 30, "sin datos", None, "BAJA", 0.0
        else:
            pred, metodo, err = pronosticar(y, 30)
            conf, base7 = confianza(y, err), float(y.tail(28).sum()) / 4
        u7, u30 = float(sum(pred[:7])), float(sum(pred[:30]))
        diario = u30 / 30
        dias_stock = None if diario <= 0 else int(p["stock"] / diario)
        sugerido = max(0, math.ceil(u30 + p["stockMinimo"] - p["stock"]))
        filas.append({
            "productoId": p["id"], "producto": p["nombre"],
            "unidadesSemana": round(u7), "unidadesMes": round(u30),
            "tendencia": tendencia(u7, base7), "confianza": conf, "metodo": metodo,
            "stock": p["stock"], "diasDeStock": dias_stock, "comprarSugerido": sugerido,
        })
    filas.sort(key=lambda f: (-f["unidadesSemana"], f["producto"]))
    return {
        "generadoEn": datetime.now().isoformat(timespec="seconds"),
        "totalSemana": sum(f["unidadesSemana"] for f in filas),
        "totalMes": sum(f["unidadesMes"] for f in filas),
        "productos": filas,
    }