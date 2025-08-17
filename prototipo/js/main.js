// ==============================
// Smooth scroll en links internos
// ==============================
(() => {
  const links = document.querySelectorAll('a.nav-link[href^="#"]');
  if (!links.length) return;

  const prefersReduced = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
  ).matches;

  links.forEach((a) => {
    a.addEventListener("click", (e) => {
      const id = a.getAttribute("href");
      if (!id || !id.startsWith("#")) return;
      const target = document.querySelector(id);
      if (!target) return;

      e.preventDefault();
      target.scrollIntoView({
        behavior: prefersReduced ? "auto" : "smooth",
        block: "start",
      });
      history.pushState(null, "", id);
    });
  });
})();

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
(() => {
  if (!("bootstrap" in window)) return;
  new bootstrap.ScrollSpy(document.body, {
    target: "#mainNavbar",
    offset: 100,
  });
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
(() => {
  const form = document.getElementById("registerForm");
  if (!form) return;

  const loader = document.getElementById("pageLoader");
  const pass = document.getElementById("password");
  const pass2 = document.getElementById("password2");
  const toggle = document.getElementById("togglePassReg");
  const eye = document.getElementById("icon-eye-reg");
  const eyeOff = document.getElementById("icon-eye-off-reg");
  const card = document.querySelector(".auth-card");

  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye.classList.toggle("d-none", isPwd);
    eyeOff.classList.toggle("d-none", !isPwd);
    pass.focus();
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    loader.classList.remove("hidden");

    // Validación de contraseñas
    if (pass.value !== pass2.value) {
      pass2.setCustomValidity("No coincide");
    } else {
      pass2.setCustomValidity("");
    }

    if (!form.checkValidity()) {
      form.classList.add("was-validated");

      // Animación shake
      card?.classList.remove("shake");
      void card?.offsetWidth; // Forzar reflow
      card?.classList.add("shake");

      setTimeout(() => {
        loader.classList.add("hidden");
      }, 2000);
      return;
    }

    // microestado del botón
    const btn = form.querySelector('button[type="submit"]');
    const html = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = "Creando cuenta…";

    // Simular registro exitoso
    await new Promise((r) => setTimeout(r, 1000));

    // Redirigir al index
    window.location.href = "index.html";
  });
})();

// ==============================
// Login: toggle de contraseña + validación básica + microestado de botón
(() => {
  const form = document.getElementById("loginForm");
  if (!form) return;

  const loader = document.getElementById("pageLoader");
  const pass = document.getElementById("password");
  const toggle = document.getElementById("togglePass");
  const eye = document.getElementById("icon-eye");
  const eyeOff = document.getElementById("icon-eye-off");

  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye.classList.toggle("d-none", isPwd);
    eyeOff.classList.toggle("d-none", !isPwd);
    pass.focus();
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    // Mostrar loader
    loader.classList.remove("hidden");

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      setTimeout(() => {
        loader.classList.add("hidden");
      }, 2000);
      return;
    }

    // microestado del botón
    const btn = form.querySelector('button[type="submit"]');
    const html = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = "Entrando…";

    // Simular validación con servidor
    await new Promise((r) => setTimeout(r, 1000));

    // Redirigir al index
    window.location.href = "index.html";
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
// Login: toggle de contraseña + validación básica + microestado de botón
(() => {
  const form = document.getElementById("loginForm");
  if (!form) return;

  const pass = document.getElementById("password");
  const toggle = document.getElementById("togglePass");
  const eye = document.getElementById("icon-eye");
  const eyeOff = document.getElementById("icon-eye-off");

  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye.classList.toggle("d-none", isPwd);
    eyeOff.classList.toggle("d-none", !isPwd);
    pass.focus();
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    // microestado
    const btn = form.querySelector('button[type="submit"]');
    const html = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = "Entrando…";

    // simular latencia
    await new Promise((r) => setTimeout(r, 800));

    btn.disabled = false;
    btn.innerHTML = html;
    form.reset();
  });
  (() => {
    const pass = document.getElementById("password");
    const hint = document.getElementById("capsHint");
    if (!pass || !hint) return;

    const toggleHint = (e) => {
      const on = e.getModifierState && e.getModifierState("CapsLock");
      hint.classList.toggle("d-none", !on);
    };
    pass.addEventListener("keydown", toggleHint);
    pass.addEventListener("keyup", toggleHint);
    pass.addEventListener("focus", (e) => toggleHint(e));
    pass.addEventListener("blur", () => hint.classList.add("d-none"));
  })();
  (() => {
    const form = document.getElementById("loginForm");
    const card = document.querySelector(".auth-card");
    if (!form || !card) return;

    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      e.stopPropagation();

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        // marca inputs inválidos para el borde rojo
        form
          .querySelectorAll(":invalid")
          .forEach((el) => el.classList.add("is-invalid"));
        // shake de la tarjeta
        card.classList.remove("shake");
        void card.offsetWidth; // reflow para reiniciar animación
        card.classList.add("shake");
        return;
      } else {
        form
          .querySelectorAll(".is-invalid")
          .forEach((el) => el.classList.remove("is-invalid"));
      }
    });
  })();
})();

(() => {
  const form = document.getElementById("registerForm");
  if (!form) return;

  // Toggle ojo
  const pass = document.getElementById("password");
  const toggle = document.getElementById("togglePassReg");
  const eye = document.getElementById("icon-eye-reg");
  const eyeOff = document.getElementById("icon-eye-off-reg");
  toggle?.addEventListener("click", () => {
    const isPwd = pass.type === "password";
    pass.type = isPwd ? "text" : "password";
    eye.classList.toggle("d-none", isPwd);
    eyeOff.classList.toggle("d-none", !isPwd);
    pass.focus();
  });

  // Validación básica + matching de contraseñas + microestado del botón
  const card = document.querySelector(".auth-card");
  const pass2 = document.getElementById("password2");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    // match de contraseñas
    if (pass.value !== pass2.value) {
      pass2.setCustomValidity("No coincide");
    } else {
      pass2.setCustomValidity("");
    }

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      // marca inputs inválidos
      form
        .querySelectorAll(":invalid")
        .forEach((el) => el.classList.add("is-invalid"));
      // shake
      card?.classList.remove("shake");
      void card?.offsetWidth;
      card?.classList.add("shake");
      return;
    } else {
      form
        .querySelectorAll(".is-invalid")
        .forEach((el) => el.classList.remove("is-invalid"));
    }

    // microestado del botón
    const btn = form.querySelector('button[type="submit"]');
    const html = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = "Creando cuenta…";

    // simular latencia
    await new Promise((r) => setTimeout(r, 900));

    btn.disabled = false;
    btn.innerHTML = html;
    form.reset();
    form.classList.remove("was-validated");
  });

  // Tel: limpiar a dígitos
  const tel = document.getElementById("telefono");
  tel?.addEventListener("input", () => {
    tel.value = tel.value.replace(/[^\d+ ]/g, "").slice(0, 20);
  });
})();
