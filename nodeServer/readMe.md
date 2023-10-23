### Node NoSQL Messaging server

This is a nodejs server that is subscribed to the topic poll and listens for finished polls published by the FeedApp

### Installation requirements:
* [Nodejs](https://nodejs.org/en)
* [MQTT broker (HiveMQ)](https://github.com/hivemq/hivemq-community-edition/releases/)
* [MongoDb](https://www.mongodb.com/docs/manual/administration/install-community/)

### Local Setup
* Create a new MongoDB database called: ``FeedApp`` and a new collection called ``PollResult``. (Should run on localhost:27017)
* Start the HiveMQ broker. (Should run on localhost:1883)