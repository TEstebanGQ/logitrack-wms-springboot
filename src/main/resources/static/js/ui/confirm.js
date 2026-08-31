/* ==========================================
   LogiTrack S.A. - Custom Confirm & Prompt Modal Component
   ========================================== */

const ConfirmDialog = {
    show({ title = '¿Confirmar Acción?', message = '¿Estás seguro de realizar esta acción?', confirmText = 'Sí, Confirmar', cancelText = 'Cancelar', type = 'danger' }) {
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

            // Close any existing open form modals to avoid stacking overlays
            document.querySelectorAll('.modal-overlay.active, .modal-backdrop.active').forEach(m => {
                if (m.id !== 'modal-confirm' && m.id !== 'modal-prompt') {
                    m.classList.remove('active');
                    m.style.display = 'none';
                }
            });

            titleEl.innerText = title;
            msgEl.innerText = message;
            btnConfirm.innerText = confirmText;
            btnCancel.innerText = cancelText;

            if (type === 'danger') {
                btnConfirm.className = 'btn btn-danger';
                if (iconEl) iconEl.innerHTML = `<svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>`;
            } else {
                btnConfirm.className = 'btn btn-primary';
                if (iconEl) iconEl.innerHTML = `<svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>`;
            }

            modal.style.display = 'flex';
            modal.classList.add('active');

            const handleConfirm = () => {
                cleanup();
                resolve(true);
            };

            const handleCancel = () => {
                cleanup();
                resolve(false);
            };

            const cleanup = () => {
                btnConfirm.removeEventListener('click', handleConfirm);
                btnCancel.removeEventListener('click', handleCancel);
                modal.classList.remove('active');
                modal.style.display = 'none';
            };

            btnConfirm.addEventListener('click', handleConfirm);
            btnCancel.addEventListener('click', handleCancel);
        });
    },

    prompt({ title = 'Información Requerida', message = 'Ingresa la información solicitada:', placeholder = 'Escribe el motivo...', defaultValue = '', confirmText = 'Aceptar' }) {
        return new Promise((resolve) => {
            const modal = document.getElementById('modal-prompt');
            const titleEl = document.getElementById('prompt-modal-title');
            const msgEl = document.getElementById('prompt-modal-message');
            const inputEl = document.getElementById('prompt-modal-input');
            const selectEl = document.getElementById('prompt-modal-select');
            const btnConfirm = document.getElementById('btn-prompt-confirm');
            const btnCancel = document.getElementById('btn-prompt-cancel');

            if (!modal || !inputEl || !btnConfirm || !btnCancel) {
                resolve(window.prompt(message, defaultValue));
                return;
            }

            // Close any existing open form modals to avoid stacking overlays
            document.querySelectorAll('.modal-overlay.active, .modal-backdrop.active').forEach(m => {
                if (m.id !== 'modal-confirm' && m.id !== 'modal-prompt') {
                    m.classList.remove('active');
                    m.style.display = 'none';
                }
            });

            if (titleEl) titleEl.innerText = title;
            if (msgEl) msgEl.innerText = message;
            if (btnConfirm) btnConfirm.innerText = confirmText;

            if (selectEl) selectEl.style.display = 'none';
            inputEl.style.display = 'block';
            inputEl.value = defaultValue;
            inputEl.placeholder = placeholder;

            modal.style.display = 'flex';
            modal.classList.add('active');
            setTimeout(() => inputEl.focus(), 100);

            const handleConfirm = () => {
                const val = inputEl.value.trim();
                cleanup();
                resolve(val || null);
            };

            const handleCancel = () => {
                cleanup();
                resolve(null);
            };

            const handleKeyDown = (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    handleConfirm();
                } else if (e.key === 'Escape') {
                    e.preventDefault();
                    handleCancel();
                }
            };

            const cleanup = () => {
                btnConfirm.removeEventListener('click', handleConfirm);
                btnCancel.removeEventListener('click', handleCancel);
                inputEl.removeEventListener('keydown', handleKeyDown);
                modal.classList.remove('active');
                modal.style.display = 'none';
            };

            btnConfirm.addEventListener('click', handleConfirm);
            btnCancel.addEventListener('click', handleCancel);
            inputEl.addEventListener('keydown', handleKeyDown);
        });
    },

    select({ title = 'Seleccionar Opción', message = 'Selecciona una de las opciones disponibles:', options = [], defaultValue = '', confirmText = 'Guardar' }) {
        return new Promise((resolve) => {
            const modal = document.getElementById('modal-prompt');
            const titleEl = document.getElementById('prompt-modal-title');
            const msgEl = document.getElementById('prompt-modal-message');
            const inputEl = document.getElementById('prompt-modal-input');
            const selectEl = document.getElementById('prompt-modal-select');
            const btnConfirm = document.getElementById('btn-prompt-confirm');
            const btnCancel = document.getElementById('btn-prompt-cancel');

            if (!modal || !selectEl || !btnConfirm || !btnCancel) {
                resolve(null);
                return;
            }

            document.querySelectorAll('.modal-overlay.active, .modal-backdrop.active').forEach(m => {
                if (m.id !== 'modal-confirm' && m.id !== 'modal-prompt') {
                    m.classList.remove('active');
                    m.style.display = 'none';
                }
            });

            if (titleEl) titleEl.innerText = title;
            if (msgEl) msgEl.innerText = message;
            if (btnConfirm) btnConfirm.innerText = confirmText;

            if (inputEl) inputEl.style.display = 'none';
            selectEl.style.display = 'block';

            selectEl.innerHTML = options.map(opt => {
                const val = typeof opt === 'object' ? opt.value : opt;
                const lbl = typeof opt === 'object' ? (opt.label || opt.value) : opt;
                const isSelected = val === defaultValue ? 'selected' : '';
                return `<option value="${val}" ${isSelected}>${lbl}</option>`;
            }).join('');

            modal.style.display = 'flex';
            modal.classList.add('active');
            setTimeout(() => selectEl.focus(), 100);

            const handleConfirm = () => {
                const val = selectEl.value;
                cleanup();
                resolve(val || null);
            };

            const handleCancel = () => {
                cleanup();
                resolve(null);
            };

            const handleKeyDown = (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    handleConfirm();
                } else if (e.key === 'Escape') {
                    e.preventDefault();
                    handleCancel();
                }
            };

            const cleanup = () => {
                btnConfirm.removeEventListener('click', handleConfirm);
                btnCancel.removeEventListener('click', handleCancel);
                selectEl.removeEventListener('keydown', handleKeyDown);
                modal.classList.remove('active');
                modal.style.display = 'none';
            };

            btnConfirm.addEventListener('click', handleConfirm);
            btnCancel.addEventListener('click', handleCancel);
            selectEl.addEventListener('keydown', handleKeyDown);
        });
    },

    cancelPrompt() {
        const modal = document.getElementById('modal-prompt');
        if (modal) {
            modal.classList.remove('active');
            modal.style.display = 'none';
        }
    }
};

window.ConfirmDialog = ConfirmDialog;

