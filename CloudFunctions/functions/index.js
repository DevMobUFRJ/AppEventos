// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFavoriteNotifications = functions.firestore.document("/semanas/{semana}/atividades/{atividade}").onUpdate(event =>{
    const splitedPath = event.after.ref.path.split("/");

    const notificationContent = {
        data: {
            body: event.after.data().nome + " sofreu uma alteração, venha ver!",
            activityId: splitedPath[3],
            weekId: splitedPath[1]
        }
    }

    const weekDetails = admin.firestore().collection("semanas").doc(splitedPath[1]).get().then(semanaModificada => {
        notificationContent.data.title = semanaModificada.data().nome;
    });

    const usersRef = admin.firestore().collection("users");
    const listUsers =  usersRef.get();

    return Promise.all([listUsers, weekDetails]).then(result =>{
        const users = result[0]
        users.forEach(user => {
            usersRef.doc(user.data().uid).collection("favorites").where("id", "=", splitedPath[3]).get().then(favoritouEvento => {
                if(!favoritouEvento.empty) admin.messaging().sendToDevice(user.data().token, notificationContent);
                console.log(user.data().token)
            });
        });
    });
});