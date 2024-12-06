// src/main/resources/static/js/export.js

function exportToPDF() {
    // Verificar si jsPDF está disponible
    if (typeof window.jspdf === 'undefined') {
        alert('jsPDF no está cargado correctamente.');
        return;
    }

    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    // Título
    doc.setFontSize(18);
    doc.text("Reportes de Movimientos", 14, 22);
    
    // Obtener la tabla
    const tableElement = document.getElementById("reportsTable");
    const rows = tableElement.querySelectorAll("tr");
    let data = [];

    rows.forEach((row, index) => {
        const cols = row.querySelectorAll("th, td");
        let rowData = [];
        cols.forEach(col => {
            // Reemplazar saltos de línea y espacios adicionales
            let text = col.innerText.replace(/\n/g, ' ').trim();
            rowData.push(text);
        });
        if (index !== 0) { // Ignorar la fila de encabezado
            data.push(rowData);
        }
    });

    // Agregar tabla al PDF usando AutoTable
    if (typeof doc.autoTable === 'function') {
        doc.autoTable({
            head: [["ID", "Tipo de Acción", "Descripción", "Fecha y Hora", "ID Relacionado"]],
            body: data,
            startY: 30,
            theme: 'striped',
            styles: { fontSize: 8 }, // Ajusta el tamaño de fuente según sea necesario
            headStyles: { fillColor: [22, 160, 133] }, // Color personalizado para el encabezado
            columnStyles: {
                0: { cellWidth: 10 }, // ID
                1: { cellWidth: 25 }, // Tipo de Acción
                2: { cellWidth: 50 }, // Descripción
                3: { cellWidth: 30 }, // Fecha y Hora
                4: { cellWidth: 20 }, // ID Relacionado
            },
        });
    } else {
        alert('AutoTable no está cargado correctamente.');
        return;
    }

    // Descargar PDF
    doc.save("reportes.pdf");
}
