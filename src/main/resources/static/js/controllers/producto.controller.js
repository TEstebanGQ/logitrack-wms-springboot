/* ==========================================
   LogiTrack S.A. - Producto Controller Module
   ========================================== */

const ProductoModuleController = {
    async load(filtroStockBajo = false) {
        try {
            let productos;
            if (filtroStockBajo) {
                productos = await ProductoService.getStockBajo(10);
            } else {
                productos = await ProductoService.getAll();
            }
            ProductoRenderer.renderTable(productos);
        } catch (err) {
            console.error('Error cargando productos:', err);
            Toast.error('Error al cargar productos');
        }
    },

    async openCreateModal() {
        const form = document.getElementById('form-producto');
        if (form) form.reset();
        document.getElementById('producto-id').value = '';
        document.getElementById('modal-producto-title').innerText = 'Nuevo Producto';
        await Promise.all([
            this.populateCategoriaSelect(),
            this.renderBodegasDistributionGrid(null)
        ]);
        App.openModal('modal-producto');
    },

    async edit(id) {
        try {
            const producto = await ProductoService.getById(id);
            await Promise.all([
                this.populateCategoriaSelect(producto.categoriaId),
                this.renderBodegasDistributionGrid(producto.id)
            ]);
            document.getElementById('producto-id').value = producto.id;
            document.getElementById('producto-nombre').value = producto.nombre;
            if (document.getElementById('producto-categoria-id')) {
                document.getElementById('producto-categoria-id').value = producto.categoriaId || '';
            }
            if (document.getElementById('producto-stock-minimo')) {
                document.getElementById('producto-stock-minimo').value = producto.stockMinimo || 10;
            }
            document.getElementById('producto-precio').value = producto.precio;
            document.getElementById('producto-descripcion').value = producto.descripcion || '';
            document.getElementById('modal-producto-title').innerText = 'Editar Producto';
            App.openModal('modal-producto');
        } catch (err) {
            Toast.error('No se pudo cargar el producto');
        }
    },

    async renderBodegasDistributionGrid(productoId = null) {
        const container = document.getElementById('producto-bodegas-distribution-container');
        if (!container) return;
        container.innerHTML = '';

        try {
            this.activeBodegas = await BodegaService.getAll(true).catch(() => []);
            if (!this.activeBodegas || this.activeBodegas.length === 0) {
                container.innerHTML = `<div style="color:var(--text-muted); font-size:12px; text-align:center; padding:8px;">No hay bodegas activas.</div>`;
                return;
            }

            let inventarioList = [];
            if (productoId) {
                inventarioList = await ProductoService.getInventario(productoId).catch(() => []);
            }

            if (Array.isArray(inventarioList) && inventarioList.length > 0) {
                inventarioList.forEach(item => {
                    this.agregarFilaBodega(item.bodegaId, item.stockActual);
                });
            } else {
                // Si es un producto nuevo o sin bodegas asignadas, agregamos 1 fila por defecto
                this.agregarFilaBodega(this.activeBodegas[0]?.id || null, 0);
            }

            this.recalcularTotalStock();
        } catch (err) {
            console.error('Error renderizando distribución de bodegas:', err);
        }
    },

    agregarFilaBodega(selectedBodegaId = null, cantidad = 0) {
        const container = document.getElementById('producto-bodegas-distribution-container');
        if (!container) return;
        if (!this.activeBodegas || this.activeBodegas.length === 0) return;

        const row = document.createElement('div');
        row.className = 'bodega-assignment-row';
        row.style.cssText = 'display:flex; align-items:center; gap:8px; background:rgba(255,255,255,0.03); padding:6px 10px; border-radius:6px; border:1px solid rgba(255,255,255,0.08);';

        const opts = this.activeBodegas.map(b => 
            `<option value="${b.id}" ${b.id == selectedBodegaId ? 'selected' : ''}>${b.nombre} (${b.ubicacion || 'General'})</option>`
        ).join('');

        row.innerHTML = `
            <select class="form-select bodega-select-input" style="flex:1; padding:5px 8px; font-size:12px;" onchange="ProductoModuleController.recalcularTotalStock()">
                ${opts}
            </select>
            <div style="display:flex; align-items:center; gap:4px;">
                <input type="number" 
                       class="form-input stock-bodega-input" 
                       value="${cantidad}" 
                       min="0" 
                       style="width:85px; padding:4px 8px; font-size:12px; text-align:right; font-family:var(--font-mono); font-weight:700;"
                       oninput="ProductoModuleController.recalcularTotalStock()">
                <span style="font-size:11px; color:var(--text-muted);">u.</span>
            </div>
            <button type="button" 
                    title="Remover esta bodega del producto"
                    style="background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:16px; padding:2px 6px; border-radius:4px; line-height:1; transition:color 0.2s;"
                    onmouseover="this.style.color='var(--accent-danger, #ef4444)'"
                    onmouseout="this.style.color='var(--text-muted)'"
                    onclick="this.closest('.bodega-assignment-row').remove(); ProductoModuleController.recalcularTotalStock();">&times;</button>
        `;

        container.appendChild(row);
        this.recalcularTotalStock();
    },

    recalcularTotalStock() {
        let total = 0;
        document.querySelectorAll('.bodega-assignment-row').forEach(row => {
            const input = row.querySelector('.stock-bodega-input');
            if (input) {
                const val = parseInt(input.value, 10);
                if (!isNaN(val) && val > 0) {
                    total += val;
                }
            }
        });
        const hiddenStock = document.getElementById('producto-stock');
        if (hiddenStock) hiddenStock.value = total;

        const badge = document.getElementById('producto-stock-total-badge');
        if (badge) badge.innerText = `Stock Total: ${total} u.`;
    },

    async save(formData) {
        const id = document.getElementById('producto-id').value;
        const stockPorBodega = {};
        let totalStockSum = 0;

        document.querySelectorAll('.bodega-assignment-row').forEach(row => {
            const select = row.querySelector('.bodega-select-input');
            const input = row.querySelector('.stock-bodega-input');
            if (select && input) {
                const bId = select.value;
                const val = parseInt(input.value, 10);
                const cant = (!isNaN(val) && val >= 0) ? val : 0;
                if (bId) {
                    stockPorBodega[bId] = (stockPorBodega[bId] || 0) + cant;
                    totalStockSum += cant;
                }
            }
        });

        const payload = {
            nombre: document.getElementById('producto-nombre').value,
            categoriaId: parseInt(document.getElementById('producto-categoria-id').value, 10),
            precio: parseFloat(document.getElementById('producto-precio').value),
            stockMinimo: parseInt(document.getElementById('producto-stock-minimo').value, 10) || 10,
            stock: totalStockSum,
            stockPorBodega: stockPorBodega,
            descripcion: document.getElementById('producto-descripcion').value
        };

        try {
            if (id) {
                await ProductoService.update(id, payload);
                Toast.success('Producto e inventarios por bodega actualizados con éxito');
            } else {
                await ProductoService.create(payload);
                Toast.success('Producto creado y asignado a bodegas con éxito');
            }
            App.closeModal('modal-producto');
            this.load();
            if (window.App && typeof window.App.loadDashboardData === 'function') {
                window.App.loadDashboardData();
            }
        } catch (err) {
            Toast.error(err.message || 'Error al guardar el producto');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Desactivar Producto',
            message: '¿Estás seguro de desactivar/eliminar este producto del catálogo?',
            confirmText: 'Eliminar Producto',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            await ProductoService.delete(id);
            Toast.success('Producto eliminado correctamente');
            this.load();
            if (window.App && typeof window.App.loadDashboardData === 'function') {
                window.App.loadDashboardData();
            }
        } catch (err) {
            Toast.error(err.message || 'Error al eliminar el producto');
        }
    },

    openCrearCategoriaModal() {
        const form = document.getElementById('form-categoria');
        if (form) form.reset();
        App.openModal('modal-categoria');
    },

    async guardarCategoria(formData) {
        try {
            const nuevaCat = await CategoriaService.create(formData);
            Toast.success(`Categoría '${nuevaCat.nombre}' creada con éxito`);
            App.closeModal('modal-categoria');
            await this.populateCategoriaSelect(nuevaCat.id);
        } catch (err) {
            Toast.error(err.message || 'Error al crear la categoría');
        }
    },

    async populateCategoriaSelect(selectedId = null) {
        try {
            const categorias = await CategoriaService.getAll().catch(() => []);
            const catSelect = document.getElementById('producto-categoria-id');
            if (catSelect) {
                if (!categorias || categorias.length === 0) {
                    catSelect.innerHTML = '<option value="">-- No hay categorías registradas --</option>';
                    return;
                }
                const opts = categorias.map(c =>
                    `<option value="${c.id}" ${c.id === selectedId ? 'selected' : ''}>${c.nombre}</option>`
                ).join('');
                catSelect.innerHTML = opts;

                if (selectedId) {
                    catSelect.value = selectedId;
                }
            }
        } catch (err) {
            console.error('Error cargando categorías:', err);
        }
    },

    async populateBodegaSelect(selectedId = null) {
        try {
            const bodegas = await BodegaService.getAll(true).catch(() => []);
            const bodegaSelect = document.getElementById('producto-bodega-id');
            if (bodegaSelect) {
                if (!bodegas || bodegas.length === 0) {
                    bodegaSelect.innerHTML = '<option value="">-- No hay bodegas activas --</option>';
                    return;
                }
                const opts = bodegas.map(b =>
                    `<option value="${b.id}" ${b.id === selectedId ? 'selected' : ''}>${b.nombre} (${b.ubicacion})</option>`
                ).join('');
                bodegaSelect.innerHTML = opts;

                if (selectedId) {
                    bodegaSelect.value = selectedId;
                }
            }
        } catch (err) {
            console.error('Error cargando bodegas para el producto:', err);
        }
    }
};

window.ProductoModuleController = ProductoModuleController;
