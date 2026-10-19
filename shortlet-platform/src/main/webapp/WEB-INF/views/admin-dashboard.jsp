<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=1200">
    <title>Admin Dashboard - Shortlet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f5f7fb; min-width: 1200px; }
        .brand-mark { width: 34px; height: 34px; border-radius: 9px; background: #16c79a; display: inline-grid; place-items: center; color: #041221; font-weight: 800; }
        .stat { border-left: 4px solid #16c79a; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid px-lg-5">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold" href="/admin"><span class="brand-mark">S</span> Shortlet Admin</a>
        <div class="ms-auto d-flex align-items-center gap-3">
            <span class="text-white-50 d-none d-md-inline">${sessionScope.user.email}</span>
            <a href="/logout" class="btn btn-outline-light btn-sm">Logout</a>
        </div>
    </div>
</nav>

<main class="container-fluid px-lg-5 py-5">
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
        <div>
            <p class="text-success fw-semibold mb-1">Protected admin workspace</p>
            <h1 class="fw-bold mb-0">Bookings and users</h1>
        </div>
        <a href="/admin/export.xlsx" class="btn btn-success btn-lg">Export to Excel</a>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="bg-white shadow-sm rounded p-4 stat">
                <div class="text-secondary small">Registered users</div>
                <div class="display-6 fw-bold">${users.size()}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="bg-white shadow-sm rounded p-4 stat">
                <div class="text-secondary small">Bookings</div>
                <div class="display-6 fw-bold">${bookings.size()}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="bg-white shadow-sm rounded p-4 stat">
                <div class="text-secondary small">Export format</div>
                <div class="display-6 fw-bold">XLSX</div>
            </div>
        </div>
    </div>

    <div class="table-responsive bg-white shadow-sm rounded">
        <table class="table table-striped table-hover align-middle mb-0">
            <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Password Hash</th>
                <th>Check-in</th>
                <th>Check-out</th>
                <th>Total</th>
                <th>Payment</th>
                <th>Apartment Title</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="booking" items="${bookings}">
                <tr>
                    <td>${booking.id}</td>
                    <td class="fw-semibold">${booking.userName}</td>
                    <td>${booking.userEmail}</td>
                    <td><code class="small">${booking.passwordHash}</code></td>
                    <td>${booking.checkIn}</td>
                    <td>${booking.checkOut}</td>
                    <td><c:if test="${not empty booking.totalAmount}">&#8358;${booking.totalAmount}</c:if></td>
                    <td>${booking.paymentMethod}</td>
                    <td>${booking.apartmentTitle}</td>
                    <td><span class="badge text-bg-${booking.status == 'CONFIRMED' ? 'success' : 'warning'}">${booking.status}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty bookings}">
                <tr><td colspan="10" class="text-center text-secondary py-4">No booking records available.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
