// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFavoriteNotifications = functions.firestore.document("/semanas/{semana}/atividades/{atividade}").onUpdate(event =>{
    const splitedPath = event.after.ref.path.split("/");

    return admin.firestore().collection("semanas").doc(splitedPath[1]).get().then(semanaModificada => {
        const notificationContent = {
            notification: {
                title: semanaModificada.data().nome,
                body: event.after.data().nome + " sofreu uma alteração, venha ver!",
            }
        };

        admin.firestore().collection("users").get().then(users => {
            users.forEach(user => {
                const favorites = admin.firestore().collection("users").doc(user.data().uid).collection("favorites");
                favorites.where("id", "=", splitedPath[3]).get().then(favoritouEvento => {
                    if(!favoritouEvento.empty) admin.messaging().sendToDevice(user.data().token, notificationContent);
                });
            });
        });
        
    });
});