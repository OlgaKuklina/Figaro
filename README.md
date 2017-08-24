# Figaro

Overview
======
Figaro is a user guide in his/her current location area, which allows authorized users to connect to their instagram profile, see videos or images taken in nearby area, see posted media and other people opinions about places of interest, favorite attractions, restaurants, events and more.

Explore and search
======

Figaro app implements the following features:

- Browse media content (video and images) taken nearby to user current location.
- Research for additional information about events and places of interest in particular area. 
- See all media content in the google map. 
- Change radius of the media search service.
- Browse recent media posted by authenticated user
- The pagination feature was implemented for item 5 via infinite scrolling with page size = 5. See the source code for details.

Release Notes
======
- The application works in instagram sandbox mode (this is the only option that instagram gives you until your app is reviewed by them). That’s why the instagram API gives us a very limited subset of media. It works only for sandbox users invited to this application. 

- For testing this solution you need to create your own instagram test account, with a new API key.  Use client_credentials.xml to put API key and your callback Uri into there.  Create a second instagram account and make it a sandbox user for the app (and connect the 2nd user to your initial instagram test account) and post some media in the tested location area. 

- The app was built using latest android studio 3.0 Canary 9. It supports min SDK version 23.

