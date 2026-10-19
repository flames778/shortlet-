<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=1200">
    <title>Error - Shortlet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { min-width: 1200px; min-height: 100vh; background: #071827; color: white; }
        .brand-mark { width: 40px; height: 40px; border-radius: 10px; background: #16c79a; display: inline-grid; place-items: center; color: #041221; font-weight: 800; }
    </style>
</head>
<body class="d-flex align-items-center">
<main class="container text-center py-5">
    <div class="d-inline-flex align-items-center gap-2 fw-bold mb-4"><span class="brand-mark">S</span> Shortlet</div>
    <h1 class="display-1 fw-bold">${code}</h1>
    <p class="lead text-white-50">${message}</p>
    <div class="d-flex justify-content-center gap-2 mt-4">
        <a href="/" class="btn btn-success btn-lg">Go home</a>
        <a href="/login" class="btn btn-outline-light btn-lg">Login</a>
    </div>
</main>
</body>
</html>
