<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=1200">
    <title>Forgot Password - Shortlet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap');
        
        body {
            font-family: 'Outfit', sans-serif;
            min-width: 1200px;
            min-height: 100vh;
            background: linear-gradient(135deg, #0f172a, #1e293b);
            color: #1e293b;
        }
        .auth-card {
            max-width: 480px;
            border: 0;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
            background: white;
        }
        .brand-mark {
            width: 40px;
            height: 40px;
            border-radius: 12px;
            background: linear-gradient(135deg, #10b981, #059669);
            display: inline-grid;
            place-items: center;
            color: white;
            font-weight: 800;
        }
        .btn-success {
            background: linear-gradient(135deg, #10b981, #059669);
            border: none;
            font-weight: 600;
        }
        .btn-success:hover {
            background: linear-gradient(135deg, #059669, #047857);
        }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center">
<main class="container py-5">
    <div class="mx-auto card auth-card">
        <div class="card-body p-5">
            <a href="/login" class="d-inline-flex align-items-center gap-2 text-decoration-none text-dark fw-bold mb-4">
                <span class="brand-mark">S</span> Shortlet
            </a>
            
            <h1 class="h3 fw-bold mb-2">Reset Password</h1>
            <p class="text-secondary small mb-4">Enter your email address and we'll send you recovery instructions.</p>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger border-0 small rounded-3 mb-4 shadow-sm">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success border-0 small rounded-3 mb-4 shadow-sm">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <form method="post" action="/forgot-password" class="mb-3">
                <div class="mb-4">
                    <label class="form-label small fw-semibold text-secondary" for="email">Email Address</label>
                    <input class="form-control form-control-lg rounded-3" id="email" name="email" type="email" placeholder="name@example.com" required>
                </div>
                <button class="btn btn-success btn-lg w-100 rounded-pill" type="submit">Send Recovery Link</button>
            </form>
            
            <div class="text-center mt-4">
                <a href="/login" class="text-secondary small text-decoration-none">← Return to Login</a>
            </div>
        </div>
    </div>
</main>
</body>
</html>
