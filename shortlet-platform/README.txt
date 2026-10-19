Shortlet Platform
=================

Shortlet is a pure Java web application using embedded Apache Tomcat, Jakarta Servlets, JSP, JDBC, H2, Bootstrap 5, Google OAuth 2.0, and Apache POI Excel export.

Requirements
------------
- Java 17+
- Maven 3.9+
- Google OAuth credentials for Google login

Run locally
-----------
1. Build the executable JAR:
   mvn clean package

2. Start the app:
   java -jar target/app.jar

3. Open:
   http://localhost:8080

Demo accounts
-------------
- Admin: admin@shortlet.com / admin123
- User: ada@example.com / password123
- User: tunde@example.com / password123

Configuration
-------------
The app reads application.properties from the classpath and lets environment variables override every setting.

Common environment variables:
- PORT=8080
- APP_BASE_URL=http://localhost:8080
- DB_URL=jdbc:h2:file:./data/shortlet;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
- DB_USER=sa
- DB_PASSWORD=
- GOOGLE_CLIENT_ID=your-google-client-id
- GOOGLE_CLIENT_SECRET=your-google-client-secret
- GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/google/callback

Google OAuth setup
------------------
1. Go to Google Cloud Console.
2. Create or select a project.
3. Configure OAuth consent screen.
4. Create OAuth Client ID credentials.
5. Choose Web application.
6. Add this authorized redirect URI for local development:
   http://localhost:8080/oauth2/google/callback
7. For Cloud Run, add:
   https://YOUR-SERVICE-URL.run.app/oauth2/google/callback
8. Set GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, and GOOGLE_REDIRECT_URI in your runtime environment.

Docker
------
1. Build:
   mvn clean package
   docker build -t shortlet-platform .

2. Run:
   docker run -p 8080:8080 \
     -e GOOGLE_CLIENT_ID=your-client-id \
     -e GOOGLE_CLIENT_SECRET=your-client-secret \
     -e GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/google/callback \
     shortlet-platform

Deploy to Google Cloud Run
--------------------------
1. Build the app:
   mvn clean package

2. Build and push with Google Cloud:
   gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/shortlet-platform

3. Deploy:
   gcloud run deploy shortlet-platform \
     --image gcr.io/YOUR_PROJECT_ID/shortlet-platform \
     --platform managed \
     --region us-central1 \
     --allow-unauthenticated \
     --set-env-vars GOOGLE_CLIENT_ID=your-client-id,GOOGLE_CLIENT_SECRET=your-client-secret,GOOGLE_REDIRECT_URI=https://YOUR-SERVICE-URL.run.app/oauth2/google/callback

4. Cloud Run will print a public .run.app URL. Add that callback URL to your Google OAuth client.

Notes
-----
- The database is H2 file-based by default at ./data/shortlet.
- Tables and demo data are created automatically on startup.
- Admin pages are protected by AuthenticationFilter and require role ADMIN.
- Excel export is generated on the fly at /admin/export.xlsx.
