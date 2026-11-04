"""The send-money service. The scaffold serves GET /healthz; the rest is yours."""

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app import config

settings = config.load()
app = FastAPI(title="send-service")


@app.get("/healthz")
def healthz() -> dict[str, str]:
    return {"status": "ok"}


@app.exception_handler(404)
async def not_found(_request: Request, _exc: Exception) -> JSONResponse:
    return JSONResponse(status_code=404, content={"code": "NOT_FOUND", "message": "no such route"})
