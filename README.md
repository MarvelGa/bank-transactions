1. Launch the application as Spring Boot App. Run the Application (org/banktransaction/Application.java).
2. App works on - http://localhost:8080/ . All setting in application.properties
4. You will see the buttons with different functionality. Click on some button and if there is an input field, enter data and press submit. Then you will see the result of your searching.

About the App:

- The App gets data form json - data.json (file src/main/resources/data.json) and save it in "hsqldb" DataBase.
  
About testing:
- there is tests for TransactionService - TransactionServiceTest. I used 2 files for testing: incorrect-test-data.json (src/test/resources/incorrect-test-data.json) and test-data.json (src/test/resources/test-data.json).

About Front-end:
- I used thymeleaf for present front-end part. All files you will find in templates (src/main/resources/templates).

