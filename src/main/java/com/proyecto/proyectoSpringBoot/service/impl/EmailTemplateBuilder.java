package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class EmailTemplateBuilder {

    public String buildWelcomeSubject(RolUsuario rol) {
        if (rol == null) {
            return "Bienvenido a LogiTrack S.A. | Tu cuenta ha sido activada";
        }
        return switch (rol) {
            case ADMIN -> "Bienvenido a LogiTrack S.A. | Acceso de Administrador del Sistema";
            case SUPERVISOR -> "Bienvenido a LogiTrack S.A. | Acceso de Supervisor de Operaciones";
            case GERENTE_LOGISTICA -> "Bienvenido a LogiTrack S.A. | Acceso de Gerencia Logística";
            case JEFE_COMPRAS -> "Bienvenido a LogiTrack S.A. | Acceso de Jefatura de Compras";
            case EMPLEADO -> "Bienvenido a LogiTrack S.A. | Acceso de Especialista de Almacén";
        };
    }

    public String buildWelcomeEmailHtml(String nombreCompleto, String email, RolUsuario rol) {
        String roleDisplayName = getRoleDisplayName(rol);
        String roleBadgeColor = getRoleBadgeColor(rol);
        String roleIcon = getRoleIcon(rol);
        String roleDescription = getRoleDescription(rol);
        String roleDutiesHtml = getRoleDutiesHtml(rol);
        int currentYear = Year.now().getValue();

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Bienvenido a LogiTrack S.A.</title>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    background-color: #f1f5f9;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    color: #1e293b;
                    -webkit-font-smoothing: antialiased;
                }
                .container {
                    max-width: 620px;
                    margin: 24px auto;
                    background: #ffffff;
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04);
                    border: 1px solid #e2e8f0;
                }
                .header {
                    background: linear-gradient(135deg, #0f172a 0%%, #1e293b 50%%, #334155 100%%);
                    padding: 36px 32px;
                    text-align: center;
                    color: #ffffff;
                }
                .logo-title {
                    font-size: 28px;
                    font-weight: 800;
                    letter-spacing: -0.5px;
                    margin: 0 0 4px 0;
                    color: #ffffff;
                }
                .logo-subtitle {
                    font-size: 13px;
                    text-transform: uppercase;
                    letter-spacing: 2px;
                    color: #94a3b8;
                    font-weight: 600;
                    margin: 0;
                }
                .content {
                    padding: 32px 32px 24px 32px;
                }
                .greeting {
                    font-size: 20px;
                    font-weight: 700;
                    color: #0f172a;
                    margin: 0 0 12px 0;
                }
                .intro-text {
                    font-size: 15px;
                    line-height: 1.6;
                    color: #475569;
                    margin: 0 0 24px 0;
                }
                .role-card {
                    background: #f8fafc;
                    border: 1px solid #e2e8f0;
                    border-left: 5px solid %s;
                    border-radius: 10px;
                    padding: 20px;
                    margin-bottom: 24px;
                }
                .role-badge {
                    display: inline-block;
                    background-color: %s;
                    color: #ffffff;
                    font-size: 12px;
                    font-weight: 700;
                    padding: 4px 12px;
                    border-radius: 9999px;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    margin-bottom: 8px;
                }
                .role-title {
                    font-size: 16px;
                    font-weight: 700;
                    color: #1e293b;
                    margin: 4px 0 6px 0;
                }
                .role-desc {
                    font-size: 14px;
                    color: #64748b;
                    margin: 0 0 14px 0;
                    line-height: 1.5;
                }
                .duties-list {
                    margin: 0;
                    padding-left: 18px;
                    font-size: 13.5px;
                    color: #334155;
                    line-height: 1.6;
                }
                .duties-list li {
                    margin-bottom: 6px;
                }
                .info-box {
                    background-color: #f0fdf4;
                    border: 1px solid #bbf7d0;
                    border-radius: 10px;
                    padding: 16px 20px;
                    margin-bottom: 24px;
                }
                .info-title {
                    font-size: 14px;
                    font-weight: 700;
                    color: #166534;
                    margin: 0 0 4px 0;
                }
                .info-text {
                    font-size: 13px;
                    color: #15803d;
                    margin: 0;
                    line-height: 1.5;
                }
                .action-container {
                    text-align: center;
                    margin: 28px 0;
                }
                .cta-button {
                    display: inline-block;
                    background: linear-gradient(135deg, #2563eb 0%%, #1d4ed8 100%%);
                    color: #ffffff !important;
                    text-decoration: none;
                    font-size: 15px;
                    font-weight: 700;
                    padding: 14px 32px;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.35);
                }
                .security-note {
                    background: #fffbeb;
                    border: 1px solid #fef3c7;
                    border-radius: 8px;
                    padding: 14px 18px;
                    font-size: 12.5px;
                    color: #92400e;
                    line-height: 1.5;
                    margin-bottom: 24px;
                }
                .footer {
                    background-color: #f8fafc;
                    border-top: 1px solid #e2e8f0;
                    padding: 24px 32px;
                    text-align: center;
                    font-size: 12px;
                    color: #94a3b8;
                    line-height: 1.6;
                }
                .footer p {
                    margin: 4px 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <!-- Header -->
                <div class="header">
                    <h1 class="logo-title">📦 LogiTrack S.A.</h1>
                    <p class="logo-subtitle">Sistema Integral WMS & ERP Industrial</p>
                </div>

                <!-- Main Content -->
                <div class="content">
                    <h2 class="greeting">¡Hola, %s! 👋</h2>
                    <p class="intro-text">
                        Te damos la más cordial bienvenida al equipo de <strong>LogiTrack S.A.</strong> Tu cuenta ha sido creada y configurada exitosamente con el correo <code>%s</code>.
                    </p>

                    <!-- Role Card -->
                    <div class="role-card">
                        <span class="role-badge">%s %s</span>
                        <h3 class="role-title">%s</h3>
                        <p class="role-desc">%s</p>
                        <ul class="duties-list">
                            %s
                        </ul>
                    </div>

                    <!-- Security Advisory -->
                    <div class="security-note">
                        🔒 <strong>Recomendación de Seguridad:</strong> Tus credenciales son de uso personal y confidencial. Si no solicitaste este registro o detectas actividad inusual, por favor contacta de inmediato al administrador de sistemas.
                    </div>

                    <!-- Quick Start Action -->
                    <div class="action-container">
                        <a href="http://localhost:8081" class="cta-button" target="_blank">Ingresar a la Plataforma</a>
                    </div>
                </div>

                <!-- Footer -->
                <div class="footer">
                    <p><strong>LogiTrack S.A.</strong> • Transformación y Automatización Logística</p>
                    <p>Este es un correo generado automáticamente por el sistema de autenticación de LogiTrack.</p>
                    <p>© %d LogiTrack S.A. Todos los derechos reservados.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                roleBadgeColor,
                roleBadgeColor,
                nombreCompleto != null && !nombreCompleto.isBlank() ? nombreCompleto : "Colaborador",
                email,
                roleIcon,
                roleDisplayName,
                roleDisplayName,
                roleDescription,
                roleDutiesHtml,
                currentYear
        );
    }

    private String getRoleDisplayName(RolUsuario rol) {
        if (rol == null) return "Usuario Registrado";
        return switch (rol) {
            case ADMIN -> "Administrador del Sistema";
            case SUPERVISOR -> "Supervisor de Operaciones & Calidad";
            case GERENTE_LOGISTICA -> "Gerente de Logística & Distribución";
            case JEFE_COMPRAS -> "Jefe de Compras & Abastecimiento";
            case EMPLEADO -> "Especialista en Operaciones & Bodega";
        };
    }

    private String getRoleBadgeColor(RolUsuario rol) {
        if (rol == null) return "#64748b";
        return switch (rol) {
            case ADMIN -> "#4f46e5";            // Indigo
            case SUPERVISOR -> "#d97706";       // Amber / Gold
            case GERENTE_LOGISTICA -> "#059669";// Emerald Green
            case JEFE_COMPRAS -> "#0284c7";     // Cyan / Sky Blue
            case EMPLEADO -> "#2563eb";         // Royal Blue
        };
    }

    private String getRoleIcon(RolUsuario rol) {
        if (rol == null) return "👤";
        return switch (rol) {
            case ADMIN -> "🛡️";
            case SUPERVISOR -> "📋";
            case GERENTE_LOGISTICA -> "📊";
            case JEFE_COMPRAS -> "🛒";
            case EMPLEADO -> "📦";
        };
    }

    private String getRoleDescription(RolUsuario rol) {
        if (rol == null) {
            return "Tienes acceso a los servicios básicos de la plataforma.";
        }
        return switch (rol) {
            case ADMIN -> "Cuentas con privilegios totales de administración, auditoría global y parametrización general del sistema WMS/ERP.";
            case SUPERVISOR -> "Supervisas el flujo operacional de las bodegas, auditorías de inventario, picking y validación de conteos cíclicos.";
            case GERENTE_LOGISTICA -> "Lideras la estrategia de almacenamiento, rotación de inventarios, expedición de guías de despacho y métricas ABC.";
            case JEFE_COMPRAS -> "Administras la relación con proveedores, catálogo comercial, compras corporativas y recepción de lotes de abastecimiento.";
            case EMPLEADO -> "Ejecutas las tareas operativas directas: preparación de pedidos (picking), serialización unitaria y movimientos físicos de bodega.";
        };
    }

    private String getRoleDutiesHtml(RolUsuario rol) {
        if (rol == null) {
            return """
                <li>Consulta de inventario y datos autorizados de la empresa.</li>
                <li>Actualización de perfil personal y preferencias.</li>
            """;
        }
        return switch (rol) {
            case ADMIN -> """
                <li><strong>Gestión de Usuarios & Seguridad:</strong> Creación, asignación de roles y control de acceso al sistema.</li>
                <li><strong>Auditoría Global:</strong> Trazabilidad completa de operaciones críticas (INSERT, UPDATE, DELETE).</li>
                <li><strong>Configuración Maestro:</strong> Parametrización de bodegas, categorías, zonas y reglas de negocio.</li>
                <li><strong>Centro de Notificaciones:</strong> Emisión y supervisión de alertas operacionales de la empresa.</li>
            """;
            case SUPERVISOR -> """
                <li><strong>Supervisión de Picking:</strong> Asignación y monitoreo en tiempo real de tareas de recolección de pedidos.</li>
                <li><strong>Conteos Cíclicos:</strong> Planificación de auditorías periódicas y conciliación de diferencias de stock.</li>
                <li><strong>Control de Calidad:</strong> Validación de ubicaciones, capacidades máximas y estados de productos.</li>
                <li><strong>Aprobación de Ajustes:</strong> Revisión de entradas y salidas de inventario justificadas.</li>
            """;
            case GERENTE_LOGISTICA -> """
                <li><strong>Estrategia & KPIs:</strong> Análisis de rotación de inventario, clasificación ABC y niveles de servicio.</li>
                <li><strong>Guías de Despacho:</strong> Emisión y seguimiento de albaranes de salida vinculados a transportadoras.</li>
                <li><strong>Gestión de Pedidos:</strong> Aprobación de pedidos de clientes y despacho de mercancía en bodegas.</li>
                <li><strong>Reportes Avanzados:</strong> Exportación de reportes ejecutivos en formatos PDF, Excel y CSV.</li>
            """;
            case JEFE_COMPRAS -> """
                <li><strong>Órdenes de Compra:</strong> Creación, cálculo de impuestos, aprobación y seguimiento de abastecimiento.</li>
                <li><strong>Directorio de Proveedores:</strong> Gestión de contactos, condiciones comerciales y catálogo de insumos.</li>
                <li><strong>Recepción de Lotes:</strong> Verificación de fechas de fabricación y caducidad de lotes ingresados.</li>
                <li><strong>Alertas de Reorden:</strong> Monitoreo constante de productos con stock inferior al mínimo requerido.</li>
            """;
            case EMPLEADO -> """
                <li><strong>Picking Asistido:</strong> Ejecución y confirmación paso a paso de productos solicitados en pedidos.</li>
                <li><strong>Trazabilidad Unitaria:</strong> Registro y verificación de números de serie para productos tecnológicos.</li>
                <li><strong>Movimientos Físicos:</strong> Registro de traslados entre pasillos, estantes y zonas de almacenamiento.</li>
                <li><strong>Recepción Física:</strong> Descarga y ubicación de mercancía en racks y gavetas asignadas.</li>
            """;
        };
    }
}
