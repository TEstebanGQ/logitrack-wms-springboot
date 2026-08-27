// charts.js - Módulo de gráficas Chart.js para LogiTrack

const Charts = (() => {
  let chartStockBodegas = null;
  let chartProductosMovidos = null;
  let chartTiposMovimiento = null;

  const COLORS = {
    primary:   ['#3b82f6','#6366f1','#8b5cf6','#a855f7','#ec4899','#f43f5e'],
    success:   '#22c55e',
    warning:   '#f59e0b',
    danger:    '#ef4444',
    entrada:   '#22c55e',
    salida:    '#ef4444',
    transferencia: '#3b82f6',
  };

  const defaultOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#e2e8f0', font: { family: 'Inter, sans-serif' } } },
      tooltip: {
        backgroundColor: '#1e293b',
        titleColor: '#f1f5f9',
        bodyColor: '#cbd5e1',
        borderColor: '#334155',
        borderWidth: 1,
      }
    },
    scales: {
      x: { ticks: { color: '#94a3b8' }, grid: { color: '#1e293b' } },
      y: { ticks: { color: '#94a3b8' }, grid: { color: '#1e293b' } }
    }
  };

  function destroy(chart) {
    if (chart) { chart.destroy(); }
    return null;
  }

  function renderStockBodegas(data) {
    const ctx = document.getElementById('chartStockBodegas');
    if (!ctx) return;
    chartStockBodegas = destroy(chartStockBodegas);
    chartStockBodegas = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map(d => d.bodegaNombre),
        datasets: [{
          label: 'Stock Total',
          data: data.map(d => d.stockTotal),
          backgroundColor: COLORS.primary,
          borderRadius: 6,
          borderSkipped: false,
        }]
      },
      options: {
        ...defaultOptions,
        plugins: {
          ...defaultOptions.plugins,
          legend: { display: false }
        }
      }
    });
  }

  function renderProductosMovidos(data) {
    const ctx = document.getElementById('chartProductosMovidos');
    if (!ctx) return;
    chartProductosMovidos = destroy(chartProductosMovidos);
    chartProductosMovidos = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: data.map(d => d.productoNombre),
        datasets: [{
          data: data.map(d => d.totalMovido),
          backgroundColor: COLORS.primary,
          hoverOffset: 8,
          borderWidth: 2,
          borderColor: '#0f172a',
        }]
      },
      options: {
        ...defaultOptions,
        scales: {},
        cutout: '65%',
      }
    });
  }

  function renderTiposMovimiento(movimientos) {
    const ctx = document.getElementById('chartTiposMovimiento');
    if (!ctx) return;
    chartTiposMovimiento = destroy(chartTiposMovimiento);

    const conteo = { ENTRADA: 0, SALIDA: 0, TRANSFERENCIA: 0 };
    (movimientos || []).forEach(m => {
      if (conteo[m.tipoMovimiento] !== undefined) conteo[m.tipoMovimiento]++;
    });

    chartTiposMovimiento = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Entradas', 'Salidas', 'Transferencias'],
        datasets: [{
          label: 'Cantidad de Movimientos',
          data: [conteo.ENTRADA, conteo.SALIDA, conteo.TRANSFERENCIA],
          backgroundColor: [COLORS.entrada, COLORS.salida, COLORS.transferencia],
          borderRadius: 6,
          borderSkipped: false,
        }]
      },
      options: {
        ...defaultOptions,
        plugins: { ...defaultOptions.plugins, legend: { display: false } },
        indexAxis: 'y',
      }
    });
  }

  function mostrarEstadoCargando() {
    ['chartStockBodegas','chartProductosMovidos','chartTiposMovimiento'].forEach(id => {
      const canvas = document.getElementById(id);
      if (canvas) {
        const ctx2d = canvas.getContext('2d');
        ctx2d.fillStyle = '#1e293b';
        ctx2d.fillRect(0, 0, canvas.width, canvas.height);
        ctx2d.fillStyle = '#64748b';
        ctx2d.font = '14px Inter, sans-serif';
        ctx2d.textAlign = 'center';
        ctx2d.fillText('Cargando datos...', canvas.width / 2, canvas.height / 2);
      }
    });
  }

  return { renderStockBodegas, renderProductosMovidos, renderTiposMovimiento, mostrarEstadoCargando };
})();
