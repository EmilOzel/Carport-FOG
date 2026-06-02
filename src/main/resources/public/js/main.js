/* ── Scroll-aware sticky header ── */
(function () {
    var lastScrollY = window.scrollY;

    window.addEventListener('scroll', function () {
        var topbar = document.querySelector('.topbar');
        if (!topbar) return;
        var currentScrollY = window.scrollY;
        if (currentScrollY > lastScrollY && currentScrollY > 68) {
            topbar.classList.add('topbar--hidden');
        } else {
            topbar.classList.remove('topbar--hidden');
        }
        lastScrollY = currentScrollY;
    }, { passive: true });
})();

/* ── Burger menu ── */
document.addEventListener('DOMContentLoaded', function () {
    var btn     = document.getElementById('hamburgerBtn');
    var overlay = document.getElementById('mobOverlay');
    var nav     = document.getElementById('mobNav');

    if (!btn || !overlay || !nav) return;

    function openMenu() {
        btn.classList.add('open');
        overlay.classList.add('open');
        nav.classList.add('open');
        btn.setAttribute('aria-expanded', 'true');
        document.body.style.overflow = 'hidden';
    }

    function closeMenu() {
        btn.classList.remove('open');
        overlay.classList.remove('open');
        nav.classList.remove('open');
        btn.setAttribute('aria-expanded', 'false');
        document.body.style.overflow = '';
    }

    btn.addEventListener('click', function () {
        btn.classList.contains('open') ? closeMenu() : openMenu();
    });
    overlay.addEventListener('click', closeMenu);
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeMenu();
    });
});

/* ── Fortjeneste-beregner (sælger) ── */
function beregnFortjeneste(input) {
    var kostpris        = parseFloat(input.dataset.kostpris) || 0;
    var salgspris       = parseFloat(input.value) || 0;
    var display         = document.getElementById('salgspris-display');
    var pctDisplay      = document.getElementById('fortjeneste-display');
    var advarsel        = document.getElementById('fortjeneste-advarsel');

    if (display) display.textContent = salgspris.toFixed(2) + ' kr';

    if (!pctDisplay || !advarsel) return;

    if (kostpris > 0) {
        var pct = ((salgspris - kostpris) / kostpris * 100);
        pctDisplay.textContent = pct.toFixed(1) + ' %';

        if (pct > 45) {
            advarsel.className = 'fortjeneste-advarsel fortjeneste-advarsel--roed';
            advarsel.textContent = '⚠️ Fortjenesten overstiger 45% – prisen er meget høj!';
            pctDisplay.className = 'fortjeneste-pct--kritisk';
        } else if (pct > 35) {
            advarsel.className = 'fortjeneste-advarsel fortjeneste-advarsel--gul';
            advarsel.textContent = '⚠️ Fortjenesten overstiger 35% – er du sikker på denne pris?';
            pctDisplay.className = 'fortjeneste-pct--advarsel';
        } else {
            advarsel.className = 'fortjeneste-advarsel';
            pctDisplay.className = '';
        }
    } else {
        pctDisplay.textContent = '—';
        pctDisplay.className = '';
        advarsel.className = 'fortjeneste-advarsel';
    }
}
