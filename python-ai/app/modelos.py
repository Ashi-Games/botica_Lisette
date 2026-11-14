"""Predicción de demanda por producto.

Idea: para cada producto se arma la serie de ventas diarias y se entrena una
regresión Ridge con el día de la semana y la tendencia. Antes de usarla se
comprueba con datos pasados (validación) que le gana a un promedio simple de los
últimos 28 días; si no le gana, se usa el promedio. Así nunca se muestra un
modelo "de adorno".
"""
from datetime import date, timedelta

import numpy as np
import pandas as pd
from sklearn.linear_model import Ridge

MIN_DIAS_MODELO = 56   # con menos historial solo se usa el promedio
VENTANA = 28           # días que se predicen en cada prueba de validación
CORTES = 4             # cuántas pruebas hacia atrás
MIN_DIAS_ESTACIONAL = 365
MARGEN = 1.15          # el modelo debe ganarle al promedio por al menos 15 %


def construir_serie(ventas, inicio: date, hoy: date) -> pd.Series:
    """ventas: lista de (fecha, unidades). Devuelve unidades por día desde
    `inicio` hasta AYER (hoy está incompleto), con 0 en los días sin ventas."""
    dias = pd.date_range(inicio, hoy - timedelta(days=1), freq="D")
    s = pd.Series(0.0, index=dias)
    for f, u in ventas:
        f = pd.Timestamp(f)
        if f in s.index:
            s[f] += u
    return s


def _variables(fechas: pd.DatetimeIndex, t0: pd.Timestamp, estacional: bool) -> np.ndarray:
    dow = np.eye(7)[fechas.dayofweek]
    t = ((fechas - t0).days.to_numpy() / 30.0)[:, None]
    cols = [dow, t]
    if estacional:
        d = fechas.dayofyear.to_numpy()[:, None]
        cols += [np.sin(2 * np.pi * d / 365.25), np.cos(2 * np.pi * d / 365.25)]
    return np.hstack(cols)


def _ajustar_y_predecir(y: pd.Series, futuro: pd.DatetimeIndex) -> np.ndarray:
    estacional = len(y) >= MIN_DIAS_ESTACIONAL
    t0 = y.index[0]
    m = Ridge(alpha=1.0).fit(_variables(y.index, t0, estacional), y.to_numpy())
    pred = m.predict(_variables(futuro, t0, estacional))
    tope = max(1.0, 3 * float(y.tail(VENTANA).max()))
    return np.clip(pred, 0, tope)


def _promedio(y: pd.Series, n: int) -> np.ndarray:
    return np.full(n, float(y.tail(VENTANA).mean()))


def _validar(y: pd.Series):
    """Devuelve (error_modelo, error_promedio) en unidades por SEMANA (error del
    total de cada ventana de 28 días / 4), o None si no hay historial para probar."""
    em, ep = [], []
    for k in range(CORTES):
        corte = len(y) - VENTANA * (k + 1)
        if corte < MIN_DIAS_MODELO:
            break
        entreno, real = y.iloc[:corte], y.iloc[corte:corte + VENTANA]
        total = float(real.sum())
        em.append(abs(float(_ajustar_y_predecir(entreno, real.index).sum()) - total) / 4)
        ep.append(abs(float(_promedio(entreno, len(real)).sum()) - total) / 4)
    if not em:
        return None
    return float(np.mean(em)), float(np.mean(ep))


def pronosticar(y: pd.Series, dias: int = 30):
    """Devuelve (predicción diaria de `dias` días, método, error semanal o None)."""
    futuro = pd.date_range(y.index[-1] + timedelta(days=1), periods=dias, freq="D")
    if len(y) < MIN_DIAS_MODELO or y.sum() == 0:
        return _promedio(y, dias), "promedio", None
    val = _validar(y)
    if val is not None and val[0] * MARGEN < val[1]:
        return _ajustar_y_predecir(y, futuro), "modelo", val[0]
    return _promedio(y, dias), "promedio", (val[1] if val else None)


def tendencia(pred7: float, base7: float) -> str:
    if base7 <= 0:
        return "SUBE" if pred7 > 0 else "ESTABLE"
    r = pred7 / base7
    return "SUBE" if r > 1.10 else "BAJA" if r < 0.90 else "ESTABLE"


def confianza(y: pd.Series, error_semanal) -> str:
    if len(y) < MIN_DIAS_MODELO or error_semanal is None:
        return "BAJA"
    semanal = float(y.tail(VENTANA).sum()) / 4
    if semanal <= 0:
        return "BAJA"
    rel = error_semanal / semanal
    return "ALTA" if rel < 0.25 else "MEDIA" if rel < 0.50 else "BAJA"