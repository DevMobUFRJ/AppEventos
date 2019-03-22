// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFavoriteNotifications = functions.firestore.document("/semanas/{semana}/atividades/{atividade}").onUpdate(event =>{
  const atividade = event.after.ref.path.split("/")[3];
  
  const notificationContent = {
    notification: {
      title: "Isso ai",
      body: "Agora foi de novo",
    }
  };
  
  return admin.firestore().collection("users").get().then(users => {

    users.forEach(user => {
      const favorites = admin.firestore().collection("users").doc(user.data().uid).collection("favorites");
      favorites.where("id", "=", atividade).get().then(favoritouEvento => {
        if(!favoritouEvento.empty){
          admin.messaging().sendToDevice(user.data().token, notificationContent);
        }
      });
    });

    return 0;
  });
});