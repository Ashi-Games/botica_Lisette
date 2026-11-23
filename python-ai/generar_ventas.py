"""Genera VENTAS SIMULADAS (2 años) para probar la predicción.

Uso:  python generar_ventas.py
Crea: ../database/datos_ventas_simuladas.sql  (ejecútalo en MySQL Workbench)

OJO: son datos inventados. En el informe debe decirse que son simulados.
No descuentan stock de los lotes (solo sirven para entrenar/probar la predicción).
Requiere que ya exista el usuario 'admin' (se crea al iniciar Spring una vez).
"""
import math
import random
from datetime import date, datetime, timedelta
from pathlib import Path

import numpy as np

rng = np.random.default_rng(42)
random.seed(42)

# (id producto, precio, ventas medias por día, efecto invierno: + sube / - baja)
PRODUCTOS = [
    (1, 0.50, 6.0, 0.1), (2, 0.80, 3.5, 0.1), (3, 1.20, 2.0, 0.3), (4, 0.70, 1.8, -0.4),
    (5, 0.90, 1.5, 0.0), (6, 2.50, 1.2, 0.2), (7, 6.50, 0.5, 0.0), (8, 1.50, 1.6, 0.4),
    (9, 5.00, 0.6, 0.0), (10, 8.50, 0.5, 0.6), (11, 1.50, 0.8, 0.2), (12, 18.00, 0.1, 0.0),
]
DOW = [1.0, 1.0, 0.95, 1.0, 1.15, 1.3, 0.8]  # lunes..domingo

hoy = date.today()
ini = hoy - timedelta(days=730)
ventas, detalles, vid = [], [], 0

dia = ini
while dia < hoy:
    items = []
    for pid, precio, media, invierno in PRODUCTOS:
        estacion = 1 + invierno * math.cos(2 * math.pi * (dia.timetuple().tm_yday - 196) / 365.25)
        crecimiento = 1 + 0.15 * (dia - ini).days / 730
        n = int(rng.poisson(max(0.0, media * DOW[dia.weekday()] * estacion * crecimiento)))
        if n:
            items.append((pid, precio, n))
    random.shuffle(items)
    # reparte los productos del día en ventas de 1 a 3 productos
    pendientes = items
    while pendientes:
        k = random.randint(1, 3)
        grupo, pendientes = pendientes[:k], pendientes[k:]
        vid += 1
        hora = datetime(dia.year, dia.month, dia.day, random.randint(8, 20),
                        random.randint(0, 59), random.randint(0, 59))
        total = sum(p * n for _, p, n in grupo)
        pago = random.choice(["Efectivo", "Efectivo", "Yape", "Plin", "Tarjeta"])
        ventas.append(f"({vid},'{hora:%Y-%m-%d %H:%M:%S}',@u,'{pago}',{total:.2f})")
        for pid, p, n in grupo:
            detalles.append(f"({vid},{pid},(SELECT MIN(id) FROM lote WHERE producto_id={pid}),"
                            f"{n},{p:.2f},{p * n:.2f})")
    dia += timedelta(days=1)

sql = [
    "USE db_botica_lisette;",
    "SET @u = (SELECT id FROM usuario WHERE username='admin' LIMIT 1);",
    "INSERT INTO venta (id, fecha_hora, usuario_id, metodo_pago, total) VALUES",
    ",\n".join(ventas) + ";",
    "INSERT INTO detalle_venta (venta_id, producto_id, lote_id, cantidad, precio_unitario, subtotal) VALUES",
    ",\n".join(detalles) + ";",
]
salida = Path(__file__).resolve().parent.parent / "database" / "datos_ventas_simuladas.sql"
salida.parent.mkdir(exist_ok=True)
salida.write_text("\n".join(sql), encoding="utf-8")
print(f"{len(ventas)} ventas, {len(detalles)} detalles -> {salida}")