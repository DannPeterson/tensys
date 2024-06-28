# 02-backend Module

This is the backend module for the TenSys application. It uses SpringBoot and MySQL database.

## Configuration

1. In the configuration file `src/main/resources/application.yml`, you need to specify:
    - Database access details
    - JWT secret key
    - `GOOGLE_API_KEY` for the translation service

2. In the configuration file `com/supportportal/constant/EmailConstant.java`, you need to specify the email settings from which user emails will be sent.
