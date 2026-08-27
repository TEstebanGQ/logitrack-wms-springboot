/* ==========================================
   LogiTrack S.A. - Custom Confirm Modal Component
   ========================================== */

const ConfirmDialog = {
    show({ title = '¿Confirmar Acción?', message = '¿Estás seguro de realizar esta acción?', confirmText = 'Sí, Eliminar', cancelText = 'Cancelar', type = 'danger' }) {
        return new Promise((resolve) => {
            const modal = document.getElementById('modal-confirm');
            const titleEl = document.getElementById('confirm-modal-title');
            const msgEl = document.getElementById('confirm-modal-message');
            const btnConfirm = document.getElementById('btn-confirm-action');
            const btnCancel = document.getElementById('btn-confirm-cancel');
            const iconEl = document.getElementById('confirm-modal-icon');

            if (!modal || !titleEl || !msgEl || !btnConfirm || !btnCancel) {
                resolve(window.confirm(message));
                return;
            }

            titleEl.innerText = title;
            msgEl.innerText = message;
            btnConfirm.innerText = confirmText;
            btnCancel.innerText = cancelText;

            if (type === 'danger') {
                btnConfirm.className = 'btn btn-danger';
                if (iconEl) iconEl.innerText = '⚠️';
            } else {
                btnConfirm.className = 'btn btn-primary';
                if (iconEl) iconEl.innerText = '❓';
            }

            modal.classList.add('active');

            const handleConfirm = () => {
                cleanup();
                modal.classList.remove('active');
                resolve(true);
            };

            const handleCancel = () => {
                cleanup();
                modal.classList.remove('active');
                resolve(false);
            };

            const cleanup = () => {
                btnConfirm.removeEventListener('click', handleConfirm);
                btnCancel.removeEventListener('click', handleCancel);
            };

            btnConfirm.addEventListener('click', handleConfirm);
            btnCancel.addEventListener('click', handleCancel);
        });
    }
};

window.ConfirmDialog = ConfirmDialog;
