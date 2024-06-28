# 01-tender-scraper Module

This module is designed to collect tender information for the TenSys database. Using Selenium, it periodically visits government tender websites of the Baltic countries - Latvia, Lithuania, and Estonia.

## Websites Scraped
- [Latvia](https://www.eis.gov.lv)
- [Lithuania](https://cvpp.eviesiejipirkimai.lt)
- [Estonia](https://riigihanked.riik.ee)

## Scrapers
The scrapers for each site are located in the package `src/main/java/com/supportportal/grabber`:
- `EisGovLvGrabber.java`
- `CpvvGrabber.java`
- `RhrGrabber.java`

## Scheduler Configuration
The data collection schedule and its depth are configured via `GrabberScheduler.java`.

## First Run Setup
1. Uncomment the method `addSources()` to add necessary entries to a new database. Do not forget to disable this method afterwards.
2. For the initial data collection, increase the depth of data collection in `GrabberScheduler`. The depth is configured in the methods `getRhrTenders`, `getEisGovLvTenders`, and `getCpvvTenders` in the first `for` loop. Generally, it is the number of pages of tenders in the past that the module will record in the database. It usually does not make sense to go back more than 1-2 months, so configure the loops accordingly for the first run. Collect all the data, then adjust the data collection schedule for daily operations, such as every couple of hours, but not too far into the past.

## Translation
One of the goals of the TenSys application is to provide convenient and quick access to relevant tenders. Therefore, all data is translated into five languages - Estonian, Latvian, Lithuanian, Russian, and English. The translation is done using the Google Translator API.

## Configuration
The Google Translator API key, as well as the database connection and JWT secret key, are specified in the `src/main/resources/application.yml` file of the module.
