/* ==========================================
   SHORTLET PREMIUM ANIMATIONS LOGIC ENGINE
   ========================================== */

document.addEventListener("DOMContentLoaded", () => {
    // 1. Initialize Smooth Page Load & Skeleton Removal (Simulated 300ms loading phase)
    setTimeout(() => {
        const skeletons = document.querySelectorAll(".skeleton-loader");
        skeletons.forEach(s => {
            s.style.opacity = "0";
            setTimeout(() => {
                s.style.display = "none"; // Hide immediately to stop pushing other elements
                document.querySelectorAll(".real-content").forEach(el => {
                    el.style.display = ""; // Restore original display (flex/block)
                    el.classList.remove('d-none');
                    el.style.visibility = "visible";
                    el.style.opacity = "1";
                });
            }, 300);
        });
    }, 500);

    // 3. Initialize Particle Background Layer
    initParticles();

    // 4. Initialize Intersection Observer for reveals & counters
    initIntersectionObserver();

    // 5. Initialize Interactive Button Ripples
    initButtonRipples();
});



// Numerical statistics counter engine
function startCounter(el) {
    if (el.dataset.started === "true") return;
    el.dataset.started = "true";

    const targetVal = parseFloat(el.getAttribute("data-target"));
    const isDecimal = el.getAttribute("data-decimal") === "true";
    const duration = 2000; // 2 seconds animation
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // EaseOutQuad multiplier
        const easeProgress = progress * (2 - progress);
        const currentVal = easeProgress * targetVal;

        if (isDecimal) {
            el.textContent = currentVal.toFixed(1);
        } else {
            el.textContent = Math.floor(currentVal);
        }

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        } else {
            el.textContent = isDecimal ? targetVal.toFixed(1) : Math.floor(targetVal);
        }
    }
    requestAnimationFrame(updateCounter);
}

// Intersection Observer for scroll triggers
function initIntersectionObserver() {
    const reveals = document.querySelectorAll(".reveal");
    
    const observerOptions = {
        root: null,
        rootMargin: "0px",
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add("active");
                
                // If this is a nested or direct counter, fire it!
                if (entry.target.classList.contains("stat-number")) {
                    startCounter(entry.target);
                }
                const nestedCounters = entry.target.querySelectorAll(".stat-number");
                nestedCounters.forEach(cnt => startCounter(cnt));

                // Once active, we don't need to observe it anymore
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    reveals.forEach(el => observer.observe(el));
}

// Interactive material ripple click mechanics
function initButtonRipples() {
    const ripples = document.querySelectorAll(".btn-glow, .btn-success, .btn-dark, .btn-outline-success");
    ripples.forEach(btn => {
        btn.addEventListener("click", function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const circle = document.createElement("span");
            circle.style.position = "absolute";
            circle.style.top = y + "px";
            circle.style.left = x + "px";
            circle.style.width = "1px";
            circle.style.height = "1px";
            circle.style.borderRadius = "50%";
            circle.style.background = "rgba(255, 255, 255, 0.4)";
            circle.style.transform = "translate(-50%, -50%) scale(0)";
            circle.style.transition = "transform 0.6s cubic-bezier(0.25, 1, 0.5, 1), opacity 0.6s ease";
            circle.style.pointerEvents = "none";

            this.appendChild(circle);
            
            // Trigger reflow
            circle.offsetWidth;

            circle.style.transform = "translate(-50%, -50%) scale(" + Math.max(rect.width, rect.height) * 2.5 + ")";
            circle.style.opacity = "0";

            setTimeout(() => circle.remove(), 600);
        });
    });
}

// Lightweight float up Canvas particles generator
function initParticles() {
    const canvas = document.getElementById("particles-canvas");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    
    let width = canvas.offsetWidth;
    let height = canvas.offsetHeight;
    canvas.width = width;
    canvas.height = height;

    // Handle resizing beautifully
    window.addEventListener("resize", () => {
        width = canvas.offsetWidth;
        height = canvas.offsetHeight;
        canvas.width = width;
        canvas.height = height;
    });

    const particles = [];
    const count = 40; // Perfect float density
    
    for (let i = 0; i < count; i++) {
        particles.push({
            x: Math.random() * width,
            y: Math.random() * height,
            r: Math.random() * 2.2 + 0.6,
            vx: Math.random() * 0.4 - 0.2,
            vy: Math.random() * -0.5 - 0.15, // steady float upwards
            alpha: Math.random() * 0.4 + 0.15
        });
    }

    function draw() {
        ctx.clearRect(0, 0, width, height);
        for (let i = 0; i < count; i++) {
            const p = particles[i];
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(22, 199, 154, ${p.alpha})`; // shortlet signature teal accent
            ctx.fill();

            p.x += p.vx;
            p.y += p.vy;

            // Recycle off-screen floaters
            if (p.y < -10) {
                p.y = height + 10;
                p.x = Math.random() * width;
            }
            if (p.x < -10 || p.x > width + 10) {
                p.x = Math.random() * width;
            }
        }
        requestAnimationFrame(draw);
    }
    draw();
}
