## Synopsis

This is my financial balance tracking App built using Java and SQL with an Apache Derby database (relational database management system) to store data.  In the background, log4j2 is used to log errors.  Right now the log configurations is set to only log errors; however, when changed to debug mode, you will be able to see a lot of informative debug messages.  This APP allows users to track financial accounts and update balances.  The reason I started this project was because I needed something to track my finances.

This project is in BETA; as a result you will see TODO notes in my classes. Also, I like to comment my codes
with a lot of extra notes, it makes it easier for me to do future modifications. I will leave these notes in to help other people understand the code too.

Note: The user login and password upon download is blank inputs for both. In order to specify a username and password
go to Main.java and assign the variables with your username and password.

  public static final String LOGIN_USERNAME = "";
  public static final String LOGIN_PASSWORD = "";

![alt tag](https://raw.githubusercontent.com/dewitjin/balanceTrackerApp/master/images/loginImage.png)

When you are ready, create another java jar file and use that jar to create accounts and update balances. However, the one provided in the out folder is good to go as is.

## Code Example

No specific examples right now.

## Motivation

The project exists because I needed to replace an excel spreadsheet that I was using to track my finances.  I wanted something that was intuitive and I could keep private. I did not do a lot of research to see what was already built and free to download before starting this project.  I wanted to build something on my own.

![alt tag](https://raw.githubusercontent.com/dewitjin/balanceTrackerApp/master/images/addNewAccount.png)

Special thanks to Matthew Lawrence for his code reviews and help to get the project done. He was instrumental in helping me clean up some critical primary key codes.

## Installation

This project was built using Eclipse but you don't need Eclipse to run the jar file in the out folder.  First, in order to run the jar file to see what the app can do, you need to go into the out directory and run this command java -jar betaTracker.jar. Second, if you would like to modify the code and customize parts of it for your use, download the files and import it to an integrated development environment (IDE) such as Eclipse and start editing.

There are many things you could do to expand this project.  There are many things I would like to do, but this simple
App will help you track your finances right now without medications.

## API Reference

In order to keep track of "cash", I used the Open Exchange Rates API (https://openexchangerates.org/) to get current exchange rates.  I have disabled this function, however, in this public BETA version.  If you would like to use it, you will need to get an APP ID here: https://openexchangerates.org/about and use it here:

  ExchangeRateJListDialog.java class and assign your API ID to the private static final String URL variable.

However, the jar file attached will have this function enabled for you to use to start converting cash balances. Note that my APP ID comes with the free version of the API, which means the currency-based rate is in USD dollars. Since I am storing all my balances in CAD dollars, I convert foreign currencies to USD and then CAD before sending data to the database.  If you need to store in different currencies then you will have to modify various methods.

## Tests

No test right now.

## Contributors

The best way to reach me is via my Email at dewi.tjin@gmail.com.

If you would like to contribute please fork the repo and I will review code.

## License

The MIT License (MIT)

Copyright (c) 2016 DEWI TJIN

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


