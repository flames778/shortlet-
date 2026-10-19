<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=1200">
    <title>Dashboard - Shortlet Discover</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap');
        
        body { 
            font-family: 'Outfit', sans-serif;
            background: #f8fafc; 
            min-width: 1200px; 
            color: #1e293b; 
        }
        .brand-mark { 
            width: 36px; 
            height: 36px; 
            border-radius: 10px; 
            background: linear-gradient(135deg, #10b981, #059669); 
            display: inline-grid; 
            place-items: center; 
            color: white; 
            font-weight: 800; 
        }
        .page-band { 
            background: linear-gradient(135deg, #0f172a, #1e293b); 
            color: white; 
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }
        
        /* Interactive Filter Chips */
        .search-chip {
            border-radius: 20px;
            padding: 6px 16px;
            font-size: 13px;
            font-weight: 600;
            transition: all 0.2s ease;
            cursor: pointer;
            border: 1px solid #e2e8f0;
            background: white;
            color: #475569;
            text-decoration: none;
            display: inline-block;
        }
        .search-chip.active {
            background: #0f172a;
            color: white;
            border-color: #0f172a;
        }
        .search-chip:hover:not(.active) {
            background: #f1f5f9;
            border-color: #cbd5e1;
        }
        
        /* Carousel styling */
        .carousel-container {
            height: 200px;
            position: relative;
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
            background: #e2e8f0;
        }
        .carousel-track {
            display: flex;
            transition: transform 0.4s cubic-bezier(0.25, 1, 0.5, 1);
            height: 100%;
        }
        .carousel-image {
            min-width: 100%;
            height: 100%;
            object-fit: cover;
            cursor: zoom-in;
        }
        .carousel-prev, .carousel-next {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            background: rgba(15, 23, 42, 0.6);
            color: white;
            border: none;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.2s;
            z-index: 2;
            font-size: 13px;
            backdrop-filter: blur(2px);
        }
        .carousel-prev:hover, .carousel-next:hover {
            background: rgba(15, 23, 42, 0.9);
        }
        .carousel-prev { left: 8px; }
        .carousel-next { right: 8px; }
        
        /* Property Card Design */
        .property-card {
            border-radius: 16px;
            overflow: hidden;
            background: white;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            border: 1px solid #f1f5f9;
        }
        .property-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 10px 10px -5px rgba(0, 0, 0, 0.03);
        }
        
        /* Wishlist Heart Icon Overlays */
        .wishlist-btn {
            position: absolute;
            top: 12px;
            right: 12px;
            background: rgba(255, 255, 255, 0.85);
            border: none;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            display: grid;
            place-items: center;
            cursor: pointer;
            transition: transform 0.2s, background 0.2s;
            z-index: 5;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(4px);
        }
        .wishlist-btn:hover {
            transform: scale(1.1);
            background: white;
        }
        .wishlist-btn svg {
            fill: none;
            stroke: #475569;
            stroke-width: 2;
            width: 20px;
            height: 20px;
            transition: fill 0.2s, stroke 0.2s;
        }
        .wishlist-btn.active svg {
            fill: #ef4444;
            stroke: #ef4444;
            animation: pulse 0.3s ease;
        }
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.25); }
            100% { transform: scale(1); }
        }
        
        /* Badges */
        .badge-reply {
            background-color: #f0fdf4;
            color: #15803d;
            border: 1px solid #bbf7d0;
            font-size: 11px;
            font-weight: 600;
        }
        .badge-views {
            background-color: #fff7ed;
            color: #c2410c;
            border: 1px solid #fed7aa;
            font-size: 11px;
            font-weight: 600;
        }
        .rating-badge {
            background-color: #fef9c3;
            color: #854d0e;
            border: 1px solid #fef08a;
            font-size: 11px;
            font-weight: 700;
        }
        
        /* Interactive Map container */
        #map {
            height: 320px;
            width: 100%;
            border-radius: 16px;
            border: 1px solid #cbd5e1;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
        
        /* Compact Timeline */
        .timeline {
            position: relative;
            padding-left: 20px;
            border-left: 2px dashed #e2e8f0;
        }
        .timeline-dot {
            position: absolute;
            left: -6px;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #10b981;
            border: 2px solid white;
        }
        .timeline-dot.checkout {
            background: #f43f5e;
        }
        
        /* Custom Autocomplete dropdown styling */
        .autocomplete-dropdown {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            z-index: 1000;
            display: none;
            overflow: hidden;
            margin-top: 4px;
        }
        .autocomplete-item {
            padding: 10px 16px;
            cursor: pointer;
            font-size: 14px;
            transition: background 0.15s;
            color: #334155;
            border-bottom: 1px solid #f1f5f9;
        }
        .autocomplete-item:last-child {
            border-bottom: none;
        }
        .autocomplete-item:hover {
            background: #f8fafc;
        }
        
        /* Vanilla Lightbox */
        .lightbox {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(15, 23, 42, 0.95);
            z-index: 9999;
            display: none;
            place-items: center;
            opacity: 0;
            transition: opacity 0.3s ease;
        }
        .lightbox.active {
            display: grid;
            opacity: 1;
        }
        .lightbox-content {
            max-width: 85%;
            max-height: 80%;
            border-radius: 16px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            transform: scale(0.95);
            transition: transform 0.3s ease;
        }
        .lightbox.active .lightbox-content {
            transform: scale(1);
        }
        .lightbox-close {
            position: absolute;
            top: 24px;
            right: 24px;
            color: white;
            font-size: 36px;
            cursor: pointer;
            background: none;
            border: none;
            transition: transform 0.2s;
        }
        .lightbox-close:hover {
            transform: scale(1.1);
        }
        
        /* Bookings sidebar styles */
        .booking-card {
            border-radius: 16px;
            border: 1px solid #f1f5f9;
            transition: all 0.2s;
        }
        .booking-card:hover {
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold" href="/dashboard"><span class="brand-mark">S</span> Shortlet Discover</a>
        <div class="ms-auto d-flex align-items-center gap-3">
            <span class="text-white-50 d-none d-md-inline">Welcome, <strong>${sessionScope.user.name}</strong></span>
            <a href="/logout" class="btn btn-outline-light btn-sm px-3 rounded-pill">Logout</a>
        </div>
    </div>
</nav>

<section class="page-band py-5">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <p class="text-success fw-semibold mb-1">Stay discover & management command center</p>
                <h1 class="fw-bold mb-2">Discover premium city shortlets</h1>
                <p class="lead mb-0 text-white-50">Compare pricing, view interactive locations, and book in a couple of clicks.</p>
            </div>
            <div class="col-md-4 text-md-end mt-3 mt-md-0">
                <div class="bg-white bg-opacity-10 p-3 rounded-3 d-inline-block text-start border border-white border-opacity-10">
                    <div class="small text-white-50">Active search session</div>
                    <div class="fw-bold fs-5 text-success">Lagos, Abuja & more</div>
                </div>
            </div>
        </div>
    </div>
</section>

<main class="container py-5">
    <c:if test="${param.booked == 'true'}">
        <div class="alert alert-success border-0 shadow-sm rounded-3 mb-4 d-flex align-items-center gap-2">
            <span>🎉</span> <div><strong>Apartment booked successfully!</strong> Check the sidebar under "My bookings" to manage your upcoming stay.</div>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger border-0 shadow-sm rounded-3 mb-4 d-flex align-items-center gap-2">
            <span>⚠️</span> <div><strong>Reservation failed:</strong> ${error}</div>
        </div>
    </c:if>

    <!-- Side-by-side Layout -->
    <div class="row g-4">
        <!-- Main Properties Area (Left, col-lg-8) -->
        <div class="col-lg-8">
            
            <!-- Recommendations Row (If available) -->
            <c:if test="${not empty recommendations}">
                <div class="mb-5">
                    <h3 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-sparkles text-warning me-1"></i> Recommended for you
                    </h3>
                    <div class="row g-3">
                        <c:forEach var="rec" items="${recommendations}">
                            <div class="col-md-4">
                                <div class="card border-0 shadow-sm h-100 property-card" id="property-rec-${rec.id}" style="min-height: 280px;">
                                    <div class="position-relative">
                                        <img src="${rec.imageUrl}" class="w-100 object-fit-cover" style="height: 120px;" alt="${rec.title}">
                                        <span class="badge rating-badge position-absolute top-2 start-2"><i class="bi bi-star-fill text-warning me-1"></i> ${rec.rating}</span>
                                    </div>
                                    <div class="card-body p-3 d-flex flex-column justify-content-between">
                                        <div>
                                            <h4 class="h6 fw-bold mb-1 text-truncate">${rec.title}</h4>
                                            <p class="text-secondary small mb-2 text-truncate">${rec.address}</p>
                                        </div>
                                        <div class="d-flex justify-content-between align-items-center border-top pt-2 mt-2">
                                            <span class="fw-bold text-success" style="font-size: 13.5px;">&#8358;${rec.nightlyRate} <span class="text-secondary fw-normal small">/nt</span></span>
                                            <button onclick="scrollToProperty(${rec.id})" class="btn btn-dark btn-sm rounded-pill px-3 py-1" style="font-size: 11px;">View</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Interactive Leaflet City Map -->
            <div class="mb-5">
                <h3 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                    <i class="bi bi-map text-success me-1"></i> Interactive City Map
                </h3>
                <div id="map"></div>
            </div>

            <!-- Search, Tabs, Filters and Sorting row -->
            <div class="mb-4 bg-white p-4 rounded-4 shadow-sm border border-slate-100">
                <div class="d-flex flex-wrap gap-2 justify-content-between align-items-center mb-4">
                    <!-- Dashboard Tabs -->
                    <div class="btn-group rounded-pill p-1 bg-light border">
                        <a href="/dashboard?tab=all" class="btn btn-sm rounded-pill px-4 ${tab != 'favourites' ? 'btn-dark' : 'btn-link text-dark text-decoration-none'}">All Apartments</a>
                        <a href="/dashboard?tab=favourites" class="btn btn-sm rounded-pill px-4 ${tab == 'favourites' ? 'btn-dark' : 'btn-link text-dark text-decoration-none'}">Favourites ❤️</a>
                    </div>
                    
                    <!-- Search & Filter Form -->
                    <form id="filterForm" class="d-flex flex-wrap gap-2 align-items-center" action="/dashboard" method="get">
                        <input type="hidden" name="tab" value="${tab}">
                        
                        <!-- Autocomplete Search Input -->
                        <div class="position-relative" style="width: 260px;">
                            <input class="form-control rounded-pill px-3" type="search" name="city" id="searchInput" value="${city}" placeholder="Search City (e.g. Lagos, Abuja)" autocomplete="off">
                            <div class="autocomplete-dropdown" id="autocompleteDropdown"></div>
                        </div>

                        <!-- Max Nightly Rate input -->
                        <input class="form-control rounded-pill px-3" type="number" name="maxPrice" value="${maxPrice}" placeholder="Max Price" style="width: 130px;">
                        
                        <!-- Hidden filter checkboxes connected to chips -->
                        <input type="checkbox" name="wifi" id="wifiCheck" value="true" ${wifi ? 'checked' : ''} class="d-none">
                        <input type="checkbox" name="selfCheckIn" id="selfCheckInCheck" value="true" ${selfCheckIn ? 'checked' : ''} class="d-none">
                        <input type="checkbox" name="nearAirport" id="nearAirportCheck" value="true" ${nearAirport ? 'checked' : ''} class="d-none">

                        <!-- Sort criteria dropdown -->
                        <select class="form-select rounded-pill px-3" name="sortBy" onchange="this.form.submit()" style="width: 150px;">
                            <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                            <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                            <option value="rating" ${sortBy == 'rating' ? 'selected' : ''}>Host Rating</option>
                        </select>

                        <button class="btn btn-dark rounded-pill px-4" type="submit">Filter</button>
                    </form>
                </div>

                <!-- Interactive Filter Chips Row -->
                <div class="d-flex flex-wrap gap-2 mt-2 align-items-center">
                    <span class="small fw-semibold text-secondary me-2">Quick filters:</span>
                    <button type="button" onclick="toggleChip('wifi')" class="search-chip ${wifi ? 'active' : ''}">📶 Free Wi-Fi</button>
                    <button type="button" onclick="toggleChip('selfCheckIn')" class="search-chip ${selfCheckIn ? 'active' : ''}">🔑 Self check-in</button>
                    <button type="button" onclick="toggleChip('nearAirport')" class="search-chip ${nearAirport ? 'active' : ''}">✈️ Near airport</button>
                    <button type="button" onclick="setPriceBound(50000)" class="search-chip ${maxPrice == '50000' ? 'active' : ''}">Under ₦50k</button>
                    <button type="button" onclick="setPriceBound(150000)" class="search-chip ${maxPrice == '150000' ? 'active' : ''}">Under ₦150k</button>
                </div>
            </div>

            <!-- Property Results Grid -->
            <div class="row g-3">
                <c:forEach var="property" items="${properties}">
                    <div class="col-md-6" id="property-${property.id}">
                        <div class="card property-card border-0 h-100 position-relative">
                            
                            <!-- Heart wishlist toggle icon overlaid on top-right -->
                            <button class="wishlist-btn ${property.isWishlisted ? 'active' : ''}" onclick="toggleWishlist(this, ${property.id})">
                                <svg viewBox="0 0 24 24">
                                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                                </svg>
                            </button>

                            <!-- Photo Gallery Carousel with custom indicators -->
                            <div class="carousel-container overflow-hidden">
                                <div class="carousel-track" data-index="0">
                                    <c:choose>
                                        <c:when test="${not empty property.images}">
                                            <c:forEach var="img" items="${property.images}">
                                                <img class="carousel-image" src="${img}" alt="${property.title}" onclick="openLightbox('${img}', '${property.title}')">
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <img class="carousel-image" src="${property.imageUrl}" alt="${property.title}" onclick="openLightbox('${property.imageUrl}', '${property.title}')">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <c:if test="${property.images.size() > 1}">
                                    <button class="carousel-prev" onclick="prevSlide(this)">&#10094;</button>
                                    <button class="carousel-next" onclick="nextSlide(this)">&#10095;</button>
                                </c:if>
                            </div>

                            <!-- Card Body -->
                            <div class="card-body p-4 d-flex flex-column justify-content-between">
                                <div>
                                    <div class="d-flex justify-content-between align-items-start gap-2 mb-2">
                                        <h3 class="h5 fw-bold mb-0 text-slate-800">${property.title}</h3>
                                        <div class="text-end fw-bold text-success fs-5">
                                            &#8358;<fmt:formatNumber value="${property.nightlyRate}" pattern="#,###"/>
                                            <span class="d-block text-secondary fw-normal" style="font-size: 11px;">/ night</span>
                                        </div>
                                    </div>
                                    <p class="text-secondary small mb-3">${property.address}</p>

                                    <!-- Badges Section (Ratings, reply speed, live views) -->
                                    <div class="d-flex flex-wrap gap-2 mb-4">
                                        <span class="badge rating-badge rounded px-2.5 py-1"><i class="bi bi-star-fill text-warning me-1"></i> ${property.rating}</span>
                                        <span class="badge badge-reply rounded px-2.5 py-1">${property.formattedReplyTime}</span>
                                        <c:if test="${property.urgencyViews > 0}">
                                            <span class="badge badge-views rounded px-2.5 py-1"><i class="bi bi-fire text-danger me-1"></i> ${property.urgencyViews} people viewing this now</span>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- Collapsible booking order -->
                                <div>
                                    <button class="btn btn-success w-100 fw-bold rounded-pill py-2 shadow-sm text-slate-900" type="button" data-bs-toggle="collapse" data-bs-target="#bookingForm-${property.id}">
                                        Book Apartment
                                    </button>
                                    
                                    <div class="collapse" id="bookingForm-${property.id}">
                                        <form class="p-3 mt-3 border bg-light rounded-3" method="post" action="/dashboard">
                                            <input type="hidden" name="propertyId" value="${property.id}">
                                            <div class="row g-2">
                                                <div class="col-sm-6">
                                                    <label class="form-label small fw-semibold">Check-in</label>
                                                    <input class="form-control" type="date" name="checkIn" required>
                                                </div>
                                                <div class="col-sm-6">
                                                    <label class="form-label small fw-semibold">Check-out</label>
                                                    <input class="form-control" type="date" name="checkOut" required>
                                                </div>
                                                <div class="col-12">
                                                    <label class="form-label small fw-semibold">Payment method</label>
                                                    <select class="form-select" name="paymentMethod" required>
                                                        <option value="CARD">Card</option>
                                                        <option value="BANK_TRANSFER">Bank transfer</option>
                                                        <option value="USSD">USSD</option>
                                                        <option value="PAY_ON_ARRIVAL">Pay on arrival</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <button class="btn btn-dark w-100 mt-3 rounded-pill" type="submit">Confirm Booking</button>
                                            <c:if test="${not empty property.sourceUrl}">
                                                <div class="text-center mt-2">
                                                    <a class="small text-secondary text-decoration-none" href="${property.sourceUrl}" target="_blank" rel="noopener">🌐 Listing reference page</a>
                                                </div>
                                            </c:if>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                
                <!-- Jiji Listings - Seamlessly Integrated inside same grid -->
                <c:forEach var="jiji" items="${jijiListings}">
                    <div class="col-md-6" id="jiji-listing-${jiji.id}">
                        <div class="card property-card border-0 h-100 position-relative">
                            
                            <!-- Jiji Badge overlaid on top-left -->
                            <span class="badge position-absolute top-2 start-2 px-3 py-1.5 rounded-pill shadow-sm" style="background-color: #00bcd4; color: #fff; font-weight: 600; font-size: 11px; z-index: 10;">From Jiji</span>

                            <!-- Jiji Image Carousel container (with single image standard) -->
                            <div class="carousel-container overflow-hidden">
                                <div class="carousel-track" data-index="0">
                                    <img class="carousel-image" src="${jiji.imageUrl}" alt="${jiji.title}" 
                                         onerror="this.src='https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=900&q=80'">
                                </div>
                            </div>

                            <!-- Card Body -->
                            <div class="card-body p-4 d-flex flex-column justify-content-between">
                                <div>
                                    <div class="d-flex justify-content-between align-items-start gap-2 mb-2">
                                        <h3 class="h5 fw-bold mb-0 text-slate-800 text-truncate" style="max-width: 200px;">${jiji.title}</h3>
                                        <div class="text-end fw-bold text-success fs-5" style="font-size: 15px !important;">
                                            ${jiji.priceText}
                                        </div>
                                    </div>
                                    <p class="text-secondary small mb-3">${jiji.location}</p>

                                    <!-- Badges Section matching native ones -->
                                    <div class="d-flex flex-wrap gap-2 mb-4">
                                        <span class="badge rating-badge rounded px-2.5 py-1"><i class="bi bi-star-fill text-warning me-1"></i> 4.5</span>
                                        <span class="badge badge-reply rounded px-2.5 py-1">Verified on Jiji</span>
                                        <span class="badge badge-views rounded px-2.5 py-1"><i class="bi bi-fire text-danger me-1"></i> 15 people viewing now</span>
                                    </div>
                                </div>

                                <!-- Collapsible booking/inquiry section -->
                                <div>
                                    <button class="btn w-100 fw-bold rounded-pill py-2 shadow-sm text-white" style="background-color: #00bcd4; border-color: #00bcd4;" type="button" data-bs-toggle="collapse" data-bs-target="#jijiForm-${jiji.id}">
                                        Inquire Apartment
                                    </button>
                                    
                                    <div class="collapse" id="jijiForm-${jiji.id}">
                                        <div class="p-3 mt-3 border bg-light rounded-3">
                                            <p class="text-muted small mb-3" style="font-size: 12px; line-height: 1.5;">${not empty jiji.description ? jiji.description : 'No description available for this Jiji shortlet listing.'}</p>
                                            <div class="row g-2">
                                                <div class="col-6">
                                                    <a class="btn btn-outline-dark w-100 rounded-pill small py-2 px-3 d-flex align-items-center justify-content-center gap-1" style="font-size: 11px;" href="${jiji.jijiUrl}" target="_blank" rel="noopener">
                                                        <i class="bi bi-box-arrow-up-right"></i> View on Jiji
                                                    </a>
                                                </div>
                                                <div class="col-6">
                                                    <button class="btn btn-dark w-100 rounded-pill small py-2 px-3 d-flex align-items-center justify-content-center gap-1" style="font-size: 11px;" onclick="inquireJiji(${jiji.id})">
                                                        <i class="bi bi-chat-left-dots"></i> Send Inquiry
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                
                <c:if test="${empty properties && empty jijiListings}">
                    <div class="col-12">
                        <div class="text-center text-secondary py-5 bg-white rounded shadow-sm">
                            <p class="mb-0 fs-5">No properties found in your selection.</p>
                            <p class="text-muted small">Try modifying your location query or price limit chips above.</p>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Sidebar Actions & Bookings (Right, col-lg-4) -->
        <div class="col-lg-4">
            
            <!-- Recent Searches Sidebar Widget -->
            <c:if test="${not empty recentSearches}">
                <div class="mb-4 bg-white p-4 rounded-4 shadow-sm border border-slate-100">
                    <h3 class="h6 fw-bold mb-3 text-secondary uppercase tracking-wider">⏱️ Recent Searches</h3>
                    <div class="d-flex flex-wrap gap-2">
                        <c:forEach var="search" items="${recentSearches}">
                            <a href="/dashboard?city=${search}" class="search-chip small text-decoration-none">${search}</a>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Bookings List -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 fw-bold mb-0">My reservations</h2>
                <span class="badge bg-success rounded-pill px-3 py-1.5 small font-weight-bold">${bookings.size()} total</span>
            </div>
            
            <div class="d-flex flex-column gap-3">
                <c:forEach var="booking" items="${bookings}">
                    <div class="card border-0 shadow-sm booking-card">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-start mb-2 gap-2">
                                <h3 class="h6 fw-bold mb-0 text-truncate" style="max-width: 190px;">${booking.apartmentTitle}</h3>
                                <span class="badge text-bg-${booking.status == 'CONFIRMED' ? 'success' : 'warning'} px-2 py-1 rounded-pill small">${booking.status}</span>
                            </div>
                            <p class="text-secondary small mb-3">${booking.city}</p>
                            
                            <!-- Compact Stay Timeline -->
                            <div class="timeline mt-2 mb-3">
                                <div class="mb-2 position-relative">
                                    <div class="timeline-dot"></div>
                                    <span class="fw-semibold text-dark small">Check-in:</span>
                                    <span class="small text-secondary">${booking.checkIn}</span>
                                </div>
                                <div class="position-relative">
                                    <div class="timeline-dot checkout"></div>
                                    <span class="fw-semibold text-dark small">Check-out:</span>
                                    <span class="small text-secondary">${booking.checkOut}</span>
                                </div>
                            </div>
                            
                            <!-- Policy Information -->
                            <div class="p-2.5 bg-light rounded-3 small text-secondary mb-3 d-flex align-items-center gap-1.5">
                                <i class="bi bi-shield-check text-success fs-6 me-1"></i> <span>Free cancellation 24h prior to arrival</span>
                            </div>
                            
                            <!-- Total and Manage actions -->
                            <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                                <div>
                                    <span class="d-block text-secondary small" style="font-size: 10px;">TOTAL PAID (${booking.nights} nights)</span>
                                    <span class="fw-bold text-success fs-5">&#8358;<fmt:formatNumber value="${booking.totalAmount}" pattern="#,###"/></span>
                                </div>
                                <button onclick="showManageAlert()" class="btn btn-outline-dark btn-sm rounded-pill px-3">Manage</button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                
                <c:if test="${empty bookings}">
                    <div class="text-center text-secondary py-5 bg-white rounded-4 shadow-sm border border-slate-100">
                        <span class="fs-1 d-block mb-2">🧳</span>
                        <p class="mb-1 small fw-bold text-slate-800">No active bookings yet.</p>
                        <p class="mb-0 text-muted small">Select an apartment and place your dates to book.</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</main>

<footer class="py-4 bg-white border-top mt-5">
    <div class="container text-secondary small d-flex justify-content-between">
        <div>Shortlet Discover &copy; 2026. All rights reserved.</div>
        <div>Standard lodging references provided from external search engines.</div>
    </div>
</footer>

<!-- Lightbox Modal -->
<div class="lightbox" id="customLightbox" onclick="closeLightbox()">
    <button class="lightbox-close">&times;</button>
    <img class="lightbox-content" id="lightboxImg" src="" alt="View" onclick="event.stopPropagation()">
    <div id="lightboxTitle" style="color: white; font-weight: 600; margin-top: 15px; text-align: center; font-size: 18px;"></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

<script>
    // Initialize Leaflet Map
    document.addEventListener("DOMContentLoaded", function() {
        var map = L.map('map').setView([9.0820, 8.6753], 6); // general view centered near Nigeria
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        // Fetch coordinates markers
        fetch('/api/properties/markers')
            .then(response => response.json())
            .then(markers => {
                if (markers && markers.length > 0) {
                    var group = new L.featureGroup();
                    markers.forEach(marker => {
                        var popupContent = `
                            <div style="font-family: 'Outfit', sans-serif; padding: 4px;">
                                <h6 style="margin: 0 0 4px 0; font-weight: 700; color: #1e293b; font-size: 13px;">\${marker.title}</h6>
                                <p style="margin: 0 0 10px 0; color: #10b981; font-weight: 800; font-size: 12px;">&#8358;\${marker.price.toLocaleString()} / night</p>
                                <button onclick="scrollToProperty(\${marker.id})" style="background: #0f172a; color: white; border: none; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 600; cursor: pointer; width: 100%;">View Details</button>
                            </div>
                        `;
                        var customMarker = L.marker([marker.latitude, marker.longitude])
                            .bindPopup(popupContent)
                            .addTo(map);
                        group.addLayer(customMarker);
                    });
                    map.fitBounds(group.getBounds(), { padding: [40, 40] });
                }
            })
            .catch(err => console.error("Error loading map markers:", err));
    });

    function scrollToProperty(id) {
        var el = document.getElementById('property-' + id);
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
            el.style.boxShadow = '0 0 0 4px #10b981';
            setTimeout(() => {
                el.style.boxShadow = '';
            }, 2500);
        }
    }

    // Quick Filter toggle helper
    function toggleChip(id) {
        var chk = document.getElementById(id + 'Check');
        if (chk) {
            chk.checked = !chk.checked;
            document.getElementById('filterForm').submit();
        }
    }

    function setPriceBound(val) {
        var filterForm = document.getElementById('filterForm');
        var priceInput = filterForm.querySelector('input[name="maxPrice"]');
        if (priceInput) {
            priceInput.value = val;
            filterForm.submit();
        }
    }

    // Autocomplete predictions search bar script
    var searchInput = document.getElementById('searchInput');
    var autocompleteDropdown = document.getElementById('autocompleteDropdown');

    if (searchInput) {
        searchInput.addEventListener('input', function() {
            var val = this.value;
            if (val.length < 2) {
                autocompleteDropdown.style.display = 'none';
                return;
            }
            fetch('/api/search/suggest?q=' + encodeURIComponent(val))
                .then(res => res.json())
                .then(suggestions => {
                    autocompleteDropdown.innerHTML = '';
                    if (suggestions && suggestions.length > 0) {
                        suggestions.forEach(item => {
                            var div = document.createElement('div');
                            div.className = 'autocomplete-item';
                            div.textContent = item;
                            div.onclick = function() {
                                searchInput.value = item;
                                autocompleteDropdown.style.display = 'none';
                                searchInput.form.submit();
                            };
                            autocompleteDropdown.appendChild(div);
                        });
                        autocompleteDropdown.style.display = 'block';
                    } else {
                        autocompleteDropdown.style.display = 'none';
                    }
                });
        });

        document.addEventListener('click', function(e) {
            if (e.target !== searchInput && e.target !== autocompleteDropdown) {
                autocompleteDropdown.style.display = 'none';
            }
        });
    }

    // Heart toggle AJAX POST request
    function toggleWishlist(btn, propertyId) {
        fetch('/wishlist/toggle?propertyId=' + propertyId, {
            method: 'POST'
        })
        .then(res => {
            if (res.status === 401) {
                alert("Please log in to manage your wishlist favourites.");
                return;
            }
            return res.json();
        })
        .then(data => {
            if (data && typeof data.wishlisted !== 'undefined') {
                if (data.wishlisted) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            }
        })
        .catch(err => console.error("Error toggling wishlist favourite:", err));
    }

    // Photos carousel mechanics
    function prevSlide(btn) {
        var track = btn.parentNode.querySelector('.carousel-track');
        var index = parseInt(track.dataset.index || '0');
        var max = track.children.length - 1;
        index = index > 0 ? index - 1 : max;
        track.dataset.index = index;
        track.style.transform = 'translateX(-' + (index * 100) + '%)';
    }

    function nextSlide(btn) {
        var track = btn.parentNode.querySelector('.carousel-track');
        var index = parseInt(track.dataset.index || '0');
        var max = track.children.length - 1;
        index = index < max ? index + 1 : 0;
        track.dataset.index = index;
        track.style.transform = 'translateX(-' + (index * 100) + '%)';
    }

    // Lightbox modal mechanics
    function openLightbox(src, title) {
        var lb = document.getElementById('customLightbox');
        var lbImg = document.getElementById('lightboxImg');
        var lbTitle = document.getElementById('lightboxTitle');
        lbImg.src = src;
        lbTitle.textContent = title;
        lb.classList.add('active');
    }

    function closeLightbox() {
        var lb = document.getElementById('customLightbox');
        lb.classList.remove('active');
    }

    // Manage Reservation alert details
    function showManageAlert() {
        alert("Stay Management Policy Details:\n\n- Cancellation terms: Free cancellation is fully permitted up to 24 hours prior to check-in.\n- Re-scheduling support: Contact support@shortlet.com or message host directly to request date updates.");
    }

    // Inquire about Jiji shortlet listings AJAX function
    function inquireJiji(listingId) {
        fetch('/inquire?listingId=' + listingId, {
            method: 'POST'
        })
        .then(res => {
            if (res.status === 401) {
                alert("Please log in to inquire about Jiji shortlets.");
                return;
            }
            return res.json();
        })
        .then(data => {
            if (data && data.success) {
                // Show a premium floating toast alert dynamically
                var alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-info border-0 shadow-lg rounded-3 mb-4 d-flex align-items-center gap-2';
                alertDiv.style.position = 'fixed';
                alertDiv.style.bottom = '20px';
                alertDiv.style.right = '20px';
                alertDiv.style.zIndex = '9999';
                alertDiv.style.maxWidth = '400px';
                alertDiv.style.transition = 'all 0.5s ease-in-out';
                alertDiv.innerHTML = `
                    <span style="font-size: 1.25rem;">📬</span>
                    <div>
                        <strong>Inquiry Submitted!</strong>
                        <div class="small mt-1">${data.message}</div>
                    </div>
                `;
                document.body.appendChild(alertDiv);
                
                // Animate entrance and fadeout
                setTimeout(() => {
                    alertDiv.style.opacity = '0';
                    alertDiv.style.transform = 'translateY(20px)';
                    setTimeout(() => alertDiv.remove(), 600);
                }, 5000);
            } else if (data && data.error) {
                alert("Inquiry failed: " + data.error);
            }
        })
        .catch(err => {
            console.error("Error submitting Jiji inquiry:", err);
            alert("An error occurred. Please try again.");
        });
    }
</script>
</body>
</html>
