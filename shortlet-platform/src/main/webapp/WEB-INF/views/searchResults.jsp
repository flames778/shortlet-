<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container py-5">
        <h1>Search Results</h1>
        <div class="row">
            <c:forEach var="property" items="${searchResults}">
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
    </div>
</body>
</html>