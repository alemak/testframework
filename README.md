TestingUtils framework has a number of settings for the WebBot(our WebDriver wrapper) configurable through the Spring property files.
This documents provides the reference on the format and purpose of those settings.

baseUrl
=======
Contains the baseUrl for the WebDriver.

*baseUrl=http://reltest05.dave.net-a-porter.com/*

WebBotLanguage
==============
Contains the language value for the regionalised pages.

*WebBotLanguage=EN*


WebBotCountry
=============
Contains the country value for the regionalised pages.

*WebBotCountry=GB*

disableLoadingDefaultHome
=========================
When you need to start browser with a blank page (baseUrl still has it's value).

*disableLoadingDefaultHome=true*

userAgentOverride
=================
A bit of a hack making the application think that the browser is something else - like Chrome pretending to be a Safari on the IPhone. Only implemented for Chrome at the moment.
So if you want to see the page intended to display on IPhone:

*userAgentOverride=Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16"*

chromeOptions
=============
If you need to set up some additional parameters for the Chrome you can use this property (for example set the window size)
Can take multiple options separated by a semicolon. Only for Chrome by definintion.

*chromeOptions=--window-size=1024,768*

chromePreferences
=================
If you need to change settings for the Chrome browser you can use this property (for example change accepted languages)
Can take multiple options separated by a semicolon. Only for Chrome.

*chromePreferences=intl.accept_languages=en,en-US;profile.default_content_settings.geolocation=2*
These sample settings will do the following:
First one:
in Chrome -> Settings -> Show advanced settings… -> Languages (Language and input settings …) -> first language will be English, second – English (United States)
Second setting:
in Chrome -> Settings ->Show advanced settings… -> Privacy (Content settings…) -> Location – third option ("Do not allow any site to track your physical location") will be selected.

proxyPAC
========
Setting the location of proxy autoconfiguration file (PAC) to use. Implemented for Firefox and Chrome.

*proxyPAC=http://wpad.london.net-a-porter.com/wpad-dev.dat*

proxyURL
========
If PAC file is not provided you can use this property to set the proxy manually. Implemented for Firefox and Chrome.
If proxyPAC is set then PAC will be used with proxyURL ignored.

*proxyUrl=devproxy.vm.wtf.nap:3128*

Appium settings
---------------
Settings for the Appium remote driver (capabilities)
----------------------------------------------------

platformName
============
iOS, Android, or FirefoxOS (iOS by default)

*platformName=iOS*

deviceName
==========
Options:
iPhone Simulator, iPad Simulator, iPhone Retina 4-inch, Android Emulator, Galaxy S4, etc
If not set - the default is iPad Simulator

*deviceName=iPhone Simulator*

platformVersion
===============
iOS system (7.1 by default)

*platformVersion=7.1*

browserName
===========
'Safari' for iOS and 'Chrome', 'Chromium', or 'Browser' for Android
(Safari by default)

*browserName=Safari*

avd
===
For Android only - which Android Virtual Device to use

*avd=AndroidMobile*

app
===
For app testing - the name of the app under test. If set - browserName is cleared

*app=MyApp*

udid
====
Unique device identifier of the connected physical device

*udid=1ae203187fc012g*
