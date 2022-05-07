# Introduction
​
__Activity Advisor API__ is a REST API that suggests activities based on location, time, temperature, phone battery percentage and steps. The activities are selected from TrueWay Destinations API and are randomly selected based on the sent parameters. All the parameters must be sent in the X-Context header.

[__OpenAPI documentation__](http://localhost:8080/api-specs/ui/?url=http://localhost:8080/api-specs/activity-advisor/openapi.json)
​
## Purpose
​
ActivityAdvisor is primarily used with mobile apps to __suggest an activity to users__ when they can't think of any activities.
​
__Typical use case__ scenario consists of:
​
1. Sending parameters to ActivityAdvisor API 
2. Getting an activity suggestion based on parameters
3. Repeating until the user is satisfied with the suggestion
​
## Functionality
​
The activity is selected based on sent parameters. True Way Destinations API serves the ActivityAdvisor API with a pool of different destinations based on location. 

So naturally the most important parameter is location. Location can be sent to Activity advisor API as a JSON object location with children longitude and latitude, or it can be sent as a place_id, which ActivityAdviser then turns into coordinates.

The next parameter is time, based on which ActivityAdvisor API sorts activities into 4 periods of day (morning, noon, evening, night). 

The temperature parameter is implemented in a way where if the temperature is higher than 10°C, then the indoor activities have a 66.66% and the outdoor activities only a 33.33% chance of being suggested. And if the temperature is lower than 10°C, then the outdoor activities have a 66.66% and the indoor activities only a 33.33% chance of being suggested.

The battery percentage parameter is implemented is a way where the battery percentage is lower than 30%, ActivityAdvisor only pick between activities that are in the 1km radius. And if the battery percentage is higher, ActivityAdvisor picks activities in the 2km radius.

And lastly the steps parameter. ActivityAdvisor takes into account steps only if the step count for the day is more than the average (10000 steps) and if the steps are higher than that treshold, ActivityAdvisor suggests a more relaxing activity.
