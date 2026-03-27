// =====================================================
//  i18n.js — Motor de internacionalización (ES / EN)
//  Carpeta: src/main/resources/static/js/
// =====================================================

const I18n = (() => {

    // Lee el idioma guardado en localStorage; por defecto "es"
    let currentLang = localStorage.getItem("lang") || "es";

    // ── Aplica las traducciones a TODOS los elementos marcados ──
    function applyTranslations() {
        const dict = translations[currentLang];
        if (!dict) return;

        // 1. Texto visible: <h1 data-i18n="clave">
        document.querySelectorAll("[data-i18n]").forEach(el => {
            const key = el.getAttribute("data-i18n");
            if (dict[key] !== undefined) el.textContent = dict[key];
        });

        // 2. Placeholders: <input data-i18n-placeholder="clave">
        document.querySelectorAll("[data-i18n-placeholder]").forEach(el => {
            const key = el.getAttribute("data-i18n-placeholder");
            if (dict[key] !== undefined) el.placeholder = dict[key];
        });

        // 3. Valor de botones submit: <input type="submit" data-i18n-value="clave">
        document.querySelectorAll("[data-i18n-value]").forEach(el => {
            const key = el.getAttribute("data-i18n-value");
            if (dict[key] !== undefined) el.value = dict[key];
        });

        // 4. Confirm de eliminación: <a data-i18n-confirm="clave">
        document.querySelectorAll("[data-i18n-confirm]").forEach(el => {
            const key = el.getAttribute("data-i18n-confirm");
            if (dict[key] !== undefined) {
                el.onclick = () => confirm(dict[key]);
            }
        });

        // 5. Title de la página (si tiene data-i18n-title en <html>)
        const titleKey = document.documentElement.getAttribute("data-i18n-title");
        if (titleKey && dict[titleKey]) document.title = dict[titleKey];

        // 6. Resalta el botón activo del selector de idioma
        document.querySelectorAll(".lang-btn").forEach(btn => {
            btn.classList.toggle("active", btn.getAttribute("data-lang") === currentLang);
        });
    }

    // ── Cambia el idioma y re-aplica sin recargar ──
    function setLang(lang) {
        if (!translations[lang]) return;
        currentLang = lang;
        localStorage.setItem("lang", lang);
        applyTranslations();
    }

    // ── Devuelve el idioma actual ──
    function getLang() {
        return currentLang;
    }

    // ── Inicializa al cargar la página ──
    document.addEventListener("DOMContentLoaded", applyTranslations);

    // API pública
    return { setLang, getLang, applyTranslations };

})();