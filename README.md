
#  ![icons8-comedy-100](https://user-images.githubusercontent.com/6971421/29690362-82d0e6a0-88db-11e7-8a34-bc84b0142bb2.png) Figaro 


![fcontext](https://user-images.githubusercontent.com/6971421/29857176-2b92589e-8d0c-11e7-8ea2-e65fe549cd0c.jpg)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/11f82c7825fe4143bc2b0484df647b8c)](https://www.codacy.com/app/OlgaKuklina/Figaro?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OlgaKuklina/Figaro&amp;utm_campaign=Badge_Grade) [![Build Status](https://travis-ci.org/OlgaKuklina/Figaro.svg?branch=master)](https://travis-ci.org/OlgaKuklina/Figaro)

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
- The pagination feature was implemented for home page via infinite scrolling with page size = 5. See the source code for details.

Release Notes
======
The application works in instagram sandbox mode (this is the only option that instagram gives you until your app is reviewed by them). That’s why the instagram API gives us a very limited subset of media. It works only for sandbox users invited to this application. 

Getting Started
======
- For testing this solution you need to create your own instagram test account, with a new API key. 
- Use client_credentials.xml to put API key and your callback Uri into there. 
- Create a second instagram account and make it a sandbox user for the app (and connect the 2nd user to your initial instagram test account) and post some media in the tested location area. 

Used libraries
======

- Picasso for loading and rendering images.
- CommonsIO/CommonsHttpClient for accessing instagram REST api.
- A number of standard Android libraries.

The app was built using latest android studio 3.0 Canary 9.

Application min API Level: 23

License
======

Copyright 2017 Olga Kuklina

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
