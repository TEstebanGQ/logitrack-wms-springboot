/* ==========================================
   LogiTrack S.A. - Clock & Gauge Utilities
   ========================================== */

(function () {
    // Reloj en tiempo real para el topbar
    function tick() {
        var now = new Date();
        var pad = function(n){ return String(n).padStart(2,'0'); };
        var clockEl = document.getElementById('clock');
        if (clockEl) clockEl.textContent = pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds());
        var dateEl = document.getElementById('topbar-date');
        if (dateEl) {
            var months = ['ENE','FEB','MAR','ABR','MAY','JUN','JUL','AGO','SEP','OCT','NOV','DIC'];
            dateEl.textContent = pad(now.getDate()) + ' ' + months[now.getMonth()] + ' ' + now.getFullYear();
        }
    }
    
    document.addEventListener('DOMContentLoaded', function() {
        tick();
        setInterval(tick, 1000);
    });

    // Gauge fill animación
    window.animateGauges = function () {
        document.querySelectorAll('.gauge-fill[data-fill]').forEach(function(el) {
            requestAnimationFrame(function() {
                el.style.width = el.getAttribute('data-fill') + '%';
            });
        });
    };
})();
