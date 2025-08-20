// ==============================
// Smooth scroll en links internos
// ==============================
// Smooth scroll SOLO en /
document.addEventListener("click", (e) => {
  const a = e.target.closest("a.nav-link, a.link-footer");
  if (!a) return;

  const href = a.getAttribute("href") || a.getAttribute("th:href") || "";
  // Thymeleaf ya habrá resuelto a "/#menu", "/#reservas", etc.
  const isAnchorToIndex = href.startsWith("/#");

  if (!isAnchorToIndex) return; // Menú (/menu) u otras rutas: deja pasar

  const onIndex =
    location.pathname === "/" || location.pathname.endsWith("/index"); // por si sirves /index

  if (!onIndex) {
    // NO hagas preventDefault: navega a "/" y luego el # ancla
    return;
  }

  // Estás en "/", intercepta y hace smooth-scroll
  e.preventDefault();
  const id = href.slice(2); // "/#menu" -> "menu"
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
});

// ====================================
// Cerrar offcanvas al navegar
// ====================================
(() => {
  const oc = document.getElementById("offcanvasNav");
  if (!oc) return;
  oc.addEventListener("click", (e) => {
    const link = e.target.closest("a.nav-link");
    if (link) bootstrap.Offcanvas.getOrCreateInstance(oc).hide();
  });
})();

// ==============================
// ScrollSpy (resalta item activo)
// ==============================
// ScrollSpy (item activo) con offset dinámico
(() => {
  const nav = document.getElementById("mainNavbar");
  if (!("bootstrap" in window) || !nav) return;

  const build = () =>
    new bootstrap.ScrollSpy(document.body, {
      target: "#mainNavbar",
      offset: Math.ceil(nav.getBoundingClientRect().height) + 8,
    });

  let spy = build();

  const refresh = () => spy?.refresh?.();

  if ("ResizeObserver" in window) {
    new ResizeObserver(refresh).observe(nav);
  } else {
    window.addEventListener("resize", refresh, { passive: true });
    nav.addEventListener("transitionend", (e) => {
      if (/padding|height/.test(e.propertyName)) refresh();
    });
  }
})();

// ==================================
// Navbar: compactar al hacer scroll
// ==================================
(() => {
  const nav = document.getElementById("mainNavbar");
  if (!nav) return;

  const onScroll = () => {
    if (window.scrollY > 10) nav.classList.add("is-scrolled");
    else nav.classList.remove("is-scrolled");
  };
  onScroll();
  window.addEventListener("scroll", onScroll, { passive: true });
})();

// ==================================
// Marca: animación de entrada 1 vez
// ==================================
(() => {
  const pieces = document.querySelectorAll("#brandML .brand-piece");
  if (!pieces.length) return;

  pieces.forEach((p) => p.classList.add("reveal-once"));
  setTimeout(
    () => pieces.forEach((p) => p.classList.remove("reveal-once")),
    900
  );
})();

// ==============================
// Ripple en botones .btn-ripple
// ==============================
(() => {
  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-ripple");
    if (!btn) return;

    const rect = btn.getBoundingClientRect();
    const x = e.clientX - rect.left,
      y = e.clientY - rect.top;

    // Posiciona el pseudo-elemento
    btn.style.setProperty("--rLeft", x - 5 + "px");
    btn.style.setProperty("--rTop", y - 5 + "px");

    // Reinicia / dispara animación
    btn.classList.remove("rippling");
    // Forzar reflow para reiniciar animación si se hace click muy rápido
    // eslint-disable-next-line no-unused-expressions
    btn.offsetHeight;
    btn.classList.add("rippling");

    setTimeout(() => btn.classList.remove("rippling"), 600);
  });
})();

// =====================================
// Efecto "magnético" leve en botones auth
// =====================================
(() => {
  const mags = document.querySelectorAll(
    ".btn-pill-outline-auth, .btn-pill-solid-auth"
  );
  if (!mags.length) return;

  mags.forEach((btn) => {
    btn.addEventListener("mousemove", (e) => {
      const r = btn.getBoundingClientRect();
      const dx = (e.clientX - (r.left + r.width / 2)) / r.width;
      const dy = (e.clientY - (r.top + r.height / 2)) / r.height;
      btn.style.transform = `translate(${dx * 4}px, ${dy * 3}px)`;
    });
    btn.addEventListener("mouseleave", () => (btn.style.transform = ""));
  });
})();

// =====================================
// Barra de progreso de scroll superior
// =====================================
(() => {
  const bar = document.getElementById("scrollbarML");
  if (!bar) return;

  const onScroll = () => {
    const h = document.documentElement;
    const max = h.scrollHeight - h.clientHeight;
    const pct = max > 0 ? (h.scrollTop / max) * 100 : 0;
    bar.style.width = pct + "%";
  };
  onScroll();
  window.addEventListener("scroll", onScroll, { passive: true });
})();

// ==============================
// Hero: inicia animación al entrar
// ==============================
(() => {
  const hero = document.getElementById("heroML");
  if (!hero || !("IntersectionObserver" in window)) return;

  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          hero.classList.add("in-view");
          io.disconnect();
        }
      });
    },
    { threshold: 0.2 }
  );

  io.observe(hero);
})();

// ==============================
// Slider Platos Destacados (hero)
// ==============================
(() => {
  const slider = document.getElementById("dishSlider");
  const track = document.getElementById("dishTrack");
  const dotsCt = document.getElementById("dishDots");
  if (!slider || !track || !dotsCt) return;

  const slides = Array.from(track.children);
  const N = slides.length;
  const INTERVAL = 4000;
  let userAt = 0; // timestamp última interacción
  let autoId;

  // Dots
  dotsCt.innerHTML = slides
    .map(
      (_, i) =>
        `<button type="button" aria-label="Ir al slide ${i + 1}"></button>`
    )
    .join("");
  const dots = Array.from(dotsCt.children);

  const width = () => track.clientWidth;
  const idxFromScroll = () => Math.round(track.scrollLeft / width());
  const clampIdx = (i) => (i + N) % N;

  const goTo = (i, smooth = true) => {
    const c = clampIdx(i);
    track.scrollTo({ left: c * width(), behavior: smooth ? "smooth" : "auto" });
    updateUI(c);
  };

  const updateUI = (i = idxFromScroll()) => {
    dots.forEach((d, k) => d.setAttribute("aria-current", String(k === i)));
    slides.forEach((s, k) => s.setAttribute("aria-label", `${k + 1} de ${N}`));
  };

  // Auto-advance con pausa si hay interacción
  const tick = () => {
    if (Date.now() - userAt < INTERVAL - 500) return;
    goTo(idxFromScroll() + 1);
  };
  const start = () => {
    stop();
    autoId = setInterval(tick, INTERVAL);
  };
  const stop = () => autoId && clearInterval(autoId);
  start();

  // Marcar interacción
  const mark = () => {
    userAt = Date.now();
  };
  ["touchstart", "mousedown", "keydown", "wheel", "scroll"].forEach((evt) => {
    track.addEventListener(evt, mark, { passive: true });
  });

  // Scroll vertical -> horizontal SOLO al mantener Shift (predecible)
  track.addEventListener(
    "wheel",
    (e) => {
      // Si el usuario ya hace scroll horizontal (trackpad), no tocamos
      const horizontalIntent = Math.abs(e.deltaX) > Math.abs(e.deltaY);
      if (horizontalIntent) return;
      if (!e.shiftKey) return;

      const maxScroll = track.scrollWidth - track.clientWidth;
      if (maxScroll <= 1) return;

      const atStart = track.scrollLeft <= 0;
      const atEnd = track.scrollLeft >= maxScroll - 1;

      // En los bordes deja pasar el scroll a la página
      if ((e.deltaY < 0 && atStart) || (e.deltaY > 0 && atEnd)) return;

      e.preventDefault(); // requiere {passive:false}
      track.scrollLeft += e.deltaY;
    },
    { passive: false }
  );

  // Prev/Next
  const prevBtn = slider.querySelector(".dish-ctrl.prev");
  const nextBtn = slider.querySelector(".dish-ctrl.next");
  prevBtn?.addEventListener("click", () => {
    mark();
    goTo(idxFromScroll() - 1);
  });
  nextBtn?.addEventListener("click", () => {
    mark();
    goTo(idxFromScroll() + 1);
  });

  // Dots click
  dots.forEach((d, i) =>
    d.addEventListener("click", () => {
      mark();
      goTo(i);
    })
  );

  // Teclado
  track.addEventListener("keydown", (e) => {
    if (e.key === "ArrowRight") {
      mark();
      goTo(idxFromScroll() + 1);
    }
    if (e.key === "ArrowLeft") {
      mark();
      goTo(idxFromScroll() - 1);
    }
  });

  // Resize: realinea
  window.addEventListener(
    "resize",
    () => {
      const i = idxFromScroll();
      goTo(i, false);
    },
    { passive: true }
  );

  // Primer posicionamiento
  updateUI(0);
})();

// ============================
// Page Transition Loader
// ============================
(() => {
  const MIN_MS = 2300;
  const MAX_MS = 5000;
  const FLAG = "ml:showLoader";

  const loader = document.getElementById("pageLoader");
  if (!loader) return;

  document.body.classList.add("js-ready");

  const clr = () => loader.classList.remove("is-entering", "is-leaving");
  const show = () => {
    loader.classList.remove("hidden");
    clr();
    loader.offsetHeight;
    loader.classList.add("is-entering");
  };
  const hide = () => {
    clr();
    loader.offsetHeight;
    loader.classList.add("is-leaving");
  };

  loader.addEventListener("animationend", () => {
    if (loader.classList.contains("is-leaving")) {
      loader.classList.add("hidden");
      clr();
    } else if (loader.classList.contains("is-entering")) {
      loader.classList.remove("is-entering");
    }
  });

  // ORIGEN: marcar navegación saliente
  document.addEventListener(
    "click",
    (e) => {
      const a = e.target.closest("a");
      if (!a) return;
      if (a.hasAttribute("data-no-loader")) return; // <- IGNORA enlaces de lightbox
      const href = a.getAttribute("href");
      if (
        !href ||
        href.startsWith("#") ||
        href.startsWith("mailto:") ||
        href.startsWith("tel:")
      )
        return;
      if (a.target === "_blank" || a.hasAttribute("download")) return;
      if (a.getAttribute("rel")?.includes("external")) return;

      if (
        href.startsWith("http") &&
        new URL(href, location.href).origin !== location.origin
      )
        return;
      if (a.dataset.bsToggle) return; // no interceptar toggles (offcanvas, collapse)

      sessionStorage.setItem(FLAG, String(Date.now()));
      show();
    },
    { capture: true }
  );

  document.addEventListener(
    "submit",
    () => {
      sessionStorage.setItem(FLAG, String(Date.now()));
      show();
    },
    { capture: true }
  );

  window.addEventListener("beforeunload", () => {
    sessionStorage.setItem(FLAG, String(Date.now()));
    show();
  });

  // DESTINO: si viene marcado, mostrar al menos MIN_MS
  const mark = sessionStorage.getItem(FLAG);
  if (mark) {
    sessionStorage.removeItem(FLAG);

    let minDone = false,
      loadDone = false,
      maxDone = false;

    show();

    setTimeout(() => {
      minDone = true;
      maybeHide();
    }, MIN_MS);
    window.addEventListener("load", () => {
      loadDone = true;
      maybeHide();
    });
    setTimeout(() => {
      maxDone = true;
      maybeHide(true);
    }, MAX_MS);

    function maybeHide(force = false) {
      if ((minDone && loadDone) || force) hide();
    }
  } else {
    window.addEventListener("load", () => {
      setTimeout(() => {
        if (!loader.classList.contains("hidden")) hide();
      }, 120);
    });
  }

  // bfcache (volver con atrás)
  window.addEventListener("pageshow", (e) => {
    if (e.persisted) {
      loader.classList.add("hidden");
      clr();
    }
  });
})();

// ==============================
// Registro: toggle de contraseña + validación básica + microestado de botón
// Registro: toggle de contraseña + validación (sin interceptar el submit)
(() => {
  const form = document.getElementById("registerForm");
  if (!form) return;

  const pass = document.getElementById("password");
  const pass2 = document.getElementById("password2");
  const toggle = document.getElementById("togglePassReg");
  const eye = document.getElementById("icon-eye-reg");
  const eyeOff = document.getElementById("icon-eye-off-reg");

  // Ojo igual al login
  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye?.classList.toggle("d-none", isPwd);
    eyeOff?.classList.toggle("d-none", !isPwd);
    const pos = pass.value.length;
    pass.focus();
    pass.setSelectionRange?.(pos, pos);
  });

  // Validación de coincidencia en tiempo real (no bloquea el submit por JS)
  const sync = () => {
    if (pass2.value && pass2.value !== pass.value) {
      pass2.setCustomValidity("No coincide");
    } else {
      pass2.setCustomValidity("");
    }
  };
  pass.addEventListener("input", sync);
  pass2.addEventListener("input", sync);

  //  limpiar teléfono
  const tel = document.getElementById("telefono");
  tel?.addEventListener("input", () => {
    tel.value = tel.value.replace(/[^\d+ ]/g, "").slice(0, 20);
  });
})();

// ============================
// Reservas: validación + límites + WhatsApp
(() => {
  const formReserva = document.getElementById("formReserva");
  const loader = document.getElementById("pageLoader");

  if (!formReserva || !loader) return;

  formReserva.addEventListener("submit", (e) => {
    e.preventDefault();
    e.stopPropagation();

    loader.classList.remove("hidden");

    if (!formReserva.checkValidity()) {
      formReserva.classList.add("was-validated");

      // Ocultar loader después de 2s en caso de error
      setTimeout(() => {
        loader.classList.add("hidden");
      }, 2000);

      return;
    }

    // Si la validación pasa: simular proceso y luego ocultar loader
    setTimeout(() => {
      loader.classList.add("hidden");

      // Mostrar toast de éxito
      const toast = new bootstrap.Toast(
        document.getElementById("toastReserva")
      );
      toast.show();

      formReserva.reset();
      formReserva.classList.remove("was-validated");
    }, 2000);
  });
})();

// ===========================================
// Momentos: Grid uniforme + Lightbox (+ filtros si existen)
// ===========================================
(() => {
  const grid = document.getElementById("momentsGrid");
  if (!grid) return;

  const items = Array.from(grid.querySelectorAll(".moments-c-item"));
  const chips = Array.from(document.querySelectorAll("#momentos .chip"));

  // Filtros por data-group (solo si hay chips)
  let current = "all";
  const applyFilter = (group) => {
    current = group;
    items.forEach((it) => {
      const ok = group === "all" || it.dataset.group === group;
      it.style.display = ok ? "" : "none";
    });
    chips.forEach((c) =>
      c.classList.toggle("active", c.dataset.filter === group)
    );
  };
  if (chips.length) {
    chips.forEach((c) =>
      c.addEventListener("click", () => applyFilter(c.dataset.filter))
    );
    applyFilter("all");
  }

  // Lightbox con Bootstrap Modal
  const modalEl = document.getElementById("momentsModal");
  if (!modalEl || !("bootstrap" in window)) return;

  const modal = new bootstrap.Modal(modalEl, { backdrop: true });
  const imgEl = document.getElementById("momentsModalImg");
  const capEl = document.getElementById("momentsModalCaption");
  const prevBtn = document.getElementById("momentsPrev");
  const nextBtn = document.getElementById("momentsNext");

  const getVisible = () => items.filter((it) => it.style.display !== "none");
  let index = 0;

  const openAt = (i) => {
    const vis = getVisible();
    if (!vis.length) return;
    index = (i + vis.length) % vis.length;
    const a = vis[index];
    imgEl.src = a.getAttribute("href");
    imgEl.alt = a.querySelector("img")?.alt ?? "";
    capEl.textContent = a.dataset.caption ?? "";
    modal.show();
  };

  grid.addEventListener("click", (e) => {
    const a = e.target.closest(".moment-item");
    if (!a) return;
    e.preventDefault(); // evita navegar a la imagen
    const vis = getVisible();
    openAt(vis.indexOf(a));
  });

  prevBtn?.addEventListener("click", () => openAt(index - 1));
  nextBtn?.addEventListener("click", () => openAt(index + 1));

  // Teclado en modal
  modalEl.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") openAt(index - 1);
    if (e.key === "ArrowRight") openAt(index + 1);
  });
})();
// ===========================================
// Momentos (solo videos): hover/tap play + lightbox
// ===========================================
(() => {
  const grid = document.getElementById("momentsVideos");
  const modalEl = document.getElementById("momentsModal");
  if (!grid || !modalEl || !("bootstrap" in window)) return;

  const cards = Array.from(grid.querySelectorAll(".video-card"));
  const getVideo = (card) => card.querySelector("video");
  const modal = new bootstrap.Modal(modalEl, { backdrop: true });
  const modalVideo = document.getElementById("momentsModalVideo");
  const capEl = document.getElementById("momentsModalCaption");
  const prevBtn = document.getElementById("momentsPrev");
  const nextBtn = document.getElementById("momentsNext");

  let index = 0;

  // Hover/tap play (silencioso)
  cards.forEach((card) => {
    const vid = getVideo(card);
    if (!vid) return;

    // Hover (desktop)
    card.addEventListener("mouseenter", () => {
      vid.muted = true;
      vid.play().catch(() => {});
    });
    card.addEventListener("mouseleave", () => {
      // Pausa y rebobina un poco para que el póster vuelva rápido
      vid.pause();
      if (vid.currentTime > 0.2) vid.currentTime = 0.0;
    });

    // Tap (móvil): toggle play
    card.addEventListener(
      "touchstart",
      () => {
        if (vid.paused) {
          vid.muted = true;
          vid.play().catch(() => {});
        } else {
          vid.pause();
        }
      },
      { passive: true }
    );
  });

  // Pausar cuando salen del viewport
  if ("IntersectionObserver" in window) {
    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          const vid = entry.target.querySelector("video");
          if (!vid) return;
          if (!entry.isIntersecting) vid.pause();
        });
      },
      { threshold: 0.1 }
    );
    cards.forEach((c) => io.observe(c));
  }

  // Abrir modal al click
  const openAt = (i) => {
    index = (i + cards.length) % cards.length;
    const card = cards[index];
    const src = card.getAttribute("data-src");
    const poster = card.getAttribute("data-poster");
    const caption = card.getAttribute("data-caption") || "";

    // Cargar fuente
    modalVideo.pause();
    modalVideo.removeAttribute("src");
    modalVideo.innerHTML = ""; // limpia <source> previos
    const source = document.createElement("source");
    source.src = src;
    source.type = "video/mp4";
    modalVideo.appendChild(source);
    modalVideo.poster = poster || "";
    capEl.textContent = caption;

    // Mostrar y reproducir (silencioso por defecto)
    modal.show();
    // Esperar a que el modal termine transición
    setTimeout(() => {
      modalVideo.muted = true;
      modalVideo.play().catch(() => {});
      modalVideo.focus();
    }, 160);
  };

  grid.addEventListener("click", (e) => {
    const card = e.target.closest(".video-card");
    if (!card) return;
    e.preventDefault();
    openAt(cards.indexOf(card));
  });

  prevBtn?.addEventListener("click", () => openAt(index - 1));
  nextBtn?.addEventListener("click", () => openAt(index + 1));

  // Teclado en modal
  modalEl.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") openAt(index - 1);
    if (e.key === "ArrowRight") openAt(index + 1);
  });

  // Limpiar al cerrar
  modalEl.addEventListener("hidden.bs.modal", () => {
    modalVideo.pause();
    modalVideo.removeAttribute("src");
    modalVideo.innerHTML = "";
  });
})();
document.querySelectorAll(".video-card").forEach((card) => {
  const video = card.querySelector("video");

  if (video) {
    card.addEventListener("mouseenter", () => {
      video.muted = true;
      video.currentTime = 0;
      video.play().catch(() => {});
    });

    card.addEventListener("mouseleave", () => {
      video.pause();
      video.currentTime = 0; // vuelve al inicio
      video.load(); // fuerza redibujo con el póster
    });
  }
});
// ==============================
// Reservas: validación + límites + WhatsApp
// ==============================
(() => {
  const form = document.getElementById("formReserva");
  if (!form) return;

  // Evita doble binding
  if (form.dataset.bound === "1") return;
  form.dataset.bound = "1";

  const tel = form.querySelector("#rWhatsapp");
  const fecha = form.querySelector("#rFecha");
  const hora = form.querySelector("#rHora");
  const pax = form.querySelector("#rPersonas");
  const minus = form.querySelector('[data-step="-1"]');
  const plus = form.querySelector('[data-step="1"]');
  const toastEl = document.getElementById("toastReserva");
  const btnWhats = document.getElementById("btnWhats");

  // Reglas: hoy en adelante, 12:00–18:00 (solo si existen los inputs)
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");

  if (fecha) fecha.min = `${yyyy}-${mm}-${dd}`;
  if (hora) {
    hora.min = "12:00";
    hora.max = "18:00";
  }

  // Tel: solo dígitos
  if (tel) {
    tel.addEventListener("input", () => {
      tel.value = tel.value.replace(/[^\d]/g, "").slice(0, 12);
    });
  }

  // Stepper personas
  if (pax) {
    const clamp = (v, min, max) => Math.max(min, Math.min(max, v));
    const minus = form.querySelector('[data-step="-1"]');
    const plus = form.querySelector('[data-step="1"]');
    minus?.addEventListener(
      "click",
      () => (pax.value = clamp(+pax.value - 1, 1, 20))
    );
  }

  // Stepper personas
  const clamp = (v, min, max) => Math.max(min, Math.min(max, v));
  minus.addEventListener(
    "click",
    () => (pax.value = clamp(+pax.value - 1, 1, 20))
  );
  plus.addEventListener(
    "click",
    () => (pax.value = clamp(+pax.value + 1, 1, 20))
  );

  // Bootstrap validation
  // Estado "enviando" visual (simulado)
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!form.checkValidity() || !validHora()) {
      form.classList.add("was-validated");
      return;
    }

    const btnSubmit = form.querySelector('button[type="submit"]');
    const original = btnSubmit.innerHTML;
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = "Enviando…";

    // simula petición
    await new Promise((r) => setTimeout(r, 800));

    if (window.bootstrap) new bootstrap.Toast(toastEl).show();
    form.reset();
    form.classList.remove("was-validated");
    btnSubmit.disabled = false;
    btnSubmit.innerHTML = original;
  });

  // Chips: auto-rellenan la hora
  document.querySelectorAll("#reservas .time-chips .chip").forEach((ch) => {
    ch.addEventListener("click", () => {
      hora.value = ch.dataset.time;
      hora.dispatchEvent(new Event("input"));
    });
  });

  function validHora() {
    if (!hora || !hora.value) return false;
    const [H, M] = hora.value.split(":").map(Number);
    const mins = H * 60 + M;
    return mins >= 12 * 60 && mins <= 18 * 60;
  }

  // WhatsApp compose
  btnWhats.addEventListener("click", () => {
    form.classList.add("was-validated");
    if (!form.checkValidity() || !validHora()) return;

    const nombre = document.getElementById("rNombre").value.trim();
    const whatsapp = tel.value.trim();
    const email = document.getElementById("rEmail").value.trim();
    const f = fecha.value;
    const h = hora.value;
    const personas = pax.value;
    const ocasion = document.getElementById("rOcas").value;
    const notas = document.getElementById("rNotas").value.trim();

    const to = "573016113151";
    const msg = `Hola Mi Leña 👋
Quiero reservar:
• Nombre: ${nombre}
• WhatsApp: ${whatsapp}
• Email: ${email || "—"}
• Fecha: ${f}
• Hora: ${h}
• Personas: ${personas}
• Ocasión: ${ocasion}
• Notas: ${notas || "—"}`;
    const url = `https://wa.me/${to}?text=${encodeURIComponent(msg)}`;
    window.open(url, "_blank", "noopener");
  });
})();

// Altura dinámica del navbar → --navH (sin parpadeos)
(() => {
  const nav = document.getElementById("mainNavbar");
  if (!nav) return;

  const set = () =>
    document.documentElement.style.setProperty(
      "--navH",
      nav.getBoundingClientRect().height + "px"
    );

  set();

  // Actualiza en tiempo real si cambia la altura (padding, colapso, etc.)
  if ("ResizeObserver" in window) {
    const ro = new ResizeObserver(set);
    ro.observe(nav);
  } else {
    // Fallback razonable
    ["load", "resize", "transitionend", "animationend"].forEach((evt) =>
      window.addEventListener(evt, set, { passive: true })
    );
    nav.addEventListener("transitionend", (e) => {
      if (/padding|height/.test(e.propertyName)) set();
    });
  }
})();

(() => {
  const path = location.pathname.replace(/\/+$/, ""); // quita / final
  document.querySelectorAll(".menu-toggle .btn-switch").forEach((a) => {
    const target = (a.getAttribute("href") || "").replace(/\/+$/, "");
    if (path === target) {
      a.classList.add("active");
      a.setAttribute("aria-current", "page");
    }
  });
})();
// MENÚ title microinteractions
(() => {
  const title = document.querySelector(".menu-title");
  if (!title) return;

  // aparecer al entrar en viewport
  if ("IntersectionObserver" in window) {
    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            title.classList.add("in-view");
            io.disconnect();
          }
        });
      },
      { threshold: 0.2 }
    );
    io.observe(title);
  } else {
    // fallback
    title.classList.add("in-view");
  }

  // parallax leve al mover el mouse (respeta reduce-motion)
  const prefersReduced = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
  ).matches;
  if (!prefersReduced) {
    const onMove = (e) => {
      const r = title.getBoundingClientRect();
      const dx = (e.clientX - (r.left + r.width / 2)) / r.width;
      const dy = (e.clientY - (r.top + r.height / 2)) / r.height;
      title.style.transform = `translateY(${
        title.classList.contains("in-view") ? 0 : 6
      }px) translate(${dx * 2}px, ${dy * 1.2}px)`;
    };
    title.addEventListener("mousemove", onMove);
    title.addEventListener("mouseleave", () => {
      title.style.transform = "";
    });
  }
})();
// Conteo de platos en badge (server o DOM)
(() => {
  const title = document.querySelector(".menu-title");
  const badge = title?.querySelector(".menu-count");
  if (!title || !badge) return;

  const fromAttr = title.getAttribute("data-count");
  let n = fromAttr && !isNaN(+fromAttr) ? +fromAttr : null;

  if (n == null) {
    // Fallback: contar tarjetas renderizadas
    n = document.querySelectorAll(".card-menu").length;
  }
  badge.textContent = new Intl.NumberFormat("es-CO").format(n) + " platos";
})();

// ===== Toggle activo por path (seguro) =====
(() => {
  const href = location.pathname.replace(/\/+$/, "");
  document.querySelectorAll(".menu-toggle .btn-switch").forEach((a) => {
    const path = a.getAttribute("href") || a.getAttribute("th:href") || "";
    if (!path) return;
    if (href.endsWith("/menu") && /\/menu$/.test(path))
      a.classList.add("active");
    if (href.endsWith("/menu/table") && /\/menu\/table$/.test(path))
      a.classList.add("active");
  });
})();

// ===== Tabla: ordenar, buscar, filtrar =====
(() => {
  const table = document.getElementById("menuTable");
  if (!table) return;

  const tbody = table.tBodies[0];
  const rows = Array.from(tbody.rows);

  // Construir opciones de categoría desde data-cat
  const catSel = document.getElementById("mt-category");
  const cats = [
    ...new Set(rows.map((r) => (r.dataset.cat || "").trim()).filter(Boolean)),
  ].sort();
  if (catSel && cats.length) {
    cats.forEach((c) => {
      const opt = document.createElement("option");
      opt.value = c;
      opt.textContent = c;
      catSel.appendChild(opt);
    });
  }

  const search = document.getElementById("mt-search");
  const availSel = document.getElementById("mt-availability");

  let sortKey = null; // índice de columna o función
  let sortDir = 0; // 1 asc, -1 desc

  function cmpText(a, b) {
    return a.localeCompare(b, "es", { sensitivity: "base" });
  }
  function cmpNum(a, b) {
    return (+a || 0) - (+b || 0);
  }
  function cmpBool(a, b) {
    return (+a || 0) - (+b || 0);
  }

  function getCellVal(tr, idx) {
    const td = tr.cells[idx];
    if (!td) return "";
    // precio: leer número plano
    if (td.querySelector(".price-chip")) {
      const n = td.textContent.replace(/[^\d]/g, "");
      return n || "0";
    }
    return td.textContent.trim();
  }

  function apply() {
    const q = (search?.value || "").toLowerCase();
    const cat = catSel?.value || "";
    const av = availSel?.value ?? "";

    // filtrar
    const filtered = rows.filter((tr) => {
      const name = (tr.dataset.name || "").toLowerCase();
      const desc = (tr.dataset.desc || "").toLowerCase();
      const catOk = !cat || (tr.dataset.cat || "") === cat;
      const avOk = av === "" || (tr.dataset.ok || "1") === av;
      const textOk = !q || name.includes(q) || desc.includes(q);
      return catOk && avOk && textOk;
    });

    // ordenar
    if (sortKey != null) {
      const ths = table.tHead.rows[0].cells;
      const type = ths[sortKey].dataset.sort || "text";
      const cmp = type === "num" ? cmpNum : type === "bool" ? cmpBool : cmpText;
      filtered.sort(
        (a, b) =>
          cmp(getCellVal(a, sortKey), getCellVal(b, sortKey)) * (sortDir || 1)
      );
    }

    // pintar + estado vacío
    if (!filtered.length) {
      const tr = document.createElement("tr");
      const td = document.createElement("td");
      td.colSpan = table.tHead.rows[0].cells.length;
      td.className = "text-center py-4";
      td.innerHTML = `<div class="text-muted">No hay platos que coincidan con tu búsqueda.</div>`;
      tr.appendChild(td);
      tbody.replaceChildren(tr);
    } else {
      tbody.replaceChildren(...filtered);
    }
  }

  // Navegación por fila si existe data-href en el <tr>
  document.getElementById("menuTable")?.addEventListener("click", (e) => {
    const tr = e.target.closest("tr");
    if (!tr) return;
    tr.classList.add("clicked");
    setTimeout(() => tr.classList.remove("clicked"), 220);
    if (tr.dataset.href) window.location.href = tr.dataset.href;
  });

  // headers sortables
  table.tHead.querySelectorAll("th[data-sort]").forEach((th, idx) => {
    th.addEventListener("click", () => {
      // reset aria-sort en todos
      table.tHead
        .querySelectorAll("th[aria-sort]")
        .forEach((x) => x.removeAttribute("aria-sort"));

      if (sortKey === idx) {
        sortDir = sortDir === 1 ? -1 : 1;
      } else {
        sortKey = idx;
        sortDir = 1;
      }
      th.setAttribute("aria-sort", sortDir === 1 ? "ascending" : "descending");
      apply();
    });
  });

  // eventos
  const deb = (fn, ms = 180) => {
    let t;
    return (...a) => {
      clearTimeout(t);
      t = setTimeout(() => fn(...a), ms);
    };
  };
  search?.addEventListener("input", deb(apply, 120));
  catSel?.addEventListener("change", apply);
  availSel?.addEventListener("change", apply);

  apply();
})();

// ====== Calcula la altura real de la barra de filtros para el sticky del THEAD ======
(() => {
  const tools = document.querySelector(".tools-bar");
  const root = document.documentElement;

  function setToolsH() {
    if (!tools) {
      root.style.setProperty("--toolsH", "0px");
      return;
    }
    const cs = getComputedStyle(tools);
    const mb = parseFloat(cs.marginBottom) || 0; // incluye margen inferior si lo hubiese
    root.style.setProperty("--toolsH", tools.offsetHeight + mb + "px");
  }

  setToolsH();
  window.addEventListener("resize", setToolsH, { passive: true });
  if (window.ResizeObserver && tools)
    new ResizeObserver(setToolsH).observe(tools);
})();

document.getElementById("menuTable")?.addEventListener("click", (e) => {
  const tr = e.target.closest("tr");
  if (!tr) return;
  tr.classList.add("clicked");
  setTimeout(() => tr.classList.remove("clicked"), 220);
});
// ====== MENÚ TABLE: Toggle de moneda COP/USD (con tasa configurable) ======
(() => {
  const currencySel = document.getElementById("mt-currency");
  const chips = Array.from(document.querySelectorAll("#menuTable .price-chip"));
  if (!currencySel || !chips.length) return;

  // Tasa por defecto y posibilidad de override desde el HTML:
  // <select id="mt-currency" data-rate="4095">…</select>
  const DEFAULT_RATE = 4100;
  let rate = parseFloat(currencySel.dataset.rate || DEFAULT_RATE);
  if (!isFinite(rate) || rate <= 0) rate = DEFAULT_RATE;

  // Opcional: decimales en USD (0 = sin centavos). Puedes cambiarlo a 2 si quieres centavos.
  const USD_DECIMALS = 0;

  // Guarda el precio COP original en data-cop y precalcula el USD en data-usd
  chips.forEach((chip) => {
    const cop = parseInt(chip.textContent.replace(/[^\d]/g, ""), 10) || 0;
    chip.dataset.cop = String(cop);
    chip.dataset.usd = String(Math.round(cop / rate)); // si pones USD_DECIMALS=2, usa toFixed(2)
  });

  const fmtCOP = (n) => new Intl.NumberFormat("es-CO").format(n);
  const fmtUSD = (n) =>
    new Intl.NumberFormat("en-US", {
      minimumFractionDigits: USD_DECIMALS,
      maximumFractionDigits: USD_DECIMALS,
    }).format(n);

  function render(cur) {
    if (cur === "USD") {
      chips.forEach((chip) => {
        const usd =
          USD_DECIMALS === 2
            ? Math.round((+chip.dataset.cop || 0) / rate)
            : (+chip.dataset.cop || 0) / rate;
        chip.dataset.usd = String(usd);
        chip.textContent = "$" + fmtUSD(usd);
      });
    } else {
      chips.forEach((chip) => {
        const cop = +chip.dataset.cop || 0;
        chip.textContent = "$" + fmtCOP(cop);
      });
    }

    // No es necesario reordenar: ordenar por COP o USD mantiene el mismo orden (factor constante).
    // Si igual quieres refrescar, descomenta la línea siguiente para re-aplicar filtros/orden:
    // document.getElementById("mt-category")?.dispatchEvent(new Event("change"));
  }

  // Inicial
  render(currencySel.value);

  // Cambios de moneda
  currencySel.addEventListener("change", () => render(currencySel.value));

  // API simple por si quieres cambiar la tasa en runtime:
  // window.setMenuUsdRate(4095)
  window.setMenuUsdRate = function (newRate) {
    const r = parseFloat(newRate);
    if (!isFinite(r) || r <= 0) return;
    rate = r;
    render(currencySel.value);
  };
})();
document.addEventListener("DOMContentLoaded", () => {
  // ======= CATEGORÍAS: construir menú desde data-cat de las filas =======
  const table = document.querySelector("#menuTable");
  const catBtn = document.querySelector("#catDropdownBtn");
  const catMenu = document.querySelector("#catDropdownMenu");
  const catHidden = document.querySelector("#mt-category");

  if (table && catMenu && catBtn && catHidden) {
    // recolectar categorías únicas
    const rows = Array.from(table.querySelectorAll("tbody tr"));
    const set = new Set(
      rows.map((r) => (r.getAttribute("data-cat") || "").trim()).filter(Boolean)
    );
    // orden preferente
    const prefer = ["res", "pollo", "pescado", "bebidas", "otros"];
    const cats = Array.from(set).sort((a, b) => {
      const ia = prefer.indexOf(a),
        ib = prefer.indexOf(b);
      if (ia !== -1 || ib !== -1)
        return (ia === -1 ? 999 : ia) - (ib === -1 ? 999 : ib);
      return a.localeCompare(b);
    });

    // limpia menú y agrega "Todas"
    catMenu.innerHTML = "";
    const liAll = document.createElement("li");
    liAll.innerHTML = `<a class="dropdown-item active" href="#" data-value="">Todas las categorías</a>`;
    catMenu.appendChild(liAll);

    // agrega cada categoría
    cats.forEach((c) => {
      const li = document.createElement("li");
      li.innerHTML = `<a class="dropdown-item" href="#" data-value="${c}">${
        c.charAt(0).toUpperCase() + c.slice(1)
      }</a>`;
      catMenu.appendChild(li);
    });

    // manejar selección
    catMenu.addEventListener("click", (e) => {
      const a = e.target.closest(".dropdown-item");
      if (!a) return;
      e.preventDefault();
      // activa visualmente
      catMenu
        .querySelectorAll(".dropdown-item")
        .forEach((i) => i.classList.remove("active"));
      a.classList.add("active");
      // actualiza botón + hidden y dispara change
      catBtn.textContent = a.textContent.trim();
      catHidden.value = a.dataset.value || "";
      catHidden.dispatchEvent(new Event("change", { bubbles: true }));
    });
  }

  // ======= MONEDA: sincroniza hidden e invoca tu lógica existente =======
  const curBtn = document.querySelector("#currencyDropdownBtn");
  const curMenu = document.querySelector("#currencyDropdownMenu");
  const curHidden = document.querySelector("#mt-currency");

  if (curBtn && curMenu && curHidden) {
    curMenu.addEventListener("click", (e) => {
      const a = e.target.closest(".dropdown-item");
      if (!a) return;
      e.preventDefault();

      curMenu
        .querySelectorAll(".dropdown-item")
        .forEach((i) => i.classList.remove("active"));
      a.classList.add("active");

      curBtn.textContent = a.textContent.trim();
      curHidden.value = a.dataset.value || "COP";
      // copia data-rate si viene en el item
      if (a.dataset.rate) {
        curHidden.setAttribute("data-rate", a.dataset.rate);
      }
      curHidden.dispatchEvent(new Event("change", { bubbles: true }));
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const btnAddToCart = document.querySelectorAll(
    "[data-bs-target='#modalCarrito']"
  );
  const cartBadge = document.getElementById("cartBadge");

  let cartCount = 0;

  btnAddToCart.forEach((btn) => {
    btn.addEventListener("click", () => {
      cartCount++;
      cartBadge.textContent = cartCount;
      cartBadge.classList.remove("d-none", "animate__fadeOut");
      cartBadge.classList.add("animate__bounceIn");

      // Después de la animación, limpiamos la clase para que se repita
      setTimeout(() => {
        cartBadge.classList.remove("animate__bounceIn");
      }, 1000);
    });
  });
});

// Login: toggle igual que registro (solo UI, no intercepta submit)
(() => {
  const pass = document.getElementById("password");
  const toggle = document.getElementById("togglePass");
  const eye = document.getElementById("icon-eye");
  const eyeOff = document.getElementById("icon-eye-off");
  const capsHint = document.getElementById("capsHint");
  if (!pass || !toggle) return;

  toggle.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";

    // Igual que en registro: alterna iconos, foco y caret al final
    eye?.classList.toggle("d-none", isPwd);
    eyeOff?.classList.toggle("d-none", !isPwd);
    const pos = pass.value.length;
    pass.focus();
    pass.setSelectionRange?.(pos, pos);

    // Accesibilidad
    toggle.setAttribute("aria-pressed", String(isPwd));
    toggle.setAttribute(
      "aria-label",
      isPwd ? "Ocultar contraseña" : "Mostrar contraseña"
    );
  });

  // Hint de CapsLock como en registro
  const setCaps = (e) => {
    const on = e.getModifierState && e.getModifierState("CapsLock");
    capsHint?.classList.toggle("d-none", !on);
  };
  pass.addEventListener("keydown", setCaps);
  pass.addEventListener("keyup", setCaps);
  pass.addEventListener("focus", setCaps);
  pass.addEventListener("blur", () => capsHint?.classList.add("d-none"));
})();

// Register: solo toggle y validación de coincidencia en tiempo real
(() => {
  const form = document.getElementById("registerForm");
  if (!form) return;

  const pass = document.getElementById("password");
  const pass2 = document.getElementById("password2");
  const toggle = document.getElementById("togglePassReg");
  const eye = document.getElementById("icon-eye-reg");
  const eyeOff = document.getElementById("icon-eye-off-reg");

  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye?.classList.toggle("d-none", isPwd);
    eyeOff?.classList.toggle("d-none", !isPwd);
    const pos = pass.value.length;
    pass.focus();
    pass.setSelectionRange?.(pos, pos);
  });

  // valida coincidencia sin bloquear el submit
  const sync = () => {
    if (pass2.value && pass2.value !== pass.value) {
      pass2.setCustomValidity("No coincide");
    } else {
      pass2.setCustomValidity("");
    }
  };
  pass.addEventListener("input", sync);
  pass2.addEventListener("input", sync);

  // (opcional) limpiar teléfono
  const tel = document.getElementById("telefono");
  tel?.addEventListener("input", () => {
    tel.value = tel.value.replace(/[^\d+ ]/g, "").slice(0, 20);
  });
})();
