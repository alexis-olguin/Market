@echo off
chcp 65001 > nul
echo ====================================================
echo Compilando microservicios activos de Minimarket...
echo ====================================================

set SERVICES=ms-eureka ms-gateway ms-auth ms-configuracion ms-producto ms-cliente ms-inventario ms-ventas ms-proveedores ms-pagos ms-informes ms-notificaciones

for %%s in (%SERVICES%) do (
    if exist "%%s" (
        echo.
        echo [INFO] Compilando %%s...
        cd "%%s"
        call mvnw.cmd clean package -DskipTests
        cd ..
    ) else (
        echo [WARN] No se encontro la carpeta %%s, saltando...
    )
)

echo.
echo ====================================================
echo Proceso de compilacion finalizado.
echo Listo para ejecutar: docker compose up --build
echo ====================================================
pause
