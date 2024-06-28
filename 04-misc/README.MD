# 04-misc Module

## CPV Codes Database

In the `CPV` folder, you will find the database of all CPV codes.

### Detailed Structure

- The `04-misc/CPV/cpv_2008_xml` directory contains all CPV codes in all European languages.
- The `04-misc/CPV/json_ru` directory contains the prepared CPV codes for the TenSys project. These are in JSON format and divided into 10 files, in the languages required for the project. These files are created to facilitate the population of the CPV table when the project is restarted and the database needs to be filled.

Make sure to use these JSON files to initialize your database with the necessary CPV codes when setting up the project. Use `04-misc/CPV/json_ru` to fill `CPV` table
