const admin = require('firebase-admin');
const functions = require('firebase-functions');
var deviceTokenString;
var today = new Date();
today.setHours(0);
today.setMinutes(0);
today.setSeconds(0);
var tomorrow = new Date();
tomorrow.setHours(23)
tomorrow.setMinutes(59);
tomorrow.setSeconds(0);
var rightNow = new Date();

admin.initializeApp(functions.config().firebase);
var db = admin.firestore();
db.settings({timestampsInSnapshots: true});

function removeOldSessions() {
    var copyDocuments = [];
    console.log("Setting up schedule for ", today.toDateString());

    db.collection("schedule")
        .get()
        .then(snapshot => {
            let promises = [];
            snapshot.forEach(element => {
                if (element.data().date.seconds < today.getTime() / 1000) {
                    copyDocuments.push(element.data());
                    const p = db.collection("schedule").doc(element.id).delete();
                    promises.push(p);
                }
            });
            return Promise.all(promises);

        })
        .catch(error => {
            console.log(error);
        });

}

exports.sendData =
    functions.https.onRequest((request, response) => {

        promises = [];
        console.log(today.toTimeString(), tomorrow.toTimeString());

        return db.collection("schedule")
            .where("date", ">", today)
            .where("date", "<", tomorrow)
            .get()
            .then(snapshot => {

                snapshot.forEach(element => {
                    console.log((element.data().date.seconds - new Date().getTime() / 1000), " difference");
                    //(element.data().date.seconds-new Date().getTime()/1000) < 15*60 && (element.data().date.seconds-new Date().getTime()/1000 > 0)
                    db.collection("students")
                        .doc(element.data().username)
                        .get()
                        .then(student => {
                            deviceTokenString = student.data().deviceToken;

                        }).then(result => {

                        let message = {
                            token: deviceTokenString,
                            data: {
                                docId: element.id,
                                studentNumber: element.data().username.toString(),
                                date: element.data().date.seconds.toString()
                            }
                        };

                        console.log(message);

                        const p = admin.messaging().send(message);
                        return (p);
                    }).catch(error => {
                        console.log(error)
                    });
                })
//return Promise.all(promises);
            }).catch(error => {

                console.log(error);

            });;
    })


//   const message = {
//     token: "dDZpbYJC1hI:APA91bFuu2zrvSafzBehgr3EK4FifPTLiK9DwEmuDzvOxkIlwmlnWlqUo9EMKboVHcmMlxIaLLdmo2qkc7r1gcLNzbxFPnyO2XwpUoZxBet3pEOjrg1dOYtFbe3wvmBzNGNjViSYpgYa",   // obtain device token id by querying data in firebase
//     data: {
//        title: "my_custom_title",
//        body:  "my_custom_body_message"
//        }
//     }

// return admin.messaging().send(message).then(response => {
//     // handle response
// });

exports.haazit =
    functions.https.onRequest((request, response) => {
        return removeOldSessions();
    });

// exports.update_daily_schedule =
// functions.pubsub.topic('daily-tick').onPublish((event) => {
//   return removeOldSessions();
// });
