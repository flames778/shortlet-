<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=1200">
    <title>Shortlet - Stay where the city happens</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Premium Animations Custom stylesheet -->
    <link href="/css/animations.css" rel="stylesheet">
    <style>
        body { background: #f7f8fb; color: #152033; }
        .brand-mark { width: 38px; height: 38px; border-radius: 10px; background: #16c79a; display: inline-grid; place-items: center; color: #041221; font-weight: 800; }
        .feature-icon { width: 44px; height: 44px; border-radius: 12px; background: #e9fff8; display: inline-grid; place-items: center; color: #087f63; font-weight: 800; }
        .property-card img { height: 220px; object-fit: cover; }
        .partner-logo { height: 32px; filter: grayscale(100%); opacity: 0.5; transition: filter 0.3s ease, opacity 0.3s ease; }
        .partner-logo:hover { filter: none; opacity: 1; }
        .team-photo { width: 110px; height: 110px; object-fit: cover; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark position-absolute w-100 z-3">
    <div class="container py-2">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold" href="/">
            <span class="brand-mark">S</span> Shortlet
        </a>
        <div class="ms-auto d-flex gap-2">
            <a href="/login" class="btn btn-outline-light px-4 rounded-pill">Login</a>
            <a href="/login" class="btn btn-success px-4 rounded-pill btn-glow">Get started</a>
        </div>
    </div>
</nav>

<header class="hero d-flex align-items-center text-white">
    <div class="hero-overlay"></div>
    
    <!-- Subtle Canvas Particles -->
    <canvas id="particles-canvas"></canvas>

    <div class="container py-5">
        <div class="col-lg-8">
            <span class="badge rounded-pill text-bg-success mb-3 px-3 py-1.5 reveal" style="font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">Shortlet stays</span>
            <!-- Typewriter Headline Container -->
            <div class="typewriter-container mb-4 d-block">
                <h1 class="display-3 fw-bold typewriter-headline single-line-headline mb-0 text-start" style="min-height: 80px;">
                    <span class="typewriter-text" id="animated-text"></span>
                    <span class="typewriter-cursor">|</span>
                </h1>
            </div>
            <p class="lead fs-3 mb-4 reveal">Book stylish temporary apartments across the city, or list your property for trusted guests.</p>
            
            <!-- Immediate Search Bar -->
            <div class="card bg-white p-4 shadow-lg reveal rounded-4 border-0">
                <form action="/search" method="get" class="row g-2 align-items-center">
                    <div class="col-md-4 text-dark">
                        <label class="small fw-bold mb-1"><i class="bi bi-geo-alt text-success me-1"></i> City</label>
                        <input type="text" name="city" class="form-control" placeholder="Lagos, Abuja, Ibadan..." required>
                    </div>
                    <div class="col-md-3 text-dark">
                        <label class="small fw-bold mb-1"><i class="bi bi-calendar-event text-success me-1"></i> Check-in</label>
                        <input type="date" name="checkin" class="form-control">
                    </div>
                    <div class="col-md-3 text-dark">
                        <label class="small fw-bold mb-1"><i class="bi bi-people text-success me-1"></i> Guests</label>
                        <select name="guests" class="form-select">
                            <option value="1">1 Guest</option>
                            <option value="2">2 Guests</option>
                            <option value="3">3 Guests</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="d-none d-md-block mb-1">&nbsp;</label>
                        <button type="submit" class="btn btn-success w-100 py-2.5 fw-bold rounded-pill btn-glow">Search</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</header>

<main>
    <!-- Trust Badges Section -->
    <div class="bg-white py-3 border-bottom shadow-sm reveal">
        <div class="container d-flex justify-content-center gap-4 flex-wrap text-muted small fw-semibold">
            <span><i class="bi bi-check-circle-fill text-success"></i> <span class="stat-number fw-bold" data-target="500" data-decimal="false">0</span>+ happy guests</span>
            <span><i class="bi bi-star-fill text-warning"></i> <span class="stat-number fw-bold" data-target="4.8" data-decimal="true">0.0</span> average rating</span>
            <span><i class="bi bi-shield-lock-fill text-primary"></i> Secure payments</span>
            <span><i class="bi bi-house-check-fill text-info"></i> Verified stays</span>
        </div>
    </div>

    <!-- Partners Section -->
    <section class="py-4 border-bottom reveal">
        <div class="container text-center">
            <div class="d-flex justify-content-center align-items-center gap-4 gap-md-5 flex-wrap">
                <img src="/images/partners/jiji.jpg" alt="Jiji" class="partner-logo">
                <img src="/images/partners/airbnb.jpg" alt="Airbnb" class="partner-logo">
                <img src="/images/partners/google.jpg" alt="Google" class="partner-logo">
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="py-5 bg-white reveal">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card border-0 p-4 h-100 card-hover bg-light rounded-4">
                        <div class="feature-icon mb-3"><i class="bi bi-search"></i></div>
                        <h3 class="h5 fw-bold">Search by city</h3>
                        <p class="text-secondary mb-0">Discover apartments for work trips, weekends, relocation windows, and everything in between.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card border-0 p-4 h-100 card-hover bg-light rounded-4">
                        <div class="feature-icon mb-3"><i class="bi bi-calendar-check"></i></div>
                        <h3 class="h5 fw-bold">Book confidently</h3>
                        <p class="text-secondary mb-0">Every booking is tracked in your dashboard with check-in, check-out, and status details.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card border-0 p-4 h-100 card-hover bg-light rounded-4">
                        <div class="feature-icon mb-3"><i class="bi bi-kanban"></i></div>
                        <h3 class="h5 fw-bold">Manage smoothly</h3>
                        <p class="text-secondary mb-0">Admins get a protected dashboard, user overview, booking table, and Excel exports.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Properties Section -->
    <section id="properties" class="py-5 reveal">
        <div class="container">
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div>
                    <p class="text-success fw-semibold mb-1">Featured stays</p>
                    <h2 class="fw-bold mb-0">City apartments with a pulse</h2>
                </div>
                <a href="/login" class="btn btn-dark rounded-pill px-4">View more</a>
            </div>
            <div class="row g-4">
                <c:forEach var="property" items="${featuredProperties}">
                    <div class="col-md-4">
                        <div class="card property-card border-0 shadow-sm h-100 card-hover rounded-4 overflow-hidden bg-white">
                            <img src="${property.imageUrl}" class="card-img-top" alt="${property.title}">
                            <div class="card-body p-4">
                                <h3 class="h5 fw-bold mb-1">${property.title}</h3>
                                <p class="text-secondary small mb-2"><i class="bi bi-geo-alt me-1 text-danger"></i> ${property.address}, ${property.city}</p>
                                <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                                    <span class="fw-bold text-success fs-5">₦<fmt:formatNumber value="${property.nightlyRate}" pattern="#,###"/> <span class="text-muted small fw-normal" style="font-size: 11px;">/ night</span></span>
                                    <span class="badge text-bg-warning text-dark px-2.5 py-1 rounded"><i class="bi bi-star-fill text-warning me-1"></i> ${property.rating}</span>
                                </div>
                                <a href="/login" class="btn btn-outline-success btn-sm w-100 mt-3 rounded-pill">View Details</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>

    <!-- Testimonials Section -->
    <section class="py-5 bg-light reveal">
        <div class="container">
            <h2 class="fw-bold text-center mb-5">What our guests say</h2>
            <div class="row g-4">
                <c:forEach var="t" items="${testimonials}">
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm h-100 p-4 card-hover rounded-4 bg-white">
                            <c:if test="${not empty t.propertyTitle}">
                                <div class="d-flex align-items-center gap-3 p-2 mb-3 rounded-3" style="background: rgba(22, 199, 154, 0.08); border: 1px solid rgba(22, 199, 154, 0.15);">
                                    <img src="${t.propertyImageUrl}" alt="${t.propertyTitle}" class="rounded-2" style="width: 64px; height: 64px; object-fit: cover;">
                                    <div style="min-width: 0;">
                                        <div class="text-success small fw-bold mb-0" style="font-size: 0.75rem;"><i class="bi bi-house-heart"></i> Stayed at</div>
                                        <div class="fw-bold text-dark text-truncate small mb-0" title="${t.propertyTitle}">${t.propertyTitle}</div>
                                    </div>
                                </div>
                            </c:if>
                            <div class="text-warning mb-2">
                                <c:forEach begin="1" end="${t.rating}"><i class="bi bi-star-fill"></i></c:forEach>
                            </div>
                            <p class="fst-italic mb-3 text-secondary">"${t.quote}"</p>
                            <div class="d-flex align-items-center gap-2 mt-auto pt-3 border-top">
                                <div class="fw-bold text-slate-800">${t.guestName}</div>
                                <div class="text-muted small">&bull; ${t.location}</div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>

    <!-- Team Section -->
    <section class="py-5 reveal">
        <div class="container text-center">
            <h2 class="fw-bold mb-2">The Team Behind Shortlet</h2>
            <p class="text-secondary mb-5">Meet the creators dedicated to elevating your short-term stay experience.</p>
            <div class="row g-4 justify-content-center">
                <c:forEach var="member" items="${team}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card border-0 shadow-sm h-100 py-4 px-3 card-hover rounded-4 bg-white">
                            <div class="card-body">
                                <img src="${member.imageUrl}" alt="${member.name}" class="rounded-circle mb-3 team-photo shadow-sm border border-3 border-success-subtle">
                                <h3 class="h5 fw-bold mb-1">${member.name}</h3>
                                <p class="text-success small fw-semibold mb-3">${member.role}</p>
                                <p class="text-secondary mb-0 small">${member.bio}</p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>
</main>

<footer class="py-4 bg-dark text-white-50 reveal">
    <div class="container">
        <div class="row g-4 mb-4">
            <div class="col-md-4">
                <h5 class="text-white mb-3">Shortlet</h5>
                <p class="small">Premium temporary accommodation in Nigeria's pulse cities. Stylish stays for work, weekends, and relocation.</p>
            </div>
            <div class="col-md-2 offset-md-2">
                <h6 class="text-white mb-3">Support</h6>
                <ul class="list-unstyled small">
                    <li><a href="#" class="text-white-50 text-decoration-none">Help Center</a></li>
                    <li><a href="#" class="text-white-50 text-decoration-none">Safety</a></li>
                    <li><a href="#" class="text-white-50 text-decoration-none">Cancellation</a></li>
                </ul>
            </div>
            <div class="col-md-2">
                <h6 class="text-white mb-3">Legal</h6>
                <ul class="list-unstyled small">
                    <li><a href="#" class="text-white-50 text-decoration-none">Privacy</a></li>
                    <li><a href="#" class="text-white-50 text-decoration-none">Terms</a></li>
                </ul>
            </div>
            <div class="col-md-2">
                <h6 class="text-white mb-3">Connect</h6>
                <div class="d-flex gap-3 fs-5">
                    <i class="bi bi-facebook text-white-50" style="cursor: pointer;"></i>
                    <i class="bi bi-twitter-x text-white-50" style="cursor: pointer;"></i>
                    <i class="bi bi-instagram text-white-50" style="cursor: pointer;"></i>
                </div>
            </div>
        </div>
        <hr class="border-secondary">
        <div class="d-flex flex-wrap justify-content-between gap-2 small">
            <span>&copy; 2026 Shortlet. All rights reserved.</span>
            <span>Temporary accommodation, made calm.</span>
        </div>
    </div>
</footer>

<!-- Premium Animations logic handler -->
<script src="/js/animations.js"></script>
<script src="${pageContext.request.contextPath}/js/typewriter.js"></script>
</body>
</html>
