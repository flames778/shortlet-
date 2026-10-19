<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Shortlet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animations.css">
</head>
<body>
    <div class="login-background">
        <div class="login-card p-4 p-md-5">
            <div class="mb-4 text-start">
                <a href="${pageContext.request.contextPath}/" class="btn btn-sm text-white-50 border-0 p-0 fs-6 back-to-home-link" style="transition: color 0.3s ease;">
                    <i class="bi bi-arrow-left me-1"></i> Back to Home
                </a>
            </div>
            
            <h2 class="text-white fw-bold mb-3">Welcome Back</h2>
            <p class="text-white-50 small mb-4">Experience premium shortlets in style.</p>
            
            <form action="login" method="post">
                <div class="mb-3 text-start text-white">
                    <label for="email" class="form-label small fw-semibold">Email</label>
                    <input type="email" name="email" id="email" class="form-control" placeholder="name@example.com" required>
                </div>
                <div class="mb-4 text-start text-white">
                    <label for="password" class="form-label small fw-semibold">Password</label>
                    <div class="input-group">
                        <input type="password" name="password" id="password" class="form-control border-end-0" placeholder="Enter password" required>
                        <button class="btn btn-outline-light border-white border-opacity-25 border-start-0" type="button" onclick="togglePassword()" style="border-radius: 0 8px 8px 0;"><i class="bi bi-eye"></i></button>
                    </div>
                </div>
                <div class="mb-4 d-flex justify-content-between align-items-center text-white small">
                    <div class="form-check">
                        <input type="checkbox" name="remember" id="remember" class="form-check-input">
                        <label for="remember" class="form-check-label text-white-50">Remember me</label>
                    </div>
                </div>
                <button type="submit" class="btn btn-success w-100 rounded-pill py-2.5 fw-semibold btn-glow">Sign In</button>
            </form>
            
            <p class="text-white-50 mt-4 mb-0 small">
                Don’t have an account? <a href="${pageContext.request.contextPath}/signup" class="text-success fw-semibold text-decoration-none">Create one</a>
            </p>
        </div>
    </div>
    
    <script>
        function togglePassword() {
            const passwordField = document.getElementById('password');
            const toggleIcon = document.querySelector('.input-group i');
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                toggleIcon.className = 'bi bi-eye-slash';
            } else {
                passwordField.type = 'password';
                toggleIcon.className = 'bi bi-eye';
            }
        }
    </script>
</body>
</html>