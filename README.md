# Dynatrace-ExtraPointsTask
The task that I completed for a Dynatrace recruitment process. The task description is available on the Dynatrace Gdańsk office website. Application was written in Java with use of Swing framework as well as Redis for windows, Jedis library, Apache POI library. The purpose of the application is to test connection to a given URL every minute and write the results into xls file of a given name which is saved into given cache service(Redis).Record is held there for 5 minutes. The filename and path can be directly chosen from the GUI, as well as URL change can also be accessed from there. All of above have given default values which are: Name: ConnectionTestResults.xls, Path: D:, URL: https://dynatrace.com. The application was written in a way to be easily extendable with another features. Adding other metrics to it is simple: Add the code for measuring the metric, add sheet to the excel workbook, add button that triggers the measurement.
