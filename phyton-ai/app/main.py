import os
from datetime import date

from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel, Field

from app.analisis import analizar

app = FastAPI(title="Botica Lisette - IA")


class Dia(BaseModel):
    fecha: date
    unidades: int = Field(ge=0)


class Producto(BaseModel):
    id: int
    nombre: str
    stock: int = Field(ge=0)
    stockMinimo: int = Field(ge=0)
    ventas: list[Dia] = []


class Solicitud(BaseModel):
    hoy: date
    productos: list[Producto]


@app.get("/salud")
def salud():
    return {"estado": "ok"}


@app.post("/analisis")
def analisis(s: Solicitud, x_api_key: str | None = Header(default=None)):
    clave = os.getenv("IA_API_KEY")
    if clave and x_api_key != clave:
        raise HTTPException(status_code=401, detail="Clave inválida")
    productos = [
        {"id": p.id, "nombre": p.nombre, "stock": p.stock, "stockMinimo": p.stockMinimo,
         "ventas": [(d.fecha, d.unidades) for d in p.ventas]}
        for p in s.productos
    ]
    return analizar(s.hoy, productos)