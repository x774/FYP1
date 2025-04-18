Guideline to Open and Run the FYP1 Project

1. Ensure all required software is downloaded, installed, and set up correctly. For setup instructions, refer to the XINWIE JOON - Final Report (https://drive.google.com/file/d/1550uk7sP_kmq7nPo8FhaR5Nyis_fGs49/view?usp=sharing), Appendix C Section.

2. Extract the FYP1 ZIP file to the location C:\xampp\htdocs. You can copy this location and paste it into the appropriate placeholder.

3. Open the XAMPP Control Panel, then start the Apache and MySQL modules. Ensure both are running (status should be green).

4. In the XAMPP Control Panel, for the MySQL module, click the Admin button. This should open phpMyAdmin in your browser at http://localhost/phpmyadmin/index.php.

5. Create a new database: Click New in the left panel. Then, create the database name "secure_voting_system"

6. In the secure_voting_system database, click the Import button in the top panel and import the secure_voting_system.sql file.

7. Open NetBeans and load the FYP1 project.

8. Wait for NetBeans to finish scanning the project.

9. Locate the login.jsp file in the FYP1/Web Pages/pages directory and run the file. The URL should be: http://localhost:8080/FYP1/pages/login.jsp

Admin Login Details: Email: admin@gmail.com Password: admin

User Login Details: Email: ali@gmail.com Password: ali

Public and Private Keys: Refer to the PublicPrivateKeys folder for the keys.

** When a new voting activity is created, the public and private key files will be downloaded to your browser's default download location, not the PublicPrivateKeys folder.
