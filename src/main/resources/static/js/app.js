/* ============================================================
   LogiTrack S.A. — app.js
   Frontend completo: Auth, Bodegas, Productos, Movimientos,
   Auditorías, Reportes
   ============================================================ */

const API = 'http://localhost:8081';

/* ---- Datos en memoria ---- */
let _bodegas     = [];
let _productos   = [];
let _movimientos = [];
let _auditorias  = [];
let _user        = null;

/* ============================================================
   INIT
   ============================================================ */
(function init() {
  const token = localStorage.getItem('lt_token');
  if (!token) { window.location.href = 'index.html'; return; }

  _user = JSON.parse(localStorage.getItem('lt_user') || '{}');

  // Reloj topbar
  setInterval(() => {
    document.getElementById('topbarDate').textContent =
      new Date().toLocaleTimeString('es-CO', { hour:'2-digit', minute:'2-digit' });
  }, 1000);

  // Info usuario
  document.getElementById('sidebarName').textContent = _user.nombre || _user.email;
  document.getElementById('sidebarRole').textContent  = _user.rol || 'EMPLEADO';
  document.getElementById('sidebarAvatar').textContent =
    (_user.nombre || 'U')[0].toUpperCase();

  // Ocultar elementos admin si no es admin
  if (_user.rol !== 'ADMIN') {
    document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
  }

  loadDashboard();
})();

/* ============================================================
   NAVEGACIÓN
   ============================================================ */
function navigate(section) {
  // Sections
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.getElementById('sec-' + section).classList.add('active');

  // Nav items
  document.querySelectorAll('.nav-item').forEach(n => {
    n.classList.toggle('active', n.dataset.section === section);
  });

  // Topbar title
  const titles = {
    dashboard:   '🏠 Dashboard',
    bodegas:     '🏭 Bodegas',
    productos:   '📦 Productos',
    movimientos: '🔄 Movimientos',
    reportes:    '📊 Reportes',
    auditorias:  '🔍 Auditorías'
  };
  document.getElementById('topbarTitle').textContent = titles[section] || section;

  // Cargar datos
  if (section === 'bodegas')     loadBodegas();
  if (section === 'productos')   loadProductos();
  if (section === 'movimientos') loadMovimientos();
  if (section === 'reportes')    loadReportes();
  if (section === 'auditorias')  loadAuditorias();
}

/* ============================================================
   API HELPER
   ============================================================ */
async function apiFetch(path, opts = {}) {
  const token = localStorage.getItem('lt_token');
  const res = await fetch(API + path, {
    ...opts,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token,
      ...(opts.headers || {})
    }
  });
  if (res.status === 401) { logout(); return null; }
  if (res.status === 204) return null;
  const text = await res.text();
  if (!text) return null;
  const data = JSON.parse(text);
  if (!res.ok) throw new Error(data.message || JSON.stringify(data));
  return data;
}

/* ============================================================
   TOAST
   ============================================================ */
function toast(msg, type = 'success') {
  const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `<span class="toast-icon">${icons[type]}</span><span class="toast-msg">${msg}</span>`;
  document.getElementById('toastContainer').appendChild(el);
  setTimeout(() => el.remove(), 4000);
}

/* ============================================================
   MODAL
   ============================================================ */
function openModal(title, bodyHtml, footerHtml) {
  document.getElementById('modalTitle').textContent = title;
  document.getElementById('modalBody').innerHTML    = bodyHtml;
  document.getElementById('modalFooter').innerHTML  = footerHtml;
  document.getElementById('modalOverlay').classList.add('show');
}
function closeModal() {
  document.getElementById('modalOverlay').classList.remove('show');
}
document.getElementById('modalOverlay').addEventListener('click', e => {
  if (e.target.id === 'modalOverlay') closeModal();
});

/* ============================================================
   LOGOUT
   ============================================================ */
function logout() {
  localStorage.removeItem('lt_token');
  localStorage.removeItem('lt_user');
  window.location.href = 'index.html';
}

/* ============================================================
   DASHBOARD
   ============================================================ */
async function loadDashboard() {
  try {
    const [bodegas, productos, movimientos, stockBajo] = await Promise.all([
      apiFetch('/api/bodegas'),
      apiFetch('/api/productos'),
      apiFetch('/api/movimientos'),
      apiFetch('/api/productos/stock-bajo?umbral=10')
    ]);

    _bodegas     = bodegas     || [];
    _productos   = productos   || [];
    _movimientos = movimientos || [];

    document.getElementById('stat-bodegas').textContent     = _bodegas.length;
    document.getElementById('stat-productos').textContent   = _productos.length;
    document.getElementById('stat-movimientos').textContent = _movimientos.length;
    document.getElementById('stat-stockbajo').textContent   = (stockBajo || []).length;

    // Últimos 5 movimientos
    const ultsMovs = [..._movimientos].reverse().slice(0, 5);
    document.getElementById('dashMovTable').innerHTML = ultsMovs.length
      ? ultsMovs.map(m => `
          <tr>
            <td>${badgeTipo(m.tipoMovimiento)}</td>
            <td>${formatDate(m.fecha)}</td>
            <td>${m.usuarioNombre || '—'}</td>
          </tr>`).join('')
      : '<tr><td colspan="3"><div class="empty-state"><p>Sin movimientos</p></div></td></tr>';

    // Stock bajo
    document.getElementById('dashStockTable').innerHTML = (stockBajo || []).length
      ? (stockBajo || []).slice(0, 6).map(p => `
          <tr>
            <td>${p.nombre}</td>
            <td><span class="badge badge-danger">${p.stock}</span></td>
            <td>$${fmt(p.precio)}</td>
          </tr>`).join('')
      : '<tr><td colspan="3"><div class="empty-state"><p>✅ Todos con stock suficiente</p></div></td></tr>';

  } catch (e) { toast('Error al cargar dashboard: ' + e.message, 'error'); }
}

/* ============================================================
   BODEGAS
   ============================================================ */
async function loadBodegas() {
  try {
    _bodegas = await apiFetch('/api/bodegas') || [];
    renderBodegas(_bodegas);
  } catch (e) { toast('Error cargando bodegas: ' + e.message, 'error'); }
}

function renderBodegas(list) {
  document.getElementById('bodegasTable').innerHTML = list.length
    ? list.map(b => `
        <tr>
          <td><span class="badge badge-primary">#${b.id}</span></td>
          <td><strong>${b.nombre}</strong></td>
          <td>📍 ${b.ubicacion}</td>
          <td>${b.capacidad.toLocaleString()}</td>
          <td>${b.encargado}</td>
          <td>${b.activo ? '<span class="badge badge-success">Activa</span>' : '<span class="badge badge-danger">Inactiva</span>'}</td>
          <td>
            <div style="display:flex;gap:6px">
              <button class="btn btn-sm btn-info" onclick="openModalBodega(${b.id})">✏️</button>
              <button class="btn btn-sm btn-danger admin-only" onclick="deleteBodega(${b.id})">🗑️</button>
            </div>
          </td>
        </tr>`).join('')
    : '<tr><td colspan="7"><div class="empty-state"><div class="empty-state-icon">🏭</div><p>No hay bodegas registradas</p></div></td></tr>';

  if (_user.rol !== 'ADMIN') {
    document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
  }
}

function filterBodegas() {
  const q = document.getElementById('searchBodegas').value.toLowerCase();
  renderBodegas(_bodegas.filter(b =>
    b.nombre.toLowerCase().includes(q) ||
    b.ubicacion.toLowerCase().includes(q) ||
    b.encargado.toLowerCase().includes(q)
  ));
}

async function openModalBodega(id = null) {
  const bodega = id ? _bodegas.find(b => b.id === id) : null;
  const title  = id ? 'Editar Bodega' : 'Nueva Bodega';

  const body = `
    <div class="form-row">
      <div class="form-group-modal">
        <label>Nombre *</label>
        <input id="f_nombre" type="text" value="${bodega?.nombre || ''}" placeholder="Bodega Central" required/>
      </div>
      <div class="form-group-modal">
        <label>Encargado *</label>
        <input id="f_encargado" type="text" value="${bodega?.encargado || ''}" placeholder="Juan Pérez" required/>
      </div>
    </div>
    <div class="form-group-modal">
      <label>Ubicación *</label>
      <input id="f_ubicacion" type="text" value="${bodega?.ubicacion || ''}" placeholder="Bogotá, Cundinamarca" required/>
    </div>
    <div class="form-group-modal">
      <label>Capacidad (unidades) *</label>
      <input id="f_capacidad" type="number" min="1" value="${bodega?.capacidad || ''}" placeholder="5000" required/>
    </div>`;

  const footer = `
    <button class="btn btn-outline btn-sm" onclick="closeModal()">Cancelar</button>
    <button class="btn btn-primary btn-sm" onclick="saveBodega(${id})">
      ${id ? '💾 Guardar cambios' : '➕ Crear Bodega'}
    </button>`;

  openModal(title, body, footer);
}

async function saveBodega(id) {
  const payload = {
    nombre:    document.getElementById('f_nombre').value.trim(),
    encargado: document.getElementById('f_encargado').value.trim(),
    ubicacion: document.getElementById('f_ubicacion').value.trim(),
    capacidad: parseInt(document.getElementById('f_capacidad').value)
  };

  if (!payload.nombre || !payload.ubicacion || !payload.encargado || !payload.capacidad) {
    toast('Completa todos los campos', 'warning'); return;
  }

  try {
    if (id) {
      await apiFetch(`/api/bodegas/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      toast('Bodega actualizada ✓', 'success');
    } else {
      await apiFetch('/api/bodegas', { method: 'POST', body: JSON.stringify(payload) });
      toast('Bodega creada ✓', 'success');
    }
    closeModal();
    loadBodegas();
  } catch (e) { toast('Error: ' + e.message, 'error'); }
}

async function deleteBodega(id) {
  if (!confirm('¿Eliminar esta bodega?')) return;
  try {
    await apiFetch(`/api/bodegas/${id}`, { method: 'DELETE' });
    toast('Bodega eliminada', 'warning');
    loadBodegas();
  } catch (e) { toast('Error: ' + e.message, 'error'); }
}

/* ============================================================
   PRODUCTOS
   ============================================================ */
async function loadProductos() {
  try {
    _productos = await apiFetch('/api/productos') || [];
    // Chips de categorías
    const cats = [...new Set(_productos.map(p => p.categoria))];
    document.getElementById('categoriaChips').innerHTML =
      `<span class="chip active" onclick="filterByCat('')">Todos</span>` +
      cats.map(c => `<span class="chip" onclick="filterByCat('${c}')">${c}</span>`).join('');
    renderProductos(_productos);
  } catch (e) { toast('Error cargando productos: ' + e.message, 'error'); }
}

function renderProductos(list) {
  document.getElementById('productosTable').innerHTML = list.length
    ? list.map(p => `
        <tr>
          <td><span class="badge badge-primary">#${p.id}</span></td>
          <td><strong>${p.nombre}</strong><br><small style="color:var(--text-muted)">${p.descripcion || ''}</small></td>
          <td><span class="badge badge-info">${p.categoria}</span></td>
          <td>${stockBadge(p.stock)}</td>
          <td>$${fmt(p.precio)}</td>
          <td>${p.activo ? '<span class="badge badge-success">Activo</span>' : '<span class="badge badge-danger">Inactivo</span>'}</td>
          <td>
            <div style="display:flex;gap:6px">
              <button class="btn btn-sm btn-info admin-only" onclick="openModalProducto(${p.id})">✏️</button>
              <button class="btn btn-sm btn-danger admin-only" onclick="deleteProducto(${p.id})">🗑️</button>
            </div>
          </td>
        </tr>`).join('')
    : '<tr><td colspan="7"><div class="empty-state"><div class="empty-state-icon">📦</div><p>No hay productos</p></div></td></tr>';

  if (_user.rol !== 'ADMIN') {
    document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
  }
}

function filterProductos() {
  const q = document.getElementById('searchProductos').value.toLowerCase();
  renderProductos(_productos.filter(p =>
    p.nombre.toLowerCase().includes(q) || p.categoria.toLowerCase().includes(q)
  ));
}

function filterByCat(cat) {
  document.querySelectorAll('#categoriaChips .chip').forEach(c => {
    c.classList.toggle('active', c.textContent === (cat || 'Todos'));
  });
  renderProductos(cat ? _productos.filter(p => p.categoria === cat) : _productos);
}

async function openModalProducto(id = null) {
  const prod  = id ? _productos.find(p => p.id === id) : null;
  const title = id ? 'Editar Producto' : 'Nuevo Producto';

  const body = `
    <div class="form-row">
      <div class="form-group-modal">
        <label>Nombre *</label>
        <input id="fp_nombre" type="text" value="${prod?.nombre || ''}" placeholder="Laptop Dell" required/>
      </div>
      <div class="form-group-modal">
        <label>Categoría *</label>
        <input id="fp_categoria" type="text" value="${prod?.categoria || ''}" placeholder="Electrónica" required/>
      </div>
    </div>
    <div class="form-row">
      <div class="form-group-modal">
        <label>Stock inicial</label>
        <input id="fp_stock" type="number" min="0" value="${prod?.stock ?? 0}"/>
      </div>
      <div class="form-group-modal">
        <label>Precio *</label>
        <input id="fp_precio" type="number" min="0" step="0.01" value="${prod?.precio || ''}" placeholder="1500000" required/>
      </div>
    </div>
    <div class="form-group-modal">
      <label>Descripción</label>
      <textarea id="fp_desc" placeholder="Descripción opcional...">${prod?.descripcion || ''}</textarea>
    </div>`;

  const footer = `
    <button class="btn btn-outline btn-sm" onclick="closeModal()">Cancelar</button>
    <button class="btn btn-primary btn-sm" onclick="saveProducto(${id})">
      ${id ? '💾 Guardar' : '➕ Crear'}
    </button>`;

  openModal(title, body, footer);
}

async function saveProducto(id) {
  const payload = {
    nombre:      document.getElementById('fp_nombre').value.trim(),
    categoria:   document.getElementById('fp_categoria').value.trim(),
    stock:       parseInt(document.getElementById('fp_stock').value) || 0,
    precio:      parseFloat(document.getElementById('fp_precio').value),
    descripcion: document.getElementById('fp_desc').value.trim()
  };

  if (!payload.nombre || !payload.categoria || isNaN(payload.precio)) {
    toast('Completa los campos requeridos', 'warning'); return;
  }

  try {
    if (id) {
      await apiFetch(`/api/productos/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      toast('Producto actualizado ✓');
    } else {
      await apiFetch('/api/productos', { method: 'POST', body: JSON.stringify(payload) });
      toast('Producto creado ✓');
    }
    closeModal();
    loadProductos();
  } catch (e) { toast('Error: ' + e.message, 'error'); }
}

async function deleteProducto(id) {
  if (!confirm('¿Eliminar este producto?')) return;
  try {
    await apiFetch(`/api/productos/${id}`, { method: 'DELETE' });
    toast('Producto eliminado', 'warning');
    loadProductos();
  } catch (e) { toast('Error: ' + e.message, 'error'); }
}

/* ============================================================
   MOVIMIENTOS
   ============================================================ */
async function loadMovimientos() {
  try {
    _movimientos = await apiFetch('/api/movimientos') || [];
    renderMovimientos(_movimientos);
  } catch (e) { toast('Error cargando movimientos: ' + e.message, 'error'); }
}

function renderMovimientos(list) {
  document.getElementById('movimientosTable').innerHTML = list.length
    ? list.map(m => `
        <tr>
          <td><span class="badge badge-primary">#${m.id}</span></td>
          <td>${badgeTipo(m.tipoMovimiento)}</td>
          <td>${formatDate(m.fecha)}</td>
          <td>${m.usuarioNombre || '—'}</td>
          <td>${m.bodegaOrigen  || '<span style="color:var(--text-muted)">—</span>'}</td>
          <td>${m.bodegaDestino || '<span style="color:var(--text-muted)">—</span>'}</td>
          <td>${(m.detalles || []).length} producto(s)</td>
        </tr>`).join('')
    : '<tr><td colspan="7"><div class="empty-state"><div class="empty-state-icon">🔄</div><p>No hay movimientos</p></div></td></tr>';
}

function filterMovimientos() {
  const q    = document.getElementById('searchMovimientos').value.toLowerCase();
  const tipo = document.getElementById('filterTipoMov').value;
  renderMovimientos(_movimientos.filter(m =>
    (!tipo || m.tipoMovimiento === tipo) &&
    (!q || (m.usuarioNombre||'').toLowerCase().includes(q) ||
           (m.bodegaOrigen||'').toLowerCase().includes(q) ||
           (m.bodegaDestino||'').toLowerCase().includes(q))
  ));
}

let _detalles = [];

async function openModalMovimiento() {
  // Cargar bodegas y productos si no están cargados
  if (!_bodegas.length)   _bodegas   = await apiFetch('/api/bodegas')   || [];
  if (!_productos.length) _productos = await apiFetch('/api/productos') || [];

  _detalles = [];

  const bodegaOpts = _bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
  const body = `
    <div class="form-group-modal">
      <label>Tipo de movimiento *</label>
      <select id="fm_tipo" onchange="toggleBodegas()">
        <option value="ENTRADA">ENTRADA</option>
        <option value="SALIDA">SALIDA</option>
        <option value="TRANSFERENCIA">TRANSFERENCIA</option>
      </select>
    </div>
    <div class="form-row" id="fmBodegas">
      <div class="form-group-modal" id="fmOrigenWrap">
        <label>Bodega Origen</label>
        <select id="fm_origen"><option value="">— Sin origen —</option>${bodegaOpts}</select>
      </div>
      <div class="form-group-modal" id="fmDestinoWrap">
        <label>Bodega Destino *</label>
        <select id="fm_destino"><option value="">— Sin destino —</option>${bodegaOpts}</select>
      </div>
    </div>
    <div class="form-group-modal">
      <label>Observaciones</label>
      <textarea id="fm_obs" placeholder="Notas opcionales..."></textarea>
    </div>
    <div class="form-group-modal">
      <label>Productos</label>
      <div id="fm_detalles"></div>
      <button class="btn btn-outline btn-sm" style="margin-top:8px" onclick="addDetalle()">+ Agregar producto</button>
    </div>`;

  const footer = `
    <button class="btn btn-outline btn-sm" onclick="closeModal()">Cancelar</button>
    <button class="btn btn-primary btn-sm" onclick="saveMovimiento()">📋 Registrar Movimiento</button>`;

  openModal('Registrar Movimiento', body, footer);
  toggleBodegas();
  addDetalle();
}

function toggleBodegas() {
  const tipo = document.getElementById('fm_tipo').value;
  const orig = document.getElementById('fmOrigenWrap');
  const dest = document.getElementById('fmDestinoWrap');
  orig.style.display = (tipo === 'SALIDA'    || tipo === 'TRANSFERENCIA') ? '' : 'none';
  dest.style.display = (tipo === 'ENTRADA'   || tipo === 'TRANSFERENCIA') ? '' : 'none';
}

function addDetalle() {
  const idx  = _detalles.length;
  _detalles.push({ productoId: null, cantidad: 1 });
  const prodOpts = _productos.map(p => `<option value="${p.id}">${p.nombre} (stock: ${p.stock})</option>`).join('');
  const el   = document.createElement('div');
  el.className = 'detalle-item';
  el.id = `det_${idx}`;
  el.innerHTML = `
    <select onchange="_detalles[${idx}].productoId=parseInt(this.value)">
      <option value="">— Producto —</option>${prodOpts}
    </select>
    <input type="number" min="1" value="1" style="width:80px;background:var(--bg-dark);border:1px solid var(--border);border-radius:8px;padding:6px 10px;color:var(--text-primary)"
      oninput="_detalles[${idx}].cantidad=parseInt(this.value)||1"/>
    <button class="btn-remove-detalle" onclick="removeDetalle(${idx})">✕</button>`;
  document.getElementById('fm_detalles').appendChild(el);
}

function removeDetalle(idx) {
  document.getElementById(`det_${idx}`)?.remove();
  _detalles[idx] = null;
}

async function saveMovimiento() {
  const tipo    = document.getElementById('fm_tipo').value;
  const origenEl = document.getElementById('fm_origen');
  const destinoEl= document.getElementById('fm_destino');
  const obs     = document.getElementById('fm_obs').value.trim();

  const detalles = _detalles.filter(d => d && d.productoId).map(d => ({
    productoId: d.productoId, cantidad: d.cantidad
  }));

  if (!detalles.length) { toast('Agrega al menos un producto', 'warning'); return; }

  const payload = { tipoMovimiento: tipo, observaciones: obs, detalles };
  const origenId  = origenEl  ? parseInt(origenEl.value)  || null : null;
  const destinoId = destinoEl ? parseInt(destinoEl.value) || null : null;
  if (origenId)  payload.bodegaOrigenId  = origenId;
  if (destinoId) payload.bodegaDestinoId = destinoId;

  try {
    await apiFetch('/api/movimientos', { method: 'POST', body: JSON.stringify(payload) });
    toast('Movimiento registrado ✓');
    closeModal();
    loadMovimientos();
    loadDashboard();
  } catch (e) { toast('Error: ' + e.message, 'error'); }
}

/* ============================================================
   REPORTES
   ============================================================ */
async function loadReportes() {
  document.getElementById('reportGrid').innerHTML =
    '<div class="loading-overlay"><div class="spinner"></div> Generando reporte...</div>';
  try {
    const data = await apiFetch('/api/reportes/general');
    if (!data) return;

    const maxStock = Math.max(...(data.stockPorBodega || []).map(b => b.stockTotal), 1);
    const maxMov   = Math.max(...(data.productosMasMovidos || []).map(p => p.totalMovido), 1);

    document.getElementById('reportGrid').innerHTML = `
      <div class="report-card">
        <div class="report-card-header">🏭 Stock Total por Bodega</div>
        ${(data.stockPorBodega || []).map(b => `
          <div class="report-bar">
            <div class="report-bar-label" title="${b.bodegaNombre}">${b.bodegaNombre}</div>
            <div style="flex:1;background:var(--bg-input);border-radius:4px;overflow:hidden">
              <div class="report-bar-fill" style="width:${Math.round(b.stockTotal/maxStock*100)}%"></div>
            </div>
            <div class="report-bar-value">${b.stockTotal.toLocaleString()}</div>
          </div>`).join('') || '<p style="padding:20px;color:var(--text-muted)">Sin datos</p>'}
      </div>
      <div class="report-card">
        <div class="report-card-header">📦 Productos más movidos</div>
        ${(data.productosMasMovidos || []).map((p, i) => `
          <div class="report-bar">
            <div class="report-bar-label" title="${p.productoNombre}">${i+1}. ${p.productoNombre}</div>
            <div style="flex:1;background:var(--bg-input);border-radius:4px;overflow:hidden">
              <div class="report-bar-fill" style="width:${Math.round(p.totalMovido/maxMov*100)}%;background:linear-gradient(90deg,var(--secondary),var(--success))"></div>
            </div>
            <div class="report-bar-value">${p.totalMovido}</div>
          </div>`).join('') || '<p style="padding:20px;color:var(--text-muted)">Sin datos</p>'}
      </div>`;
  } catch (e) { toast('Error en reportes: ' + e.message, 'error'); }
}

/* ============================================================
   AUDITORÍAS
   ============================================================ */
async function loadAuditorias() {
  try {
    _auditorias = await apiFetch('/api/auditorias') || [];
    renderAuditorias(_auditorias);
  } catch (e) { toast('Error cargando auditorías: ' + e.message, 'error'); }
}

function renderAuditorias(list) {
  document.getElementById('auditoriasTable').innerHTML = list.length
    ? list.map(a => `
        <tr>
          <td><span class="badge badge-primary">#${a.id}</span></td>
          <td><strong>${a.entidad}</strong>${a.entidadId ? ' #'+a.entidadId : ''}</td>
          <td>${badgeOp(a.tipoOperacion)}</td>
          <td>${formatDate(a.fechaHora)}</td>
          <td>${a.usuarioEmail || '—'}</td>
          <td><small style="color:var(--text-secondary)">${a.descripcion || '—'}</small></td>
        </tr>`).join('')
    : '<tr><td colspan="6"><div class="empty-state"><div class="empty-state-icon">🔍</div><p>Sin registros de auditoría</p></div></td></tr>';
}

function filterAuditorias() {
  const q    = document.getElementById('searchAuditorias').value.toLowerCase();
  const tipo = document.getElementById('filterTipoAud').value;
  renderAuditorias(_auditorias.filter(a =>
    (!tipo || a.tipoOperacion === tipo) &&
    (!q || a.entidad.toLowerCase().includes(q) ||
          (a.usuarioEmail || '').toLowerCase().includes(q) ||
          (a.descripcion  || '').toLowerCase().includes(q))
  ));
}

/* ============================================================
   UTILIDADES
   ============================================================ */
function fmt(n) {
  return parseFloat(n).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

function formatDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleString('es-CO', {
    day:'2-digit', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit'
  });
}

function badgeTipo(tipo) {
  const map = {
    ENTRADA:       '<span class="badge badge-success">↓ ENTRADA</span>',
    SALIDA:        '<span class="badge badge-danger">↑ SALIDA</span>',
    TRANSFERENCIA: '<span class="badge badge-warning">⇄ TRANSFERENCIA</span>'
  };
  return map[tipo] || `<span class="badge badge-primary">${tipo}</span>`;
}

function badgeOp(op) {
  const map = {
    INSERT: '<span class="badge badge-success">INSERT</span>',
    UPDATE: '<span class="badge badge-warning">UPDATE</span>',
    DELETE: '<span class="badge badge-danger">DELETE</span>'
  };
  return map[op] || `<span class="badge badge-primary">${op}</span>`;
}

function stockBadge(s) {
  if (s <= 0)  return `<span class="badge badge-danger">0</span>`;
  if (s < 10)  return `<span class="badge badge-warning">${s}</span>`;
  return `<span class="badge badge-success">${s}</span>`;
}
