const admin = require('firebase-admin');
const functions = require('firebase-functions');
var deviceTokenString;
var today = new Date();
today.setHours(0);
today.setMinutes(0);
today.setSeconds(0);
var tomorrow = new Date();
tomorrow.setHours(23);
tomorrow.setMinutes(59);
tomorrow.setSeconds(0);
var rightNow = new Date();

admin.initializeApp();
var db = admin.firestore();
db.settings({timestampsInSnapshots: true});

exports.sendData = functions.pubsub.topic('daily-tick').onPublish(() => {

    return db.collection('schedule')
        .where('date', '>', today)
        .where('date', '<', tomorrow)
        .get()
        .then(snapshot => {
            let promises = [];
            snapshot.forEach(course => {
                promises.push(db.collection('courses')
                    .where('courseCode', '==', course.data().courseCode)
                    .get())
            });
            return Promise.all(promises);
        })
        .then(courses => {
            let promises = [];
            courses.forEach(course => {
                course.forEach(element => {
                    promises.push(db.collection('courses').doc(element.id).collection('students').get());
                });
            });
            return Promise.all(promises);
        })
        .then(studentColls => {
            let promises = [];
            studentColls.forEach(studentColl => {
                studentColl.forEach(student => {
                    promises.push(db.collection('students')
                        .where('studentNumber', '==', student.data().studentNumber)
                        .get())
                });
            });
            return Promise.all(promises);
        })
        .then(students => {
            let promises = [];
            students.forEach(student => {
                student.forEach(element => {
                    console.log('PLEASE ' + JSON.stringify(element));
                    message = {
                        token: element.data().deviceToken
                    };
                    console.log(JSON.stringify(message));
                    promises.push(admin.messaging().send(message));
                });
            });
            return Promise.all(promises);
        })
        .catch(error => {
            console.log(error);
        })


});


