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
            case SUPER_ADMIN -> "Bienvenido a LogiTrack S.A. | Acceso de Super Administrador Global";
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
                @import url('https://fonts.googleapis.com/css2?family=Oswald:wght@600;700&family=Inter:wght@400;500;600;700&display=swap');
                body {
                    margin: 0;
                    padding: 0;
                    background-color: #0d1117;
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    color: #e2e8f0;
                    -webkit-font-smoothing: antialiased;
                }
                .container {
                    max-width: 620px;
                    margin: 28px auto;
                    background: #161b22;
                    border-radius: 14px;
                    overflow: hidden;
                    box-shadow: 0 16px 36px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.08);
                    border: 1px solid #30363d;
                }
                .header {
                    background: linear-gradient(180deg, #12161d 0%%, #161b22 100%%);
                    padding: 36px 32px 30px 32px;
                    text-align: center;
                    border-bottom: 3px solid #ff6a2b;
                }
                .logo-container {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: 12px;
                    margin-bottom: 8px;
                }
                .logo-title {
                    font-family: 'Oswald', 'Segoe UI', sans-serif;
                    font-size: 30px;
                    font-weight: 700;
                    letter-spacing: 0.5px;
                    margin: 0;
                    color: #ffffff;
                    text-transform: uppercase;
                }
                .logo-title span {
                    color: #ff6a2b;
                }
                .logo-subtitle {
                    font-size: 11px;
                    text-transform: uppercase;
                    letter-spacing: 2.5px;
                    color: #8b949e;
                    font-weight: 600;
                    margin: 8px 0 0 0;
                }
                .content {
                    padding: 32px 32px 28px 32px;
                }
                .greeting {
                    font-size: 22px;
                    font-weight: 700;
                    color: #ffffff;
                    margin: 0 0 14px 0;
                }
                .intro-text {
                    font-size: 14.5px;
                    line-height: 1.65;
                    color: #94a3b8;
                    margin: 0 0 26px 0;
                }
                .intro-text code {
                    background: #21262d;
                    color: #ffb020;
                    padding: 2px 8px;
                    border-radius: 4px;
                    font-family: monospace;
                    border: 1px solid #30363d;
                }
                .role-card {
                    background: #0d1117;
                    border: 1px solid #30363d;
                    border-left: 4px solid %s;
                    border-radius: 10px;
                    padding: 22px;
                    margin-bottom: 26px;
                }
                .role-badge {
                    display: inline-block;
                    background-color: %s;
                    color: #ffffff;
                    font-size: 11.5px;
                    font-weight: 700;
                    padding: 5px 14px;
                    border-radius: 6px;
                    text-transform: uppercase;
                    letter-spacing: 0.8px;
                    margin-bottom: 10px;
                }
                .role-title {
                    font-size: 17px;
                    font-weight: 700;
                    color: #ffffff;
                    margin: 4px 0 8px 0;
                }
                .role-desc {
                    font-size: 13.5px;
                    color: #8b949e;
                    margin: 0 0 14px 0;
                    line-height: 1.55;
                }
                .duties-list {
                    margin: 0;
                    padding-left: 18px;
                    font-size: 13px;
                    color: #cbd5e1;
                    line-height: 1.65;
                }
                .duties-list li {
                    margin-bottom: 6px;
                }
                .security-note {
                    background: rgba(255, 106, 43, 0.08);
                    border: 1px solid rgba(255, 106, 43, 0.25);
                    border-radius: 8px;
                    padding: 14px 18px;
                    font-size: 12.5px;
                    color: #ffb020;
                    line-height: 1.55;
                    margin-bottom: 26px;
                }
                .action-container {
                    text-align: center;
                    margin: 32px 0 12px 0;
                }
                .cta-button {
                    display: inline-block;
                    background: linear-gradient(135deg, #ff6a2b 0%%, #e55d22 100%%);
                    color: #ffffff !important;
                    text-decoration: none;
                    font-family: 'Oswald', 'Segoe UI', sans-serif;
                    font-size: 16px;
                    font-weight: 700;
                    letter-spacing: 0.5px;
                    text-transform: uppercase;
                    padding: 14px 36px;
                    border-radius: 6px;
                    box-shadow: 0 4px 16px rgba(255, 106, 43, 0.4);
                    transition: all 0.2s ease;
                }
                .footer {
                    background-color: #0d1117;
                    border-top: 1px solid #21262d;
                    padding: 24px 32px;
                    text-align: center;
                    font-size: 12px;
                    color: #6e7681;
                    line-height: 1.6;
                }
                .footer p {
                    margin: 4px 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <!-- Header with SVG Brand Logo -->
                <div class="header">
                    <div class="logo-container">
                        <!-- Isometric Hexagonal Cube Logo Icon -->
                        <svg width="44" height="44" viewBox="0 0 200 200" style="vertical-align: middle;">
                            <path d="M 100 22 L 168 61 L 168 139 L 100 178 L 32 139 L 32 61 Z" fill="none" stroke="#FF6A2B" stroke-width="12" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 32 61 L 100 100 L 168 61" fill="none" stroke="#FF6A2B" stroke-width="10" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 66 80 L 66 122 L 100 141" fill="none" stroke="#FF6A2B" stroke-width="10" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 100 100 L 100 156" fill="none" stroke="#FFB020" stroke-width="11" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 76 100 L 138 64" fill="none" stroke="#FFB020" stroke-width="11" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 112 59 L 147 59 L 144 94" fill="none" stroke="#FFB020" stroke-width="10" stroke-linecap="round" stroke-linejoin="round" />
                        </svg>
                        <h1 class="logo-title">Logi<span>Track</span> S.A.</h1>
                    </div>
                    <p class="logo-subtitle">Sistema Integral WMS &amp; ERP Industrial</p>
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

    public String buildSuperAdminNotificationHtml(String nombreAdmin, String nombreNuevoUsuario, String emailNuevoUsuario) {
        int currentYear = Year.now().getValue();
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Nuevo Registro de Usuario en LogiTrack S.A.</title>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Oswald:wght@600;700&family=Inter:wght@400;500;600;700&display=swap');
                body { margin: 0; padding: 0; background-color: #0d1117; font-family: 'Inter', sans-serif; color: #e2e8f0; }
                .container { max-width: 620px; margin: 28px auto; background: #161b22; border-radius: 14px; overflow: hidden; border: 1px solid #30363d; }
                .header { background: linear-gradient(180deg, #12161d 0%%, #161b22 100%%); padding: 32px; text-align: center; border-bottom: 3px solid #e11d48; }
                .logo-title { font-family: 'Oswald', sans-serif; font-size: 28px; font-weight: 700; color: #ffffff; text-transform: uppercase; margin: 0; }
                .logo-title span { color: #ff6a2b; }
                .content { padding: 32px; }
                .alert-card { background: rgba(225, 29, 72, 0.08); border: 1px solid rgba(225, 29, 72, 0.3); border-left: 4px solid #e11d48; border-radius: 10px; padding: 20px; margin-bottom: 24px; }
                .cta-button { display: inline-block; background: linear-gradient(135deg, #e11d48 0%%, #be123c 100%%); color: #ffffff !important; text-decoration: none; font-family: 'Oswald', sans-serif; font-size: 15px; font-weight: 700; text-transform: uppercase; padding: 12px 30px; border-radius: 6px; }
                .footer { background-color: #0d1117; border-top: 1px solid #21262d; padding: 20px; text-align: center; font-size: 12px; color: #6e7681; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <div style="margin-bottom:8px;">
                        <svg width="40" height="40" viewBox="0 0 200 200" style="vertical-align: middle;">
                            <path d="M 100 22 L 168 61 L 168 139 L 100 178 L 32 139 L 32 61 Z" fill="none" stroke="#FF6A2B" stroke-width="12" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 32 61 L 100 100 L 168 61" fill="none" stroke="#FF6A2B" stroke-width="10" stroke-linecap="round" stroke-linejoin="round" />
                            <path d="M 100 100 L 100 156" fill="none" stroke="#FFB020" stroke-width="11" stroke-linecap="round" stroke-linejoin="round" />
                        </svg>
                    </div>
                    <h1 class="logo-title">Logi<span>Track</span> S.A.</h1>
                    <p style="font-size:11px; text-transform:uppercase; letter-spacing:2px; color:#8b949e; margin:6px 0 0;">Control de Acceso &amp; Seguridad</p>
                </div>
                <div class="content">
                    <h2 style="font-size:20px; color:#ffffff; margin-top:0;">👑 Hola, %s (Super Admin)</h2>
                    <p style="font-size:14.5px; color:#94a3b8; line-height:1.6;">
                        Se ha registrado una nueva persona en la plataforma de <strong>LogiTrack S.A.</strong> Por defecto se le asignó el rol de <strong>EMPLEADO</strong>.
                    </p>
                    <div class="alert-card">
                        <span style="background:#e11d48; color:white; font-size:11px; font-weight:700; padding:3px 10px; border-radius:4px; text-transform:uppercase;">NUEVO REGISTRO PENDIENTE</span>
                        <h3 style="font-size:16px; color:white; margin:10px 0 6px;">%s</h3>
                        <p style="font-size:13.5px; color:#cbd5e1; margin:0 0 10px;">Correo electrónico: <code style="background:#21262d; color:#ffb020; padding:2px 6px; border-radius:4px;">%s</code></p>
                        <p style="font-size:12.5px; color:#8b949e; margin:0;">💬 <em>Mira, esta persona fue registrada en el sistema. Por favor verifica su labor corporativa y asígnale el rol correspondiente (ADMIN, SUPERVISOR, GERENTE, COMPRAS o EMPLEADO).</em></p>
                    </div>
                    <div style="text-align:center; margin-top:28px;">
                        <a href="http://localhost:8081" class="cta-button" target="_blank">Gestionar Usuarios &amp; Asignar Rol</a>
                    </div>
                </div>
                <div class="footer">
                    <p><strong>LogiTrack S.A.</strong> • Control de Seguridad y Accesos</p>
                    <p>© %d LogiTrack S.A. Todos los derechos reservados.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
            nombreAdmin != null && !nombreAdmin.isBlank() ? nombreAdmin : "Super Admin",
            nombreNuevoUsuario,
            emailNuevoUsuario,
            currentYear
        );
    }

    private String getRoleDisplayName(RolUsuario rol) {
        if (rol == null) return "Usuario Registrado";
        return switch (rol) {
            case SUPER_ADMIN -> "Super Administrador Global";
            case ADMIN -> "Administrador del Sistema";
            case SUPERVISOR -> "Supervisor de Operaciones & Calidad";
            case GERENTE_LOGISTICA -> "Gerente de Logística & Distribución";
            case JEFE_COMPRAS -> "Jefe de Compras & Abastecimiento";
            case EMPLEADO -> "Especialista en Operaciones & Bodega";
        };
    }

    private String getRoleBadgeColor(RolUsuario rol) {
        if (rol == null) return "#8993a8";
        return switch (rol) {
            case SUPER_ADMIN -> "#e11d48";       // Crimson Red
            case ADMIN -> "#ff6a2b";            // Accent Orange
            case SUPERVISOR -> "#ffb020";       // Amber Gold
            case GERENTE_LOGISTICA -> "#33d6a6";// Emerald Teal
            case JEFE_COMPRAS -> "#4ea1ff";     // Cyber Cyan
            case EMPLEADO -> "#8993a8";         // Industrial Slate
        };
    }

    private String getRoleIcon(RolUsuario rol) {
        if (rol == null) return "👤";
        return switch (rol) {
            case SUPER_ADMIN -> "👑";
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
            case SUPER_ADMIN -> "Cuentas con autoridad máxima en el sistema LogiTrack S.A. Recibes alertas prioritarias sobre nuevos registros de personal para verificar sus labores y asignarles el rol definitivo.";
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
            case SUPER_ADMIN -> """
                <li><strong>Gestión Global de Personal:</strong> Notificaciones directas vía correo y asignación de roles para personal registrado.</li>
                <li><strong>Administración Total:</strong> Control de seguridad, perfiles, bodegas, inventarios y finanzas.</li>
                <li><strong>Auditoría Completa:</strong> Trazabilidad ilimitada de operaciones e inicio de sesión.</li>
            """;
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

    public String buildRoleChangeEmailHtml(String nombreCompleto, String email, RolUsuario rolAnterior, RolUsuario nuevoRol) {
        String oldRoleName = getRoleDisplayName(rolAnterior);
        String newRoleName = getRoleDisplayName(nuevoRol);
        String newBadgeColor = getRoleBadgeColor(nuevoRol);
        String newRoleIcon = getRoleIcon(nuevoRol);
        String newRoleDescription = getRoleDescription(nuevoRol);
        String newRoleDutiesHtml = getRoleDutiesHtml(nuevoRol);
        int currentYear = Year.now().getValue();

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Actualización de Rol - LogiTrack S.A.</title>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Oswald:wght@600;700&family=Inter:wght@400;500;600;700&display=swap');
                body {
                    margin: 0; padding: 0; background-color: #0d1117;
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    color: #e2e8f0;
                }
                .container {
                    max-width: 620px; margin: 28px auto; background: #161b22;
                    border-radius: 14px; overflow: hidden;
                    box-shadow: 0 16px 36px rgba(0, 0, 0, 0.4); border: 1px solid #30363d;
                }
                .header {
                    background: linear-gradient(180deg, #12161d 0%%, #161b22 100%%);
                    padding: 32px 40px; text-align: center; border-bottom: 1px solid #30363d;
                }
                .logo-title {
                    font-family: 'Oswald', sans-serif; font-size: 26px; font-weight: 700;
                    letter-spacing: 2px; color: #ffffff; text-transform: uppercase; margin: 0;
                }
                .subtitle {
                    font-size: 13px; color: #8b949e; letter-spacing: 1.5px; text-transform: uppercase; margin-top: 6px;
                }
                .content { padding: 36px 40px; }
                .greeting { font-size: 18px; font-weight: 600; color: #ffffff; margin-bottom: 16px; }
                .text { font-size: 14.5px; line-height: 1.6; color: #c9d1d9; margin-bottom: 24px; }
                .role-card {
                    background: #21262d; border-radius: 10px; padding: 22px; margin-bottom: 28px;
                    border-left: 4px solid %s; border: 1px solid #30363d;
                }
                .role-header { display: flex; align-items: center; margin-bottom: 12px; }
                .role-icon { font-size: 24px; margin-right: 12px; }
                .role-badge {
                    display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px;
                    font-weight: 700; text-transform: uppercase; color: #ffffff; background-color: %s;
                }
                .change-summary {
                    background: #0d1117; padding: 12px 16px; border-radius: 6px; font-size: 13px;
                    color: #8b949e; margin-bottom: 12px; border: 1px solid #30363d;
                }
                .footer {
                    background-color: #0d1117; padding: 24px 40px; text-align: center;
                    border-top: 1px solid #30363d; font-size: 12px; color: #8b949e;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <div class="logo-title">LOGITRACK S.A.</div>
                    <div class="subtitle">SISTEMA DE GESTIÓN Y AUDITORÍA DE BODEGAS</div>
                </div>
                <div class="content">
                    <div class="greeting">Hola, %s</div>
                    <div class="text">
                        Te informamos que un Administrador de la plataforma ha actualizado tu nivel de acceso y responsabilidades en el sistema.
                    </div>
                    <div class="role-card">
                        <div class="change-summary">
                            <strong>Cambio de Rol:</strong> %s ➔ <span style="color:#58a6ff; font-weight:700;">%s</span>
                        </div>
                        <div class="role-header">
                            <span class="role-icon">%s</span>
                            <span class="role-badge">%s</span>
                        </div>
                        <div style="font-size: 13.5px; color: #c9d1d9; line-height: 1.5;">
                            %s
                        </div>
                    </div>
                    <div style="font-size: 14px; font-weight: 600; color: #ffffff; margin-bottom: 10px;">
                        Tus nuevas atribuciones en la plataforma:
                    </div>
                    <ul style="padding-left: 20px; font-size: 13.5px; color: #c9d1d9; line-height: 1.6; margin-bottom: 28px;">
                        %s
                    </ul>
                </div>
                <div class="footer">
                    &copy; %d LogiTrack S.A. Todos los derechos reservados.
                </div>
            </div>
        </body>
        </html>
        """.formatted(
            newBadgeColor,
            newBadgeColor,
            nombreCompleto,
            oldRoleName,
            newRoleName,
            newRoleIcon,
            newRoleName,
            newRoleDescription,
            newRoleDutiesHtml,
            currentYear
        );
    }
}
