// src/main/resources/static/js/pagination.js

document.addEventListener('DOMContentLoaded', function() {
    // Seleccionar todas las tablas que necesitan paginación
    const paginatedTables = document.querySelectorAll('.paginated-table');

    paginatedTables.forEach(function(table) {
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        const pagination = table.nextElementSibling.querySelector('.pagination');
        const pageSizeSelect = table.parentElement.querySelector('#pageSizeSelect'); // Asegúrate de que el select está dentro del mismo contenedor

        if (!pagination || !pageSizeSelect) {
            console.warn('No se encontró el contenedor de paginación o el selector de tamaño de página para la tabla:', table);
            return;
        }

        let currentPage = 1;
        let pageSize = pageSizeSelect.value === 'all' ? 'all' : parseInt(pageSizeSelect.value);
        let totalPages = pageSize === 'all' ? 1 : Math.ceil(rows.length / pageSize);

        /**
         * Función para mostrar una página específica.
         * @param {number} page - Número de página a mostrar.
         */
        function displayPage(page) {
            // Validar número de página
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            currentPage = page;

            // Ocultar todas las filas
            rows.forEach((row, index) => {
                row.style.display = 'none';
            });

            // Mostrar las filas correspondientes a la página actual
            if (pageSize !== 'all') {
                let start = (page - 1) * pageSize;
                let end = start + pageSize;
                rows.slice(start, end).forEach(row => {
                    row.style.display = '';
                });
            } else {
                // Mostrar todas las filas si pageSize es 'all'
                rows.forEach(row => {
                    row.style.display = '';
                });
            }

            updatePagination();
        }

        /**
         * Función para actualizar los controles de paginación.
         */
        function updatePagination() {
            // Limpiar paginación existente
            pagination.innerHTML = '';

            if (pageSize === 'all') {
                return; // No mostrar controles de paginación si se selecciona "Todas"
            }

            totalPages = Math.ceil(rows.length / pageSize);

            // Crear botón "Anterior"
            const prevLi = document.createElement('li');
            prevLi.className = 'page-item' + (currentPage === 1 ? ' disabled' : '');
            const prevLink = document.createElement('a');
            prevLink.className = 'page-link';
            prevLink.href = '#';
            prevLink.innerText = 'Anterior';
            prevLink.onclick = function(e) {
                e.preventDefault();
                if (currentPage > 1) {
                    displayPage(currentPage - 1);
                }
            };
            prevLi.appendChild(prevLink);
            pagination.appendChild(prevLi);

            // Crear botones de página
            for (let i = 1; i <= totalPages; i++) {
                const li = document.createElement('li');
                li.className = 'page-item' + (i === currentPage ? ' active' : '');
                const a = document.createElement('a');
                a.className = 'page-link';
                a.href = '#';
                a.innerText = i;
                a.onclick = (function(page) {
                    return function(e) {
                        e.preventDefault();
                        displayPage(page);
                    };
                })(i);
                li.appendChild(a);
                pagination.appendChild(li);
            }

            // Crear botón "Siguiente"
            const nextLi = document.createElement('li');
            nextLi.className = 'page-item' + (currentPage === totalPages ? ' disabled' : '');
            const nextLink = document.createElement('a');
            nextLink.className = 'page-link';
            nextLink.href = '#';
            nextLink.innerText = 'Siguiente';
            nextLink.onclick = function(e) {
                e.preventDefault();
                if (currentPage < totalPages) {
                    displayPage(currentPage + 1);
                }
            };
            nextLi.appendChild(nextLink);
            pagination.appendChild(nextLi);
        }

        /**
         * Función para cambiar el tamaño de página.
         * @param {string} size - Nuevo tamaño de página seleccionado.
         */
        function changePageSize(size) {
            if (size === 'all') {
                pageSize = 'all';
                currentPage = 1;
                rows.forEach(row => {
                    row.style.display = '';
                });
                pagination.innerHTML = ''; // Limpiar controles de paginación
            } else {
                pageSize = parseInt(size);
                totalPages = Math.ceil(rows.length / pageSize);
                displayPage(1);
            }
        }

        // Evento para cambiar el tamaño de página
        pageSizeSelect.addEventListener('change', function() {
            changePageSize(this.value);
        });

        // Inicializar la tabla
        changePageSize(pageSizeSelect.value);
    });
});
