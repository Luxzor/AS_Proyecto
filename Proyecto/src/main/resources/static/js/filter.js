// src/main/resources/static/js/filter.js

document.addEventListener('DOMContentLoaded', function() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const filterButton = document.getElementById('filterButton');

    function toggleFilterButton() {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        if (startDate && endDate) {
            // Verificar que endDate no sea anterior a startDate
            if (new Date(endDate) >= new Date(startDate)) {
                filterButton.disabled = false;
                endDateInput.setCustomValidity(''); // Limpiar mensajes de error
            } else {
                filterButton.disabled = true;
                endDateInput.setCustomValidity('La fecha fin no puede ser anterior a la fecha inicio.');
            }
        } else {
            filterButton.disabled = true;
            endDateInput.setCustomValidity('');
        }
    }

    // Inicializar el estado del botón al cargar la página
    toggleFilterButton();

    // Añadir eventos de escucha a los campos de fecha
    startDateInput.addEventListener('input', toggleFilterButton);
    endDateInput.addEventListener('input', toggleFilterButton);
});
