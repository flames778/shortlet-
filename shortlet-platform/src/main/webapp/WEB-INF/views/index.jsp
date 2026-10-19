<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shortlet - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/custom.css">
</head>
<body>
    <header class="bg-dark text-white text-center py-5">
        <h1>Short stays, serious comfort</h1>
        <p>Book stylish temporary apartments across the city, or list your property for trusted guests.</p>
        <div class="d-flex justify-content-center gap-3">
            <a href="#search" class="btn btn-primary">Find a stay</a>
            <a href="#" class="btn btn-outline-light">List your property</a>
        </div>
    </header>

    <section id="search" class="container py-5">
        <h2>Search for a stay</h2>
        <form action="search" method="post" class="row g-3">
            <div class="col-md-4">
                <input type="text" name="city" class="form-control" placeholder="Lagos, Abuja, Ibadan, Kano" required>
            </div>
            <div class="col-md-2">
                <input type="date" name="checkin" class="form-control" required>
            </div>
            <div class="col-md-2">
                <input type="date" name="checkout" class="form-control" required>
            </div>
            <div class="col-md-2">
                <select name="guests" class="form-select" required>
                    <option value="1">1 Guest</option>
                    <option value="2">2 Guests</option>
                    <option value="3">3 Guests</option>
                    <option value="4">4 Guests</option>
                    <option value="5">5 Guests</option>
                    <option value="6">6 Guests</option>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100">Search</button>
            </div>
        </form>
    </section>

    <section class="container py-5">
        <h2>Featured Stays</h2>
        <div class="row">
            <c:forEach var="property" items="${featuredProperties}">
                <div class="col-md-4">
                    <div class="card">
                        <img src="${property.imageUrl}" class="card-img-top" alt="Property Image">
                        <div class="card-body">
                            <h5 class="card-title">${property.name}</h5>
                            <p class="card-text">${property.neighbourhood}, ${property.city}</p>
                            <p class="card-text">₦${property.pricePerNight} per night</p>
                            <p class="card-text">Rating: ${property.rating}</p>
                            <a href="property.jsp?id=${property.id}" class="btn btn-primary">View Details</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <footer class="bg-light text-center py-3">
        <p>&copy; 2026 Shortlet. All rights reserved.</p>
    </footer>
</body>
</html>