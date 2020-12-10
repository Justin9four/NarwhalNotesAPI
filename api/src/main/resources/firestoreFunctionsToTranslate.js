// cd /Users/jeremy/Desktop/SeniorProjectFirebase/functions && alias firebase="`npm config get prefix`/bin/firebase" && firebase deploy && git add . && git commit -m "add modules" && git push origin master

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// https://stackoverflow.com/a/13403498
function generateQuickGuid() {
    return Math.random().toString(36).substring(2, 15) +
            Math.random().toString(36).substring(2, 15);
}

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Fawkes!");
});

/**********************************************************************
 * NOTE_FUNCTIONS
 *
 *
 **********************************************************************/

exports.addNote = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = generateQuickGuid();
var title = request.query.title;
var creator = request.query.creator;
var date = admin.firestore.FieldValue.serverTimestamp();
var text = request.query.text;


var newNote = {
    id: id,
    title: title,
    creator: creator,
    date: date,
    text: text
};

admin.firestore().collection("notes").doc(id).set(newNote).then(function() {
    response.send({success:true, id:id});
    return;
}, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});

exports.updateNote = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = request.query.id;
var title = request.query.title;
var creator = request.query.creator;
var text = request.query.text;

var updateFields = {};
var success = false;

if (typeof title !== 'undefined') {
    // the variable is defined
    updateFields.title = title;
    success = true;
};
if (typeof creator !== 'undefined') {
    // the variable is defined
    updateFields.creator = creator;
    success = true;
};
if (typeof text !== 'undefined') {
    // the variable is defined
    updateFields.text = text;
    success = true;
};

if(success == true){
    admin.firestore().collection("notes").doc(id).update(updateFields).then(function() {
        response.send({success:true});
        return;
    }, reason => {
        console.log(reason);
    response.send({success:false, message:reason});
    return;
});
}
else {
    response.send({success:false, message:"Please send fields to update."});
}
});

exports.getNotesByCreator = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);
var creator = request.query.creator;
var docs = [];

admin.firestore().collection("notes").where("creator", "==", creator)
        .get()
        .then(function(documents) {
            if(documents._size != 0) {
                documents.forEach((doc) => {
                    var data = doc.data();
                if (typeof data !== 'undefined'){
                    data.date = data.date.toDate();
                };
                docs.push(data);
            });
                response.send(docs);
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});

exports.getNote = functions.https.onRequest((request, response) => {
    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = request.query.id;

admin.firestore().collection("notes").where("id", "==", id)
        .get()
        .then(function(documents) {
            if (documents._size != 0) {
                documents.forEach((doc) => {
                    var data = doc.data();
                if (typeof data !== 'undefined'){
                    data.date = data.date.toDate();
                    response.send(data);
                };
            });
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});
exports.deleteNote = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = request.query.id;

admin.firestore().collection("notes").where("id", "==", id)
        .get()
        .then(function(documents) {
            if (documents._size != 0) {
                documents.forEach((doc) => {
                    doc.ref.delete()
                            .then(
                                    response.send({success:true})
                                    , reason => {
                        console.log(reason);
                response.send({success:false, message:reason});
            });
            });
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});

/**********************************************************************
 * USER_FUNCTIONS
 *
 *
 **********************************************************************/

exports.addUser = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = generateQuickGuid();
var firstName = request.query.firstName;
var lastName = request.query.lastName;
var username = request.query.username;
var hash = request.query.hash;
var dob = request.query.dob;
var email = request.query.email;
var creationDate = admin.firestore.FieldValue.serverTimestamp();

var newUser = {
    id: id,
    firstName: firstName,
    lastName: lastName,
    username: username,
    hash: hash,
    dob: dob,
    email: email,
    date: creationDate
};

admin.firestore().collection("users").doc(id).set(newUser).then(function() {
    response.send({success:true, id:id});
    return;
}, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});

exports.updateUser = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = request.query.id;
var firstName = request.query.firstName;
var lastName = request.query.lastName;
var username = request.query.username;
var hash = request.query.hash;
var dob = request.query.dob;
var email = request.query.email;

var updateFields = {};
var success = false;

if (typeof firstName !== 'undefined') {
    // the variable is defined
    updateFields.firstName = firstName;
    success = true;
};
if (typeof lastName !== 'undefined') {
    // the variable is defined
    updateFields.lastName = lastName;
    success = true;
};
if (typeof username !== 'undefined') {
    // the variable is defined
    updateFields.username = username;
    success = true;
};
if (typeof hash !== 'undefined') {
    // the variable is defined
    updateFields.hash = hash;
    success = true;
};
if (typeof dob !== 'undefined') {
    // the variable is defined
    updateFields.dob = dob;
    success = true;
};
if (typeof email !== 'undefined') {
    // the variable is defined
    updateFields.email = email;
    success = true;
};

if(success == true){
    admin.firestore().collection("users").doc(id).update(updateFields).then(function() {
        response.send({success:true});
        return;
    }, reason => {
        console.log(reason);
    response.send({success:false, message:reason});
    return;
});
} else {
    response.send({success:false, message:"Please send fields to update."});
}
});

exports.getUserByUsername = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);
var username = request.query.username;

admin.firestore().collection("users").where("username", "==", username)
        .get()
        .then(function(documents) {
            if (documents._size != 0) {
                documents.forEach((doc) => {
                    var data = doc.data();
                if (typeof data !== 'undefined'){
                    data.date = data.date.toDate();
                    response.send(data);
                };
            });
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }), reason => {
    console.log(reason);
    response.send({success:false, message:reason});
    return;
};
});

exports.getUser = functions.https.onRequest((request, response) => {
    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);
var id = request.query.id;

admin.firestore().collection("users").where("id", "==", id)
        .get()
        .then(function(documents) {
            if (documents._size != 0) {
                documents.forEach((doc) => {
                    var data = doc.data();
                if (typeof data !== 'undefined'){
                    data.date = data.date.toDate();
                    response.send(data);
                };
            });
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});

exports.deleteUser = functions.https.onRequest((request, response) => {

    if (typeof request.headers.origin !== 'undefined')
response.setHeader('Access-Control-Allow-Origin', request.headers.origin);
else
response.setHeader('Access-Control-Allow-Origin', "http://localhost:3000");
response.setHeader('Access-Control-Allow-Credentials', true);

var id = request.query.id;

admin.firestore().collection("users").where("id", "==", id)
        .get()
        .then(function(documents) {
            if (documents._size != 0) {
                documents.forEach((doc) => {
                    doc.ref.delete()
                            .then(
                                    response.send({success:true})
                                    , reason => {
                        console.log(reason);
                response.send({success:false, message:reason});
            });
            });
            } else {
                response.send({success:false, message:"Document could not be found."});
            };
            return;
        }, reason => {
    console.log(reason);
response.send({success:false, message:reason});
return;
});
});