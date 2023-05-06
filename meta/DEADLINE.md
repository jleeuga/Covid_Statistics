# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice looking HTML.

## Part 1: App Description

> Please provide a firendly description of your app, including the
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

https://github.com/jleeuga/cs1302-api

This app allows the users to find information about a city and statistics
relating to Covid-19 and the coutnry the city resides in. To use this app,
the user must type in a real city name in the textfield at the
top of the app and press go. The app will tell the user if the input
is invalid. This input the user gives is used as input
for the city API which will generate the longitude, latitude,
population, and the specifc name of the city which includes the country
the city resides in. Using the specific name of the city from the
first API, the country the city resides in is taken and put as
input into the second API which generates statistics about the country
and its relation to Covid-19. From the second API, information such as
total cases, total deaths, ratio of cases per million, ratio of deaths
per million, and the total population of the country is generated.
Using the information generated, from both APIs, the city
is generated at the top while the country information is generated at
the bottom. If the name of a city is used by multiple cities,
the more well known one will be used. For example, if the user types
in Memphis, Memphis, Tennessee will be used instead of
Memphis, Egypt.

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

Something new I learned from working on this project is how to
use the serailzied names interface. I encountered
a problem with the names of my variables since they had to
match the format of the APIs, but they violated some of the
checkstyle rules. To fix this problem, I learned how to use
serialized names.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

If I could start the project over, I would spend more time
on researching different APIs. I feel that if I spent
more time researching different APIs,
I would have made something much more unique, and I would have
avoided troubles such figuring out how to connect the two
distinct APIs. In addtion, I wish I spent more time planning
the design of my app. To me, my
app feels a little plain.
