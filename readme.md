![Heroku](https://heroku-badge.herokuapp.com/?app=strava-mattermost-connector)
# Strava Mattermost Connector

Integrate your Mattermost chat server with Strava. Under active development. 

You can start using it today by visiting: https://strava-mattermost-connector.herokuapp.com/

Be warned this application is under active development and so things may change or break! 😊

## Worklog (keeping track of what I've done each day)

* 3/8/2020
	* Refactored registration flow!
	* User can now provide their own mattermost details (it was being stored before, but provided automatically)
	* Added 'verified' field to the user details, removed the notion of storing user:code keys, only using user:athleteId now.
Using google static map api we're now displaying a preview of the route in the MM message!
* 3/7/2020
	* Installed Linux for Windows so I could run redis locally
	* Store mattermost configuration details for a user now instead of only their strava token, this will allow the app to accommodate multiple mattermost servers across multiple users
* 3/5/2020
	* Added thymleaf 
	* Created views for all of the pages in the existing flow (welcome, register, verify, done)
	* Added redis
	* Now persisting tokens into redis store
	* Got rid of h2 
* 3/3/2020
	* Hosting on heroku
	* Cleaned up properties
	* Refactored / simplified services
	* Added h2 for persisting tokens
	* Got registration / auth strava flow working
	* Runs now posting title to MM
* 3/2/2020
	* Create repo
	* Create Strava App (got client id, secret, etc)
	* Use ngrok to expose webhook
	* Setup VM with Ubuntu to run MM server
	* Codebase started, currently posting to MM and responding to Strava webhook subscription + events
