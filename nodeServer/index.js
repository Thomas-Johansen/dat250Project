const mqtt = require("mqtt");
const { MongoClient } = require("mongodb");

// MQTT
const hostname = "localhost";
const port = "1883";
const mqttClient = mqtt.connect(`mqtt://${hostname}:${port}`);

const topic = "poll";

// MongoDb
const url = "mongodb://localhost:27017";
const mongoClient = new MongoClient(url);

mqttClient.on("connect", () => {
    mqttClient.subscribe(topic);
    console.log("Connected and subscribed");
});

mqttClient.on('error', (error) => {
    console.error("MQTT error: ", error);
});

mqttClient.on('close', () => {
    console.log("Closed connection");
});

mqttClient.on("message", (topic, message) => {
    console.log(`Received message on topic ${topic}: ${message.toString()}`);
    
    
    storeResult(message.toString())
});


async function storeResult(finishedPoll){
    console.log(finishedPoll);

    const dbName = "FeedApp";
    const collectionName = "PollResult";

    await mongoClient.connect();
    
    const db = mongoClient.db(dbName);
    const collection = db.collection(collectionName);

    await collection.insertOne(JSON.parse(finishedPoll));
    console.log("Added poll to NoSql database");
    mongoClient.close();
}